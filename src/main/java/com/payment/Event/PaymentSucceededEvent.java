package com.payment.Event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSucceededEvent {
    private Long paymentId;
    private double amount;
    private String currency;
    private Long merchantId;
    private LocalDateTime succeededAt;
}
