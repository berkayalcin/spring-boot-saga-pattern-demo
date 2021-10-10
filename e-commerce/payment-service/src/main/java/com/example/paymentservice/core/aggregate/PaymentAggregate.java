package com.example.paymentservice.core.aggregate;

import com.example.coreapi.command.ProcessPaymentCommand;
import com.example.coreapi.event.PaymentProcessedEvent;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Slf4j
@Builder
public class PaymentAggregate {
    @AggregateIdentifier
    private String paymentId;
    private String orderId;
    private String userId;
    private String ibanNumber;

    @CommandHandler
    public PaymentAggregate(final ProcessPaymentCommand processPaymentCommand) {
        final var paymentProcessedEvent = buildPaymentProcessedEvent(processPaymentCommand);
        AggregateLifecycle.apply(paymentProcessedEvent);
        log.info("Payment Processed For {} And Order {}", paymentProcessedEvent.getPaymentId(), paymentProcessedEvent.getOrderId());
    }

    private PaymentProcessedEvent buildPaymentProcessedEvent(final ProcessPaymentCommand processPaymentCommand) {
        return PaymentProcessedEvent.builder()
                .paymentId(processPaymentCommand.getPaymentId())
                .ibanNumber(processPaymentCommand.getIbanNumber())
                .orderId(processPaymentCommand.getOrderId())
                .userId(processPaymentCommand.getUserId())
                .build();
    }

    @EventSourcingHandler
    public void on(final PaymentProcessedEvent paymentProcessedEvent) {
        this.paymentId = paymentProcessedEvent.getPaymentId();
        this.orderId = paymentProcessedEvent.getOrderId();
        this.userId = paymentProcessedEvent.getUserId();
        this.ibanNumber = paymentProcessedEvent.getIbanNumber();
    }
}
