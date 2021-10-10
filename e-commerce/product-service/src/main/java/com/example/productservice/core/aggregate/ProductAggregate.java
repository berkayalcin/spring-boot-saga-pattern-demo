package com.example.productservice.core.aggregate;

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

    @EventSourcingHandler
    public void on(ProductCreatedEvent productCreatedEvent) {
        this.id = productCreatedEvent.getId();
        this.price = productCreatedEvent.getPrice();
        this.name = productCreatedEvent.getName();
        this.quantity = productCreatedEvent.getQuantity();
    }

    private ProductCreatedEvent buildProductCreatedEvent(final CreateProductCommand createProductCommand) {
        return ProductCreatedEvent.builder()
                .id(createProductCommand.getId())
                .price(createProductCommand.getPrice())
                .name(createProductCommand.getName())
                .quantity(createProductCommand.getQuantity())
                .build();
    }


}
