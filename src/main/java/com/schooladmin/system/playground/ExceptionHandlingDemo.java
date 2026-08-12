package com.schooladmin.system.playground;

import com.schooladmin.system.domain.Student;
import com.schooladmin.system.domain.exception.DuplicateEnrollmentException;
import com.schooladmin.system.domain.exception.InvalidStudentDataException;
import com.schooladmin.system.domain.exception.StudentNotFoundException;

/**
 * Standalone demo (no Spring) of custom exceptions, exception chaining, and catch-vs-rethrow,
 * built against a real {@link StudentRegistry}. Run directly:
 *
 * <pre>{@code
 * java -cp target/classes com.schooladmin.system.playground.ExceptionHandlingDemo
 * }</pre>
 */
public class ExceptionHandlingDemo {

    public static void main(String[] args) {
        StudentRegistry registry = new StudentRegistry();
        registry.register(new Student("Ada", "Lovelace", "ada@school.edu", "S-1001"));

        demonstrateDuplicateEnrollment(registry);
        demonstrateStudentNotFound(registry);
        demonstrateExceptionChaining();
    }

    private static void demonstrateDuplicateEnrollment(StudentRegistry registry) {
        try {
            registry.register(new Student("Ada", "Lovelace", "ada2@school.edu", "S-1001"));
        } catch (DuplicateEnrollmentException e) {
            System.out.println("Caught: " + e.getMessage());
        }
    }

    private static void demonstrateStudentNotFound(StudentRegistry registry) {
        try {
            registry.findByStudentNumber("S-9999");
        } catch (StudentNotFoundException e) {
            System.out.println("Caught: " + e.getMessage());
        }
    }

    private static void demonstrateExceptionChaining() {
        try {
            parseEnrollmentYear("not-a-year");
        } catch (InvalidStudentDataException e) {
            System.out.println("Caught: " + e.getMessage());
            System.out.println("Caused by: " + e.getCause());
        }
    }

    private static int parseEnrollmentYear(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new InvalidStudentDataException("Invalid enrollment year: '" + raw + "'", e);
        }
    }
}
