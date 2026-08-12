package com.schooladmin.system.domain;

/**
 * A {@link Person} enrolled at the school, identified by a unique {@code studentNumber}.
 */
public class Student extends Person {

    private final String studentNumber;

    /**
     * @param firstName     student's first name
     * @param lastName      student's last name
     * @param email         student's contact email
     * @param studentNumber unique student identifier, e.g. {@code "S-1001"}
     */
    public Student(String firstName, String lastName, String email, String studentNumber) {
        super(firstName, lastName, email);
        this.studentNumber = studentNumber;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    @Override
    public String describe() {
        return getFullName() + " is a student (#" + studentNumber + ")";
    }
}
