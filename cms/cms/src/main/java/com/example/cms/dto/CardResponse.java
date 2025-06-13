package com.example.cms.dto;

import com.example.cms.model.Card;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class CardResponse {
    private UUID id;
    private String status;
    private Date expiry;
    private String cardNumber;
    private List<UUID> accountIds;
    public CardResponse (Card card) {
        this.setId(card.getId());
        this.setStatus(card.getStatus());
        this.setExpiry(card.getExpiry());
        this.setCardNumber(card.getCardNumber());
        if (card.getAccounts() != null) {
            this.setAccountIds(card.getAccounts().stream()
                    .map(account -> account.getId())
                    .toList());
        }
    }

}

