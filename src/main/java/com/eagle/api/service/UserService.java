package com.eagle.api.service;

import com.eagle.api.dto.CreateUserRequest;
import com.eagle.api.dto.UpdateUserRequest;
import com.eagle.api.dto.UserResponse;
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
        String id = generateUserId();
        Instant now = Instant.now();
        UserResponse u = new UserResponse();
        u.setId(id);
        u.setName(req.getName());
        u.setAddress(req.getAddress());
        u.setPhoneNumber(req.getPhoneNumber());
        u.setEmail(req.getEmail());
        u.setCreatedTimestamp(now.toString());
        u.setUpdatedTimestamp(now.toString());
        users.put(id, u);
        emailToId.put(req.getEmail(), id);

        UserDetails userDetails = User.withUsername(req.getEmail())
                .password(encoder.encode(req.getEmail()))
                .roles("USER")
                .build();

        this.userDetailsManager.createUser(userDetails);

        return u;
    }

    public UserResponse getUser(String userId) {
        UserResponse u = users.get(userId);
        if (u == null) throw new NoSuchElementException("User not found");
        return u;
    }

    public UserResponse updateUser(String userId, UpdateUserRequest req) {
        UserResponse u = getUser(userId);
        if (req.getName() != null) u.setName(req.getName());
        if (req.getAddress() != null) u.setAddress(req.getAddress());
        if (req.getPhoneNumber() != null) u.setPhoneNumber(req.getPhoneNumber());
        if (req.getEmail() != null) u.setEmail(req.getEmail());
        u.setUpdatedTimestamp(Instant.now().toString());
        users.put(userId, u);
        return u;
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
}
