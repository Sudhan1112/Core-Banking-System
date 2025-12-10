// src/test/java/com/cbs/service/AuditServiceImplTest.java
package com.cbs.service;

import com.cbs.model.entity.AuditLog;
import com.cbs.model.enums.AuditAction;
import com.cbs.repository.AuditLogRepository;
import com.cbs.service.impl.AuditServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuditServiceImplTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditServiceImpl auditService;

    private AuditLog testAuditLog;
    private final Long testUserId = 1L;
    private final String testEntityType = "User";
    private final Long testEntityId = 1L;

    @BeforeEach
    void setUp() {
        testAuditLog = new AuditLog(testUserId, AuditAction.LOGIN, testEntityType, testEntityId);
        testAuditLog.setAuditId(1L);
        testAuditLog.setIpAddress("192.168.1.1");
        testAuditLog.setUserAgent("Mozilla/5.0");
        testAuditLog.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testLogAction() {
        // Given
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testAuditLog);

        // When
        AuditLog result = auditService.logAction(testUserId, AuditAction.LOGIN, testEntityType, testEntityId,
                null, null, "192.168.1.1", "Mozilla/5.0");

        // Then
        assertNotNull(result);
        assertEquals(testUserId, result.getUserId());
        assertEquals(AuditAction.LOGIN, result.getAction());
        assertEquals(testEntityType, result.getEntityType());
        assertEquals(testEntityId, result.getEntityId());
        assertEquals("192.168.1.1", result.getIpAddress());
        assertEquals("Mozilla/5.0", result.getUserAgent());
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testLogActionMinimal() {
        // Given
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testAuditLog);

        // When
        AuditLog result = auditService.logAction(testUserId, AuditAction.LOGIN, testEntityType, testEntityId);

        // Then
        assertNotNull(result);
        assertEquals(testUserId, result.getUserId());
        assertEquals(AuditAction.LOGIN, result.getAction());
        assertEquals(testEntityType, result.getEntityType());
        assertEquals(testEntityId, result.getEntityId());
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testGetAuditLogsByUserId() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<AuditLog> auditLogs = Arrays.asList(testAuditLog);
        Page<AuditLog> auditLogPage = new PageImpl<>(auditLogs, pageable, 1);

        when(auditLogRepository.findByUserId(testUserId, pageable)).thenReturn(auditLogPage);

        // When
        Page<AuditLog> result = auditService.getAuditLogsByUserId(testUserId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testUserId, result.getContent().get(0).getUserId());
        verify(auditLogRepository, times(1)).findByUserId(testUserId, pageable);
    }

    @Test
    void testGetAuditLogsByAction() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<AuditLog> auditLogs = Arrays.asList(testAuditLog);
        Page<AuditLog> auditLogPage = new PageImpl<>(auditLogs, pageable, 1);

        when(auditLogRepository.findByAction(AuditAction.LOGIN, pageable)).thenReturn(auditLogPage);

        // When
        Page<AuditLog> result = auditService.getAuditLogsByAction(AuditAction.LOGIN, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(AuditAction.LOGIN, result.getContent().get(0).getAction());
        verify(auditLogRepository, times(1)).findByAction(AuditAction.LOGIN, pageable);
    }

    @Test
    void testGetAuditLogsByEntityType() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<AuditLog> auditLogs = Arrays.asList(testAuditLog);
        Page<AuditLog> auditLogPage = new PageImpl<>(auditLogs, pageable, 1);

        when(auditLogRepository.findByEntityType(testEntityType, pageable)).thenReturn(auditLogPage);

        // When
        Page<AuditLog> result = auditService.getAuditLogsByEntityType(testEntityType, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testEntityType, result.getContent().get(0).getEntityType());
        verify(auditLogRepository, times(1)).findByEntityType(testEntityType, pageable);
    }

    @Test
    void testGetAuditLogsByUserIdAndAction() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<AuditLog> auditLogs = Arrays.asList(testAuditLog);
        Page<AuditLog> auditLogPage = new PageImpl<>(auditLogs, pageable, 1);

        when(auditLogRepository.findByUserIdAndAction(testUserId, AuditAction.LOGIN, pageable))
                .thenReturn(auditLogPage);

        // When
        Page<AuditLog> result = auditService.getAuditLogsByUserIdAndAction(testUserId, AuditAction.LOGIN, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testUserId, result.getContent().get(0).getUserId());
        assertEquals(AuditAction.LOGIN, result.getContent().get(0).getAction());
        verify(auditLogRepository, times(1)).findByUserIdAndAction(testUserId, AuditAction.LOGIN, pageable);
    }

    @Test
    void testGetAuditLogsByDateRange() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10);
        List<AuditLog> auditLogs = Arrays.asList(testAuditLog);
        Page<AuditLog> auditLogPage = new PageImpl<>(auditLogs, pageable, 1);

        when(auditLogRepository.findByDateRange(startDate, endDate, pageable)).thenReturn(auditLogPage);

        // When
        Page<AuditLog> result = auditService.getAuditLogsByDateRange(startDate, endDate, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(auditLogRepository, times(1)).findByDateRange(startDate, endDate, pageable);
    }

    @Test
    void testGetAuditLogsByUserIdAndDateRange() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10);
        List<AuditLog> auditLogs = Arrays.asList(testAuditLog);
        Page<AuditLog> auditLogPage = new PageImpl<>(auditLogs, pageable, 1);

        when(auditLogRepository.findByUserIdAndDateRange(testUserId, startDate, endDate, pageable))
                .thenReturn(auditLogPage);

        // When
        Page<AuditLog> result = auditService.getAuditLogsByUserIdAndDateRange(testUserId, startDate, endDate, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testUserId, result.getContent().get(0).getUserId());
        verify(auditLogRepository, times(1)).findByUserIdAndDateRange(testUserId, startDate, endDate, pageable);
    }

    @Test
    void testCountActionsByTypeAndDateRange() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        long expectedCount = 5L;

        when(auditLogRepository.countByActionAndDateRange(AuditAction.LOGIN, startDate, endDate))
                .thenReturn(expectedCount);

        // When
        long result = auditService.countActionsByTypeAndDateRange(AuditAction.LOGIN, startDate, endDate);

        // Then
        assertEquals(expectedCount, result);
        verify(auditLogRepository, times(1)).countByActionAndDateRange(AuditAction.LOGIN, startDate, endDate);
    }
}