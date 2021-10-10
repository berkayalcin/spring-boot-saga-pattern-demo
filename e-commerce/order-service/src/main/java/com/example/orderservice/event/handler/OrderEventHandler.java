package com.example.orderservice.event.handler;

import com.example.orderservice.core.entity.Order;
import com.example.orderservice.core.enums.OrderStatus;
import com.example.orderservice.core.repository.OrderRepository;
import com.example.orderservice.event.model.OrderApprovedEvent;
import com.example.orderservice.event.model.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup(value = "order-group")
public class OrderEventHandler {
    private final OrderRepository orderRepository;

    @EventHandler
    public void on(final OrderCreatedEvent orderCreatedEvent) {
        final var order = buildOrder(orderCreatedEvent);
        orderRepository.save(order);
    }

    private Order buildOrder(final OrderCreatedEvent orderCreatedEvent) {
        return Order.builder()
                .orderStatus(orderCreatedEvent.getOrderStatus())
                .id(orderCreatedEvent.getId())
                .addressId(orderCreatedEvent.getAddressId())
                .productId(orderCreatedEvent.getProductId())
                .quantity(orderCreatedEvent.getQuantity())
                .userId(orderCreatedEvent.getUserId())
                .build();
    }

    @EventHandler
    public void on(final OrderApprovedEvent orderApprovedEvent) {
        final var order = orderRepository.findOrderById(orderApprovedEvent.getId());
        order.setOrderStatus(OrderStatus.APPROVED);
        orderRepository.save(order);
    }
}
