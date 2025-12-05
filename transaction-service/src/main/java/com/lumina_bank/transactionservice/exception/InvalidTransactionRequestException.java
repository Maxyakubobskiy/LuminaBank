package com.lumina_bank.transactionservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidTransactionRequestException extends BusinessException {
    public InvalidTransactionRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
