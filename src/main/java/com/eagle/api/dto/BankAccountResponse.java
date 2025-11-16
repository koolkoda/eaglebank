package com.eagle.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BankAccountResponse {
    private String accountNumber;
    private String sortCode;
    private String name;
    private String accountType;
    private BigDecimal balance;
    private String currency;
    private String createdTimestamp;
    private String updatedTimestamp;
}
