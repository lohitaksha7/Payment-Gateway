package com.payment.dto;

import com.payment.entity.PaymentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentUpdateRequest {
    private PaymentStatus status;
}
