package com.lumina_bank.transactionservice.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountOperationDto(BigDecimal amount) {
}
