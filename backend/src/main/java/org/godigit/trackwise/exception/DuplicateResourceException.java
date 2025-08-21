package org.godigit.trackwise.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends ApiException {
    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT); // 409 Conflict is a good status code for this
    }
}