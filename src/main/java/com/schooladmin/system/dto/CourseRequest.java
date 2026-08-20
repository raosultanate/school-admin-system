package com.schooladmin.system.dto;

import com.schooladmin.system.domain.Course;

public record CourseRequest(String title, int capacity) {

    public Course toEntity() {
        return new Course(title, capacity);
    }
}
