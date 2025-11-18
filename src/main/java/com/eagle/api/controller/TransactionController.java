package com.eagle.api.controller;


import com.eagle.api.dto.CreateTransactionRequest;
import com.eagle.api.dto.ListTransactionsResponse;
import com.eagle.api.dto.TransactionResponse;
import com.eagle.api.security.CurrentUser;
import com.eagle.api.service.TransactionService;
import com.eagle.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/accounts/{accountNumber}/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final CurrentUser currentUser;

    public TransactionController(TransactionService transactionService, UserService userService, CurrentUser currentUser) {
        this.transactionService = transactionService;
        this.currentUser = currentUser;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@PathVariable String accountNumber,
                                                                 @Valid @RequestBody CreateTransactionRequest req) {
        TransactionResponse transactionResponse = transactionService.createTransaction(currentUser.getId(), accountNumber, req);
        return ResponseEntity.status(201).body(transactionResponse);
    }

    @GetMapping
    public ResponseEntity<ListTransactionsResponse> listTransactions(@PathVariable String accountNumber) {
        return ResponseEntity.ok(transactionService.listTransactions(currentUser.getId(), accountNumber));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> fetchTransaction(@PathVariable String accountNumber,
                                                                @PathVariable String transactionId) {
        return ResponseEntity.ok(transactionService.getTransaction(currentUser.getId(), accountNumber, transactionId));
    }
}
