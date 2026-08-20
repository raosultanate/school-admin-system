package com.schooladmin.system.domain;

import com.schooladmin.system.dto.StudentRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * A {@link Person} enrolled at the school, identified by a unique {@code studentNumber}.
 */
// @Entity: this class gets a real database table. @Table names it explicitly -- without
// this, Hibernate's default naming strategy would call it "student" (singular, from the
// class name), not "students"; being explicit avoids relying on a default that's easy to
// get wrong.
@Entity
@Table(name = "students")
public class Student extends Person {

    // @Column: studentNumber must be unique and present -- the database now enforces what
    // StudentRegistry (java-refresher) used to check by hand with a linear scan. A duplicate
    // insert will fail at the database level; wiring that failure into a proper exception
    // response is Module 4 (Validation & Exception Handling in Spring).
    @Column(nullable = false, unique = true)
    private String studentNumber;

    private int enrollmentYear;

    // Required by JPA, same reasoning as Person's no-arg constructor: Hibernate builds this
    // object first, empty, then populates fields via reflection afterward.
    protected Student() {
    }

    /**
     * @param firstName      student's first name
     * @param lastName       student's last name
     * @param email          student's contact email
     * @param studentNumber  unique student identifier, e.g. {@code "S-1001"}
     * @param enrollmentYear year the student enrolled, e.g. {@code 2024}
     */
    public Student(String firstName, String lastName, String email, String studentNumber,
            int enrollmentYear) {
        super(firstName, lastName, email);
        this.studentNumber = studentNumber;
        this.enrollmentYear = enrollmentYear;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public int getEnrollmentYear() {
        return enrollmentYear;
    }

    // Reassigns every field from a StudentRequest onto this ALREADY-PERSISTED entity, for
    // the PUT/update flow. Kept as one method here rather than a pile of individual setter
    // calls scattered in the controller -- "how an update applies" is the entity's business,
    // same reasoning as toEntity() living on StudentRequest for creation.
    public void updateFrom(StudentRequest request) {
        setFirstName(request.firstName());
        setLastName(request.lastName());
        setEmail(request.email());
        this.studentNumber = request.studentNumber();
        this.enrollmentYear = request.enrollmentYear();
    }

    @Override
    public String describe() {
        return getFullName() + " is a student (#" + studentNumber + ", enrolled " + enrollmentYear + ")";
    }
}
