package com.eagle.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateTransactionRequest {
    @DecimalMin("0.00")
    private BigDecimal amount;
    @NotNull
    private Currency currency;
    @NotNull
    private TransactionType type;
    private String reference;
}