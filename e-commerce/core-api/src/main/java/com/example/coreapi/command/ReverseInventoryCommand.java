package com.example.coreapi.command;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReverseInventoryCommand {
    @TargetAggregateIdentifier
    private String productId;
    private String orderId;
    private int quantity;
    private String paymentId;
}
