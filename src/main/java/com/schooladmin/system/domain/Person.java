package com.schooladmin.system.domain;

/**
 * Base type for anyone the school tracks — currently a {@link Student} or a {@link Teacher}.
 *
 * <p>Holds only the fields every person has in common. Subclasses add their own fields and
 * must implement {@link #describe()} to say what kind of person they are.
 */
public abstract class Person {

    private final String firstName;
    private final String lastName;
    private final String email;

    /**
     * @param firstName person's first name
     * @param lastName  person's last name
     * @param email     person's contact email
     */
    protected Person(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * A short, role-specific description of this person (e.g. student number for a
     * {@link Student}, department for a {@link Teacher}).
     *
     * @return a one-line description of this person
     */
    public abstract String describe();
}
