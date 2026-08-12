package com.schooladmin.system.domain.exception;

/**
 * Thrown when attempting to register a {@link com.schooladmin.system.domain.Student} whose
 * student number is already in use.
 */
public class DuplicateEnrollmentException extends RuntimeException {

    public DuplicateEnrollmentException(String message) {
        super(message);
    }

    public DuplicateEnrollmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
