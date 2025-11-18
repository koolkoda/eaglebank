package com.eagle.api.controller;

import com.eagle.api.dto.CreateUserRequest;
import com.eagle.api.dto.UpdateUserRequest;
import com.eagle.api.dto.UserResponse;
import com.eagle.api.exception.DeleteException;
import com.eagle.api.exception.ForbiddenException;
import com.eagle.api.service.AccountService;
import com.eagle.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
public class UserController {
    private final UserService userService;
    private final AccountService accountService;

    public UserController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest req) {
        UserResponse created = userService.createUser(req);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> fetchUser(@PathVariable String userId) {
        verifyUser(userId);

        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String userId, @Valid @RequestBody UpdateUserRequest req) {
        verifyUser(userId);

        return ResponseEntity.ok(userService.updateUser(userId, req));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        verifyUser(userId);

        if(!accountService.listAccounts(userId).getAccounts().isEmpty()) {
            throw new DeleteException("User has associated bank accounts and cannot be deleted");
        }
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    private void verifyUser(String userId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        var currentUserId = this.userService.getUserIdByEmail(email);

        if (!userId.equals(currentUserId)) {
            throw new ForbiddenException("Access denied");
        }
    }
}
