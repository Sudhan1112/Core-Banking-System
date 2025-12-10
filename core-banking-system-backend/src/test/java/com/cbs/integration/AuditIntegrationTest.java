// src/test/java/com/cbs/integration/AuditIntegrationTest.java
package com.cbs.integration;

import com.cbs.model.entity.AuditLog;
import com.cbs.model.enums.AuditAction;
import com.cbs.repository.AuditLogRepository;
import com.cbs.service.interface_.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuditIntegrationTest {

    @Autowired
    private AuditService auditService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    private final Long testUserId = 1L;
    private final String testEntityType = "User";
    private final Long testEntityId = 1L;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        auditLogRepository.deleteAll();
    }

    @Test
    void testLogAndRetrieveAuditLog() {
        // Given
        AuditLog createdLog = auditService.logAction(testUserId, AuditAction.LOGIN, testEntityType, testEntityId,
                null, null, "192.168.1.1", "Mozilla/5.0");

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<AuditLog> retrievedLogs = auditService.getAuditLogsByUserId(testUserId, pageable);

        // Then
        assertNotNull(createdLog);
        assertNotNull(createdLog.getAuditId());
        assertEquals(1, retrievedLogs.getContent().size());
        assertEquals(testUserId, retrievedLogs.getContent().get(0).getUserId());
        assertEquals(AuditAction.LOGIN, retrievedLogs.getContent().get(0).getAction());
    }

    @Test
    void testLogMultipleActionsAndRetrieveByAction() {
        // Given
        auditService.logAction(testUserId, AuditAction.LOGIN, testEntityType, testEntityId);
        auditService.logAction(testUserId, AuditAction.LOGIN, testEntityType, testEntityId);
        auditService.logAction(testUserId, AuditAction.LOGOUT, testEntityType, testEntityId);

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<AuditLog> loginLogs = auditService.getAuditLogsByAction(AuditAction.LOGIN, pageable);
        Page<AuditLog> logoutLogs = auditService.getAuditLogsByAction(AuditAction.LOGOUT, pageable);

        // Then
        assertEquals(2, loginLogs.getContent().size());
        assertEquals(1, logoutLogs.getContent().size());
    }

    @Test
    void testLogMultipleEntityTypesAndRetrieveByEntityType() {
        // Given
        auditService.logAction(testUserId, AuditAction.CREATE, "User", testEntityId);
        auditService.logAction(testUserId, AuditAction.CREATE, "Account", 2L);
        auditService.logAction(testUserId, AuditAction.UPDATE, "User", testEntityId);

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<AuditLog> userLogs = auditService.getAuditLogsByEntityType("User", pageable);
        Page<AuditLog> accountLogs = auditService.getAuditLogsByEntityType("Account", pageable);

        // Then
        assertEquals(2, userLogs.getContent().size());
        assertEquals(1, accountLogs.getContent().size());
    }

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Test
    void testLogActionsAndRetrieveByDateRange() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime twoDaysAgo = now.minusDays(2);

        // Use direct SQL to insert logs with specific timestamps to bypass @CreatedDate
        // and updatable=false
        String sql = "INSERT INTO audit_logs (user_id, action, entity_type, entity_id, created_at) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql, testUserId, AuditAction.LOGIN.name(), testEntityType, testEntityId, yesterday);
        jdbcTemplate.update(sql, testUserId, AuditAction.LOGIN.name(), testEntityType, testEntityId, twoDaysAgo);

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<AuditLog> logsFromYesterday = auditService.getAuditLogsByDateRange(yesterday.minusHours(1),
                yesterday.plusHours(1), pageable);
        Page<AuditLog> logsFromTwoDays = auditService.getAuditLogsByDateRange(twoDaysAgo.minusHours(1),
                twoDaysAgo.plusHours(1), pageable);

        // Then
        assertEquals(1, logsFromYesterday.getContent().size());
        assertEquals(1, logsFromTwoDays.getContent().size());
    }

    @Test
    void testCountActionsByTypeAndDateRange() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);

        // Insert current logs via service (normal flow)
        auditService.logAction(testUserId, AuditAction.LOGIN, testEntityType, testEntityId);
        auditService.logAction(testUserId, AuditAction.LOGIN, testEntityType, testEntityId);

        // Insert old log via SQL
        String sql = "INSERT INTO audit_logs (user_id, action, entity_type, entity_id, created_at) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, testUserId, AuditAction.LOGOUT.name(), testEntityType, testEntityId, yesterday);

        // When
        long loginCount = auditService.countActionsByTypeAndDateRange(AuditAction.LOGIN, now.minusHours(1),
                now.plusHours(1));
        long logoutCount = auditService.countActionsByTypeAndDateRange(AuditAction.LOGOUT, yesterday.minusHours(1),
                yesterday.plusHours(1));

        // Then
        assertEquals(2, loginCount);
        assertEquals(1, logoutCount);
    }
}