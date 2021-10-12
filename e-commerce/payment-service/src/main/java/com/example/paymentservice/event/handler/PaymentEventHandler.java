package com.example.paymentservice.event.handler;

import com.example.coreapi.event.PaymentCancelledEvent;
import com.example.coreapi.event.PaymentProcessedEvent;
import com.example.paymentservice.core.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ProcessingGroup("payment-group")
@RequiredArgsConstructor
public class PaymentEventHandler {
    private final OrderRepository orderRepository;

    @EventHandler
    public void on(final PaymentProcessedEvent paymentProcessedEvent) {
        log.info("Payment Processed {}", paymentProcessedEvent.getPaymentId());
        final var order = orderRepository.findOrderById(paymentProcessedEvent.getOrderId());
        order.setPaymentId(paymentProcessedEvent.getPaymentId());
        orderRepository.save(order);
    }

    @EventHandler
    public void on(final PaymentCancelledEvent paymentCancelledEvent) {
        log.info("Payment Processed {}", paymentCancelledEvent.getPaymentId());
        final var order = orderRepository.findOrderById(paymentCancelledEvent.getOrderId());
        order.setPaymentId(null);
        orderRepository.save(order);
    }
}
