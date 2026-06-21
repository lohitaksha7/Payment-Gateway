package com.payment.consumer;


import com.payment.Event.PaymentSucceededEvent;
import com.payment.Event.RefundCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@KafkaListener(
        topics = "payment-events",
        groupId = "settlement-service-group"
)
public class SettlementConsumer {

    @KafkaHandler
    public void handleSucceeded(PaymentSucceededEvent event){
        double fee = event.getAmount()*0.02;
        double payout = event.getAmount()-fee;
        log.info("💰 [SETTLEMENT SERVICE] Calculated Settlement for payment id={}: Gross={}, Fee (2%)={}, Net Payout={} to merchant id={}",
                event.getPaymentId(), event.getAmount(), fee, payout, event.getMerchantId());
    }

    @KafkaHandler
    public void handleRefund(RefundCreatedEvent event){
        double feeRefund = event.getRefundAmount()*0.02;
        double payoutDeduction = event.getRefundAmount()-feeRefund;
        log.info("💰 [SETTLEMENT SERVICE] Processed REFUND for payment id={}: Gross Deduction=-{}, Fee Returned=-{}, Net Payout Adjusted=-{} to merchant id={}",
                event.getPaymentId(), event.getRefundAmount(), feeRefund, payoutDeduction, event.getMerchantId());
    }

    @KafkaHandler(isDefault = true)
    public void handleObject(Object event){

    }
}
