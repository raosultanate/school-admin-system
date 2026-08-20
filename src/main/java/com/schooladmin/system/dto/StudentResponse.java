package com.schooladmin.system.dto;

import com.schooladmin.system.domain.Student;

/**
 * What a {@link Student} looks like over the API. A {@code record} -- immutable, just data,
 * exactly the shape of Java class a DTO should be.
 */
// fullName is included here on purpose -- the difference from the earlier accidental leak
// isn't "never expose computed fields," it's "expose them because you decided to, not
// because Jackson serializes every public getter it finds on an entity by default."
public record StudentResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String fullName,
        String studentNumber,
        int enrollmentYear) {

    public static StudentResponse from(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getFullName(),
                student.getStudentNumber(),
                student.getEnrollmentYear());
    }
}
