package com.example.cms.account;

import com.example.cms.card.Card;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class Account {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String status; // ACTIVE, INACTIVE

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private String currency; // e.g., "USD", "EUR"

    @ManyToMany(mappedBy = "accounts")
    private List<Card> cards;
}
