package com.schooladmin.system.repository;

import com.schooladmin.system.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
