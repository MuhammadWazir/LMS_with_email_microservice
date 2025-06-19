package com.example.cms.transaction;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionResponse {
    private UUID id;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private TransactionType transactionType;
    private UUID cardId;
    private String currency;
    public TransactionResponse(Transaction transaction) {
        this.setId(transaction.getId());
        this.setAmount(transaction.getAmount());
        this.setTimestamp(transaction.getTimestamp());
        this.setTransactionType(transaction.getTransactionType());
        this.setCurrency(transaction.getCurrency());
        if (transaction.getCard() != null) {
            this.setCardId(transaction.getCard().getId());
        }
    }
}

