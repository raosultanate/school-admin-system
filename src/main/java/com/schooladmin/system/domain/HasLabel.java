package com.schooladmin.system.domain;

/**
 * Implemented by enum types that carry a human-readable label alongside their constant
 * name, so unrelated enum types can be handled polymorphically wherever only the label
 * matters.
 */
public interface HasLabel {
    String label();
}
