package com.cbs.controller;

import com.cbs.model.dto.request.DepositRequest;
import com.cbs.model.dto.request.TransferRequest;
import com.cbs.model.dto.request.WithdrawalRequest;
import com.cbs.model.dto.response.TransactionResponse;
import com.cbs.service.interface_.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    
    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody DepositRequest request) {
        TransactionResponse transactionResponse = transactionService.deposit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponse);
    }
    
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@Valid @RequestBody WithdrawalRequest request) {
        try {
            TransactionResponse transactionResponse = transactionService.withdraw(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@Valid @RequestBody TransferRequest request) {
        try {
            TransactionResponse transactionResponse = transactionService.transfer(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long transactionId) {
        return transactionService.getTransactionById(transactionId)
                .map(response -> ResponseEntity.ok(response))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/reference/{transactionReference}")
    public ResponseEntity<TransactionResponse> getTransactionByReference(@PathVariable String transactionReference) {
        return transactionService.getTransactionByReference(transactionReference)
                .map(response -> ResponseEntity.ok(response))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccountId(@PathVariable Long accountId) {
        List<TransactionResponse> transactions = transactionService.getTransactionsByAccountId(accountId);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByUserId(@PathVariable Long userId) {
        List<TransactionResponse> transactions = transactionService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByUserIdAndStatus(
            @PathVariable Long userId, @PathVariable String status) {
        List<TransactionResponse> transactions = transactionService.getTransactionsByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        List<TransactionResponse> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<TransactionResponse> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByUserIdAndDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<TransactionResponse> transactions = transactionService.getTransactionsByUserIdAndDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }
    
    @PutMapping("/{transactionId}/status")
    public ResponseEntity<TransactionResponse> updateTransactionStatus(
            @PathVariable Long transactionId, @RequestBody Map<String, String> status) {
        TransactionResponse transactionResponse = transactionService.updateTransactionStatus(transactionId, status.get("status"));
        return ResponseEntity.ok(transactionResponse);
    }
    
    @PostMapping("/{transactionId}/reverse")
    public ResponseEntity<?> reverseTransaction(@PathVariable Long transactionId) {
        try {
            TransactionResponse transactionResponse = transactionService.reverseTransaction(transactionId);
            return ResponseEntity.ok(transactionResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/account/{accountId}/debits")
    public ResponseEntity<Map<String, Object>> getTotalDebits(@PathVariable Long accountId) {
        BigDecimal totalDebits = transactionService.getTotalDebits(accountId);
        return ResponseEntity.ok(Map.of("totalDebits", totalDebits));
    }
    
    @GetMapping("/account/{accountId}/credits")
    public ResponseEntity<Map<String, Object>> getTotalCredits(@PathVariable Long accountId) {
        BigDecimal totalCredits = transactionService.getTotalCredits(accountId);
        return ResponseEntity.ok(Map.of("totalCredits", totalCredits));
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<TransactionResponse>> getPendingTransactions() {
        List<TransactionResponse> transactions = transactionService.getPendingTransactions();
        return ResponseEntity.ok(transactions);
    }
    
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long transactionId) {
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }
}