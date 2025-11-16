package com.eagle.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class ListBankAccountsResponse {
    private List<BankAccountResponse> accounts;

}