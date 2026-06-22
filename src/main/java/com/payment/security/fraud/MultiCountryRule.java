package com.payment.security.fraud;

import com.payment.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.concurrent.TimeUnit;
@Slf4j
@Component
@RequiredArgsConstructor
public class MultiCountryRule implements FraudRule{

    private final RedisTemplate<String, Object> redisTemplate;

    public int evaluate(PaymentRequest request, Long merchantId, String clientIp){

        String country = request.getCurrency().equals("INR")?"IN":"US";
        String redisKey = "fraud:countries" + merchantId;

        try{
            redisTemplate.opsForSet().add(redisKey, country);
            redisTemplate.expire(redisKey,15,TimeUnit.MINUTES);

            Set<Object> countriesSeen = redisTemplate.opsForSet().members(redisKey);

            if(countriesSeen!=null && countriesSeen.size()>=3){
                log.warn("🚨 Fraud Alert: Transactions originating from {} distinct countries in under 15 mins",
                        countriesSeen.size());
                return 75;
            }
        }catch (Exception e){
            log.error("Failed to execute multi-country fraud rule. Failing-safe.", e);
        }
        return 0;
    }

    public String getRuleName(){
        return "Multi_Country_Payment_Alert";
    }
}
