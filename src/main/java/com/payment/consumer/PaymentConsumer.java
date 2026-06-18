package com.payment.consumer;

import com.payment.Event.PaymentCreatedEvent;
import com.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {
    private final PaymentService paymentService;

    @KafkaListener(
            topics = "payment-events",
            groupId = "payment-processing-group"
    )
    public void consumePaymentCreated(PaymentCreatedEvent event){
        log.info("Received PaymentCreatedEvent from Kafka for payment id={}", event.getId());
        try {
                paymentService.processPayment(event.getId());
                log.info("Successfully finished async processing for payment id={}", event.getId());
        }catch (Exception e){
            log.error("Failed to process payment id={} asynchronously in consumer: {}",
                    event.getId(), e.getMessage(), e);
        }
    }
}
