package com.cbs.service.impl;

import com.cbs.model.dto.request.DepositRequest;
import com.cbs.model.dto.request.TransferRequest;
import com.cbs.model.dto.request.WithdrawalRequest;
import com.cbs.model.dto.response.TransactionResponse;
import com.cbs.model.entity.Account;
import com.cbs.model.entity.Transaction;
import com.cbs.model.enums.AccountStatus;
import com.cbs.model.enums.TransactionStatus;
import com.cbs.model.enums.TransactionType;
import com.cbs.repository.AccountRepository;
import com.cbs.repository.TransactionRepository;
import com.cbs.service.interface_.AccountService;
import com.cbs.service.interface_.TransactionService;
import com.cbs.util.TransactionIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
    
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionIdGenerator transactionIdGenerator;
    
    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                 AccountRepository accountRepository,
                                 TransactionIdGenerator transactionIdGenerator) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.transactionIdGenerator = transactionIdGenerator;
    }
    
    @Override
    public TransactionResponse deposit(DepositRequest request) {
        logger.info("Processing deposit for account ID: {}", request.getAccountId());
        
        // Get account
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Cannot deposit to inactive account");
        }
        
        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setTransactionReference(transactionIdGenerator.generateTransactionId());
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setAmount(request.getAmount());
        transaction.setDestinationAccountId(request.getAccountId());
        transaction.setUserId(account.getUserId());
        transaction.setDescription(request.getDescription());
        transaction.setStatus(TransactionStatus.COMPLETED);
        
        // Update account balance
        BigDecimal newBalance = account.getBalance().add(request.getAmount());
        account.setBalance(newBalance);
        transaction.setBalanceAfter(newBalance);
        
        // Save transaction and account
        Transaction savedTransaction = transactionRepository.save(transaction);
        accountRepository.save(account);
        
        logger.info("Deposit completed successfully. Transaction ID: {}", savedTransaction.getTransactionId());
        return convertToResponse(savedTransaction);
    }
    
    @Override
    public TransactionResponse withdraw(WithdrawalRequest request) throws Exception {
        logger.info("Processing withdrawal for account ID: {}", request.getAccountId());
        
        // Get account
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Cannot withdraw from inactive account");
        }
        
        // Check balance
        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
        if (account.getMinimumBalance() != null && 
            newBalance.compareTo(account.getMinimumBalance()) < 0) {
            // Check if overdraft is allowed
            if (account.getOverdraftLimit() == null || 
                newBalance.compareTo(account.getOverdraftLimit().negate()) < 0) {
                throw new Exception("Insufficient balance");
            }
        }
        
        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setTransactionReference(transactionIdGenerator.generateTransactionId());
        transaction.setTransactionType(TransactionType.WITHDRAWAL);
        transaction.setAmount(request.getAmount());
        transaction.setSourceAccountId(request.getAccountId());
        transaction.setUserId(account.getUserId());
        transaction.setDescription(request.getDescription());
        transaction.setStatus(TransactionStatus.COMPLETED);
        
        // Update account balance
        account.setBalance(newBalance);
        transaction.setBalanceAfter(newBalance);
        
        // Save transaction and account
        Transaction savedTransaction = transactionRepository.save(transaction);
        accountRepository.save(account);
        
        logger.info("Withdrawal completed successfully. Transaction ID: {}", savedTransaction.getTransactionId());
        return convertToResponse(savedTransaction);
    }
    
    @Override
    public TransactionResponse transfer(TransferRequest request) throws Exception {
        logger.info("Processing transfer from account {} to account {}", 
                request.getSourceAccountId(), request.getDestinationAccountId());
        
        // Get accounts
        Account sourceAccount = accountRepository.findById(request.getSourceAccountId())
                .orElseThrow(() -> new RuntimeException("Source account not found"));
        
        Account destinationAccount = accountRepository.findById(request.getDestinationAccountId())
                .orElseThrow(() -> new RuntimeException("Destination account not found"));
        
        if (sourceAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Cannot transfer from inactive source account");
        }
        
        if (destinationAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Cannot transfer to inactive destination account");
        }
        
        // Check source account balance
        BigDecimal newSourceBalance = sourceAccount.getBalance().subtract(request.getAmount());
        if (sourceAccount.getMinimumBalance() != null && 
            newSourceBalance.compareTo(sourceAccount.getMinimumBalance()) < 0) {
            // Check if overdraft is allowed
            if (sourceAccount.getOverdraftLimit() == null || 
                newSourceBalance.compareTo(sourceAccount.getOverdraftLimit().negate()) < 0) {
                throw new Exception("Insufficient balance in source account");
            }
        }
        
        // Calculate fee (if any)
        BigDecimal feeAmount = calculateTransferFee(request.getAmount());
        BigDecimal totalAmount = request.getAmount().add(feeAmount);
        
        // Check if source account can cover fee as well
        if (totalAmount.compareTo(sourceAccount.getBalance()) > 0) {
            throw new Exception("Insufficient balance to cover transfer amount and fee");
        }
        
        // Create debit transaction (source account)
        Transaction debitTransaction = new Transaction();
        debitTransaction.setTransactionReference(transactionIdGenerator.generateTransactionId());
        debitTransaction.setTransactionType(TransactionType.TRANSFER);
        debitTransaction.setAmount(request.getAmount());
        debitTransaction.setSourceAccountId(request.getSourceAccountId());
        debitTransaction.setDestinationAccountId(request.getDestinationAccountId());
        debitTransaction.setUserId(sourceAccount.getUserId());
        debitTransaction.setDescription(request.getDescription());
        debitTransaction.setStatus(TransactionStatus.COMPLETED);
        debitTransaction.setFeeAmount(feeAmount);
        
        // Create credit transaction (destination account)
        Transaction creditTransaction = new Transaction();
        creditTransaction.setTransactionReference(transactionIdGenerator.generateTransactionId());
        creditTransaction.setTransactionType(TransactionType.TRANSFER);
        creditTransaction.setAmount(request.getAmount());
        creditTransaction.setSourceAccountId(request.getSourceAccountId());
        creditTransaction.setDestinationAccountId(request.getDestinationAccountId());
        creditTransaction.setUserId(destinationAccount.getUserId());
        creditTransaction.setDescription(request.getDescription());
        creditTransaction.setStatus(TransactionStatus.COMPLETED);
        
        // Update account balances
        BigDecimal sourceBalanceAfter = sourceAccount.getBalance().subtract(totalAmount);
        sourceAccount.setBalance(sourceBalanceAfter);
        debitTransaction.setBalanceAfter(sourceBalanceAfter);
        
        BigDecimal destinationBalanceAfter = destinationAccount.getBalance().add(request.getAmount());
        destinationAccount.setBalance(destinationBalanceAfter);
        creditTransaction.setBalanceAfter(destinationBalanceAfter);
        
        // Link transactions
        Transaction savedDebitTransaction = transactionRepository.save(debitTransaction);
        creditTransaction.setRelatedTransactionId(savedDebitTransaction.getTransactionId());
        Transaction savedCreditTransaction = transactionRepository.save(creditTransaction);
        
        // Update accounts
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
        
        logger.info("Transfer completed successfully. Debit Transaction ID: {}, Credit Transaction ID: {}", 
                savedDebitTransaction.getTransactionId(), savedCreditTransaction.getTransactionId());
        
        return convertToResponse(savedDebitTransaction);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<TransactionResponse> getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .map(this::convertToResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<TransactionResponse> getTransactionByReference(String transactionReference) {
        return transactionRepository.findByTransactionReference(transactionReference)
                .map(this::convertToResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByAccountId(Long accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        return transactions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByUserId(Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        return transactions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByUserIdAndStatus(Long userId, String status) {
        TransactionStatus transactionStatus = TransactionStatus.valueOf(status.toUpperCase());
        List<Transaction> transactions = transactionRepository.findByUserIdAndStatus(userId, transactionStatus);
        return transactions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        return transactions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = transactionRepository.findByTransactionDateBetween(startDate, endDate);
        return transactions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);
        return transactions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public TransactionResponse updateTransactionStatus(Long transactionId, String status) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        TransactionStatus newStatus = TransactionStatus.valueOf(status.toUpperCase());
        transaction.setStatus(newStatus);
        
        Transaction updatedTransaction = transactionRepository.save(transaction);
        logger.info("Transaction status updated to {} for transaction ID: {}", newStatus, transactionId);
        
        return convertToResponse(updatedTransaction);
    }
    
    @Override
    public TransactionResponse reverseTransaction(Long transactionId) throws Exception {
        Transaction originalTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (originalTransaction.getStatus() != TransactionStatus.COMPLETED) {
            throw new Exception("Cannot reverse a non-completed transaction");
        }
        
        // Create reversal transaction
        Transaction reversalTransaction = new Transaction();
        reversalTransaction.setTransactionReference(transactionIdGenerator.generateTransactionId());
        reversalTransaction.setTransactionType(TransactionType.REFUND);
        reversalTransaction.setAmount(originalTransaction.getAmount());
        reversalTransaction.setRelatedTransactionId(transactionId);
        reversalTransaction.setUserId(originalTransaction.getUserId());
        reversalTransaction.setDescription("Reversal of transaction: " + originalTransaction.getTransactionReference());
        reversalTransaction.setStatus(TransactionStatus.COMPLETED);
        
        // Handle reversal based on transaction type
        if (originalTransaction.getTransactionType() == TransactionType.DEPOSIT) {
            // Original was a deposit, so we need to withdraw
            reversalTransaction.setSourceAccountId(originalTransaction.getDestinationAccountId());
            
            Account account = accountRepository.findById(originalTransaction.getDestinationAccountId())
                    .orElseThrow(() -> new RuntimeException("Account not found"));
            
            BigDecimal newBalance = account.getBalance().subtract(originalTransaction.getAmount());
            account.setBalance(newBalance);
            reversalTransaction.setBalanceAfter(newBalance);
            accountRepository.save(account);
            
        } else if (originalTransaction.getTransactionType() == TransactionType.WITHDRAWAL) {
            // Original was a withdrawal, so we need to deposit
            reversalTransaction.setDestinationAccountId(originalTransaction.getSourceAccountId());
            
            Account account = accountRepository.findById(originalTransaction.getSourceAccountId())
                    .orElseThrow(() -> new RuntimeException("Account not found"));
            
            BigDecimal newBalance = account.getBalance().add(originalTransaction.getAmount());
            account.setBalance(newBalance);
            reversalTransaction.setBalanceAfter(newBalance);
            accountRepository.save(account);
            
        } else if (originalTransaction.getTransactionType() == TransactionType.TRANSFER) {
            // Original was a transfer, so we need to reverse it
            reversalTransaction.setSourceAccountId(originalTransaction.getDestinationAccountId());
            reversalTransaction.setDestinationAccountId(originalTransaction.getSourceAccountId());
            
            // Reverse source account (give money back)
            Account sourceAccount = accountRepository.findById(originalTransaction.getSourceAccountId())
                    .orElseThrow(() -> new RuntimeException("Source account not found"));
            
            BigDecimal newSourceBalance = sourceAccount.getBalance().add(originalTransaction.getAmount());
            sourceAccount.setBalance(newSourceBalance);
            accountRepository.save(sourceAccount);
            
            // Reverse destination account (take money back)
            Account destinationAccount = accountRepository.findById(originalTransaction.getDestinationAccountId())
                    .orElseThrow(() -> new RuntimeException("Destination account not found"));
            
            BigDecimal newDestinationBalance = destinationAccount.getBalance().subtract(originalTransaction.getAmount());
            destinationAccount.setBalance(newDestinationBalance);
            reversalTransaction.setBalanceAfter(newDestinationBalance);
            accountRepository.save(destinationAccount);
        }
        
        // Update original transaction status
        originalTransaction.setStatus(TransactionStatus.REVERSED);
        transactionRepository.save(originalTransaction);
        
        // Save reversal transaction
        Transaction savedReversalTransaction = transactionRepository.save(reversalTransaction);
        
        logger.info("Transaction reversal completed. Original ID: {}, Reversal ID: {}", 
                transactionId, savedReversalTransaction.getTransactionId());
        
        return convertToResponse(savedReversalTransaction);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalDebits(Long accountId) {
        BigDecimal total = transactionRepository.sumDebitsByAccountIdAndStatus(
                accountId, TransactionStatus.COMPLETED, TransactionType.WITHDRAWAL);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalCredits(Long accountId) {
        BigDecimal total = transactionRepository.sumCreditsByAccountIdAndStatus(
                accountId, TransactionStatus.COMPLETED, TransactionType.DEPOSIT);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getPendingTransactions() {
        List<Transaction> transactions = transactionRepository.findByStatus(TransactionStatus.PENDING);
        return transactions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        transaction.setStatus(TransactionStatus.CANCELLED);
        transactionRepository.save(transaction);
        
        logger.info("Transaction {} marked as cancelled", transactionId);
    }
    
    private BigDecimal calculateTransferFee(BigDecimal amount) {
        // Simple fee calculation: 0.5% of transfer amount, minimum $1, maximum $50
        BigDecimal feePercentage = new BigDecimal("0.005");
        BigDecimal fee = amount.multiply(feePercentage);
        
        BigDecimal minFee = new BigDecimal("1.00");
        BigDecimal maxFee = new BigDecimal("50.00");
        
        if (fee.compareTo(minFee) < 0) {
            return minFee;
        } else if (fee.compareTo(maxFee) > 0) {
            return maxFee;
        } else {
            return fee;
        }
    }
    
    private TransactionResponse convertToResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setTransactionId(transaction.getTransactionId());
        response.setTransactionReference(transaction.getTransactionReference());
        response.setTransactionType(transaction.getTransactionType());
        response.setAmount(transaction.getAmount());
        response.setSourceAccountId(transaction.getSourceAccountId());
        response.setDestinationAccountId(transaction.getDestinationAccountId());
        response.setUserId(transaction.getUserId());
        response.setStatus(transaction.getStatus());
        response.setDescription(transaction.getDescription());
        response.setTransactionDate(transaction.getTransactionDate());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setUpdatedAt(transaction.getUpdatedAt());
        response.setBalanceAfter(transaction.getBalanceAfter());
        response.setFeeAmount(transaction.getFeeAmount());
        response.setRelatedTransactionId(transaction.getRelatedTransactionId());
        return response;
    }
}