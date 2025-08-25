package org.godigit.trackwise.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * A global exception handler for the entire application.
 * The @ControllerAdvice annotation allows this class to intercept exceptions
 * thrown from any controller, providing a centralized and consistent
 * way to handle errors and format API responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles specific validation errors from @Valid annotations in controllers.
     * This method catches Spring's validation exception and converts it into
     * our custom, more detailed ValidationException response.
     *
     * @param ex The MethodArgumentNotValidException thrown by Spring.
     * @return A ResponseEntity with a 422 Unprocessable Entity status and detailed field errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        // Create a map to hold the field-specific error messages.
        Map<String, String> errors = new HashMap<>();
        // Extract each field error and add it to the map.
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // Create the final, structured error response body.
        Map<String, Object> errorDetails = Map.of(
                "timestamp", Instant.now(),
                "status", HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "error", "Validation Failed",
                "message", "One or more fields have an error.",
                "fieldErrors", errors
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Handles all custom exceptions that extend our base ApiException class.
     * This includes NotFoundException, DuplicateResourceException, etc.
     *
     * @param ex The custom ApiException that was thrown.
     * @return A ResponseEntity with the appropriate status code and message from the exception.
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
        // Create a structured error response using the details from the custom exception.
        Map<String, Object> errorDetails = Map.of(
                "timestamp", Instant.now(),
                "status", ex.getStatus().value(),
                "error", ex.getStatus().getReasonPhrase(),
                "message", ex.getMessage()
        );
        // Return the response with the specific HTTP status from the exception (e.g., 404, 409).
        return new ResponseEntity<>(errorDetails, ex.getStatus());
    }

    /**
     * A fallback handler for any other unexpected exceptions that are not
     * specifically handled. This prevents stack traces from being sent to the client.
     *
     * @param ex The generic Exception that was thrown.
     * @return A ResponseEntity with a 500 Internal Server Error status.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        // Log the full exception stack trace for debugging purposes.
        // In a real application, you would use a proper logging framework.
        ex.printStackTrace();

        // Create a generic, user-friendly error message.
        Map<String, Object> errorDetails = Map.of(
                "timestamp", Instant.now(),
                "status", 500,
                "error", "Internal Server Error",
                "message", "An unexpected error occurred. Please contact support."
        );
        // Return a 500 status to indicate a server-side problem.
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}