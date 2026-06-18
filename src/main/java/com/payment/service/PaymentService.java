package com.payment.service;

import com.payment.dto.PaymentRequest;
import com.payment.dto.PaymentResponse;
import com.payment.dto.PaymentUpdateRequest;
import com.payment.entity.Payment;
import com.payment.entity.PaymentStatus;
import com.payment.exception.InvalidStatusTransitionException;
import com.payment.exception.PaymentNotFoundException;
import com.payment.repository.PaymentRepository;
import com.payment.repository.TransactionRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Recover;

import org.springframework.stereotype.Service;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final  TransactionRecordService transactionRecordService;

    @Transactional(rollbackFor = Exception.class)
    @Retryable(
            retryFor = { SQLException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public PaymentResponse createPayment(PaymentRequest request){
        log.info("Creating payment: amount={}, currency={}",
                request.getAmount(), request.getCurrency());

        Payment payment = Payment.builder()
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(PaymentStatus.CREATED)
                .build();

        Payment saved = paymentRepository.save(payment);
        transactionRecordService.Record(
                saved,
                PaymentStatus.CREATED,
                "Payment created with amount "+ request.getAmount() +" in "+
                        request.getCurrency()
        );
        log.info("Payment created with id: {}", saved.getId());
        return mapToResponse(saved);
    }

    @Transactional(
            readOnly = true,
            noRollbackFor = PaymentNotFoundException.class
    )
    public PaymentResponse getPayment(Long id){
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(
                        ()->
                                new PaymentNotFoundException("Payment not found.")
                );
        return mapToResponse(payment);
    }
    
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments(){
        return paymentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(
            rollbackFor = Exception.class,
            isolation = Isolation.REPEATABLE_READ
    )
    public PaymentResponse updatePaymentStatus(Long id, PaymentUpdateRequest request){
        log.info("Updating payment id={} to status={}", id, request.getStatus());
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(
                        () ->
                                new PaymentNotFoundException("Payment not found with id: "+id)
                );
        PaymentStatus currentStatus = payment.getStatus();
        PaymentStatus newStatus = request.getStatus();

        if(!currentStatus.canTransactionTo(newStatus)){
            throw new InvalidStatusTransitionException(
                "Cannot to transaction from " + currentStatus +
                        " to " + newStatus + ". Allowed transaction is "+
                        currentStatus.getAllowedTransactions()
            );
        }


        payment.setStatus(request.getStatus());
        payment.setUpdatedAt(LocalDateTime.now());

        Payment updated = paymentRepository.save(payment);
        transactionRecordService.Record(
                updated,
                newStatus,
                "Status transitioned from "
                        +currentStatus+" to "+ newStatus
        );
        log.info("Payment id={} successfully transitioned: {} → {}", id, currentStatus, newStatus);
        return mapToResponse(updated);
    }

    @Recover
    public PaymentResponse recoverCreatePayment(DataAccessException ex, PaymentRequest request){
        log.error("Critical: Payment creation failed after all retries. Reason = {}", ex.getMessage(), ex);
        throw new ResponseStatusException(
            HttpStatus.SERVICE_UNAVAILABLE,
                "Payment service is temporarily busy.Please try again later.",
                ex
        );
    }

    @Recover
    public PaymentResponse recoverCreatePayment(SQLException ex, PaymentRequest request){
        log.error("Critical: Payment creation failed after all retries, Database error: {}", ex.getMessage(), ex);
        throw new ResponseStatusException(
            HttpStatus.SERVICE_UNAVAILABLE,
                "Payment Service is temporarily busy. Please try again later.",
                ex
        );
    }

    @Transactional(
            rollbackFor = Exception.class,
            isolation = Isolation.REPEATABLE_READ
    )
    public PaymentResponse processPayment(Long id){
        log.info("Starting Processing for Payment id = {}", id);
        Payment payment = paymentRepository.findByIdWithLock(id)
                .orElseThrow(()->
                    new PaymentNotFoundException(
                            "Payment not found with id: "+ id
                    )
                );
                updatePaymentStatus(id,new PaymentUpdateRequest(PaymentStatus.PROCESSING));
                boolean isGatewaySuccess = simulateExternalGatewayCall();
                PaymentStatus finalStatus = isGatewaySuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
                log.info("External gateway returned success={}. Transitioning payment id={} to {}",
                                    isGatewaySuccess, id, finalStatus);

                return updatePaymentStatus(id, new PaymentUpdateRequest(finalStatus));
    }

    private boolean simulateExternalGatewayCall() {
        try {
            Thread.sleep(500);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
        return Math.random()>0.1;
    }

    private PaymentResponse mapToResponse(Payment payment){
        return PaymentResponse.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus().name())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
