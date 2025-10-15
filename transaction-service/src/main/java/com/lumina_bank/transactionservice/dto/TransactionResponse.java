package com.lumina_bank.transactionservice.dto;

import com.lumina_bank.transactionservice.model.Transaction;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransactionResponse(
        Long id,
        Long fromAccountId,
        Long toAccountId,
        BigDecimal amount,
        String status) {

    public static TransactionResponse fromEntity(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .fromAccountId(t.getFromAccountId())
                .toAccountId(t.getToAccountId())
                .amount(t.getAmount())
                .status(t.getStatus().name())
                .build();
    }
}
