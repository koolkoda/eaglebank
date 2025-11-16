package com.eagle.api.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private AddressRequest address;
    private String phoneNumber;
    private String email;
}