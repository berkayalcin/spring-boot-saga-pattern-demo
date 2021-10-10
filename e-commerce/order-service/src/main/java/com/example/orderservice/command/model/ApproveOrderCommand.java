package com.example.orderservice.command.model;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApproveOrderCommand {
    @TargetAggregateIdentifier
    private String id;
}
