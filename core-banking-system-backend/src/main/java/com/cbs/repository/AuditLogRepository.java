// src/main/java/com/cbs/repository/AuditLogRepository.java
package com.cbs.repository;

import com.cbs.model.entity.AuditLog;
import com.cbs.model.enums.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findByUserId(Long userId, Pageable pageable);
    
    Page<AuditLog> findByAction(AuditAction action, Pageable pageable);
    
    Page<AuditLog> findByEntityType(String entityType, Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId AND a.action = :action")
    Page<AuditLog> findByUserIdAndAction(@Param("userId") Long userId, @Param("action") AuditAction action, Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    Page<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId AND a.createdAt BETWEEN :startDate AND :endDate")
    Page<AuditLog> findByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.action = :action AND a.createdAt BETWEEN :startDate AND :endDate")
    long countByActionAndDateRange(@Param("action") AuditAction action, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}