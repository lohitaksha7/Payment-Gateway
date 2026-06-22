package com.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private double amount;
    private String currency;
    private String status;
    private int fraudScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
