package com.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class SmsService {
    public void sendSms(String recipientPhone, String message){
        log.info("Preparing to dispatch sms to {}",recipientPhone);
        log.info("\n" +
                        "┌──────────────────────────────────────────────────────────┐\n" +
                        "│                     ✉️ OUTGOING SMS                       │\n" +
                        "├──────────────────────────────────────────────────────────┤\n" +
                        "│ To:      %-47s │\n" +
                        "│ From:    Mock Gateway (Shortcode: 9999)                  │\n" +
                        "├──────────────────────────────────────────────────────────┤\n" +
                        "│ Message:                                                 │\n" +
                        "│ %-56s │\n" +
                        "└──────────────────────────────────────────────────────────┘",
                recipientPhone, message);
    }
}
