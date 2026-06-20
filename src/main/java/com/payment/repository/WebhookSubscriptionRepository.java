package com.payment.repository;

import com.payment.entity.WebhookSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WebhookSubscriptionRepository extends JpaRepository<WebhookSubscription, Long> {
    Optional<WebhookSubscription> findByMerchantIdAndActiveTrue(Long merchantId);
}
