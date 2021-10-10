package com.example.orderservice.event.model;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderApprovedEvent {
    private String id;
}
