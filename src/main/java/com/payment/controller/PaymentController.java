package com.payment.controller;

import com.payment.entity.Payment;
import com.payment.entity.User;
import com.payment.service.PaymentService;
import com.payment.dto.PaymentRequest;
import com.payment.dto.PaymentResponse;
import com.payment.dto.PaymentUpdateRequest;
import com.payment.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.shaded.com.google.protobuf.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

import java.util.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody PaymentRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey
    ){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        Long merchantId = currentUser.getId();

        if(idempotencyKey == null || idempotencyKey.trim().isEmpty()){
            PaymentResponse response = paymentService.createPayment(request,merchantId);
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(ApiResponse.success("Payment Request accepted for processing", response));
        }

        String redisKey = "Idempotency:"+idempotencyKey;

        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(redisKey,"processing",10,TimeUnit.SECONDS);

        if(Boolean.FALSE.equals(acquired)){
            Object cachedValue = redisTemplate.opsForValue().get(redisKey);
            if("processing".equals(cachedValue)){
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponse.error("A duplicate payment request is currently being processed. Please wait."));
            }
            if(cachedValue!=null){
                try{
                    PaymentResponse cachedResponse = objectMapper.convertValue(cachedValue, PaymentResponse.class);
                    return ResponseEntity.ok(ApiResponse.success("Payment request already completed(cached).",cachedResponse));
                }
                catch (IllegalArgumentException e){
                    redisTemplate.delete(redisKey);
                }
            }

        }

        try{
            PaymentResponse response = paymentService.createPayment(request,merchantId);
            redisTemplate.opsForValue().set(redisKey,response,24,TimeUnit.HOURS);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(ApiResponse.success("Payment Request accepted for processing.", response));
        }catch (Exception e){
            redisTemplate.delete(redisKey);
            throw e;
        }
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<ApiResponse<PaymentResponse>> refundPayment(@PathVariable Long id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        Long merchantId = currentUser.getId();

        PaymentResponse response = paymentService.refundPayment(id,merchantId);
        return ResponseEntity.ok(ApiResponse.success(
            "Payment successfully refunded",response
        ));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PaymentResponse>> updatePaymentStatus(
            @PathVariable Long id,
            @RequestBody PaymentUpdateRequest request
    ){
        PaymentResponse response = paymentService.updatePaymentStatus(id,request);
        return ResponseEntity.ok(ApiResponse.success("Payment Status updated successfully",response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPayments(){
        List<PaymentResponse> response = paymentService.getAllPayments();
        return ResponseEntity.ok(
            ApiResponse.success("payments fetched successfully.", response)
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(@PathVariable Long id){
        PaymentResponse response = paymentService.getPayment(id);
        return ResponseEntity.ok(
            ApiResponse.success("Payment fetched successfully.",response)
        );
    }

}
