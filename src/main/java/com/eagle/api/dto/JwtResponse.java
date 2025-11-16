package com.eagle.api.dto;


import lombok.Getter;

@Getter
public class JwtResponse {
    private String accessToken;
    private long expiresIn;

    public JwtResponse(String accessToken, long expiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }
}
