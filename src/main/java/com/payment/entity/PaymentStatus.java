package com.payment.entity;

import java.util.Set;

public enum PaymentStatus {

    REFUNDED(Set.of()),
    FAILED(Set.of()),
    SUCCESS(Set.of(PaymentStatus.REFUNDED)),
    PROCESSING(Set.of(PaymentStatus.SUCCESS, PaymentStatus.FAILED)),
    CREATED(Set.of(PaymentStatus.PROCESSING));

    private final Set<PaymentStatus> allowedTransactions;

    PaymentStatus(Set<PaymentStatus> allowedTransactions){
        this.allowedTransactions = allowedTransactions;
    }

    public boolean canTransactionTo(PaymentStatus next){
        return allowedTransactions.contains(next);
    }

    public Set<PaymentStatus> getAllowedTransactions(){
        return allowedTransactions;
    }
}


