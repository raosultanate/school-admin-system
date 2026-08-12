package com.schooladmin.system.domain;

public class Student extends Person {

    private final String studentNumber;

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
