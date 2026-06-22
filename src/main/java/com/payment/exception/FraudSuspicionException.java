package com.payment.exception;


public class FraudSuspicionException extends RuntimeException{
    public FraudSuspicionException(String message){
        super(message);
    }
}
