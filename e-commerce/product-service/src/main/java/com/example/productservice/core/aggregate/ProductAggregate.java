package com.example.productservice.core.aggregate;

import com.example.coreapi.command.ReserveProductCommand;
import com.example.coreapi.command.ReverseInventoryCommand;
import com.example.coreapi.event.InventoryReservationCancelledEvent;
import com.example.coreapi.event.ProductReservedEvent;
import com.example.productservice.command.model.CreateProductCommand;
import com.example.productservice.event.model.ProductCreatedEvent;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;

@Aggregate
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Slf4j
public class ProductAggregate {
    @AggregateIdentifier
    private String id;
    private String name;
    private BigDecimal price;
    private int quantity;

    @CommandHandler
    public ProductAggregate(final CreateProductCommand createProductCommand) {
        if (createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price cannot be less or equal than zero");
        }
        if (createProductCommand.getName() == null || createProductCommand.getName().isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        final var productCreatedEvent = buildProductCreatedEvent(createProductCommand);
        AggregateLifecycle.apply(productCreatedEvent);
    }

    private ProductCreatedEvent buildProductCreatedEvent(final CreateProductCommand createProductCommand) {
        return ProductCreatedEvent.builder()
                .id(createProductCommand.getId())
                .price(createProductCommand.getPrice())
                .name(createProductCommand.getName())
                .quantity(createProductCommand.getQuantity())
                .build();
    }

    @CommandHandler
    public void handle(final ReserveProductCommand reserveProductCommand) {
        if (reserveProductCommand.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity cannot be less or equal than zero");
        }

        if (reserveProductCommand.getProductId() == null || reserveProductCommand.getProductId().isBlank()) {
            throw new IllegalArgumentException("ProductId cannot be null or empty");
        }

        if (reserveProductCommand.getOrderId() == null || reserveProductCommand.getOrderId().isBlank()) {
            throw new IllegalArgumentException("OrderId cannot be null or empty");
        }

        final var productReservedEvent = buildProductReservedEvent(reserveProductCommand);
        AggregateLifecycle.apply(productReservedEvent);
        log.info("Product {} Reserved {} unit For Order {} ", reserveProductCommand.getProductId(),
                reserveProductCommand.getQuantity(), reserveProductCommand.getOrderId());
    }

    private ProductReservedEvent buildProductReservedEvent(final ReserveProductCommand reserveProductCommand) {
        return ProductReservedEvent.builder()
                .productId(reserveProductCommand.getProductId())
                .orderId(reserveProductCommand.getOrderId())
                .quantity(reserveProductCommand.getQuantity())
                .build();
    }

    @CommandHandler
    public void handle(final ReverseInventoryCommand reverseInventoryCommand) {
        final var inventoryReservationCancelledEvent = InventoryReservationCancelledEvent.builder()
                .orderId(reverseInventoryCommand.getOrderId())
                .productId(reverseInventoryCommand.getProductId())
                .quantity(reverseInventoryCommand.getQuantity())
                .build();
        AggregateLifecycle.apply(inventoryReservationCancelledEvent);
    }

    @EventSourcingHandler
    public void on(final ProductCreatedEvent productCreatedEvent) {
        this.id = productCreatedEvent.getId();
        this.price = productCreatedEvent.getPrice();
        this.name = productCreatedEvent.getName();
        this.quantity = productCreatedEvent.getQuantity();
    }

    @EventSourcingHandler
    public void on(final ProductReservedEvent productReservedEvent) {
        this.quantity = this.quantity - productReservedEvent.getQuantity();
    }

    @EventSourcingHandler
    public void on(final InventoryReservationCancelledEvent inventoryReservationCancelledEvent) {
        this.quantity = this.quantity + inventoryReservationCancelledEvent.getQuantity();
    }
}
