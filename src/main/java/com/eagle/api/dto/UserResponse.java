package com.eagle.api.dto;

import lombok.Data;

@Data
public class UserResponse {
    private String id;
    private String name;
    private AddressRequest address;
    private String phoneNumber;
    private String email;
    private String createdTimestamp;
    private String updatedTimestamp;
}