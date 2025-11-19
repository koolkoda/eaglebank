package com.eagle.api.controller;


import com.eagle.api.dto.CreateTransactionRequest;
import com.eagle.api.dto.ListTransactionsResponse;
import com.eagle.api.dto.TransactionResponse;
import com.eagle.api.exception.AccountNotFoundException;
import com.eagle.api.exception.ForbiddenException;
import com.eagle.api.exception.TransactionNotFoundException;
import com.eagle.api.security.CurrentUser;
import com.eagle.api.service.AccountService;
import com.eagle.api.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/accounts/{accountNumber}/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final CurrentUser currentUser;

    public TransactionController(TransactionService transactionService,
                                 AccountService accountService,
                                 CurrentUser currentUser) {
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.currentUser = currentUser;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@PathVariable String accountNumber,
                                                                 @Valid @RequestBody CreateTransactionRequest req) {
        verifyAccount(accountNumber);
        TransactionResponse transactionResponse = transactionService.createTransaction(currentUser.getId(), accountNumber, req);
        return ResponseEntity.status(201).body(transactionResponse);
    }

    @GetMapping
    public ResponseEntity<ListTransactionsResponse> listTransactions(@PathVariable String accountNumber) {
        verifyAccount(accountNumber);
        return ResponseEntity.ok(transactionService.listTransactions(currentUser.getId(), accountNumber));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> fetchTransaction(@PathVariable String accountNumber,
                                                                @PathVariable String transactionId) {
        verifyAccount(accountNumber);
        verifyTransaction(accountNumber, transactionId);
        return ResponseEntity.ok(transactionService.getTransaction(currentUser.getId(), accountNumber, transactionId));
    }

    private void verifyAccount(String accountNumber) {
        if(!accountService.accountExists(accountNumber)) {
            throw new AccountNotFoundException("Account not found");
        }

        var currentUserId = this.currentUser.getId();
        var accountUserId = accountService.getAccountOwner(accountNumber);

        if (!currentUserId.equals(accountUserId)) {
            throw new ForbiddenException("Access denied");
        }
    }

    private void verifyTransaction(String accountNumber, String transactionId) {
        if(!transactionService.transactionExists(transactionId)
        || transactionService.getTransaction(currentUser.getId(), accountNumber, transactionId) == null) {
            throw new TransactionNotFoundException("Transaction not found");
        }
    }
}
