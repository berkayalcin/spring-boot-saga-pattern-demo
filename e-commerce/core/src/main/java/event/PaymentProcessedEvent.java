package event;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentProcessedEvent {
    private String paymentId;
    private String orderId;
    private String userId;
    private String ibanNumber;
}
