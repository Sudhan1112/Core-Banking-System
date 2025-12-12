package com.cbs.aspect;

import com.cbs.model.entity.User;
import com.cbs.service.ErrorService;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class ErrorLoggingAspect {

    private final ErrorService errorService;

    @Autowired
    public ErrorLoggingAspect(ErrorService errorService) {
        this.errorService = errorService;
    }

    @AfterThrowing(pointcut = "execution(* com.cbs.controller..*(..)) || execution(* com.cbs.service..*(..))", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        try {
            // Get current user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = null;
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                userId = ((User) authentication.getPrincipal()).getUserId();
            }

            // Get request details
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String requestUrl = request.getRequestURI();
            String requestMethod = request.getMethod();
            String ipAddress = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");

            // Log the error
            errorService.logError(ex, userId, requestUrl, requestMethod, ipAddress, userAgent);

        } catch (Exception e) {
            // Fallback to prevent infinite recursion if logging itself fails
            System.err.println("Failed to log error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}