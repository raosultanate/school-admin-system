package com.schooladmin.system.dto;

import com.schooladmin.system.domain.Course;

public record CourseResponse(Long id, String title, int capacity) {

    public static CourseResponse from(Course course) {
        return new CourseResponse(course.getId(), course.getTitle(), course.getCapacity());
    }
}
