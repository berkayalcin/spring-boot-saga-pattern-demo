package com.example.orderservice.event.model;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderCancelledEvent {
    private String id;
    private String reason;
}
