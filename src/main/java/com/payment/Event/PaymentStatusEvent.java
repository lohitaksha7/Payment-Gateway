package com.payment.Event;

import com.payment.dto.PaymentResponse;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentStatusEvent extends ApplicationEvent {
    private final Long merchantId;
    private final PaymentResponse paymentResponse;

    public PaymentStatusEvent(Object source, Long merchantId, PaymentResponse response){
        super(source);
        this.merchantId = merchantId;
        this.paymentResponse = response;
    }
}
