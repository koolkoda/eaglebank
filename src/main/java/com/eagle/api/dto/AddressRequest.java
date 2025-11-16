package com.eagle.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequest {
    @NotBlank
    private String line1;
    private String line2;
    private String line3;
    @NotBlank
    private String town;
    @NotBlank
    private String county;
    @NotBlank
    private String postcode;
}
