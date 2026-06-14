package com.payment.service;

import com.payment.dto.PaymentRequest;
import com.payment.dto.PaymentResponse;
import com.payment.dto.PaymentUpdateRequest;
import com.payment.entity.Payment;
import com.payment.entity.PaymentStatus;
import com.payment.exception.PaymentNotFoundException;
import com.payment.repository.PaymentRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentResponse createPayment(PaymentRequest request){
        Payment payment = Payment.builder()
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(PaymentStatus.CREATED)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
                .build();

        Payment saved = paymentRepository.save(payment);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
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

    @Transactional
    public PaymentResponse updatePaymentStatus(Long id, PaymentUpdateRequest request){
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(
                        () ->
                                new PaymentNotFoundException("Payment not found with id: "+id)
                );
        payment.setStatus(request.getStatus());
        payment.setUpdatedAt(LocalDateTime.now());

        Payment updated = paymentRepository.save(payment);
        return mapToResponse(updated);
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
