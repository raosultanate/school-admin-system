package com.schooladmin.system.domain.exception;

/**
 * Shared base for "a lookup by id found nothing" across every resource -- lets
 * {@code GlobalExceptionHandler} map all of them to {@code 404} with a single
 * {@code @ExceptionHandler(NotFoundException.class)}, instead of one handler method per
 * resource type. Abstract on purpose: always throw a specific subtype
 * ({@link StudentNotFoundException}, etc.), never this class directly -- the subtype is what
 * makes a stack trace or log line self-explanatory at a glance.
 */
public abstract class NotFoundException extends RuntimeException {

    protected NotFoundException(String message) {
        super(message);
    }

    protected NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
