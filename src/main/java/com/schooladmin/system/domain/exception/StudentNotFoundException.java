package com.schooladmin.system.domain.exception;

/**
 * Thrown when a lookup can't find a {@link com.schooladmin.system.domain.Student} by the
 * identifier given (e.g. student number).
 */
// Unchecked (extends RuntimeException, not Exception): a lookup miss is a business-rule
// failure, not something every caller up the call stack should be forced to declare/catch.
public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(String message) {
        super(message);
    }

    public StudentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
