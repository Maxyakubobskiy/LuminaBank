package com.lumina_bank.accountservice.service;

import com.lumina_bank.accountservice.dto.CardCreateDto;
import com.lumina_bank.accountservice.dto.CardResponse;
import com.lumina_bank.accountservice.enums.AccountType;
import com.lumina_bank.accountservice.enums.Status;
import com.lumina_bank.accountservice.exception.CardNotFoundException;
import com.lumina_bank.accountservice.model.Account;
import com.lumina_bank.accountservice.model.Card;
import com.lumina_bank.accountservice.repository.AccountRepository;
import com.lumina_bank.accountservice.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final AccountService accountService;

    @Transactional
    public Card createCard(Long accountId, CardCreateDto cardCreateDto) {
        Account account = accountService.getAccountById(accountId);

        String cardNumber = generateCardNumber();
        while (cardRepository.existsByCardNumber(cardNumber)) {
            cardNumber = generateCardNumber();
        }

        Card card = Card.builder()
                .cardNumber(cardNumber)
                .expirationDate(YearMonth.now().plusYears(4))// TODO: можливо доробити щоб не завжди 4 роки термін дії
                .cvv(generateCVV())
                .cardType(cardCreateDto.cardType())
                .cardNetwork(cardCreateDto.cardNetwork())
                .accountType(account.getType())
                .status(Status.ACTIVE)
                .limit(cardCreateDto.limit())
                .account(account)
                .build();

        return cardRepository.save(card);
    }

    @Transactional
    public Card setActive(Long cardId, Status status) {
        Card card = getCardById(cardId);

        card.setStatus(status);
        return cardRepository.save(card);
    }

    @Transactional(readOnly = true)
    public List<CardResponse> getCardsByAccountId(Long accountId,Status status) {
        Account account = accountService.getAccountById(accountId);

        return cardRepository.findAllByAccount(account)
                .stream()
                .filter((Card card) -> card.getStatus() == status)
                .map(CardResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public Card getCardById(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card with id " + cardId + " not found"));
    }


    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String generateCVV() {
        Random random = new Random();
        // Формуємо число від 0 до 999, і завжди отримуємо 3 символи (з ведучими нулями)
        return String.format("%03d", random.nextInt(1000));
    }

}
