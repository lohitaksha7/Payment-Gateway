package com.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
// import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(PaymentNotFoundException ex){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "error", ex.getMessage(),
                    "timestamp", LocalDateTime.now().toString()
                ));
    }
}


//@RestControllerAdvice
//public class GlobalExceptionHandler {

//    @ExceptionHandler(
//            ResourceNotFoundException.class)
//    public ResponseEntity<?> handleNotFound(
//            ResourceNotFoundException ex) {
//
//        return ResponseEntity.status(
//                        HttpStatus.NOT_FOUND)
//                .body(Map.of(
//                        "error",
//                        ex.getMessage()));
//    }
//}
