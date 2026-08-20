package com.schooladmin.system.dto;

import com.schooladmin.system.domain.Student;

/**
 * What a client sends to create a {@link Student}. Deliberately has no {@code id} field --
 * not a missing feature, the actual fix. A client cannot set what doesn't exist to set.
 */
public record StudentRequest(
        String firstName,
        String lastName,
        String email,
        String studentNumber,
        int enrollmentYear) {

    // toEntity() always goes through Student's real constructor, which never accepts an id
    // either -- there is no path, anywhere in this class, for a caller-supplied id to reach
    // the database. save() will always see a fresh entity with id == null, meaning it always
    // INSERTs, never overwrites an existing row by guessing its id.
    public Student toEntity() {
        return new Student(firstName, lastName, email, studentNumber, enrollmentYear);
    }
}
