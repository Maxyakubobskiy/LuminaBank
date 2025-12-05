package com.lumina_bank.accountservice.exception;

public class InvalidAmountException extends RuntimeException
{
    public InvalidAmountException(String message) {
        super(message);
    }
}
