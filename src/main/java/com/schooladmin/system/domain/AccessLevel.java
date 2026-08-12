package com.schooladmin.system.domain;

/**
 * Permission tier for an {@link Admin}. Replaces what used to be a raw {@code String} on
 * {@code Admin} — see {@code docs/notes/module-02-enums.md} for why that matters.
 */
public enum AccessLevel implements HasLabel {
    // Each line here runs the constructor below exactly once and creates one singleton
    // instance -- SUPER_ADMIN, ADMIN, and SUPPORT are the only AccessLevel objects that
    // will ever exist for the lifetime of the program.
    SUPER_ADMIN(3, "Full access to all system functions"),
    ADMIN(2, "Can manage students, teachers, and courses"),
    SUPPORT(1, "Read-only access for support purposes");

    private final int rank;
    private final String description;

    // Enum constructors are implicitly private -- Java won't even let this be declared
    // public. There's no "new AccessLevel(...)" anywhere outside this file; the three
    // constants above are the only way instances get created.
    AccessLevel(int rank, String description) {
        this.rank = rank;
        this.description = description;
    }

    public int getRank() {
        return rank;
    }

    // Real behavior, not just a data holder -- an enum can have methods like any class.
    public boolean outranks(AccessLevel other) {
        return this.rank > other.rank;
    }

    @Override
    public String label() {
        return description;
    }
}
