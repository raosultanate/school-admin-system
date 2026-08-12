package com.schooladmin.system.domain.exception;

/**
 * Thrown when raw input for a {@link com.schooladmin.system.domain.Student} can't be turned
 * into valid data (e.g. a malformed enrollment year). Always wraps the lower-level parsing
 * failure as its cause, via exception chaining, so the original error is never lost — there
 * is deliberately no no-cause constructor, since this exception is never thrown standalone.
 */
public class InvalidStudentDataException extends RuntimeException {

    public InvalidStudentDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
