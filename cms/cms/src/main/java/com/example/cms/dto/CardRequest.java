package com.example.cms.dto;

import lombok.Data;
import java.util.List;
import java.util.Date;
import java.util.UUID;

@Data
public class CardRequest {
    private String status;
    private Date expiry;
    private String cardNumber;
    private List<UUID> accountIds;
}
