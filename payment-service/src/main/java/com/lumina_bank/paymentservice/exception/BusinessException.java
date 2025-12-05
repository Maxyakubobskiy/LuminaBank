package com.lumina_bank.paymentservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus status;

    protected BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    protected BusinessException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }
}
