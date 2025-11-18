package com.eagle.api.security;

import com.eagle.api.exception.UnauthorizedException;
import com.eagle.api.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    private final UserService userService;

    public CurrentUser(UserService userService) {
        this.userService = userService;
    }

    public String getId() {
        var context = SecurityContextHolder.getContext();
        var auth = context.getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Unauthenticated request");
        }

        String email = auth.getName();
        if (email == null || email.isBlank()) {
            throw new UnauthorizedException("Unauthenticated request");
        }

        String userId = this.userService.getUserIdByEmail(email);
        if (userId == null || userId.isBlank()) {
            throw new UnauthorizedException("User not found");
        }

        return userId;
    }
}

