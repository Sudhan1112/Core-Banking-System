// src/main/java/com/cbs/aspect/AuditAspect.java
package com.cbs.aspect;

import com.cbs.model.entity.User;
import com.cbs.service.interface_.AuditService;
import com.cbs.model.enums.AuditAction;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuditAspect {

    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    @Autowired
    public AuditAspect(AuditService auditService, ObjectMapper objectMapper) {
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(com.cbs.annotation.Auditable)")
    public Object auditAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            userId = ((User) authentication.getPrincipal()).getUserId();
        }

        // Get request details
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        // Extract entity information
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String entityType = className.replace("Controller", "").replace("Service", "");

        // Extract entity ID if available
        Long entityId = null;
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            // Try to extract ID from first argument
            try {
                if (args[0] instanceof Long) {
                    entityId = (Long) args[0];
                } else if (args[0] != null) {
                    // Try to get ID via reflection
                    entityId = (Long) args[0].getClass().getMethod("getId").invoke(args[0]);
                }
            } catch (Exception e) {
                // Ignore if ID extraction fails
            }
        }

        // Determine action based on method name
        AuditAction action = determineAction(methodName);

        // Execute the method
        Object result = null;
        String oldValues = null;
        String newValues = null;

        try {
            // For update operations, try to capture old values
            if (action == AuditAction.UPDATE && args.length > 0 && args[0] != null) {
                try {
                    oldValues = objectMapper.writeValueAsString(args[0]);
                } catch (Exception e) {
                    // Ignore if serialization fails
                }
            }

            // Proceed with the method execution
            result = joinPoint.proceed();

            // For create/update operations, try to capture new values
            if ((action == AuditAction.CREATE || action == AuditAction.UPDATE) && result != null) {
                try {
                    newValues = objectMapper.writeValueAsString(result);
                } catch (Exception e) {
                    // Ignore if serialization fails
                }
            }

            // Log the successful action
            auditService.logAction(userId, action, entityType, entityId, oldValues, newValues, ipAddress, userAgent);

            return result;
        } catch (Exception e) {
            // Log the failed action
            auditService.logAction(userId, action, entityType, entityId, oldValues, "Error: " + e.getMessage(),
                    ipAddress, userAgent);
            throw e;
        }
    }

    private AuditAction determineAction(String methodName) {
        if (methodName.startsWith("create") || methodName.startsWith("add") || methodName.startsWith("save")) {
            return AuditAction.CREATE;
        } else if (methodName.startsWith("update") || methodName.startsWith("modify")
                || methodName.startsWith("change")) {
            return AuditAction.UPDATE;
        } else if (methodName.startsWith("delete") || methodName.startsWith("remove")) {
            return AuditAction.DELETE;
        } else if (methodName.startsWith("get") || methodName.startsWith("find") || methodName.startsWith("retrieve")) {
            return AuditAction.READ;
        } else if (methodName.startsWith("login") || methodName.startsWith("authenticate")) {
            return AuditAction.LOGIN;
        } else if (methodName.startsWith("logout")) {
            return AuditAction.LOGOUT;
        } else if (methodName.startsWith("register") || methodName.startsWith("signup")) {
            return AuditAction.REGISTER;
        } else if (methodName.startsWith("deposit")) {
            return AuditAction.DEPOSIT;
        } else if (methodName.startsWith("withdraw")) {
            return AuditAction.WITHDRAW;
        } else if (methodName.startsWith("transfer")) {
            return AuditAction.TRANSFER;
        } else if (methodName.startsWith("apply") && methodName.contains("loan")) {
            return AuditAction.LOAN_APPLICATION;
        } else if (methodName.startsWith("approve") && methodName.contains("loan")) {
            return AuditAction.LOAN_APPROVAL;
        } else if (methodName.startsWith("reject") && methodName.contains("loan")) {
            return AuditAction.LOAN_REJECTION;
        } else if (methodName.startsWith("upload") && methodName.contains("kyc")) {
            return AuditAction.KYC_UPLOAD;
        } else if (methodName.startsWith("approve") && methodName.contains("kyc")) {
            return AuditAction.KYC_APPROVAL;
        } else if (methodName.startsWith("reject") && methodName.contains("kyc")) {
            return AuditAction.KYC_REJECTION;
        } else if (methodName.startsWith("change") && methodName.contains("password")) {
            return AuditAction.PASSWORD_CHANGE;
        } else if (methodName.startsWith("lock") && methodName.contains("account")) {
            return AuditAction.ACCOUNT_LOCK;
        } else if (methodName.startsWith("unlock") && methodName.contains("account")) {
            return AuditAction.ACCOUNT_UNLOCK;
        } else {
            return AuditAction.READ; // Default to READ for unknown methods
        }
    }
}