package com.payment.dto;

import com.payment.entity.PaymentStatus;
import lombok.Data;

@Data
public class PaymentUpdateRequest {
    private PaymentStatus status;
}
