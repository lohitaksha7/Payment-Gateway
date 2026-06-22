package com.payment.security.fraud;

import com.payment.dto.PaymentRequest;
import com.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@RequiredArgsConstructor
public class UnusualAmountRule implements FraudRule{

    private final PaymentRepository paymentRepository;

    @Override
    public int evaluate(PaymentRequest request, Long merchantId, String clientIp){

        double current_amount = request.getAmount();
        Double averageAmount = paymentRepository.getAverageSuccessfulAmountByMerchant(merchantId);
        if(averageAmount==null) averageAmount = 0.0;
        log.debug("Merchant id={} average transaction amount: {}, current request: {}",
                merchantId, averageAmount, current_amount);

        if(averageAmount>0 && current_amount > averageAmount*10){
            log.warn("🚨 Fraud Alert: Transaction value (%.2f) is over 10x the merchant's historical average (%.2f)",
                    current_amount, averageAmount);
            return 60;
        }
        return 0;
    }

    public String getRuleName(){
        return "UNUSUAL_AMOUNT_ALERT";
    }
}
