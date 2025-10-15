package com.lumina_bank.accountservice.controller;

import com.lumina_bank.accountservice.dto.CardCreateDto;
import com.lumina_bank.accountservice.dto.CardResponse;
import com.lumina_bank.accountservice.enums.Status;
import com.lumina_bank.accountservice.model.Card;
import com.lumina_bank.accountservice.service.CardService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @PostMapping("/{accountId}")
    public ResponseEntity<?> addCard(
            @PathVariable Long accountId,
            @Valid @RequestBody CardCreateDto cardCreateDto) {
        Card card = cardService.createCard(accountId,cardCreateDto);
        return ResponseEntity.ok().body(CardResponse.fromEntity(card));
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<?> setActiveCard(
            @PathVariable Long cardId,
            @RequestBody Status status) {
        Card card = cardService.setActive(cardId,status);
        return ResponseEntity.ok().body(CardResponse.fromEntity(card));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<?> getCardsByAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok().body(cardService.getCardsByAccountId(accountId));
    }
}
