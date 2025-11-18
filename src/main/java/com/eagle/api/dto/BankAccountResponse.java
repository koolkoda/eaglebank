package com.eagle.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BankAccountResponse {
    private String accountNumber;
    private String sortCode;
    private String name;
    private AccountType accountType;
    private BigDecimal balance;
    private String currency;
    private String createdTimestamp;
    private String updatedTimestamp;
}
