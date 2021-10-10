package com.example.coreapi.event;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class PaymentProcessedEvent {
    String paymentId;
    String orderId;
    String userId;
    String ibanNumber;
}
