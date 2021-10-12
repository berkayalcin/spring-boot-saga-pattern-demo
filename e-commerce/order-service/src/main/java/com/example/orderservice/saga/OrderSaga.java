package com.example.orderservice.saga;

import com.example.coreapi.command.ProcessPaymentCommand;
import com.example.coreapi.command.ReserveProductCommand;
import com.example.coreapi.command.ReverseInventoryCommand;
import com.example.coreapi.command.ReversePaymentCommand;
import com.example.coreapi.event.InventoryReservationCancelledEvent;
import com.example.coreapi.event.PaymentCancelledEvent;
import com.example.coreapi.event.PaymentProcessedEvent;
import com.example.coreapi.event.ProductReservedEvent;
import com.example.orderservice.command.model.ApproveOrderCommand;
import com.example.orderservice.command.model.CancelOrderCommand;
import com.example.orderservice.core.entity.Order;
import com.example.orderservice.event.model.OrderApprovedEvent;
import com.example.orderservice.event.model.OrderCancelledEvent;
import com.example.orderservice.event.model.OrderCreatedEvent;
import com.example.orderservice.query.model.FindOrderQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Saga
@Slf4j
@RequiredArgsConstructor
public class OrderSaga {
    @Autowired
    private transient CommandGateway commandGateway;
    @Autowired
    private transient QueryGateway queryGateway;

    private String paymentId;

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(final OrderCreatedEvent orderCreatedEvent) {
        log.info("Start Order Create Saga With {}", orderCreatedEvent.getId());

        final var processPaymentCommand = buildProcessPaymentCommand(orderCreatedEvent);
        SagaLifecycle.associateWith("paymentId", processPaymentCommand.getPaymentId());
        commandGateway.send(processPaymentCommand, (commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                // Start compensating transaction
                log.error(commandResultMessage.exceptionResult().getMessage());
                final var cancelOrderCommand = CancelOrderCommand.builder()
                        .id(orderCreatedEvent.getId())
                        .reason(commandResultMessage.exceptionResult().getMessage())
                        .build();
                commandGateway.send(cancelOrderCommand);
            }
        });
    }

    private ProcessPaymentCommand buildProcessPaymentCommand(final OrderCreatedEvent orderCreatedEvent) {
        this.paymentId = UUID.randomUUID().toString();
        return ProcessPaymentCommand.builder()
                .paymentId(this.paymentId)
                .ibanNumber("IBAN")
                .userId(orderCreatedEvent.getUserId())
                .orderId(orderCreatedEvent.getId())
                .build();
    }

    @SagaEventHandler(associationProperty = "paymentId")
    public void handle(final PaymentProcessedEvent paymentProcessedEvent) {
        log.info("Payment Processed {} For Order {}", paymentProcessedEvent.getPaymentId(), paymentProcessedEvent.getOrderId());
        final var reserveProductCommand = buildReserveProductCommand(paymentProcessedEvent);
        SagaLifecycle.associateWith("productId", reserveProductCommand.getProductId());
        commandGateway.send(reserveProductCommand, (commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                // Start compensating transaction
                log.error(commandResultMessage.exceptionResult().getMessage());
                final var reversePaymentCommand = ReversePaymentCommand.builder()
                        .paymentId(paymentProcessedEvent.getPaymentId())
                        .orderId(paymentProcessedEvent.getOrderId())
                        .build();
                commandGateway.send(reversePaymentCommand);
            }
        });
    }

    private ReserveProductCommand buildReserveProductCommand(final PaymentProcessedEvent paymentProcessedEvent) {
        final var findOrderQuery = FindOrderQuery.builder()
                .orderId(paymentProcessedEvent.getOrderId())
                .build();
        final var order = queryGateway.query(findOrderQuery, ResponseTypes.instanceOf(Order.class)).join();
        return ReserveProductCommand.builder()
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .orderId(order.getId())
                .build();
    }

    @SagaEventHandler(associationProperty = "productId")
    public void handle(final ProductReservedEvent productReservedEvent) {
        log.info("Product Reserved {} For Order {}", productReservedEvent.getProductId(), productReservedEvent.getOrderId());
        final var approveOrderCommand = buildApproveOrderCommand(productReservedEvent);
        commandGateway.send(approveOrderCommand, (commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                // Start compensating transaction
                log.error(commandResultMessage.exceptionResult().getMessage());
                final var reverseInventoryCommand = ReverseInventoryCommand.builder()
                        .orderId(productReservedEvent.getOrderId())
                        .productId(productReservedEvent.getProductId())
                        .quantity(productReservedEvent.getQuantity())
                        .build();
                commandGateway.send(reverseInventoryCommand);
            }
        });
    }

    private ApproveOrderCommand buildApproveOrderCommand(final ProductReservedEvent productReservedEvent) {
        return ApproveOrderCommand.builder()
                .id(productReservedEvent.getOrderId())
                .build();
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(final OrderApprovedEvent orderApprovedEvent) {
        log.info("Order Approved {}", orderApprovedEvent.getId());
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "id")
    public void handle(final OrderCancelledEvent orderCancelledEvent) {
        log.info("Order Cancelled {}", orderCancelledEvent.getId());
    }

    @SagaEventHandler(associationProperty = "paymentId")
    public void handle(final PaymentCancelledEvent paymentCancelledEvent) {
        log.info("Payment Cancelled For {}", paymentCancelledEvent.getOrderId());
        final var cancelOrderCommand = CancelOrderCommand.builder()
                .id(paymentCancelledEvent.getOrderId())
                .reason("")
                .build();
        commandGateway.send(cancelOrderCommand);
    }

    @SagaEventHandler(associationProperty = "productId")
    public void handle(final InventoryReservationCancelledEvent inventoryReservationCancelledEvent) {
        log.info("Inventory cancelled {}", inventoryReservationCancelledEvent.getOrderId());
        final var findOrderQuery = FindOrderQuery.builder()
                .orderId(inventoryReservationCancelledEvent.getOrderId())
                .build();
        final var order = queryGateway.query(findOrderQuery, ResponseTypes.instanceOf(Order.class)).join();
        final var reversePaymentCommand = ReversePaymentCommand.builder()
                .orderId(inventoryReservationCancelledEvent.getOrderId())
                .paymentId(order.getPaymentId())
                .build();
        commandGateway.send(reversePaymentCommand);
    }


}
