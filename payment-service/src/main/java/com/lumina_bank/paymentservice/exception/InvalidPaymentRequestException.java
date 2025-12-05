package com.lumina_bank.paymentservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidPaymentRequestException extends BusinessException{
    public InvalidPaymentRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
