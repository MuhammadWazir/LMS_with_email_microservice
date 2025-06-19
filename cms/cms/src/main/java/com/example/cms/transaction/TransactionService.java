package com.example.cms.transaction;

import com.example.cms.exception.ValidationException;
import com.example.cms.account.Account;
import com.example.cms.card.Card;
import com.example.cms.account.AccountRepository;
import com.example.cms.card.CardRepository;
import com.example.cms.utils.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;

@RequiredArgsConstructor
@Service
public class TransactionService {
    @Autowired
    private final TransactionRepository transactionRepository;
    @Autowired
    private final CardRepository cardRepository;
    @Autowired
    private final AccountRepository accountRepository;

    public TransactionResponse create(TransactionRequest request) {

        Card card = cardRepository.findByCardNumber(HashUtil.sha256(request.getCardNumber()))
                .orElseThrow(() -> new ValidationException("Invalid card"));

        Account account = accountRepository.findByCardNumberAndCurrency(request.getCardNumber(), request.getCurrency())
                .orElseThrow(() -> new ValidationException("Invalid account for the given card and currency"));

        if (!"ACTIVE".equals(card.getStatus()) || card.getExpiry().before(new java.util.Date())) {
            throw new ValidationException("Card not eligible");
        }

        if (!"ACTIVE".equals(account.getStatus())) {
            throw new ValidationException("Account not eligible");
        }

        if ("D".equals(request.getTransactionType()) &&
                account.getBalance().compareTo(request.getTransactionAmount()) < 0) {
            throw new ValidationException("Insufficient balance");
        }

        BigDecimal newBalance = "C".equals(request.getTransactionType())
                ? account.getBalance().add(request.getTransactionAmount())
                : account.getBalance().subtract(request.getTransactionAmount());

        account.setBalance(newBalance);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAmount(request.getTransactionAmount());
        transaction.setTimestamp(LocalDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")));
        transaction.setTransactionType(TransactionType.valueOf(request.getTransactionType()));
        transaction.setCurrency(request.getCurrency());
        transaction.setCard(card);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return new TransactionResponse(transaction);
    }
}
