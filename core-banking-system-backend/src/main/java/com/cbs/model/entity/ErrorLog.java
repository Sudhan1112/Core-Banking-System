package com.cbs.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.cbs.model.enums.ErrorLevel;

import java.time.LocalDateTime;

@Entity
@Table(name = "error_logs")
@EntityListeners(AuditingEntityListener.class)
public class ErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long errorId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "error_level", nullable = false)
    private ErrorLevel errorLevel;

    @NotBlank(message = "Error message is required")
    @Column(name = "error_message", nullable = false, columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "stack_trace", columnDefinition = "LONGTEXT")
    private String stackTrace;

    @Column(name = "user_id")
    private Long userId; // Can be null if the error occurs outside a user context

    @Column(name = "request_url")
    private String requestUrl;

    @Column(name = "request_method")
    private String requestMethod;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public ErrorLog() {
    }

    public ErrorLog(ErrorLevel errorLevel, String errorMessage, String stackTrace) {
        this.errorLevel = errorLevel;
        this.errorMessage = errorMessage;
        this.stackTrace = stackTrace;
    }

    // Getters and Setters
    public Long getErrorId() {
        return errorId;
    }

    public void setErrorId(Long errorId) {
        this.errorId = errorId;
    }

    public ErrorLevel getErrorLevel() {
        return errorLevel;
    }

    public void setErrorLevel(ErrorLevel errorLevel) {
        this.errorLevel = errorLevel;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}