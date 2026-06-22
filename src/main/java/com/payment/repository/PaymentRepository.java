package com.payment.repository;

import com.payment.entity.Payment;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.id = :id")
    Optional<Payment> findByIdWithLock(Long id);

    @Query("SELECT AVG(p.amount) FROM Payment p " +
            "WHERE p.merchantId = :merchantId AND p.status = com.payment.entity.PaymentStatus.SUCCESS")
    Double getAverageSuccessfulAmountByMerchant(@Param("merchantId") Long merchantId);
}
