package com.schooladmin.system.dto;

import com.schooladmin.system.domain.Course;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/** What a client sends to create or update a {@link Course}. */
public record CourseRequest(
        @NotBlank(message = "must not be blank") String title,
        @Min(value = 1, message = "must be at least 1") int capacity) {

    public Course toEntity() {
        return new Course(title, capacity);
    }
}
