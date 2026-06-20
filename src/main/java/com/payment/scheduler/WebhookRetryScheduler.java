package com.payment.scheduler;

import com.payment.entity.WebhookDeliveryLog;
import com.payment.repository.WebhookDeliveryLogRepository;
import com.payment.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@EnableScheduling // Enable task scheduling
@RequiredArgsConstructor
public class WebhookRetryScheduler {

    private final WebhookDeliveryLogRepository webhookDeliveryLogRepository;

    private final WebhookService webhookService;

    @Scheduled(fixedDelay = 60000)
    public void processFailedWebhooks(){
        LocalDateTime now = LocalDateTime.now();

        List<WebhookDeliveryLog> pendingRetries =
                webhookDeliveryLogRepository.findBySuccessFalseAndAttemptCountLessThanAndNextAttemptAtBefore(5,now);
        if(!pendingRetries.isEmpty()){
            log.info("Found {} failed webhooks to retry.",pendingRetries.size());
            for(WebhookDeliveryLog logEntry: pendingRetries){
                try{
                    webhookService.retryWebhook(logEntry);
                }catch (Exception e){
                    log.error("Error executing retry task for log id = {}",logEntry.getId(),e);
                }
            }
        }
    }
}
