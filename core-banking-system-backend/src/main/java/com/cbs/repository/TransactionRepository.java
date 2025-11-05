package com.cbs.repository;

import com.cbs.model.entity.Transaction;
import com.cbs.model.enums.TransactionStatus;
import com.cbs.model.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Optional<Transaction> findByTransactionReference(String transactionReference);
    
    List<Transaction> findBySourceAccountId(Long sourceAccountId);
    
    List<Transaction> findByDestinationAccountId(Long destinationAccountId);
    
    List<Transaction> findByUserId(Long userId);
    
    List<Transaction> findByUserIdAndStatus(Long userId, TransactionStatus status);
    
    List<Transaction> findByStatus(TransactionStatus status);
    
    List<Transaction> findByTransactionType(TransactionType transactionType);
    
    @Query("SELECT t FROM Transaction t WHERE t.sourceAccountId = :accountId OR t.destinationAccountId = :accountId")
    List<Transaction> findByAccountId(@Param("accountId") Long accountId);
    
    @Query("SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findByTransactionDateBetween(@Param("startDate") LocalDateTime startDate, 
                                                   @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findByUserIdAndTransactionDateBetween(@Param("userId") Long userId,
                                                           @Param("startDate") LocalDateTime startDate,
                                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.sourceAccountId = :accountId AND t.status = :status AND t.transactionType = :type")
    BigDecimal sumDebitsByAccountIdAndStatus(@Param("accountId") Long accountId,
                                           @Param("status") TransactionStatus status,
                                           @Param("type") TransactionType type);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.destinationAccountId = :accountId AND t.status = :status AND t.transactionType = :type")
    BigDecimal sumCreditsByAccountIdAndStatus(@Param("accountId") Long accountId,
                                             @Param("status") TransactionStatus status,
                                             @Param("type") TransactionType type);
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.status = :status")
    long countByStatus(@Param("status") TransactionStatus status);
    
    @Query("SELECT t FROM Transaction t WHERE t.relatedTransactionId = :transactionId")
    List<Transaction> findByRelatedTransactionId(@Param("transactionId") Long transactionId);
}