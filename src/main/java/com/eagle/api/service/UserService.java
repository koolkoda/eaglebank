package com.eagle.api.service;

import com.eagle.api.dto.CreateUserRequest;
import com.eagle.api.dto.UpdateUserRequest;
import com.eagle.api.dto.UserResponse;
import com.eagle.api.exception.UserExistsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private final Map<String, UserResponse> users = new ConcurrentHashMap<>();
    private final Map<String, String> emailToId = new ConcurrentHashMap<>();
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder encoder;

    public UserService(UserDetailsManager userDetailsManager, PasswordEncoder encoder) {
        this.userDetailsManager = userDetailsManager;
        this.encoder = encoder;
    }

    public UserResponse createUser(CreateUserRequest req) {
        String email = req.getEmail().trim().toLowerCase();

        String id = generateUserId();
        Instant now = Instant.now();
        UserResponse userResponse = new UserResponse();
        userResponse.setId(id);
        userResponse.setName(req.getName());
        userResponse.setAddress(req.getAddress());
        userResponse.setPhoneNumber(req.getPhoneNumber());
        userResponse.setEmail(email);
        userResponse.setCreatedTimestamp(now.toString());
        userResponse.setUpdatedTimestamp(now.toString());

        String existing = emailToId.putIfAbsent(email, id);
        if (existing != null) {
            throw new UserExistsException("User with email already exists");
        }

        UserDetails userDetails = User.withUsername(email)
                .password(encoder.encode(email))
                .roles("USER")
                .build();

        try {
            this.userDetailsManager.createUser(userDetails);
        } catch (RuntimeException ex) {
            // rollback partial state
            emailToId.remove(email);
            throw ex;
        }

        // put the user after successful reservation
        users.put(id, userResponse);

        return userResponse;
    }

    public UserResponse getUser(String userId) {
        UserResponse userResponse = users.get(userId);
        if (userResponse == null) throw new NoSuchElementException("User not found");
        return userResponse;
    }

    public UserResponse updateUser(String userId, UpdateUserRequest req) {
        UserResponse userResponse = getUser(userId);
        if (req.getName() != null) userResponse.setName(req.getName());
        if (req.getAddress() != null) userResponse.setAddress(req.getAddress());
        if (req.getPhoneNumber() != null) userResponse.setPhoneNumber(req.getPhoneNumber());
        if (req.getEmail() != null) userResponse.setEmail(req.getEmail());

        userResponse.setUpdatedTimestamp(Instant.now().toString());
        users.put(userId, userResponse);

        return userResponse;
    }

    public void deleteUser(String userId) {
        UserResponse userResponse = null;
        if ((userResponse = users.remove(userId)) == null) throw new NoSuchElementException("User not found");


        this.userDetailsManager.deleteUser(userResponse.getEmail());
        emailToId.remove(userResponse.getEmail());
    }

    public String getUserIdByEmail(String email) {
        return emailToId.get(email);
    }

    private String generateUserId() {
        return "usr-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    public boolean existsById(String userId) {
        return users.containsKey(userId);
    }
}
