// src/main/java/com/cbs/service/AuditService.java
package com.cbs.service.interface_;

import com.cbs.model.entity.AuditLog;
import com.cbs.model.enums.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface AuditService {
    AuditLog logAction(Long userId, AuditAction action, String entityType, Long entityId, String oldValues,
            String newValues, String ipAddress, String userAgent);

    AuditLog logAction(Long userId, AuditAction action, String entityType, Long entityId);

    Page<AuditLog> getAllAuditLogs(Pageable pageable);

    Page<AuditLog> getAuditLogsByUserId(Long userId, Pageable pageable);

    Page<AuditLog> getAuditLogsByAction(AuditAction action, Pageable pageable);

    Page<AuditLog> getAuditLogsByEntityType(String entityType, Pageable pageable);

    Page<AuditLog> getAuditLogsByUserIdAndAction(Long userId, AuditAction action, Pageable pageable);

    Page<AuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<AuditLog> getAuditLogsByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate,
            Pageable pageable);

    long countActionsByTypeAndDateRange(AuditAction action, LocalDateTime startDate, LocalDateTime endDate);
}