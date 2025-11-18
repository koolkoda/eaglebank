package com.eagle.api.controller;

import com.eagle.api.dto.BankAccountResponse;
import com.eagle.api.dto.CreateBankAccountRequest;
import com.eagle.api.dto.ListBankAccountsResponse;
import com.eagle.api.dto.UpdateBankAccountRequest;
import com.eagle.api.exception.UnauthorizedException;
import com.eagle.api.service.AccountService;
import com.eagle.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/accounts")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

    public AccountController(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<BankAccountResponse> createAccount(@Valid @RequestBody CreateBankAccountRequest req) {
        BankAccountResponse created = accountService.createAccount(getCurrentUserId(), req);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping
    public ResponseEntity<ListBankAccountsResponse> listAccounts() {
        return ResponseEntity.ok(accountService.listAccounts(getCurrentUserId()));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<BankAccountResponse> fetchAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getAccount(getCurrentUserId(), accountNumber));
    }

    @PatchMapping("/{accountNumber}")
    public ResponseEntity<BankAccountResponse> updateAccount(@PathVariable String accountNumber,
                                                             @Valid @RequestBody UpdateBankAccountRequest req) {
        return ResponseEntity.ok(accountService.updateAccount(getCurrentUserId(), accountNumber, req));
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String accountNumber) {
        accountService.deleteAccount(getCurrentUserId(), accountNumber);
        return ResponseEntity.noContent().build();
    }

    // Validate that the security context and authentication exist before accessing them.
    private String getCurrentUserId() {
        var context = SecurityContextHolder.getContext();

        var auth = context.getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Unauthenticated request");
        }

        String email = auth.getName();
        if (email == null || email.isBlank()) {
            throw new UnauthorizedException("Unauthenticated request");
        }

        String userId = this.userService.getUserIdByEmail(email);
        if (userId == null || userId.isBlank()) {
            throw new UnauthorizedException("User not found");
        }

        return userId;
    }
}