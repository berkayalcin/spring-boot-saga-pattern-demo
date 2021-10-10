package com.example.productservice.command.model;

import lombok.Builder;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

@Value
@Builder
public class CreateProductCommand {
    @TargetAggregateIdentifier
    String id;
    String name;
    BigDecimal price;
    int quantity;
}
