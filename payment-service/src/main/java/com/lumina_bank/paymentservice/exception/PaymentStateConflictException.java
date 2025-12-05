package com.lumina_bank.paymentservice.exception;

import org.springframework.http.HttpStatus;

public class PaymentStateConflictException extends BusinessException{
    public PaymentStateConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
