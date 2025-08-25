package org.godigit.trackwise.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when an operation is attempted on a resource that is not in a valid state for that operation.
 * For example, trying to check out an asset that is already retired.
 * Responds with HTTP 409 Conflict.
 */
public class InvalidStateException extends ApiException {
  public InvalidStateException(String message) {
    super(message, HttpStatus.CONFLICT);
  }
}