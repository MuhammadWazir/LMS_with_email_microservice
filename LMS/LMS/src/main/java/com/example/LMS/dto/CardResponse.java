package com.example.LMS.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CardResponse {
    private UUID id;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private CardTransactionType transactionType;
    private UUID cardId;
    private String currency;
}