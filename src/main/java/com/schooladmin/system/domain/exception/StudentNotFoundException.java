package com.schooladmin.system.domain.exception;

/**
 * Thrown when a lookup can't find a {@link com.schooladmin.system.domain.Student} by the
 * identifier given (e.g. student number).
 */
// Extends NotFoundException (Module 4), not RuntimeException directly -- a deliberate
// divergence from the frozen java-refresher copy of this class, made here because this
// project's version needs to plug into GlobalExceptionHandler's shared 404 handling.
// Unchecked either way: a lookup miss is a business-rule failure, not something every caller
// up the call stack should be forced to declare/catch.
public class StudentNotFoundException extends NotFoundException {

    public StudentNotFoundException(String message) {
        super(message);
    }

    public StudentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
