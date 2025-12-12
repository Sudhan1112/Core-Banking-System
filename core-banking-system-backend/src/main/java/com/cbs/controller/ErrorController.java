package com.cbs.controller;

import com.cbs.model.entity.ErrorLog;
import com.cbs.model.enums.ErrorLevel;
import com.cbs.model.dto.response.ApiResponse;
import com.cbs.service.ErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/errors")
public class ErrorController {

    private final ErrorService errorService;

    @Autowired
    public ErrorController(ErrorService errorService) {
        this.errorService = errorService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ErrorLog>>> getAllErrorLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<ErrorLog> errorLogs = errorService.getErrorLogs(pageable);

        return ResponseEntity.ok(new ApiResponse<>(true, "Error logs retrieved successfully", errorLogs));
    }

    @GetMapping("/level/{level}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ErrorLog>>> getErrorLogsByLevel(
            @PathVariable ErrorLevel level,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ErrorLog> errorLogs = errorService.getErrorLogsByLevel(level, pageable);

        return ResponseEntity
                .ok(new ApiResponse<>(true, "Error logs for level " + level + " retrieved successfully", errorLogs));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<ApiResponse<Page<ErrorLog>>> getErrorLogsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ErrorLog> errorLogs = errorService.getErrorLogsByUser(userId, pageable);

        return ResponseEntity
                .ok(new ApiResponse<>(true, "Error logs for user " + userId + " retrieved successfully", errorLogs));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getErrorStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        Map<String, Long> errorStats = new HashMap<>();
        for (ErrorLevel level : ErrorLevel.values()) {
            long count = errorService.countErrorsByLevelAndDateRange(level, startDate, endDate);
            errorStats.put(level.name(), count);
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Error statistics retrieved successfully", errorStats));
    }
}