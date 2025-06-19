package com.example.cms.account;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequest {
    private String status;
    private BigDecimal balance;
    private String currency;
}

