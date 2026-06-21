package com.payment.consumer;

import com.payment.Event.PaymentFailedEvent;
import com.payment.Event.PaymentSucceededEvent;
import com.payment.service.EmailTemplateBuilder;
import com.payment.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(
        topics = "payment-events",
        groupId = "notification-service-group"
)
public class NotificationConsumer {

    private final JavaMailSender mailSender;
    private final SmsService smsService;

    @KafkaHandler
    public void handleSucceededEvent(PaymentSucceededEvent event){
        log.info("[NOTIFICATION SERVICE] Payment Succeeded! Sending email receipt for payment id={} to merchant id={}",
                event.getPaymentId(), event.getMerchantId());

        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,"utf-8");

            String htmlContent = EmailTemplateBuilder.buildPaymentSuccessTemplate(event.getPaymentId(),event.getAmount(),
                    event.getCurrency(), event.getMerchantId(),event.getSucceededAt());

            helper.setText(htmlContent,true);
            helper.setFrom("no-reply@payment-gateway@gmail.com");
            helper.setTo("merchant-"+event.getMerchantId()+"@payment-gateway.com");
            helper.setSubject("Payment Success receipt id-{}"+event.getPaymentId());

            String smsBody = "Alert: Payment of " + event.getAmount() + " " + event.getCurrency() +
                    " was successful. ID: " + event.getPaymentId();
            smsService.sendSms("+1234567890", smsBody);
            mailSender.send(mimeMessage);
            log.info("Html email receipt sent successfully for payment id:{}",event.getPaymentId());
        }catch (Exception e){
            log.error("Failed to send HTML success email receipt for payment id={}: {}",
                    event.getPaymentId(), e.getMessage(), e);
        }

    }

    @KafkaHandler
    public void handleFailedEvent(PaymentFailedEvent event){
        log.warn("[NOTIFICATION SERVICE] Payment Failed! Sending alert email for payment id={}. Reason: '{}'",
                event.getPaymentId(), event.getErrorMessage());

        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlContent = EmailTemplateBuilder.buildPaymentFailureTemplate(event.getPaymentId(),event.getAmount(),
                    event.getCurrency(),event.getMerchantId(),event.getErrorMessage(),event.getFailedAt());

            helper.setText(htmlContent,true);
            helper.setFrom("no-reply@payment-gateway.com");
            helper.setTo("merchant-"+event.getMerchantId()+"@payment-gateway.com");

            helper.setSubject("Payment failed alert for payment id: "+event.getPaymentId());
            String smsBody = "Alert: Payment of " + event.getAmount() + " " + event.getCurrency() +
                    " has failed. Reason: " + event.getErrorMessage();
            smsService.sendSms("+1234567890", smsBody);
            mailSender.send(mimeMessage);
            log.info("Html email failure alert sent successfully for payment id = {}",event.getPaymentId());
        }catch (Exception e){
            log.error("Failed to send HTML failure email alert for payment id={}: {}",
                    event.getPaymentId(), e.getMessage(), e);
        }
    }


    @KafkaHandler(isDefault = true)
    public void handleUnknown(Object event){

    }
}
