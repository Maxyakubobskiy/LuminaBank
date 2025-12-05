package com.lumina_bank.paymentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class PaymentCancellationException extends BusinessException {
    public PaymentCancellationException(String message) {
        super(message,HttpStatus.BAD_REQUEST);
    }
}

