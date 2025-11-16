package com.eagle.api.controller;

import com.eagle.api.dto.BankAccountResponse;
import com.eagle.api.dto.CreateBankAccountRequest;
import com.eagle.api.dto.ListBankAccountsResponse;
import com.eagle.api.dto.UpdateBankAccountRequest;
import com.eagle.api.service.AccountService;
import com.eagle.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/accounts")
public class AccountController {

    private final AccountService svc;
    private final UserService userService;

    public AccountController(AccountService svc, UserService userService) {
        this.svc = svc;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<BankAccountResponse> createAccount(@Valid @RequestBody CreateBankAccountRequest req) {
        BankAccountResponse created = svc.createAccount(getCurrentUserId(), req);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping
    public ResponseEntity<ListBankAccountsResponse> listAccounts() {
        return ResponseEntity.ok(svc.listAccounts(getCurrentUserId()));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<BankAccountResponse> fetchAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(svc.getAccount(getCurrentUserId(), accountNumber));
    }

    @PatchMapping("/{accountNumber}")
    public ResponseEntity<BankAccountResponse> updateAccount(@PathVariable String accountNumber,
                                                             @Valid @RequestBody UpdateBankAccountRequest req) {
        return ResponseEntity.ok(svc.updateAccount(getCurrentUserId(), accountNumber, req));
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String accountNumber) {
        svc.deleteAccount(getCurrentUserId(), accountNumber);
        return ResponseEntity.noContent().build();
    }

    private String getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return this.userService.getUserIdByEmail(email);
    }
}