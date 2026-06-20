package com.payment.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "webhook_delivery_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookDeliveryLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long merchantId;

    @Column(nullable = false)
    private Long paymentId;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload; // Serialized JSON sent to merchant

    @Column(columnDefinition = "TEXT")
    private String responseBody; // What the merchant server returned

    private Integer responseStatus; // HTTP Status code (e.g. 200, 500)
    private int attemptCount;
    private boolean success;
    private LocalDateTime sentAt;
    private LocalDateTime nextAttemptAt;

    @PrePersist
    protected void onCreate() {
        this.sentAt = LocalDateTime.now();
    }
}
