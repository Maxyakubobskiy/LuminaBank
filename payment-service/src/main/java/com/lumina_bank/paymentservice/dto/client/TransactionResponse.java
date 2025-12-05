package com.lumina_bank.paymentservice.dto.client;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransactionResponse(
        Long id,
        Long fromAccountId,
        Long toAccountId,
        BigDecimal amount,
        String status,
        String direction) {
}
