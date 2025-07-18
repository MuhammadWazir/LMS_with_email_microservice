package com.example.cms.account;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AccountResponse {
    private UUID id;
    private String status;
    private BigDecimal balance;
    private String currency;
}

