package com.payment.service;


import com.payment.Event.PaymentCreatedEvent;
import com.payment.Event.PaymentFailedEvent;
import com.payment.Event.PaymentSucceededEvent;
import com.payment.Event.RefundCreatedEvent;
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
    public void PublishPaymentSucceeded(Payment payment){
        log.info("Preparing to publish PaymentSucceededEvent for payment id={}",payment.getId());

        PaymentSucceededEvent event = PaymentSucceededEvent.builder()
                .paymentId(payment.getId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .merchantId(payment.getMerchantId())
                .succeededAt(LocalDateTime.now())
                .build();

        try{
            kafkaTemplate.send(Topic,String.valueOf(payment.getId()),event)
                    .whenComplete((result, ex)->{
                        if(ex==null){
                            log.info("Successfully published PaymentSucceededEvent to Kafka for payment id={}. Metadata: {}",
                                    payment.getId(), result.getRecordMetadata().toString());
                        }else{
                            log.error("Failed to publish PaymentSucceededEvent to Kafka for payment id={} due to: {}",
                                    payment.getId(), ex.getMessage(), ex);
                        }
                    });
        }catch (Exception ex){
            log.error("Sync block error when sending PaymentSucceededEvent for payment id={}", payment.getId(), ex);
        }
    }

    public void PublishPaymentFailed(Payment payment, String errorMessage){
        log.info("Preparing to publish PaymentFailedEvent of payment id={}",payment.getId());
        PaymentFailedEvent event = PaymentFailedEvent.builder()
                .paymentId(payment.getId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .errorMessage(errorMessage)
                .failedAt(LocalDateTime.now())
                .merchantId(payment.getMerchantId())
                .build();

        try{
            kafkaTemplate.send(Topic, String.valueOf(payment.getId()),event)
                    .whenComplete((result,ex)->{
                        if(ex==null){
                            log.info("Successfully published pPaymentFailedEvent to kafaka for payment id={}" +
                                    "metadata: {}", payment.getId(), result.getRecordMetadata().toString());
                        }else{
                            log.error("Failed to publish PaymentFailedEvent to kafka of payment id={} due to {}",
                                    payment.getId(),ex.getMessage(),ex);
                        }
                    });
        }catch (Exception e){
            log.error("Sync block error when sending PaymentFailedEvent for payment id={}", payment.getId(),e);
        }
    }

    public void PublishRefundCreated(Payment payment){
        log.info("Preparing to publish RefundCreated event of payment id={}",payment.getId());

        RefundCreatedEvent event = RefundCreatedEvent.builder()
                .paymentId(payment.getId())
                .refundAmount(payment.getAmount())
                .currency(payment.getCurrency())
                .merchantId(payment.getMerchantId())
                .refundedAt(LocalDateTime.now())
                .build();

        try{
            kafkaTemplate.send(Topic,String.valueOf(payment.getId()),event)
                    .whenComplete((result, ex)->{
                         if(ex==null){
                             log.info("Successfully published RefundCreated event to kafka for payment id={}" +
                                     "and metadata:{}", payment.getId(), result.getRecordMetadata().toString());
                         }else {
                             log.error("Failed to publish RefundCreatedEvent to Kafka for payment id={} due to: {}",
                                     payment.getId(), ex.getMessage(), ex);
                         }
                    });
        }catch (Exception ex){
            log.error("Sync block error when sending RefundCreatedEvent for payment id={}", payment.getId(), ex);
        }
    }

}
