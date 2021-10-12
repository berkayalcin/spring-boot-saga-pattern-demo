package com.example.coreapi.command;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReversePaymentCommand {
    @TargetAggregateIdentifier
    private String paymentId;
    private String orderId;
}

