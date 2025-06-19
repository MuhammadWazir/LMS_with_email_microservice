package com.example.cms.transaction;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {
    private String cardNumber;
    private String currency;
    private String transactionType;
    private BigDecimal transactionAmount;
}
