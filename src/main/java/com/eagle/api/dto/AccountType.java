package com.eagle.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AccountType {
    PERSONAL("personal");

    private final String value;

    AccountType(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }

    @JsonCreator
    public static AccountType fromValue(String value) {
        if (value == null) return null;
        if (value.equalsIgnoreCase("personal")) {
            return PERSONAL;
        }
        throw new IllegalArgumentException("Unknown AccountType: " + value);
    }
}
