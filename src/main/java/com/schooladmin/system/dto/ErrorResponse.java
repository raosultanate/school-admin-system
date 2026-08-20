package com.schooladmin.system.dto;

import java.time.Instant;

/**
 * The one shape every error response takes, regardless of what actually went wrong --
 * deliberately doesn't include a stack trace or any internal detail (contrast with Spring's
 * own default error body, which includes both -- see GlobalExceptionHandler).
 */
public record ErrorResponse(Instant timestamp, int status, String message, String path) {

    public static ErrorResponse of(int status, String message, String path) {
        return new ErrorResponse(Instant.now(), status, message, path);
    }
}
