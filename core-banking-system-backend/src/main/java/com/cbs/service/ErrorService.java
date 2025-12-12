package com.cbs.service;

import com.cbs.model.entity.ErrorLog;
import com.cbs.model.enums.ErrorLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ErrorService {

    ErrorLog logError(ErrorLevel level, String message, String stackTrace, Long userId, String requestUrl, String requestMethod, String ipAddress, String userAgent);

    ErrorLog logError(Exception ex, Long userId, String requestUrl, String requestMethod, String ipAddress, String userAgent);

    Page<ErrorLog> getErrorLogs(Pageable pageable);

    Page<ErrorLog> getErrorLogsByLevel(ErrorLevel errorLevel, Pageable pageable);

    Page<ErrorLog> getErrorLogsByUser(Long userId, Pageable pageable);

    Page<ErrorLog> getErrorLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    long countErrorsByLevelAndDateRange(ErrorLevel errorLevel, LocalDateTime startDate, LocalDateTime endDate);
}
