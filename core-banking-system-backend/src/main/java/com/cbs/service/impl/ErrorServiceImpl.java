package com.cbs.service.impl;

import com.cbs.model.entity.ErrorLog;
import com.cbs.model.enums.ErrorLevel;
import com.cbs.repository.ErrorLogRepository;
import com.cbs.service.ErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@Service
@Transactional
public class ErrorServiceImpl implements ErrorService {

    private final ErrorLogRepository errorLogRepository;

    @Autowired
    public ErrorServiceImpl(ErrorLogRepository errorLogRepository) {
        this.errorLogRepository = errorLogRepository;
    }

    @Override
    public ErrorLog logError(ErrorLevel level, String message, String stackTrace, Long userId, String requestUrl,
            String requestMethod, String ipAddress, String userAgent) {
        ErrorLog errorLog = new ErrorLog(level, message, stackTrace);
        errorLog.setUserId(userId);
        errorLog.setRequestUrl(requestUrl);
        errorLog.setRequestMethod(requestMethod);
        errorLog.setIpAddress(ipAddress);
        errorLog.setUserAgent(userAgent);
        return errorLogRepository.save(errorLog);
    }

    @Override
    public ErrorLog logError(Exception ex, Long userId, String requestUrl, String requestMethod, String ipAddress,
            String userAgent) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String stackTrace = sw.toString();

        // Determine error level based on exception type
        ErrorLevel level = ErrorLevel.ERROR;
        if (ex instanceof RuntimeException) {
            level = ErrorLevel.FATAL;
        }

        String message = ex.getMessage();
        if (message == null || message.trim().isEmpty()) {
            message = ex.getClass().getName();
        }

        return logError(level, message, stackTrace, userId, requestUrl, requestMethod, ipAddress, userAgent);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ErrorLog> getErrorLogs(Pageable pageable) {
        return errorLogRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ErrorLog> getErrorLogsByLevel(ErrorLevel errorLevel, Pageable pageable) {
        return errorLogRepository.findByErrorLevel(errorLevel, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ErrorLog> getErrorLogsByUser(Long userId, Pageable pageable) {
        return errorLogRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ErrorLog> getErrorLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return errorLogRepository.findByDateRange(startDate, endDate, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long countErrorsByLevelAndDateRange(ErrorLevel errorLevel, LocalDateTime startDate, LocalDateTime endDate) {
        return errorLogRepository.countByErrorLevelAndDateRange(errorLevel, startDate, endDate);
    }
}