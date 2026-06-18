package com.payment.service;


import com.payment.entity.Payment;
import com.payment.entity.PaymentStatus;
import com.payment.entity.Transaction;
import com.payment.repository.TransactionRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionRecordService {
    private final TransactionRepository transactionRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public void Record(Payment payment, PaymentStatus status, String message){
        log.info("Recording new Transaction: paymentId = {}, status = {}",payment.getId(), status);

        Transaction transaction = Transaction.builder()
                .payment(payment)
                .status(status)
                .message(message)
                .build();

        transactionRepository.save(transaction);
    }
}
