package com.eagle.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Currency {
    GBP("GBP");

    private final String value;

    Currency(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }

    @JsonCreator
    public static Currency fromValue(String value) {
        if (value == null) return null;
        if (value.equalsIgnoreCase("gbp")) {
            return GBP;
        }
        throw new IllegalArgumentException("Unknown Currency: " + value);
    }
}
