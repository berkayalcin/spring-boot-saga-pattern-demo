package com.example.productservice.event.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ProductCreatedEvent {
    String id;
    String name;
    BigDecimal price;
    int quantity;
}
