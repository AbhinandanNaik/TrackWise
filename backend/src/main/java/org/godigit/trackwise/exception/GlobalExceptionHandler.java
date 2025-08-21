package org.godigit.trackwise.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // This handler will catch your custom API exceptions
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
        Map<String, Object> errorDetails = Map.of(
                "timestamp", Instant.now(),
                "status", ex.getStatus().value(),
                "error", ex.getStatus().getReasonPhrase(),
                "message", ex.getMessage()
        );
        return new ResponseEntity<>(errorDetails, ex.getStatus());
    }

    // This is a fallback handler for any other unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        // Log the full exception for debugging
        ex.printStackTrace();

        Map<String, Object> errorDetails = Map.of(
                "timestamp", Instant.now(),
                "status", 500,
                "error", "Internal Server Error",
                "message", "An unexpected error occurred. Please try again later."
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException ex) {
        Map<String, Object> errorDetails = Map.of(
                "timestamp", Instant.now(),
                "status", ex.getStatus().value(),
                "error", ex.getStatus().getReasonPhrase(),
                "message", ex.getMessage(),
                "fieldErrors", ex.getErrors() // Include the map of field-specific errors
        );
        return new ResponseEntity<>(errorDetails, ex.getStatus());
    }
}