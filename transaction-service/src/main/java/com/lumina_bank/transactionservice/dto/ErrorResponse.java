package com.lumina_bank.transactionservice.dto;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Builder
public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        Instant timestamp
) {
    public static ErrorResponse buildErrorResponse(HttpStatus status, String message, String path){
        return ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .timestamp(Instant.now())
                .build();
    }
}
