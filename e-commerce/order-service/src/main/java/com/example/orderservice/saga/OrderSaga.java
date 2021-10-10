package com.example.orderservice.saga;

import com.example.coreapi.command.ProcessPaymentCommand;
import com.example.coreapi.event.PaymentProcessedEvent;
import com.example.orderservice.event.model.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
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

    @SagaEventHandler(associationProperty = "paymentId")
    public void handle(final PaymentProcessedEvent paymentProcessedEvent) {
        log.info("Payment Processed {} For Order {}", paymentProcessedEvent.getPaymentId(), paymentProcessedEvent.getOrderId());
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
}
