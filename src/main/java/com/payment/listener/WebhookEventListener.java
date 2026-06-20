package com.payment.listener;

import com.payment.Event.PaymentStatusEvent;
import com.payment.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookEventListener {
    private final WebhookService webhookService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentStatusEvent(PaymentStatusEvent event){
        log.info("Received payment status for payment id = {} after DB commit. Dispatching Webhook...",
                event.getPaymentResponse().getId());
        webhookService.sendWebHook(event.getMerchantId(), event.getPaymentResponse());
    }
}
