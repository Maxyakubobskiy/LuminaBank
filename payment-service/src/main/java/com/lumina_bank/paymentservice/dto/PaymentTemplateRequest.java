package com.lumina_bank.paymentservice.dto;

import com.lumina_bank.paymentservice.enums.PaymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PaymentTemplateRequest(
        @NotBlank(message = "Name is required")
        String name,            // Назва шаблону, наприклад "Оплата комуналки"

        @NotNull(message = "From account ID is required")
        Long fromAccountId,     // Звідки платимо

        @NotNull(message = "To account ID is required")
        Long toAccountId,       // Куди платимо

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,      // Типова сума

        @Size(max = 255, message = "Description must be less than 255 characters")
        String description,      // Призначення платежу

        @NotNull(message = "Payment type is required")
        PaymentType paymentType,

        @NotNull(message = "isRecurring is required")
        Boolean isRecurring,

        String recurrenceCron,

        Long userId
) {
}