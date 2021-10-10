package com.example.orderservice.command.model;

import com.example.orderservice.core.enums.OrderStatus;
import lombok.Builder;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
@Builder
public class CreateOrderCommand {
    @TargetAggregateIdentifier
    String id;
    String userId;
    String productId;
    int quantity;
    String addressId;
    OrderStatus orderStatus;
}
