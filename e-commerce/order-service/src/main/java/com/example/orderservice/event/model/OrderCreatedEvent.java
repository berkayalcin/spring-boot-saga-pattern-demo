package com.example.orderservice.event.model;

import com.example.orderservice.core.enums.OrderStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderCreatedEvent {
    String id;
    String userId;
    String productId;
    int quantity;
    String addressId;
    OrderStatus orderStatus;
}
