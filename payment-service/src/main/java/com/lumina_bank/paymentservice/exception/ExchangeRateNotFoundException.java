package com.lumina_bank.paymentservice.exception;

import org.springframework.http.HttpStatus;

public class ExchangeRateNotFoundException extends BusinessException {
    public ExchangeRateNotFoundException(String message) {
        super(message, HttpStatus.BAD_GATEWAY);
    }
    public ExchangeRateNotFoundException(String message, Throwable cause) {
        super(message, cause,HttpStatus.BAD_GATEWAY);
    }
}
