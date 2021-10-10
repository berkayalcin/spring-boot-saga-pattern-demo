package com.example.coreapi.event;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class ProductReservedEvent {
    String productId;
    String orderId;
    int quantity;
}
