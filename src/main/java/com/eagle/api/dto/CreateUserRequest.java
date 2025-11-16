package com.eagle.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank
    private String name;
    private AddressRequest address;
    @NotBlank
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$")
    private String phoneNumber;
    @Email
    private String email;
}