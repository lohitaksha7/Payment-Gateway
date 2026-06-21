package com.payment.consumer;

import com.payment.Event.PaymentFailedEvent;
import com.payment.Event.PaymentSucceededEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaListener(
        topics = "payment-events",
        groupId = "notification-service-group"
)
public class NotificationConsumer {
    @KafkaHandler
    public void handleSucceededEvent(PaymentSucceededEvent event){
        log.info("[NOTIFICATION SERVICE] Payment Succeeded! Sending email receipt for payment id={} to merchant id={}",
                event.getPaymentId(), event.getMerchantId());
    }

    @KafkaHandler
    public void handleFailedEvent(PaymentFailedEvent event){
        log.warn("[NOTIFICATION SERVICE] Payment Failed! Sending alert email for payment id={}. Reason: '{}'",
                event.getPaymentId(), event.getErrorMessage());
    }

    @KafkaHandler(isDefault = true)
    public void handleUnkown(Object event){

    }
}
