package com.example.paymentservice.event.handler;

import com.example.coreapi.event.PaymentProcessedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ProcessingGroup("payment-group")
public class PaymentEventHandler {
    @EventHandler
    public void on(final PaymentProcessedEvent paymentProcessedEvent) {
        log.info("Payment Processed {}", paymentProcessedEvent.getPaymentId());
    }
}
