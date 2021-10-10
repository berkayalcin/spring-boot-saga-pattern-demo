package com.example.orderservice.saga;

import com.example.coreapi.command.ProcessPaymentCommand;
import com.example.coreapi.command.ReserveProductCommand;
import com.example.coreapi.event.PaymentProcessedEvent;
import com.example.coreapi.event.ProductReservedEvent;
import com.example.orderservice.command.model.ApproveOrderCommand;
import com.example.orderservice.core.entity.Order;
import com.example.orderservice.event.model.OrderApprovedEvent;
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
            }
        });
    }

    private ProcessPaymentCommand buildProcessPaymentCommand(final OrderCreatedEvent orderCreatedEvent) {
        final var paymentId = UUID.randomUUID().toString();
        return ProcessPaymentCommand.builder()
                .paymentId(paymentId)
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
}
