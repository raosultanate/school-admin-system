package com.schooladmin.system.domain;

/**
 * Academic department a {@link Teacher} belongs to. Replaces what used to be a raw
 * {@code String} on {@code Teacher}.
 */
public enum Department implements HasLabel {
    COMPUTER_SCIENCE("Computer Science"),
    MATHEMATICS("Mathematics"),
    HISTORY("History");

    private final String displayName;

    // Implicitly private, like every enum constructor -- see AccessLevel for why that's
    // enforced by the language, not just a convention.
    Department(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String label() {
        return displayName;
    }
}
