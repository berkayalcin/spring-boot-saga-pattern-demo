package com.example.coreapi.event;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class PaymentCancelledEvent {
    String paymentId;
    String orderId;
}
