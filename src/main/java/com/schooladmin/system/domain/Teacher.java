package com.schooladmin.system.domain;

public class Teacher extends Person {

    private final String department;

    public Teacher(String firstName, String lastName, String email, String department) {
        super(firstName, lastName, email);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public String describe() {
        return getFullName() + " teaches in " + department;
    }
}
