package com.cbs.service.interface_;

import com.cbs.model.dto.request.AccountCreationRequest;
import com.cbs.model.dto.response.AccountResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountService {
    
    AccountResponse createAccount(AccountCreationRequest request);
    
    Optional<AccountResponse> getAccountById(Long accountId);
    
    Optional<AccountResponse> getAccountByNumber(String accountNumber);
    
    List<AccountResponse> getAccountsByUserId(Long userId);
    
    List<AccountResponse> getAccountsByUserIdAndStatus(Long userId, String status);
    
    List<AccountResponse> getAllAccounts();
    
    AccountResponse updateAccountStatus(Long accountId, String status);
    
    AccountResponse deposit(Long accountId, BigDecimal amount);
    
    AccountResponse withdraw(Long accountId, BigDecimal amount) throws Exception;
    
    List<AccountResponse> getAccountsWithLowBalance();
    
    boolean existsByAccountNumber(String accountNumber);
    
    void deleteAccount(Long accountId);
}