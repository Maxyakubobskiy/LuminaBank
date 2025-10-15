package com.lumina_bank.transactionservice.controller;

import com.lumina_bank.transactionservice.dto.TransactionCreateDto;
import com.lumina_bank.transactionservice.dto.TransactionResponse;
import com.lumina_bank.transactionservice.model.Transaction;
import com.lumina_bank.transactionservice.sevice.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<?> makeTransaction(@Valid @RequestBody TransactionCreateDto request) {
        Transaction transaction = transactionService.makeTransaction(
                request.fromAccountId(),
                request.toAccountId(),
                request.amount(),
                request.description()
        );
        return ResponseEntity.ok(TransactionResponse.fromEntity(transaction));
    }

}
