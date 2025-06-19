package com.example.cms.card;

import com.example.cms.account.Account;
import com.example.cms.transaction.Transaction;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
public class Card {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String status;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date expiry;

    @Column(nullable = false, unique = true)
    private String cardNumber;

    @ManyToMany
    @JoinTable(
            name = "card_account",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private List<Account> accounts;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    private List<Transaction> transactions;
}

