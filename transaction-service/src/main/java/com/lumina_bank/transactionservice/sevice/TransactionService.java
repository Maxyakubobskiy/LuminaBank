package com.lumina_bank.transactionservice.sevice;

import com.lumina_bank.transactionservice.dto.AccountOperationDto;
import com.lumina_bank.transactionservice.dto.AccountResponse;
import com.lumina_bank.transactionservice.enums.Status;
import com.lumina_bank.transactionservice.model.Transaction;
import com.lumina_bank.transactionservice.repository.TransactionRepository;
import com.lumina_bank.transactionservice.sevice.client.AccountClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountClientService accountClientService;

    @Transactional
    public Transaction makeTransaction(Long fromAccountId, Long toAccountId, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if(fromAccountId.equals(toAccountId)){
            throw new IllegalArgumentException("Sender and receiver accounts cannot be the same");
        }

        // 1. Зняти гроші з рахунку відправника
        accountClientService.withdraw(fromAccountId,
                AccountOperationDto.builder().amount(amount).build());

        // 2. Зарахувати гроші на рахунок отримувача
        accountClientService.deposit(toAccountId,
                AccountOperationDto.builder().amount(amount).build());

        // 3. Зберегти транзакцію в БД
        Transaction transaction = Transaction.builder()
                .fromAccountId(fromAccountId)
                .toAccountId(toAccountId)
                .amount(amount)
                .status(Status.SUCCESS)
                .description(description != null ? description : "")
                .build();

        return transactionRepository.save(transaction);
    }

}
