package com.eagle.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionType {
    DEPOSIT("deposit"),
    WITHDRAWAL("withdrawal");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }

    @JsonCreator
    public static TransactionType fromValue(String value) {
        if (value == null) return null;
        if (value.equalsIgnoreCase("deposit")) {
            return DEPOSIT;
        }
        if (value.equalsIgnoreCase("withdrawal")) {
            return WITHDRAWAL;
        }
        throw new IllegalArgumentException("Unknown TransactionType: " + value);
    }
}
