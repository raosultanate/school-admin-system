package com.schooladmin.system.domain.exception;

/**
 * Thrown when attempting to register a {@link com.schooladmin.system.domain.Student} whose
 * student number is already in use.
 */
// Unchecked (extends RuntimeException, not Exception): same reasoning as
// StudentNotFoundException -- a business-rule failure, not a mandatory throws/catch.
public class DuplicateEnrollmentException extends RuntimeException {

    public DuplicateEnrollmentException(String message) {
        super(message);
    }

    public DuplicateEnrollmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
