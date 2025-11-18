package com.eagle.api.service;

import com.eagle.api.dto.CreateTransactionRequest;
import com.eagle.api.dto.ListTransactionsResponse;
import com.eagle.api.dto.TransactionResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TransactionService {

    private final Map<String, Map<String, TransactionResponse>> store = new ConcurrentHashMap<>();
    private final AccountService accountService;

    public TransactionService(AccountService accountService) {
        this.accountService = accountService;
    }

    public TransactionResponse createTransaction(String userId, String accountNumber, CreateTransactionRequest req) {
        // validate account existence
        accountService.getAccount(userId, accountNumber);
        BigDecimal amount = req.getAmount();
        BigDecimal delta = req.getType().equals("deposit") ? amount : amount.negate();

        try {
            accountService.adjustBalance(userId, accountNumber, delta);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setId(generateTransactionId());
        transactionResponse.setAmount(amount);
        transactionResponse.setCurrency(req.getCurrency());
        transactionResponse.setType(req.getType());
        transactionResponse.setReference(req.getReference());
        transactionResponse.setCreatedTimestamp(Instant.now().toString());

        store.computeIfAbsent(accountNumber, k -> new ConcurrentHashMap<>()).put(transactionResponse.getId(), transactionResponse);
        return transactionResponse;
    }

    public ListTransactionsResponse listTransactions(String userId, String accountNumber) {
        accountService.getAccount(userId, accountNumber);
        List<TransactionResponse> list = new ArrayList<>(store.getOrDefault(accountNumber, Collections.emptyMap()).values());
        ListTransactionsResponse listTransactionsResponse = new ListTransactionsResponse();
        listTransactionsResponse.setTransactions(list);
        return listTransactionsResponse;
    }

    public TransactionResponse getTransaction(String userId, String accountNumber, String transactionId) {
        accountService.getAccount(userId, accountNumber);
        Map<String, TransactionResponse> m = store.get(accountNumber);
        if (m == null || !m.containsKey(transactionId)) throw new NoSuchElementException("Transaction not found");
        return m.get(transactionId);
    }

    private String generateTransactionId() {
        return "tan-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}