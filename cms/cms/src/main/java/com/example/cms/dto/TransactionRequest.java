package com.example.cms.dto;

import com.example.cms.model.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransactionRequest {
    private String cardNumber;
    private String currency;
    private String transactionType;
    private BigDecimal transactionAmount;
}
