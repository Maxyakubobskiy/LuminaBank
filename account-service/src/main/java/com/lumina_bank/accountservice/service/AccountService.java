package com.lumina_bank.accountservice.service;

import com.lumina_bank.accountservice.dto.AccountCreateDto;
import com.lumina_bank.accountservice.dto.AccountResponse;
import com.lumina_bank.accountservice.enums.CountryBankCode;
import com.lumina_bank.accountservice.enums.Status;
import com.lumina_bank.accountservice.model.Account;
import com.lumina_bank.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;


    @Transactional
    public Account createAccount(AccountCreateDto accountDto) {
         String iban = generateIban();
         while (accountRepository.existsByIban(iban))
             iban = generateIban();

        Account account = Account.builder().
                userId(accountDto.userId()).
                balance(BigDecimal.ZERO).
                iban(iban).
                currency(accountDto.currency()).
                status(Status.ACTIVE).
                type(accountDto.type()).
                build();

        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByUserId(Long userId) {
        return accountRepository.findAllByUserId(userId)
                .stream()
                .filter((Account account) -> account.getStatus() == Status.ACTIVE)
                .map(AccountResponse::fromEntity).toList();
    }

    @Transactional
    public Account deposit (Long id, BigDecimal amount) {
        if(amount.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        Account account = getAccountById(id);
        account.setBalance(account.getBalance().add(amount));
        return accountRepository.save(account);
    }

    @Transactional
    public Account withdraw (Long id, BigDecimal amount) {
        if(amount.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        Account account = getAccountById(id);

        if (account.getBalance().compareTo(amount) < 0 ) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(()-> new NoSuchElementException("Account with id " + accountId + " not found"));
    }

    @Transactional(readOnly = true)
    public Account getAccountByIban(String iban) {
        return accountRepository.findByIban(iban)
                .orElseThrow(()-> new NoSuchElementException("Account with iban " + iban + " not found"));

    }

    private String generateIban(){
        Random random = new Random();
        int controlDigit = random.nextInt(90)+10;

        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < 19; i++){
            accountNumber.append(random.nextInt(10));
        }
        return CountryBankCode.UA.name() + controlDigit + CountryBankCode.UA.getBankCode() + accountNumber;
    }

    @Transactional
    public Account setActive(Long accountId, Status status) {
        Account account = getAccountById(accountId);
        account.setStatus(status);

        if(status.equals(Status.INACTIVE) || status.equals(Status.BLOCKED)){
            account.getCards().forEach((card) -> card.setStatus(status));
        }

        return accountRepository.save(account);
    }
}
