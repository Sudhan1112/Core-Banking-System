package com.cbs.repository;

import com.cbs.model.entity.Account;
import com.cbs.model.enums.AccountStatus;
import com.cbs.model.enums.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AccountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setAccountNumber("SB250001001");
        testAccount.setAccountType(AccountType.SAVINGS);
        testAccount.setBalance(new BigDecimal("1000.00"));
        testAccount.setUserId(1L);
        testAccount.setStatus(AccountStatus.ACTIVE);
        testAccount.setCreatedAt(LocalDateTime.now());
        testAccount.setMinimumBalance(new BigDecimal("1000.00"));
    }

    @Test
    @DisplayName("Should find account by account number")
    void findByAccountNumber_Success() {
        // Arrange
        entityManager.persistAndFlush(testAccount);

        // Act
        Optional<Account> result = accountRepository.findByAccountNumber("SB250001001");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("SB250001001", result.get().getAccountNumber());
        assertEquals(AccountType.SAVINGS, result.get().getAccountType());
    }

    @Test
    @DisplayName("Should return empty when account number not found")
    void findByAccountNumber_NotFound() {
        // Act
        Optional<Account> result = accountRepository.findByAccountNumber("NONEXISTENT");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should find accounts by user ID")
    void findByUserId_Success() {
        // Arrange
        entityManager.persistAndFlush(testAccount);

        // Act
        List<Account> result = accountRepository.findByUserId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("SB250001001", result.get(0).getAccountNumber());
        assertEquals(1L, result.get(0).getUserId());
    }

    @Test
    @DisplayName("Should find accounts by user ID and status")
    void findByUserIdAndStatus_Success() {
        // Arrange
        entityManager.persistAndFlush(testAccount);

        // Act
        List<Account> result = accountRepository.findByUserIdAndStatus(1L, AccountStatus.ACTIVE);

        // Assert
        assertEquals(1, result.size());
        assertEquals("SB250001001", result.get(0).getAccountNumber());
        assertEquals(AccountStatus.ACTIVE, result.get(0).getStatus());
    }

    @Test
    @DisplayName("Should find accounts by account type")
    void findByAccountType_Success() {
        // Arrange
        entityManager.persistAndFlush(testAccount);

        // Act
        List<Account> result = accountRepository.findByAccountType(AccountType.SAVINGS);

        // Assert
        assertEquals(1, result.size());
        assertEquals("SB250001001", result.get(0).getAccountNumber());
        assertEquals(AccountType.SAVINGS, result.get(0).getAccountType());
    }

    @Test
    @DisplayName("Should find accounts by status")
    void findByStatus_Success() {
        // Arrange
        entityManager.persistAndFlush(testAccount);

        // Act
        List<Account> result = accountRepository.findByStatus(AccountStatus.ACTIVE);

        // Assert
        assertEquals(1, result.size());
        assertEquals("SB250001001", result.get(0).getAccountNumber());
        assertEquals(AccountStatus.ACTIVE, result.get(0).getStatus());
    }

    @Test
    @DisplayName("Should check if account exists by number")
    void existsByAccountNumber_True() {
        // Arrange
        entityManager.persistAndFlush(testAccount);

        // Act
        boolean result = accountRepository.existsByAccountNumber("SB250001001");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when account number doesn't exist")
    void existsByAccountNumber_False() {
        // Act
        boolean result = accountRepository.existsByAccountNumber("NONEXISTENT");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should find accounts with balance below minimum")
    void findAccountsWithBalanceBelowMinimum_Success() {
        // Arrange
        testAccount.setMinimumBalance(new BigDecimal("2000.00"));
        entityManager.persistAndFlush(testAccount);

        // Act
        List<Account> result = accountRepository.findAccountsWithBalanceBelowMinimum(new BigDecimal("1500.00"));

        // Assert
        assertEquals(1, result.size());
        assertEquals("SB250001001", result.get(0).getAccountNumber());
    }

    @Test
    @DisplayName("Should find account by user ID, account type, and status")
    void findByUserIdAndAccountTypeAndStatus_Success() {
        // Arrange
        entityManager.persistAndFlush(testAccount);

        // Act
        Optional<Account> result = accountRepository.findByUserIdAndAccountTypeAndStatus(
                1L, AccountType.SAVINGS, AccountStatus.ACTIVE);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("SB250001001", result.get().getAccountNumber());
        assertEquals(1L, result.get().getUserId());
        assertEquals(AccountType.SAVINGS, result.get().getAccountType());
        assertEquals(AccountStatus.ACTIVE, result.get().getStatus());
    }

    @Test
    @DisplayName("Should count accounts by user ID and status")
    void countByUserIdAndStatus_Success() {
        // Arrange
        entityManager.persistAndFlush(testAccount);

        // Act
        long result = accountRepository.countByUserIdAndStatus(1L, AccountStatus.ACTIVE);

        // Assert
        assertEquals(1, result);
    }
}