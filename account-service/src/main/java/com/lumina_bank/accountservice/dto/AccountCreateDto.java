package com.lumina_bank.accountservice.dto;

import com.lumina_bank.accountservice.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AccountCreateDto(
        @NotNull(message = "User ID is required")
        Long userId,
        @NotBlank(message = "Currency is required")
        String currency,
        @NotNull(message = "Account type is required")
        AccountType type
) {
}
