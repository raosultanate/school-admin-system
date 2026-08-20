package com.schooladmin.system.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A course a {@link Teacher} teaches and {@link Student}s enroll in.
 */
// Not a Person subtype -- a course isn't a kind of person, it's an unrelated concept, so
// this is a standalone entity rather than extending anything. No @MappedSuperclass to
// inherit id from, so it gets its own @Id here, same shape as Person's.
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private int capacity;

    protected Course() {
    }

    /**
     * @param title    course title, e.g. {@code "Intro to Algorithms"}
     * @param capacity maximum number of students that can enroll
     */
    public Course(String title, int capacity) {
        this.title = title;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getCapacity() {
        return capacity;
    }
}
