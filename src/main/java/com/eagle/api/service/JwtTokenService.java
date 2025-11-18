package com.eagle.api.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JwtTokenService {

    private final SecretKey key;
    @Getter
    private final long expirationMs;

    public JwtTokenService(
            @Value("${jwt.secret:}") String secret,
            @Value("${jwt.expiration-ms:3600000}") long expirationMs,
            Environment env
    ) {
        // If no secret provided, and we're running with the 'local' profile, generate a secure secret.
        if (secret == null || secret.isBlank()) {
            String[] profiles = env.getActiveProfiles();
            boolean isLocal = Arrays.stream(profiles).anyMatch(p -> p.equalsIgnoreCase("local"));
            if (isLocal) {
                byte[] bytes = new byte[64];
                new SecureRandom().nextBytes(bytes);
                secret = Base64.getEncoder().encodeToString(bytes);
                // Do NOT log the secret in real apps. This is generated only for local development.
            } else {
                throw new IllegalStateException("Missing jwt.secret; set JWT_SECRET environment variable or run with the 'local' profile to auto-generate one for dev.");
            }
        }

        // If the secret is Base64, decode; otherwise fallback to raw bytes.
        SecretKey k;
        try {
            k = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        } catch (IllegalArgumentException e) {
            k = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        }
        this.key = k;
        this.expirationMs = expirationMs;
    }

    public String generateToken(UserDetails user) {
        Instant now = Instant.now();
        Instant exp = now.plusMillis(expirationMs);
        String roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(user.getUsername())
                .claims(Map.of("roles", roles))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(String token, UserDetails user) {
        try {
            var payload = Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token)
                    .getPayload();
            String username = payload.getSubject();
            Date exp = payload.getExpiration();
            return username.equals(user.getUsername()) && exp.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

}