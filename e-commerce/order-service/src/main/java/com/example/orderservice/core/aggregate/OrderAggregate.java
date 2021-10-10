package com.example.orderservice.core.aggregate;

import com.example.orderservice.command.model.CreateOrderCommand;
import com.example.orderservice.core.enums.OrderStatus;
import com.example.orderservice.event.model.OrderCreatedEvent;
import lombok.*;
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
@Builder
public class OrderAggregate {
    @AggregateIdentifier
    private String id;
    private String productId;
    private String userId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus;

    @CommandHandler
    public OrderAggregate(final CreateOrderCommand createOrderCommand) {
        if (createOrderCommand.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity cannot be less or equal than zero");
        }

        if (createOrderCommand.getAddressId() == null || createOrderCommand.getAddressId().isBlank()) {
            throw new IllegalArgumentException("Address cannot be empty");
        }

        if (createOrderCommand.getProductId() == null || createOrderCommand.getProductId().isBlank()) {
            throw new IllegalArgumentException("Product cannot be empty");
        }

        final var orderCreatedEvent = buildOrderCreatedEvent(createOrderCommand);
        AggregateLifecycle.apply(orderCreatedEvent);
    }

    private OrderCreatedEvent buildOrderCreatedEvent(final CreateOrderCommand createOrderCommand) {
        return OrderCreatedEvent.builder()
                .addressId(createOrderCommand.getAddressId())
                .id(createOrderCommand.getId())
                .orderStatus(createOrderCommand.getOrderStatus())
                .productId(createOrderCommand.getProductId())
                .quantity(createOrderCommand.getQuantity())
                .userId(createOrderCommand.getUserId())
                .build();
    }

    @EventSourcingHandler
    public void handle(final OrderCreatedEvent orderCreatedEvent) {
        this.id = orderCreatedEvent.getId();
        this.productId = orderCreatedEvent.getProductId();
        this.quantity = orderCreatedEvent.getQuantity();
        this.addressId = orderCreatedEvent.getAddressId();
        this.orderStatus = orderCreatedEvent.getOrderStatus();
        this.userId = orderCreatedEvent.getUserId();
    }
}