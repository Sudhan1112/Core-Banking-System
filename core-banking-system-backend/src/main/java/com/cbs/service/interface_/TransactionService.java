package com.cbs.service.interface_;

import com.cbs.model.dto.request.DepositRequest;
import com.cbs.model.dto.request.TransferRequest;
import com.cbs.model.dto.request.WithdrawalRequest;
import com.cbs.model.dto.response.TransactionResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionService {
    
    TransactionResponse deposit(DepositRequest request);
    
    TransactionResponse withdraw(WithdrawalRequest request) throws Exception;
    
    TransactionResponse transfer(TransferRequest request) throws Exception;
    
    Optional<TransactionResponse> getTransactionById(Long transactionId);
    
    Optional<TransactionResponse> getTransactionByReference(String transactionReference);
    
    List<TransactionResponse> getTransactionsByAccountId(Long accountId);
    
    List<TransactionResponse> getTransactionsByUserId(Long userId);
    
    List<TransactionResponse> getTransactionsByUserIdAndStatus(Long userId, String status);
    
    List<TransactionResponse> getAllTransactions();
    
    List<TransactionResponse> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    List<TransactionResponse> getTransactionsByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    TransactionResponse updateTransactionStatus(Long transactionId, String status);
    
    TransactionResponse reverseTransaction(Long transactionId) throws Exception;
    
    BigDecimal getTotalDebits(Long accountId);
    
    BigDecimal getTotalCredits(Long accountId);
    
    List<TransactionResponse> getPendingTransactions();
    
    void deleteTransaction(Long transactionId);
}