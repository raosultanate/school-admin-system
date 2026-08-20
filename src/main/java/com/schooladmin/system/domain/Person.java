package com.schooladmin.system.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * Base type for anyone the school tracks — currently a {@link Student} or a {@link Teacher}.
 *
 * <p>Holds only the fields every person has in common. Subclasses add their own fields and
 * must implement {@link #describe()} to say what kind of person they are.
 */
// @MappedSuperclass: Person is never its own database table, but its fields (including id,
// below) become real columns on whatever @Entity extends it (Student, Teacher) -- the JPA
// analog of "this class only exists to be subclassed," which was already true here in plain
// OOP terms.
@MappedSuperclass
public abstract class Person {

    // Every entity needs a primary key. It lives here, not on each subclass, because the
    // fields it identifies (firstName/lastName/email, below) live here too.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // No longer final: Hibernate builds entities via a no-arg constructor (below), then
    // populates fields afterward through reflection -- a final field can only be assigned
    // once, inside a constructor, which makes it incompatible with that approach.
    private String firstName;
    private String lastName;
    private String email;

    // Required by JPA: the persistence provider needs a way to construct this object before
    // it has any data to put in it. protected, not public -- application code should never
    // call this directly, only the 3-arg constructor below; it exists for Hibernate's
    // reflection-based construction only.
    protected Person() {
    }

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

    public Long getId() {
        return id;
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
