package com.payment.security.fraud;

import com.payment.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
@Slf4j
@Component
@RequiredArgsConstructor

public class HighFrequencyRule implements FraudRule{

    private final RedisTemplate<String,Object> redisTemplate;
    private static final int MAX_ATTEMPTS_PER_MINUTE = 5;

    @Override
    public int evaluate(PaymentRequest request, Long merchantId, String clientIp){
        long currentMin = Instant.now().getEpochSecond()/60;
        String redisKey = "fraud:velocity"+clientIp+currentMin;

        try{
            Long count = redisTemplate.opsForValue().increment(redisKey);
            if(count == 1){
                redisTemplate.expire(redisKey, 60,TimeUnit.SECONDS);
            }
            if(count>MAX_ATTEMPTS_PER_MINUTE){
                log.warn("🚨 Fraud Alert: Velocity threshold exceeded for IP: {}. Requests: {}/min", clientIp, count);
                return 80;
            }
        }catch (Exception e){
            log.error("Failed to execute velocity fraud rule check. Failing-safe.", e);
        }
        return 0;
    }

    public String getRuleName(){
        return "HIGH_FREQUENCY_VELOCITY_CHECK";
    }
}
