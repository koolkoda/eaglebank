package com.eagle.api.dto;

import lombok.Data;

@Data
public class UpdateBankAccountRequest {
    private String name;
    private String accountType;
}
