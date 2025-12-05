package com.lumina_bank.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UserUpdateDto(
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Last name is required") String lastName,
        @NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email,
        @NotBlank(message = "Phone number is required") String phoneNumber,
        LocalDate birthDate,
        String street,
        String city,
        String houseNumber,
        String zipCode,
        String country
) {
}
