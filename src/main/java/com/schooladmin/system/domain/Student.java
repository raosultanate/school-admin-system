package com.schooladmin.system.domain;

/**
 * A {@link Person} enrolled at the school, identified by a unique {@code studentNumber}.
 */
public class Student extends Person {

    private final String studentNumber;
    private final int enrollmentYear;

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

    @Override
    public String describe() {
        return getFullName() + " is a student (#" + studentNumber + ", enrolled " + enrollmentYear + ")";
    }
}
