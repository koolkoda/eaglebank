package com.eagle.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DeleteException extends RuntimeException {
    private final HttpStatus status = HttpStatus.CONFLICT;

    public DeleteException(String message) {
        super(message);
    }

}