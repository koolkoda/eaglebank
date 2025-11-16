package com.eagle.api.controller;


import com.eagle.api.dto.CreateTransactionRequest;
import com.eagle.api.dto.ListTransactionsResponse;
import com.eagle.api.dto.TransactionResponse;
import com.eagle.api.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/accounts/{accountNumber}/transactions")
public class TransactionController {

    private final TransactionService svc;

    public TransactionController(TransactionService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@PathVariable String accountNumber,
                                                                 @Valid @RequestBody CreateTransactionRequest req) {
        TransactionResponse tr = svc.createTransaction(accountNumber, req);
        return ResponseEntity.status(201).body(tr);
    }

    @GetMapping
    public ResponseEntity<ListTransactionsResponse> listTransactions(@PathVariable String accountNumber) {
        return ResponseEntity.ok(svc.listTransactions(accountNumber));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> fetchTransaction(@PathVariable String accountNumber,
                                                                @PathVariable String transactionId) {
        return ResponseEntity.ok(svc.getTransaction(accountNumber, transactionId));
    }
}
