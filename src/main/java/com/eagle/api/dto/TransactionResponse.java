package com.eagle.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionResponse {
    private String id;
    private BigDecimal amount;
    private String currency;
    private TransactionType type;
    private String reference;
    private String userId;
    private String createdTimestamp;
}