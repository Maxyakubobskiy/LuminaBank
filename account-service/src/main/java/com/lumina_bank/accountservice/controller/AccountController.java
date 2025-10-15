package com.lumina_bank.accountservice.controller;

import com.lumina_bank.accountservice.dto.AccountCreateDto;
import com.lumina_bank.accountservice.dto.AccountOperationDto;
import com.lumina_bank.accountservice.dto.AccountResponse;
import com.lumina_bank.accountservice.enums.Status;
import com.lumina_bank.accountservice.model.Account;
import com.lumina_bank.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<?> createAccount(@Valid @RequestBody AccountCreateDto accountDto) {
        Account account = accountService.createAccount(accountDto);
        return ResponseEntity.ok(AccountResponse.fromEntity(account));
    }

    @PutMapping("/{accountId}/deposit")
    public ResponseEntity<?> deposit(
            @PathVariable Long accountId,
            @Valid @RequestBody AccountOperationDto accountDto) {
        Account updateAccount = accountService.deposit(accountId,accountDto.amount());
        return ResponseEntity.ok(AccountResponse.fromEntity(updateAccount));
    }

    @PutMapping("/{accountId}/withdraw")
    public ResponseEntity<?> withdraw(
            @PathVariable Long accountId,
            @Valid @RequestBody AccountOperationDto accountDto
    ){
        Account updateAccount = accountService.withdraw(accountId,accountDto.amount());
        return ResponseEntity.ok(AccountResponse.fromEntity(updateAccount));
    }

    @GetMapping("/{userId}/user-accounts")
    public ResponseEntity<?> getUserAccounts(@PathVariable Long userId){
        return ResponseEntity.ok().body(accountService.getAccountsByUserId(userId));
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<?> setActiveAccount(
            @PathVariable Long accountId,
            @RequestBody Status status){
        Account account = accountService.setActive(accountId,status);
        return ResponseEntity.ok(AccountResponse.fromEntity(account));
    }


}
