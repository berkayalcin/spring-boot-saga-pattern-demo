package com.example.orderservice.query.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindOrderQuery {
    private String orderId;
}
