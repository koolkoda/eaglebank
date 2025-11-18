package com.eagle.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBankAccountRequest {
    @NotBlank
    private String name;
    private AccountType accountType;
}