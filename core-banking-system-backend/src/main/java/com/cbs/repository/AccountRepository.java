package com.cbs.repository;

import com.cbs.model.entity.Account;
import com.cbs.model.enums.AccountStatus;
import com.cbs.model.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    Optional<Account> findByAccountNumber(String accountNumber);
    
    List<Account> findByUserId(Long userId);
    
    List<Account> findByUserIdAndStatus(Long userId, AccountStatus status);
    
    List<Account> findByAccountType(AccountType accountType);
    
    List<Account> findByStatus(AccountStatus status);
    
    boolean existsByAccountNumber(String accountNumber);
    
    @Query("SELECT a FROM Account a WHERE a.balance < :minimumBalance")
    List<Account> findAccountsWithBalanceBelowMinimum(@Param("minimumBalance") BigDecimal minimumBalance);
    
    @Query("SELECT a FROM Account a WHERE a.userId = :userId AND a.accountType = :accountType AND a.status = :status")
    Optional<Account> findByUserIdAndAccountTypeAndStatus(
            @Param("userId") Long userId,
            @Param("accountType") AccountType accountType,
            @Param("status") AccountStatus status);
    
    @Query("SELECT COUNT(a) FROM Account a WHERE a.userId = :userId AND a.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") AccountStatus status);
}