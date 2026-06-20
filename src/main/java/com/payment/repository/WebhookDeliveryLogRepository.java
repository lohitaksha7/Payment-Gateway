package com.payment.repository;

import com.payment.entity.WebhookDeliveryLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WebhookDeliveryLogRepository extends JpaRepository<WebhookDeliveryLog, Long> {

    List<WebhookDeliveryLog> findBySuccessFalseAndAttemptCountLessThanAndNextAttemptAtBefore(int maxAttempts, LocalDateTime time);

}
