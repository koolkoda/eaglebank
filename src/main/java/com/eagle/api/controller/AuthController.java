package com.eagle.api.controller;

import com.eagle.api.dto.JwtResponse;
import com.eagle.api.dto.LoginRequest;
import com.eagle.api.exception.UnauthorizedException;
import com.eagle.api.service.JwtTokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtTokenService jwt;

    public AuthController(AuthenticationManager authManager, JwtTokenService jwt) {
        this.authManager = authManager;
        this.jwt = jwt;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody @Valid LoginRequest req) {
        try {
            var authReq = new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword());
            var auth = authManager.authenticate(authReq);
            var user = (UserDetails) auth.getPrincipal();
            String token = jwt.generateToken(user);
            return ResponseEntity.ok(new JwtResponse(token, jwt.getExpirationMs() / 1000));
        } catch (AuthenticationException ex) {
            throw new UnauthorizedException("Invalid credentials");
        }
    }
}
