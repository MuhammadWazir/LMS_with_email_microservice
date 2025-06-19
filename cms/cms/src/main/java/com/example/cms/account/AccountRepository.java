package com.example.cms.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    @Query("select a from Account a join a.cards c where c.cardNumber = :cardNumber and a.currency = :currency")
    Optional<Account> findByCardNumberAndCurrency(@Param("cardNumber") String cardNumber, @Param("currency") String currency);
}

