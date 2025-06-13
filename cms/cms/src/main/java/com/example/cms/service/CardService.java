package com.example.cms.service;

import com.example.cms.dto.CardRequest;
import com.example.cms.dto.CardResponse;
import com.example.cms.model.Account;
import com.example.cms.model.Card;
import com.example.cms.repository.AccountRepository;
import com.example.cms.repository.CardRepository;
import com.example.cms.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.cms.utils.HashUtil;
import java.util.UUID;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class CardService {
    @Autowired
    private final CardRepository cardRepository;
    @Autowired
    private final AccountRepository accountRepository;

    public CardResponse create(CardRequest request) {
        List<UUID> accountIds = request.getAccountIds();
        if (accountIds == null || accountIds.isEmpty()) {
            throw new IllegalArgumentException("At least one account ID is required.");
        }

        List<Account> accounts = accountRepository.findAllById(accountIds);
        if (accounts.isEmpty()) {
            throw new NotFoundException("No valid accounts found.");
        }

        // Validate that all accounts have unique currencies
        Set<String> currencies = new HashSet<>();
        for (Account account : accounts) {
            if (!currencies.add(account.getCurrency())) {
                throw new IllegalArgumentException("All linked accounts must have different currencies.");
            }
        }

        Card card = new Card();
        card.setStatus(request.getStatus());
        card.setExpiry(request.getExpiry());
        card.setCardNumber(HashUtil.sha256(request.getCardNumber()));
        card.setAccounts(accounts); // Many-to-Many

        Card savedCard = cardRepository.save(card);
        return new CardResponse(card);
    }


    public CardResponse get(UUID id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Card not found"));
        return new CardResponse(card);

    }

    public CardResponse changeStatus(UUID id, String status) {
        Card card = getEntity(id);
        card.setStatus(status);
        Card updatedCard = cardRepository.save(card);
        return new CardResponse(updatedCard);
    }
    private Card getEntity(UUID id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Card not found"));
    }
}
