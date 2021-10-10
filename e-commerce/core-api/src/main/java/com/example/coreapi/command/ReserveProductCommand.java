package com.example.coreapi.command;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReserveProductCommand {
    @TargetAggregateIdentifier
    private String productId;
    private String orderId;
    private int quantity;
}
