package com.eagle.api.controller;


import com.eagle.api.dto.CreateTransactionRequest;
import com.eagle.api.dto.ListTransactionsResponse;
import com.eagle.api.dto.TransactionResponse;
import com.eagle.api.service.TransactionService;
import com.eagle.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/accounts/{accountNumber}/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@PathVariable String accountNumber,
                                                                 @Valid @RequestBody CreateTransactionRequest req) {
        TransactionResponse transactionResponse = transactionService.createTransaction(getCurrentUserId(), accountNumber, req);
        return ResponseEntity.status(201).body(transactionResponse);
    }

    @GetMapping
    public ResponseEntity<ListTransactionsResponse> listTransactions(@PathVariable String accountNumber) {
        return ResponseEntity.ok(transactionService.listTransactions(getCurrentUserId(), accountNumber));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> fetchTransaction(@PathVariable String accountNumber,
                                                                @PathVariable String transactionId) {
        return ResponseEntity.ok(transactionService.getTransaction(getCurrentUserId(), accountNumber, transactionId));
    }

    private String getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return this.userService.getUserIdByEmail(email);
    }
}
