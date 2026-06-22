package com.payment.service;

import com.payment.dto.PaymentRequest;
import com.payment.security.fraud.FraudRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Math.max;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudDetectionService {
    private final List<FraudRule> fraudRules;

    public int detectFraud(PaymentRequest request, Long merchantId, String clientIp){
        log.info("Running fraud checks for merchant id={} from IP={}", merchantId, clientIp);

        int total_score = 0;
        for(FraudRule rule: fraudRules){
            int score = rule.evaluate(request, merchantId, clientIp);
            log.debug("Rule {} returned risk score: {}", rule.getRuleName(), score);
            total_score = max(total_score,score);
        }
        log.info("Composite Fraud Score: {}", total_score);
        return total_score;
    }
}
