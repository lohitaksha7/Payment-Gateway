package com.payment.service;

import com.payment.dto.PaymentResponse;
import com.payment.entity.WebhookDeliveryLog;
import com.payment.entity.WebhookSubscription;
import com.payment.repository.WebhookDeliveryLogRepository;
import com.payment.repository.WebhookSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {
    private final WebhookDeliveryLogRepository webhookDeliveryLogRepository;
    private final WebhookSubscriptionRepository webhookSubscriptionRepository;
    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper;

    private static final int MAX_RETRIES = 5;

    public void sendWebHook(Long merchantId, PaymentResponse payload){
        attemptWebhookDispatch(merchantId, payload, 1, null);
    }

    public void retryWebhook(WebhookDeliveryLog logEntry){
        try{
            PaymentResponse payload = objectMapper.readValue(logEntry.getPayload(), PaymentResponse.class);
            attemptWebhookDispatch(logEntry.getMerchantId(),payload, logEntry.getAttemptCount()+1,logEntry);
        }catch (Exception e){
            log.error("Failed to reconstruct payload for webhook retry log id={}", logEntry.getId(), e);
        }
    }

    private void attemptWebhookDispatch(Long merchantId, PaymentResponse payload, int attemptCount, WebhookDeliveryLog existingLog){
        log.info("Triggering webhook dispatch (Attempt {}) for payment id= {} to merchant id={}",attemptCount,payload.getId(),merchantId);

        WebhookSubscription subscription =
                webhookSubscriptionRepository.findByMerchantIdAndActiveTrue(merchantId)
                        .orElse(null);
        if(subscription==null){
            log.info("No active webhook subscription registered for merchant id={}, skipping dispatch", merchantId);
            return;
        }
        String url = subscription.getUrl();
        String secretKey = subscription.getSecretKey();

        try{
            String jsonPayload = objectMapper.writeValueAsString(payload);
            String signature = caluclateHmacSha256(jsonPayload,secretKey);

            WebhookDeliveryLog logEntry = existingLog;
            if(logEntry==null){
                logEntry = WebhookDeliveryLog.builder()
                        .paymentId(payload.getId())
                        .merchantId(merchantId)
                        .url(url)
                        .payload(jsonPayload)
                        .attemptCount(attemptCount)
                        .success(false)
                        .build();
            }else{
                logEntry.setAttemptCount(attemptCount);
            }

            try{
                ResponseEntity<String> response = restClient.post()
                        .uri(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Gateway-Signature",signature)
                        .body(jsonPayload)
                        .retrieve()
                        .toEntity(String.class);

                logEntry.setResponseStatus(response.getStatusCode().value());
                logEntry.setResponseBody(response.getBody());

                if(response.getStatusCode().is2xxSuccessful()){
                    log.info("Webhook delivered on attempt {} to url {}",attemptCount,url);
                    logEntry.setSuccess(true);
                    logEntry.setNextAttemptAt(null);
                }else{
                    log.error("Webhook returned error status code: {}", response.getStatusCode());
                    scheduleNextAttempt(logEntry);
                }

            }catch (Exception e){
                log.error("Failed to dispatch webhook to URL: {} due to: {}", url, e.getMessage());
                logEntry.setResponseBody(e.getMessage());
                logEntry.setResponseStatus(500);
                scheduleNextAttempt(logEntry);
            }

            webhookDeliveryLogRepository.save(logEntry);
        }catch (Exception e){
            log.error("Failed to process webhook payload for payment id={}", payload.getId(), e);
        }

    }

    private void scheduleNextAttempt(WebhookDeliveryLog logEntry){
        if(logEntry.getAttemptCount()<MAX_RETRIES){
            long delayMinutes = (long) Math.pow(2, logEntry.getAttemptCount()-1);
            logEntry.setNextAttemptAt(LocalDateTime.now().plusMinutes(delayMinutes));
            log.info("Scheduled webhook retry attempt {} for payment id={} in {} minutes",
                    logEntry.getAttemptCount() + 1, logEntry.getPaymentId(), delayMinutes);

        }else{
            log.warn("Webhook reached max retries ({}) for payment id={}. Marking as permanently failed.",
                    MAX_RETRIES, logEntry.getPaymentId());
            logEntry.setNextAttemptAt(null);
        }
    }


    private String caluclateHmacSha256(String data, String key) throws Exception{

        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8),"HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : rawHmac) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
