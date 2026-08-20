package com.schooladmin.system.controller;

import com.schooladmin.system.domain.exception.DuplicateEnrollmentException;
import com.schooladmin.system.domain.exception.InvalidStudentDataException;
import com.schooladmin.system.domain.exception.NotFoundException;
import com.schooladmin.system.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * One place every exception that reaches a controller gets turned into a response --
 * instead of Spring's own default (a body that includes a full stack trace, internal class
 * names, and raw SQL -- confirmed live, see docs/notes/module-04-validation-exceptions.md).
 * {@code @RestControllerAdvice} applies to every {@code @RestController} in the app, not
 * just Student's -- these handlers cover Teacher/Course too, automatically.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // One handler for every "not found" exception -- StudentNotFoundException,
    // TeacherNotFoundException, CourseNotFoundException, and any future subtype of
    // NotFoundException -- matched by supertype, not listed individually. A new resource
    // adding its own NotFoundException subtype gets 404 handling automatically, no change
    // needed here.
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException e, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    // Registered even though nothing currently throws it through the normal API flow --
    // studentNumber is server-generated now (StudentService), so a client can no longer
    // supply a duplicate one directly. Kept wired for a future path that DOES accept an
    // externally-supplied number (e.g. importing existing students from another system).
    @ExceptionHandler(DuplicateEnrollmentException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateEnrollmentException e, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, e.getMessage(), request);
    }

    // Same honesty note as above -- registered for completeness (the roadmap's "wire the
    // existing custom exceptions" goal), but nothing in this REST layer currently performs
    // the kind of low-level-parsing-failure translation this exception exists for.
    @ExceptionHandler(InvalidStudentDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidData(InvalidStudentDataException e, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage(), request);
    }

    // Thrown automatically by Spring when @Valid fails on a @RequestBody -- before this
    // handler existed, Spring's own default handling already returned 400 for this one, but
    // with its own default (leaky) body shape, not ours. e.getBindingResult() has one
    // FieldError per failed constraint; joined into one readable message here.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");
        return build(HttpStatus.BAD_REQUEST, message, request);
    }

    // Defensive backstop, not the primary path: StudentService already checks for a
    // collision before generating a student number, so this mainly guards against a genuine
    // race (two requests colliding at the database level between the check and the insert)
    // or any other unique/not-null constraint this handler doesn't know about by name.
    // Confirmed live (before this handler existed): a raw duplicate insert produced 500 with
    // a full Hibernate/SQL stack trace in the response body -- this replaces that with 409
    // and no internal detail.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(DataIntegrityViolationException e, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "A database constraint was violated", request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse body = ErrorResponse.of(status.value(), message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
