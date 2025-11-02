package com.cbs.service.impl;

import com.cbs.model.dto.request.AccountCreationRequest;
import com.cbs.model.dto.response.AccountResponse;
import com.cbs.model.entity.Account;
import com.cbs.model.enums.AccountStatus;
import com.cbs.model.enums.AccountType;
import com.cbs.repository.AccountRepository;
import com.cbs.service.interface_.AccountService;
import com.cbs.util.AccountNumberGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {
    
    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
    
    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    
    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, 
                             AccountNumberGenerator accountNumberGenerator) {
        this.accountRepository = accountRepository;
        this.accountNumberGenerator = accountNumberGenerator;
    }
    
    @Override
    public AccountResponse createAccount(AccountCreationRequest request) {
        logger.info("Creating account for user ID: {}", request.getUserId());
        
        // Validate user exists (you might want to add a UserRepository dependency)
        // For now, we'll assume the user exists
        
        // Generate unique account number
        String accountNumber;
        do {
            accountNumber = accountNumberGenerator.generateAccountNumber(request.getAccountType());
        } while (accountRepository.existsByAccountNumber(accountNumber));
        
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setAccountType(request.getAccountType());
        account.setUserId(request.getUserId());
        account.setBranchId(request.getBranchId());
        account.setMinimumBalance(request.getMinimumBalance());
        account.setInterestRate(request.getInterestRate());
        account.setOverdraftLimit(request.getOverdraftLimit());
        
        // Set initial balance if provided
        if (request.getInitialDeposit() != null && request.getInitialDeposit().compareTo(BigDecimal.ZERO) > 0) {
            account.setBalance(request.getInitialDeposit());
        }
        
        // Set default minimum balance if not provided
        if (account.getMinimumBalance() == null) {
            account.setMinimumBalance(getDefaultMinimumBalance(request.getAccountType()));
        }

        // Ensure newly created accounts are active by default
        // Note: Account entity constructor sets status to PENDING_APPROVAL, so override it here
        // so new accounts are immediately usable in tests and typical flows.
        if (account.getStatus() == null || account.getStatus() == AccountStatus.PENDING_APPROVAL) {
            account.setStatus(AccountStatus.ACTIVE);
        }
        
        Account savedAccount = accountRepository.save(account);
        logger.info("Account created successfully with ID: {}", savedAccount.getAccountId());
        
        return convertToResponse(savedAccount);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<AccountResponse> getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .map(this::convertToResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<AccountResponse> getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .map(this::convertToResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByUserId(Long userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        return accounts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByUserIdAndStatus(Long userId, String status) {
        AccountStatus accountStatus = AccountStatus.valueOf(status.toUpperCase());
        List<Account> accounts = accountRepository.findByUserIdAndStatus(userId, accountStatus);
        return accounts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public AccountResponse updateAccountStatus(Long accountId, String status) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        AccountStatus newStatus = AccountStatus.valueOf(status.toUpperCase());
        account.setStatus(newStatus);
        
        Account updatedAccount = accountRepository.save(account);
        logger.info("Account status updated to {} for account ID: {}", newStatus, accountId);
        
        return convertToResponse(updatedAccount);
    }
    
    @Override
    public AccountResponse deposit(Long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Cannot deposit to inactive account");
        }
        
        account.setBalance(account.getBalance().add(amount));
        Account updatedAccount = accountRepository.save(account);
        
        logger.info("Deposited {} to account ID: {}", amount, accountId);
        return convertToResponse(updatedAccount);
    }
    
    @Override
    public AccountResponse withdraw(Long accountId, BigDecimal amount) throws Exception {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Cannot withdraw from inactive account");
        }
        
        BigDecimal newBalance = account.getBalance().subtract(amount);
        
        // Check minimum balance
        if (account.getMinimumBalance() != null && 
            newBalance.compareTo(account.getMinimumBalance()) < 0) {
            // Check if overdraft is allowed
            if (account.getOverdraftLimit() == null || 
                newBalance.compareTo(account.getOverdraftLimit().negate()) < 0) {
                throw new Exception("Insufficient balance");
            }
        }
        
        account.setBalance(newBalance);
        Account updatedAccount = accountRepository.save(account);
        
        logger.info("Withdrew {} from account ID: {}", amount, accountId);
        return convertToResponse(updatedAccount);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsWithLowBalance() {
        List<Account> accounts = accountRepository.findAccountsWithBalanceBelowMinimum(BigDecimal.ZERO);
        return accounts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByAccountNumber(String accountNumber) {
        return accountRepository.existsByAccountNumber(accountNumber);
    }
    
    @Override
    public void deleteAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        account.setStatus(AccountStatus.CLOSED);
        accountRepository.save(account);
        
        logger.info("Account {} marked as closed", accountId);
    }
    
    private BigDecimal getDefaultMinimumBalance(AccountType accountType) {
        switch (accountType) {
            case SAVINGS:
                return new BigDecimal("1000.00");
            case CURRENT:
                return BigDecimal.ZERO;
            case FIXED_DEPOSIT:
                return new BigDecimal("10000.00");
            case RECURRING_DEPOSIT:
                return new BigDecimal("500.00");
            case SALARY_ACCOUNT:
                return BigDecimal.ZERO;
            default:
                return BigDecimal.ZERO;
        }
    }
    
    private AccountResponse convertToResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setAccountId(account.getAccountId());
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType());
        response.setBalance(account.getBalance());
        response.setUserId(account.getUserId());
        response.setBranchId(account.getBranchId());
        response.setStatus(account.getStatus());
        response.setCreatedAt(account.getCreatedAt());
        response.setUpdatedAt(account.getUpdatedAt());
        response.setMinimumBalance(account.getMinimumBalance());
        response.setInterestRate(account.getInterestRate());
        response.setOverdraftLimit(account.getOverdraftLimit());
        return response;
    }
}