package com.schooladmin.system.domain.exception;

/**
 * Thrown when a lookup can't find a {@link com.schooladmin.system.domain.Course} by the
 * identifier given.
 */
// Extends NotFoundException -- same reasoning as StudentNotFoundException: a lookup miss is
// a business-rule failure, not something every caller up the call stack should be forced to
// declare/catch, and it plugs into GlobalExceptionHandler's shared 404 handling for free.
public class CourseNotFoundException extends NotFoundException {

    public CourseNotFoundException(String message) {
        super(message);
    }

    public CourseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
