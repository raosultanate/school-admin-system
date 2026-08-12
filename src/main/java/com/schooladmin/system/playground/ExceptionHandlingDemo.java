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
        // Register one real student up front so the two demos below have something to
        // collide with / fail to find.
        registry.register(new Student("Ada", "Lovelace", "ada@school.edu", "S-1001"));

        demonstrateDuplicateEnrollment(registry);
        demonstrateStudentNotFound(registry);
        demonstrateExceptionChaining();
    }

    private static void demonstrateDuplicateEnrollment(StudentRegistry registry) {
        try {
            // Same student number ("S-1001") as the one already registered in main() above,
            // even though the name/email differ -- register() only checks the number, so
            // this is guaranteed to throw DuplicateEnrollmentException.
            registry.register(new Student("Ada", "Lovelace", "ada2@school.edu", "S-1001"));
        } catch (DuplicateEnrollmentException e) {
            // Caught here, not left to propagate, because there's something real to do
            // about it: report it and let the program continue past this one failure.
            System.out.println("Caught: " + e.getMessage());
        }
    }

    private static void demonstrateStudentNotFound(StudentRegistry registry) {
        try {
            // "S-9999" was never registered, so this is guaranteed to throw
            // StudentNotFoundException.
            registry.findByStudentNumber("S-9999");
        } catch (StudentNotFoundException e) {
            System.out.println("Caught: " + e.getMessage());
        }
    }

    private static void demonstrateExceptionChaining() {
        try {
            // "not-a-year" isn't a valid int, so parseEnrollmentYear (below) will fail
            // internally and rethrow as InvalidStudentDataException.
            parseEnrollmentYear("not-a-year");
        } catch (InvalidStudentDataException e) {
            System.out.println("Caught: " + e.getMessage());
            // getCause() returns the original NumberFormatException that parseEnrollmentYear
            // caught and wrapped -- proof that chaining preserved it instead of losing it.
            System.out.println("Caused by: " + e.getCause());
        }
    }

    private static int parseEnrollmentYear(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            // Exception chaining: translate the low-level, technical NumberFormatException
            // into a domain-meaningful one for the caller, passing the original as the
            // cause (the "e" argument below) so it's still visible via getCause() -- not
            // caught here just to be silently discarded.
            throw new InvalidStudentDataException("Invalid enrollment year: '" + raw + "'", e);
        }
    }
}
