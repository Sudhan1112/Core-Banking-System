package com.cbs.controller;

import com.cbs.model.dto.request.AccountCreationRequest;
import com.cbs.model.dto.response.AccountResponse;
import com.cbs.service.interface_.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    
    private final AccountService accountService;
    
    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
    
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountCreationRequest request) {
        AccountResponse accountResponse = accountService.createAccount(request);
        return new ResponseEntity<>(accountResponse, HttpStatus.CREATED);
    }
    
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long accountId) {
        return accountService.getAccountById(accountId)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountByNumber(@PathVariable String accountNumber) {
        return accountService.getAccountByNumber(accountNumber)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountResponse>> getAccountsByUserId(@PathVariable Long userId) {
        List<AccountResponse> accounts = accountService.getAccountsByUserId(userId);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<AccountResponse>> getAccountsByUserIdAndStatus(
            @PathVariable Long userId, @PathVariable String status) {
        List<AccountResponse> accounts = accountService.getAccountsByUserIdAndStatus(userId, status);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }
    
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> accounts = accountService.getAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }
    
    @PutMapping("/{accountId}/status")
    public ResponseEntity<AccountResponse> updateAccountStatus(
            @PathVariable Long accountId, @RequestBody Map<String, String> status) {
        AccountResponse accountResponse = accountService.updateAccountStatus(accountId, status.get("status"));
        return new ResponseEntity<>(accountResponse, HttpStatus.OK);
    }
    
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<AccountResponse> deposit(
            @PathVariable Long accountId, @RequestBody Map<String, BigDecimal> request) {
        BigDecimal amount = request.get("amount");
        AccountResponse accountResponse = accountService.deposit(accountId, amount);
        return new ResponseEntity<>(accountResponse, HttpStatus.OK);
    }
    
    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<?> withdraw(
            @PathVariable Long accountId, @RequestBody Map<String, BigDecimal> request) {
        BigDecimal amount = request.get("amount");
        try {
            AccountResponse accountResponse = accountService.withdraw(accountId, amount);
            return new ResponseEntity<>(accountResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/low-balance")
    public ResponseEntity<List<AccountResponse>> getAccountsWithLowBalance() {
        List<AccountResponse> accounts = accountService.getAccountsWithLowBalance();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }
    
    @GetMapping("/exists/{accountNumber}")
    public ResponseEntity<Boolean> checkAccountExists(@PathVariable String accountNumber) {
        boolean exists = accountService.existsByAccountNumber(accountNumber);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }
    
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}