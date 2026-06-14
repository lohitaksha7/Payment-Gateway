package com.payment.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id",nullable = false)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private String message;

    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate(){
        this.createdAt = LocalDateTime.now();
    }
}
