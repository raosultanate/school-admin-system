package com.schooladmin.system.domain;

/**
 * A {@link Person} who administers the school system, with a given {@code accessLevel}.
 */
public class Admin extends Person {

    private final String accessLevel;

    /**
     * @param firstName   admin's first name
     * @param lastName    admin's last name
     * @param email       admin's contact email
     * @param accessLevel permission tier, e.g. {@code "SUPER_ADMIN"}
     */
    public Admin(String firstName, String lastName, String email, String accessLevel) {
        super(firstName, lastName, email);
        this.accessLevel = accessLevel;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    @Override
    public String describe() {
        return getFullName() + " administers the system (" + accessLevel + ")";
    }
}
