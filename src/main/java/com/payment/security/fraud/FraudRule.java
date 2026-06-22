package com.payment.security.fraud;

import com.payment.dto.PaymentRequest;

public interface FraudRule {
    int evaluate(PaymentRequest request, Long merchantId, String clientIp);
    String getRuleName();
}
