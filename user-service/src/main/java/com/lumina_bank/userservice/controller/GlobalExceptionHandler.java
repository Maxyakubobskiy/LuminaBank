package com.lumina_bank.userservice.controller;

import com.lumina_bank.userservice.exception.UserAlreadyExistsException;
import com.lumina_bank.userservice.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

import static com.lumina_bank.userservice.dto.ErrorResponse.buildErrorResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Валідація DTO Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msq = ex.getBindingResult().getFieldErrors()
                .stream().map(e -> e.getField() + ":" + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation Exception: {}", msq);

        return ResponseEntity.badRequest().body(buildErrorResponse(HttpStatus.BAD_REQUEST, msq, req.getRequestURI()));
    }

    // Некоректні дані (наприклад email вже існує)
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest req) {
        log.warn("UserAlreadyExists Exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI()));
    }

    // Запис не знайдено (наприклад користувач)
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException ex, HttpServletRequest req) {
        log.warn("UserNotFound Exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI()));
    }

    //Інші помилки
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleOtherException(Exception ex, HttpServletRequest req) {
        log.warn("Unexpected exception at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req.getRequestURI()));
    }


}
