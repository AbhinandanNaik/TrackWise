package org.godigit.trackwise.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import java.util.Map;

@Getter
public class ValidationException extends ApiException {
  private final Map<String, String> errors;

  public ValidationException(String message, Map<String, String> errors) {
    super(message, HttpStatus.UNPROCESSABLE_ENTITY); // 422 is a common code for validation errors
    this.errors = errors;
  }
}