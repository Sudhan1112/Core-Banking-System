package com.cbs.security;

import com.cbs.model.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            Long currentUserId = ((User) principal).getUserId();
            return currentUserId != null && currentUserId.equals(userId);
        }

        return false;
    }
}
