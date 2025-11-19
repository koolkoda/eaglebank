package com.eagle.api.controller;

import com.eagle.api.dto.BankAccountResponse;
import com.eagle.api.dto.CreateBankAccountRequest;
import com.eagle.api.dto.ListBankAccountsResponse;
import com.eagle.api.dto.UpdateBankAccountRequest;
import com.eagle.api.exception.AccountNotFoundException;
import com.eagle.api.exception.ForbiddenException;
import com.eagle.api.security.CurrentUser;
import com.eagle.api.service.AccountService;
import com.eagle.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/accounts")
public class AccountController {

    private final AccountService accountService;
    private final CurrentUser currentUser;

    public AccountController(AccountService accountService, UserService userService, CurrentUser currentUser) {
        this.accountService = accountService;
        this.currentUser = currentUser;
    }

    @PostMapping
    public ResponseEntity<BankAccountResponse> createAccount(@Valid @RequestBody CreateBankAccountRequest req) {
        BankAccountResponse created = accountService.createAccount(currentUser.getId(), req);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping
    public ResponseEntity<ListBankAccountsResponse> listAccounts() {
        return ResponseEntity.ok(accountService.listAccounts(currentUser.getId()));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<BankAccountResponse> fetchAccount(@PathVariable String accountNumber) {
        verifyAccount(accountNumber);
        return ResponseEntity.ok(accountService.getAccount(currentUser.getId(), accountNumber));
    }

    @PatchMapping("/{accountNumber}")
    public ResponseEntity<BankAccountResponse> updateAccount(@PathVariable String accountNumber,
                                                             @Valid @RequestBody UpdateBankAccountRequest req) {
        verifyAccount(accountNumber);
        return ResponseEntity.ok(accountService.updateAccount(currentUser.getId(), accountNumber, req));
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String accountNumber) {
        verifyAccount(accountNumber);
        accountService.deleteAccount(currentUser.getId(), accountNumber);
        return ResponseEntity.noContent().build();
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
}