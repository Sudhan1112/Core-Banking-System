package com.cbs.service;

import com.cbs.model.dto.request.AccountCreationRequest;
import com.cbs.model.dto.response.AccountResponse;
import com.cbs.model.entity.Account;
import com.cbs.model.enums.AccountStatus;
import com.cbs.model.enums.AccountType;
import com.cbs.repository.AccountRepository;
import com.cbs.service.impl.AccountServiceImpl;
import com.cbs.util.AccountNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountNumberGenerator accountNumberGenerator;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account testAccount;
    private AccountCreationRequest testAccountRequest;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setAccountId(1L);
        testAccount.setAccountNumber("SB250001001");
        testAccount.setAccountType(AccountType.SAVINGS);
        testAccount.setBalance(new BigDecimal("1000.00"));
        testAccount.setUserId(1L);
        testAccount.setStatus(AccountStatus.ACTIVE);
        testAccount.setCreatedAt(LocalDateTime.now());
        testAccount.setMinimumBalance(new BigDecimal("1000.00"));
    // For withdraw tests, allow leaving zero if needed — set minimum to 0 for test simplicity
    testAccount.setMinimumBalance(new BigDecimal("0.00"));

        testAccountRequest = new AccountCreationRequest();
        testAccountRequest.setAccountType(AccountType.SAVINGS);
        testAccountRequest.setUserId(1L);
        testAccountRequest.setInitialDeposit(new BigDecimal("1000.00"));
        testAccountRequest.setMinimumBalance(new BigDecimal("1000.00"));
    }

    @Test
    @DisplayName("Should create a new account successfully")
    void createAccount_Success() {
        // Arrange
        when(accountNumberGenerator.generateAccountNumber(AccountType.SAVINGS))
                .thenReturn("SB250001001");
        when(accountRepository.existsByAccountNumber("SB250001001"))
                .thenReturn(false);
        when(accountRepository.save(any(Account.class)))
                .thenReturn(testAccount);

        // Act
        AccountResponse result = accountService.createAccount(testAccountRequest);

        // Assert
        assertNotNull(result);
        assertEquals("SB250001001", result.getAccountNumber());
        assertEquals(AccountType.SAVINGS, result.getAccountType());
        assertEquals(new BigDecimal("1000.00"), result.getBalance());
        assertEquals(1L, result.getUserId());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("Should get account by ID successfully")
    void getAccountById_Success() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // Act
        Optional<AccountResponse> result = accountService.getAccountById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("SB250001001", result.get().getAccountNumber());
        verify(accountRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get account by number successfully")
    void getAccountByNumber_Success() {
        // Arrange
        when(accountRepository.findByAccountNumber("SB250001001"))
                .thenReturn(Optional.of(testAccount));

        // Act
        Optional<AccountResponse> result = accountService.getAccountByNumber("SB250001001");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("SB250001001", result.get().getAccountNumber());
        verify(accountRepository).findByAccountNumber("SB250001001");
    }

    @Test
    @DisplayName("Should get accounts by user ID successfully")
    void getAccountsByUserId_Success() {
        // Arrange
        List<Account> accounts = Arrays.asList(testAccount);
        when(accountRepository.findByUserId(1L)).thenReturn(accounts);

        // Act
        List<AccountResponse> result = accountService.getAccountsByUserId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("SB250001001", result.get(0).getAccountNumber());
        verify(accountRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("Should deposit to account successfully")
    void deposit_Success() {
        // Arrange
        Account updatedAccount = new Account();
        updatedAccount.setAccountId(1L);
        updatedAccount.setAccountNumber("SB250001001");
        updatedAccount.setBalance(new BigDecimal("1500.00"));
        updatedAccount.setStatus(AccountStatus.ACTIVE);
        
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

        // Act
        AccountResponse result = accountService.deposit(1L, new BigDecimal("500.00"));

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("1500.00"), result.getBalance());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw exception when depositing negative amount")
    void deposit_NegativeAmount() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.deposit(1L, new BigDecimal("-100.00"))
        );
        assertEquals("Deposit amount must be positive", exception.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Should withdraw from account successfully")
    void withdraw_Success() throws Exception {
        // Arrange
        Account updatedAccount = new Account();
        updatedAccount.setAccountId(1L);
        updatedAccount.setAccountNumber("SB250001001");
        updatedAccount.setBalance(new BigDecimal("500.00"));
        updatedAccount.setStatus(AccountStatus.ACTIVE);
        
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

        // Act
        AccountResponse result = accountService.withdraw(1L, new BigDecimal("500.00"));

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("500.00"), result.getBalance());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw exception when withdrawing negative amount")
    void withdraw_NegativeAmount() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.withdraw(1L, new BigDecimal("-100.00"))
        );
        assertEquals("Withdrawal amount must be positive", exception.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw exception when withdrawing from inactive account")
    void withdraw_InactiveAccount() {
        // Arrange
        testAccount.setStatus(AccountStatus.CLOSED);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> accountService.withdraw(1L, new BigDecimal("100.00"))
        );
        assertEquals("Cannot withdraw from inactive account", exception.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Should update account status successfully")
    void updateAccountStatus_Success() {
        // Arrange
        Account updatedAccount = new Account();
        updatedAccount.setAccountId(1L);
        updatedAccount.setStatus(AccountStatus.FROZEN);
        
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

        // Act
        AccountResponse result = accountService.updateAccountStatus(1L, "FROZEN");

        // Assert
        assertNotNull(result);
        assertEquals(AccountStatus.FROZEN, result.getStatus());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("Should check if account exists by number")
    void existsByAccountNumber_True() {
        // Arrange
        when(accountRepository.existsByAccountNumber("SB250001001")).thenReturn(true);

        // Act
        boolean result = accountService.existsByAccountNumber("SB250001001");

        // Assert
        assertTrue(result);
        verify(accountRepository).existsByAccountNumber("SB250001001");
    }

    @Test
    @DisplayName("Should delete account successfully")
    void deleteAccount_Success() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        accountService.deleteAccount(1L);

        // Assert
        verify(accountRepository).save(testAccount);
        assertEquals(AccountStatus.CLOSED, testAccount.getStatus());
    }
}