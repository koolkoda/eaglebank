package com.eagle.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateTransactionRequest {
    @DecimalMin("0.00")
    private BigDecimal amount;
    @NotBlank
    private String currency;
    @NotBlank
    private String type;
    private String reference;
}