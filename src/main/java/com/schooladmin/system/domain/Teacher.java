package com.schooladmin.system.domain;

/**
 * A {@link Person} who teaches at the school, affiliated with a single {@link Department}.
 */
public class Teacher extends Person {

    // Department, not a raw String -- same reasoning as Admin.accessLevel: a fixed, known
    // set of valid values belongs in an enum, not a String that can hold anything.
    private final Department department;

    /**
     * @param firstName  teacher's first name
     * @param lastName   teacher's last name
     * @param email      teacher's contact email
     * @param department department the teacher belongs to
     */
    public Teacher(String firstName, String lastName, String email, Department department) {
        super(firstName, lastName, email);
        this.department = department;
    }

    public Department getDepartment() {
        return department;
    }

    @Override
    public String describe() {
        return getFullName() + " teaches in " + department.label();
    }
}
