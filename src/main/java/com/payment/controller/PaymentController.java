package com.payment.controller;

import com.payment.entity.Payment;
import com.payment.service.PaymentService;
import com.payment.dto.PaymentRequest;
import com.payment.dto.PaymentResponse;
import com.payment.dto.PaymentUpdateRequest;
import com.payment.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(@Valid @RequestBody PaymentRequest request){
        
        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success("Payment accepted for processing.", response));
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
