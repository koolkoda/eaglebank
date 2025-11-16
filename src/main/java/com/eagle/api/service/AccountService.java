package com.eagle.api.service;

import com.eagle.api.dto.BankAccountResponse;
import com.eagle.api.dto.CreateBankAccountRequest;
import com.eagle.api.dto.ListBankAccountsResponse;
import com.eagle.api.dto.UpdateBankAccountRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountService {
    private final Map<String, BankAccountResponse> accounts = new ConcurrentHashMap<>();

    public BankAccountResponse createAccount(CreateBankAccountRequest req) {
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
        return a;
    }

    public ListBankAccountsResponse listAccounts() {
        ListBankAccountsResponse r = new ListBankAccountsResponse();
        r.setAccounts(new ArrayList<>(accounts.values()));
        return r;
    }

    public BankAccountResponse getAccount(String accountNumber) {
        BankAccountResponse a = accounts.get(accountNumber);
        if (a == null) throw new NoSuchElementException("Account not found");
        return a;
    }

    public BankAccountResponse updateAccount(String accountNumber, UpdateBankAccountRequest req) {
        BankAccountResponse a = getAccount(accountNumber);
        if (req.getName() != null) a.setName(req.getName());
        if (req.getAccountType() != null) a.setAccountType(req.getAccountType());
        a.setUpdatedTimestamp(Instant.now().toString());
        accounts.put(accountNumber, a);
        return a;
    }

    public void deleteAccount(String accountNumber) {
        if (accounts.remove(accountNumber) == null) throw new NoSuchElementException("Account not found");
    }

    // internal helpers
    private String generateAccountNumber() {
        Random r = new Random();
        int n = r.nextInt(1_000_000);
        return String.format("01%06d", n);
    }

    // used by TransactionService
    public void adjustBalance(String accountNumber, BigDecimal delta) {
        BankAccountResponse a = getAccount(accountNumber);
        BigDecimal newBal = a.getBalance().add(delta);
        if (newBal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        a.setBalance(newBal);
        a.setUpdatedTimestamp(Instant.now().toString());
        accounts.put(accountNumber, a);
    }
}
