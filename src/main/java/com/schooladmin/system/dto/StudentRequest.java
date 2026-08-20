package com.schooladmin.system.dto;

import com.schooladmin.system.domain.Student;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * What a client sends to create or update a {@link Student}. Deliberately has no {@code id}
 * field -- not a missing feature, the actual fix from Module 3. Deliberately has no
 * {@code studentNumber} either, for the same reason: it's a server-generated, auto-issued
 * identifier (see {@code StudentController}), not something a caller supplies or can change.
 */
// Bean Validation on a record: the annotation goes on the record component itself (the
// declaration below), and Spring validates it automatically wherever this type is used with
// @Valid on a @RequestBody parameter -- confirmed live, see module-04 notes for the before/
// after (garbage data silently accepted -> rejected with a clear 400).
public record StudentRequest(
        @NotBlank(message = "must not be blank") String firstName,
        @NotBlank(message = "must not be blank") String lastName,
        @NotBlank(message = "must not be blank") @Email(message = "must be a valid address") String email,
        @Min(value = 2000, message = "must be 2000 or later")
        @Max(value = 2100, message = "must be 2100 or earlier") int enrollmentYear) {

    // toEntity() takes the generated studentNumber as a parameter rather than reading it
    // from this record -- there is no path, anywhere in this class, for a caller to supply
    // one, the same reasoning as the missing id field.
    public Student toEntity(String studentNumber) {
        return new Student(firstName, lastName, email, studentNumber, enrollmentYear);
    }
}
