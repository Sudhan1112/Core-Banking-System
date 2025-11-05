package com.cbs.repository;

import com.cbs.model.entity.Transaction;
import com.cbs.model.enums.TransactionStatus;
import com.cbs.model.enums.TransactionType;
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
class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testTransaction = new Transaction();
        testTransaction.setTransactionReference("TXN2025012612345678901234");
        testTransaction.setTransactionType(TransactionType.DEPOSIT);
        testTransaction.setAmount(new BigDecimal("1000.00"));
        testTransaction.setDestinationAccountId(1L);
        testTransaction.setUserId(1L);
        testTransaction.setStatus(TransactionStatus.COMPLETED);
        testTransaction.setTransactionDate(LocalDateTime.now());
        testTransaction.setBalanceAfter(new BigDecimal("2000.00"));
    }

    @Test
    @DisplayName("Should find transaction by reference")
    void findByTransactionReference_Success() {
        // Arrange
        entityManager.persistAndFlush(testTransaction);

        // Act
        Optional<Transaction> result = transactionRepository.findByTransactionReference("TXN2025012612345678901234");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("TXN2025012612345678901234", result.get().getTransactionReference());
        assertEquals(TransactionType.DEPOSIT, result.get().getTransactionType());
    }

    @Test
    @DisplayName("Should return empty when transaction reference not found")
    void findByTransactionReference_NotFound() {
        // Act
        Optional<Transaction> result = transactionRepository.findByTransactionReference("NONEXISTENT");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should find transactions by source account ID")
    void findBySourceAccountId_Success() {
        // Arrange
        testTransaction.setSourceAccountId(1L);
        entityManager.persistAndFlush(testTransaction);

        // Act
        List<Transaction> result = transactionRepository.findBySourceAccountId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getSourceAccountId());
    }

    @Test
    @DisplayName("Should find transactions by destination account ID")
    void findByDestinationAccountId_Success() {
        // Arrange
        entityManager.persistAndFlush(testTransaction);

        // Act
        List<Transaction> result = transactionRepository.findByDestinationAccountId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getDestinationAccountId());
    }

    @Test
    @DisplayName("Should find transactions by user ID")
    void findByUserId_Success() {
        // Arrange
        entityManager.persistAndFlush(testTransaction);

        // Act
        List<Transaction> result = transactionRepository.findByUserId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUserId());
    }

    @Test
    @DisplayName("Should find transactions by user ID and status")
    void findByUserIdAndStatus_Success() {
        // Arrange
        entityManager.persistAndFlush(testTransaction);

        // Act
        List<Transaction> result = transactionRepository.findByUserIdAndStatus(1L, TransactionStatus.COMPLETED);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUserId());
        assertEquals(TransactionStatus.COMPLETED, result.get(0).getStatus());
    }

    @Test
    @DisplayName("Should find transactions by status")
    void findByStatus_Success() {
        // Arrange
        entityManager.persistAndFlush(testTransaction);

        // Act
        List<Transaction> result = transactionRepository.findByStatus(TransactionStatus.COMPLETED);

        // Assert
        assertEquals(1, result.size());
        assertEquals(TransactionStatus.COMPLETED, result.get(0).getStatus());
    }

    @Test
    @DisplayName("Should find transactions by transaction type")
    void findByTransactionType_Success() {
        // Arrange
        entityManager.persistAndFlush(testTransaction);

        // Act
        List<Transaction> result = transactionRepository.findByTransactionType(TransactionType.DEPOSIT);

        // Assert
        assertEquals(1, result.size());
        assertEquals(TransactionType.DEPOSIT, result.get(0).getTransactionType());
    }

    @Test
    @DisplayName("Should find transactions by account ID (source or destination)")
    void findByAccountId_Success() {
        // Arrange
        testTransaction.setSourceAccountId(1L);
        entityManager.persistAndFlush(testTransaction);

        // Act
        List<Transaction> result = transactionRepository.findByAccountId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getSourceAccountId());
    }

    @Test
    @DisplayName("Should find transactions by date range")
    void findByTransactionDateBetween_Success() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        entityManager.persistAndFlush(testTransaction);

        // Act
        List<Transaction> result = transactionRepository.findByTransactionDateBetween(startDate, endDate);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).getTransactionDate().isAfter(startDate.minusSeconds(1)));
        assertTrue(result.get(0).getTransactionDate().isBefore(endDate.plusSeconds(1)));
    }

    @Test
    @DisplayName("Should sum debits by account ID and status")
    void sumDebitsByAccountIdAndStatus_Success() {
        // Arrange
        testTransaction.setTransactionType(TransactionType.WITHDRAWAL);
        testTransaction.setSourceAccountId(1L);
        testTransaction.setStatus(TransactionStatus.COMPLETED);
        entityManager.persistAndFlush(testTransaction);

        // Act
        BigDecimal result = transactionRepository.sumDebitsByAccountIdAndStatus(
                1L, TransactionStatus.COMPLETED, TransactionType.WITHDRAWAL);

        // Assert
        assertEquals(0, new BigDecimal("1000.00").compareTo(result));
    }

    @Test
    @DisplayName("Should sum credits by account ID and status")
    void sumCreditsByAccountIdAndStatus_Success() {
        // Arrange
        entityManager.persistAndFlush(testTransaction);

        // Act
        BigDecimal result = transactionRepository.sumCreditsByAccountIdAndStatus(
                1L, TransactionStatus.COMPLETED, TransactionType.DEPOSIT);

        // Assert
        assertEquals(0, new BigDecimal("1000.00").compareTo(result));
    }

    @Test
    @DisplayName("Should count transactions by status")
    void countByStatus_Success() {
        // Arrange
        entityManager.persistAndFlush(testTransaction);

        // Act
        long result = transactionRepository.countByStatus(TransactionStatus.COMPLETED);

        // Assert
        assertEquals(1, result);
    }

    @Test
    @DisplayName("Should find transactions by related transaction ID")
    void findByRelatedTransactionId_Success() {
        // Arrange
        Transaction relatedTransaction = new Transaction();
        relatedTransaction.setTransactionReference("TXN2025012612345678901235");
        relatedTransaction.setTransactionType(TransactionType.REFUND);
        relatedTransaction.setAmount(new BigDecimal("1000.00"));
        relatedTransaction.setRelatedTransactionId(1L);
        relatedTransaction.setUserId(1L);
        relatedTransaction.setStatus(TransactionStatus.COMPLETED);
        
        entityManager.persistAndFlush(testTransaction);
        entityManager.persistAndFlush(relatedTransaction);

        // Act
        List<Transaction> result = transactionRepository.findByRelatedTransactionId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getRelatedTransactionId());
        assertEquals(TransactionType.REFUND, result.get(0).getTransactionType());
    }
}