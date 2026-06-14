package com.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRequest {
    @NotNull(message = "Amount cannot be null.")
    @Positive(message = "Amount must be greater than 0.")
    private double amount;

    @NotBlank(message = "Currency is required.")
    private String currency;
}
