package com.schooladmin.system.domain;

/**
 * A {@link Person} who administers the school system, with a given {@link AccessLevel}.
 */
public class Admin extends Person {

    // AccessLevel, not a raw String: the compiler now rejects a typo like "SUPRE_ADMIN" at
    // compile time instead of it silently becoming a bug discovered at runtime -- the
    // Module 2 refactor (see docs/notes/module-02-enums.md).
    private final AccessLevel accessLevel;

    /**
     * @param firstName   admin's first name
     * @param lastName    admin's last name
     * @param email       admin's contact email
     * @param accessLevel permission tier
     */
    public Admin(String firstName, String lastName, String email, AccessLevel accessLevel) {
        super(firstName, lastName, email);
        this.accessLevel = accessLevel;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    @Override
    public String describe() {
        return getFullName() + " administers the system (" + accessLevel + ")";
    }
}
