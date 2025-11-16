package com.eagle.api.controller;

import com.eagle.api.dto.BankAccountResponse;
import com.eagle.api.dto.CreateBankAccountRequest;
import com.eagle.api.dto.ListBankAccountsResponse;
import com.eagle.api.dto.UpdateBankAccountRequest;
import com.eagle.api.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/accounts")
public class AccountController {

    private final AccountService svc;

    public AccountController(AccountService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<BankAccountResponse> createAccount(@Valid @RequestBody CreateBankAccountRequest req) {
        BankAccountResponse created = svc.createAccount(req);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping
    public ResponseEntity<ListBankAccountsResponse> listAccounts() {
        return ResponseEntity.ok(svc.listAccounts());
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<BankAccountResponse> fetchAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(svc.getAccount(accountNumber));
    }

    @PatchMapping("/{accountNumber}")
    public ResponseEntity<BankAccountResponse> updateAccount(@PathVariable String accountNumber,
                                                             @Valid @RequestBody UpdateBankAccountRequest req) {
        return ResponseEntity.ok(svc.updateAccount(accountNumber, req));
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String accountNumber) {
        svc.deleteAccount(accountNumber);
        return ResponseEntity.noContent().build();
    }
}