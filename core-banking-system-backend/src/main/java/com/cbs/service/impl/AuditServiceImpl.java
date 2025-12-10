// src/main/java/com/cbs/service/impl/AuditServiceImpl.java
package com.cbs.service.impl;

import com.cbs.model.entity.AuditLog;
import com.cbs.model.enums.AuditAction;
import com.cbs.repository.AuditLogRepository;
import com.cbs.service.interface_.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public AuditLog logAction(Long userId, AuditAction action, String entityType, Long entityId, String oldValues,
            String newValues, String ipAddress, String userAgent) {
        AuditLog auditLog = new AuditLog(userId, action, entityType, entityId);
        auditLog.setOldValues(oldValues);
        auditLog.setNewValues(newValues);
        auditLog.setIpAddress(ipAddress);
        auditLog.setUserAgent(userAgent);

        return auditLogRepository.save(auditLog);
    }

    @Override
    public AuditLog logAction(Long userId, AuditAction action, String entityType, Long entityId) {
        return logAction(userId, action, entityType, entityId, null, null, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByUserId(Long userId, Pageable pageable) {
        return auditLogRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByAction(AuditAction action, Pageable pageable) {
        return auditLogRepository.findByAction(action, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByEntityType(String entityType, Pageable pageable) {
        return auditLogRepository.findByEntityType(entityType, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByUserIdAndAction(Long userId, AuditAction action, Pageable pageable) {
        return auditLogRepository.findByUserIdAndAction(userId, action, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return auditLogRepository.findByDateRange(startDate, endDate, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate,
            Pageable pageable) {
        return auditLogRepository.findByUserIdAndDateRange(userId, startDate, endDate, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActionsByTypeAndDateRange(AuditAction action, LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.countByActionAndDateRange(action, startDate, endDate);
    }
}