package com.payment.service;


import com.payment.Event.PaymentCreatedEvent;
import com.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String Topic = "payment-events";

    public void PublishPaymentCreated(Payment payment){
        log.info("Preparing to publish PaymentCreatedEvent for payment id={}", payment.getId());

        PaymentCreatedEvent event = PaymentCreatedEvent.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .createdAt(LocalDateTime.now())
                .build();

        try{
            kafkaTemplate.send(Topic, String.valueOf(payment.getId()), event)
                    .whenComplete((result,ex) -> {
                        if(ex == null){
                            log.info("Successfully published PaymentCreatedEvent to Kafka for payment id={}. Metadata: {}",
                                    payment.getId(), result.getRecordMetadata().toString());
                        }else{
                            log.error("Failed to publish PaymentCreatedEvent to Kafka for payment id={} due to: {}",
                                    payment.getId(), ex.getMessage(), ex);
                        }
                    });
        }catch (Exception ex){
            log.error("Sync block error when sending PaymentCreatedEvent for payment id={}", payment.getId(), ex);
        }
    }

}
