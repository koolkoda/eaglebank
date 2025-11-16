package com.eagle.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class ListTransactionsResponse {
    private List<TransactionResponse> transactions;
}