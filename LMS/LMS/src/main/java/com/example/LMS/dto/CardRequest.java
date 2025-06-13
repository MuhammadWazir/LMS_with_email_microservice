package com.example.LMS.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CardRequest {
    private String cardNumber;
    private String currency;
    private String transactionType;
    private BigDecimal transactionAmount;
}
