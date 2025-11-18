package com.eagle.api.controller;

import com.eagle.api.dto.CreateUserRequest;
import com.eagle.api.dto.ListBankAccountsResponse;
import com.eagle.api.dto.UpdateUserRequest;
import com.eagle.api.dto.UserResponse;
import com.eagle.api.exception.DeleteException;
import com.eagle.api.exception.ForbiddenException;
import com.eagle.api.exception.UserNotFoundException;
import com.eagle.api.security.CurrentUser;
import com.eagle.api.service.AccountService;
import com.eagle.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
public class UserController {
    private final UserService userService;
    private final AccountService accountService;
    private final CurrentUser currentUser;

    public UserController(UserService userService, AccountService accountService, CurrentUser currentUser) {
        this.userService = userService;
        this.accountService = accountService;
        this.currentUser = currentUser;
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

        var listBankAccountsResponse = accountService.listAccounts(userId);
        if(listBankAccountsResponse != null) {
            var accounts = listBankAccountsResponse.getAccounts();
            if( accounts != null && !accounts.isEmpty()){
                throw new DeleteException("User has associated bank accounts and cannot be deleted");
            }
        }
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    private void verifyUser(String userId) {
        if(!userService.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        var currentUserId = this.currentUser.getId();

        if (!userId.equals(currentUserId)) {
            throw new ForbiddenException("Access denied");
        }
    }
}
