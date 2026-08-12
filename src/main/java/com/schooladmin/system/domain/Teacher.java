package com.schooladmin.system.domain;

/**
 * A {@link Person} who teaches at the school, affiliated with a single {@code department}.
 */
public class Teacher extends Person {

    private final String department;

    /**
     * @param firstName  teacher's first name
     * @param lastName   teacher's last name
     * @param email      teacher's contact email
     * @param department department the teacher belongs to, e.g. {@code "Computer Science"}
     */
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
