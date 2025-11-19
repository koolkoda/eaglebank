package com.eagle.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InsufficientFundsException extends RuntimeException {
    private final HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

    public InsufficientFundsException(String message) {
        super(message);
    }
}
