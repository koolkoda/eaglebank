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
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountService {
    private final Map<String, BankAccountResponse> accounts = new ConcurrentHashMap<>();
    private final SetMultimap<String, String> userAccounts = HashMultimap.create();
    private final Map<String, String> accountToUser = new ConcurrentHashMap<>();

    public BankAccountResponse createAccount(String userId, CreateBankAccountRequest req) {
        String accNum = generateAccountNumber();
        Instant now = Instant.now();
        BankAccountResponse bankAccountResponse = new BankAccountResponse();
        bankAccountResponse.setAccountNumber(accNum);
        bankAccountResponse.setSortCode("10-10-10");
        bankAccountResponse.setName(req.getName());
        bankAccountResponse.setAccountType(req.getAccountType());
        bankAccountResponse.setBalance(BigDecimal.ZERO);
        bankAccountResponse.setCurrency("GBP");
        bankAccountResponse.setCreatedTimestamp(now.toString());
        bankAccountResponse.setUpdatedTimestamp(now.toString());

        accounts.put(accNum, bankAccountResponse);
        accountToUser.put(accNum, userId);
        userAccounts.put(userId, accNum);
        return bankAccountResponse;
    }

    public ListBankAccountsResponse listAccounts(String userId) {
        ListBankAccountsResponse listBankAccountsResponse = new ListBankAccountsResponse();
        listBankAccountsResponse.setAccounts(new ArrayList<>());

        var accountIds = userAccounts.get(userId);
        for(String accId : accountIds) {
            BankAccountResponse bankAccountResponse = accounts.get(accId);
            if (bankAccountResponse != null) {
                listBankAccountsResponse.getAccounts().add(bankAccountResponse);
            }
        }
        return listBankAccountsResponse;
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

        userAccounts.remove(userId, accountNumber);
        accounts.remove(accountNumber);
        accountToUser.remove(accountNumber);
    }

    // internal helpers
    private String generateAccountNumber() {
        Random random = new Random();
        int n = random.nextInt(1_000_000);
        return String.format("01%06d", n);
    }

    // used by TransactionService
    public void adjustBalance(String userId, String accountNumber, BigDecimal delta) {
        BankAccountResponse bankAccountResponse = getAccount(userId, accountNumber);
        BigDecimal newBal = bankAccountResponse.getBalance().add(delta);
        if (newBal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        bankAccountResponse.setBalance(newBal);
        bankAccountResponse.setUpdatedTimestamp(Instant.now().toString());
        accounts.put(accountNumber, bankAccountResponse);
    }

    public String getAccountOwner(String accountNumber) {
        return accountToUser.get(accountNumber);
    }

    public boolean accountExists(String accountNumber) {
        return accountToUser.containsKey(accountNumber);
    }
}
