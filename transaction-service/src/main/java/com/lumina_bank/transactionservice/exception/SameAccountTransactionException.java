package com.lumina_bank.transactionservice.exception;

import org.springframework.http.HttpStatus;

public class SameAccountTransactionException extends BusinessException
{
    public SameAccountTransactionException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
