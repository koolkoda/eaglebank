package com.eagle.api.service;

import com.eagle.api.dto.BankAccountResponse;
import com.eagle.api.dto.CreateBankAccountRequest;
import com.eagle.api.dto.ListBankAccountsResponse;
import com.eagle.api.dto.UpdateBankAccountRequest;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountService {
    private final Map<String, BankAccountResponse> accounts = new ConcurrentHashMap<>();
    private final SetMultimap<String, String> userAccounts = HashMultimap.create();

    public BankAccountResponse createAccount(String userId, CreateBankAccountRequest req) {
        String accNum = generateAccountNumber();
        Instant now = Instant.now();
        BankAccountResponse a = new BankAccountResponse();
        a.setAccountNumber(accNum);
        a.setSortCode("10-10-10");
        a.setName(req.getName());
        a.setAccountType(req.getAccountType());
        a.setBalance(BigDecimal.ZERO);
        a.setCurrency("GBP");
        a.setCreatedTimestamp(now.toString());
        a.setUpdatedTimestamp(now.toString());
        accounts.put(accNum, a);

        userAccounts.put(userId, accNum);
        return a;
    }

    public ListBankAccountsResponse listAccounts(String userId) {
        ListBankAccountsResponse r = new ListBankAccountsResponse();

        var accountIds = userAccounts.get(userId);
        for(String accId : accountIds) {
            BankAccountResponse a = accounts.get(accId);
            if (a != null) {
                r.getAccounts().add(a);
            }
        }
        return r;
    }

    public BankAccountResponse getAccount(String userId, String accountNumber) {
        var accountIds = userAccounts.get(userId);
        if (!accountIds.contains(accountNumber)) {
            throw new NoSuchElementException("Account not found");
        }

        return accounts.get(accountNumber);
    }

    public BankAccountResponse updateAccount(String userId, String accountNumber, UpdateBankAccountRequest req) {
        BankAccountResponse a = getAccount(userId, accountNumber);
        if (req.getName() != null) a.setName(req.getName());
        if (req.getAccountType() != null) a.setAccountType(req.getAccountType());
        a.setUpdatedTimestamp(Instant.now().toString());
        accounts.put(accountNumber, a);
        return a;
    }

    public void deleteAccount(String userId, String accountNumber) {
        var accountIds = userAccounts.get(userId);
        if (!accountIds.contains(accountNumber)) {
            throw new NoSuchElementException("Account not found");
        }

        accounts.remove(accountNumber);
    }

    // internal helpers
    private String generateAccountNumber() {
        Random r = new Random();
        int n = r.nextInt(1_000_000);
        return String.format("01%06d", n);
    }

    // used by TransactionService
    public void adjustBalance(String userId, String accountNumber, BigDecimal delta) {
        BankAccountResponse a = getAccount(userId, accountNumber);
        BigDecimal newBal = a.getBalance().add(delta);
        if (newBal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        a.setBalance(newBal);
        a.setUpdatedTimestamp(Instant.now().toString());
        accounts.put(accountNumber, a);
    }
}
