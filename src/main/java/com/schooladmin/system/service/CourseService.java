package com.schooladmin.system.service;

import com.schooladmin.system.domain.Course;
import com.schooladmin.system.domain.exception.CourseNotFoundException;
import com.schooladmin.system.dto.CourseRequest;
import com.schooladmin.system.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Same shape as {@link StudentService}/{@link TeacherService} -- {@code CourseController}
 * only translates HTTP ⇄ Java and delegates everything else here.
 */
@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("No course found with id " + id));
    }

    public Course create(CourseRequest request) {
        return courseRepository.save(request.toEntity());
    }

    public Course update(Long id, CourseRequest request) {
        Course course = findById(id);
        course.updateFrom(request);
        return courseRepository.save(course);
    }

    public void delete(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new CourseNotFoundException("No course found with id " + id);
        }
        courseRepository.deleteById(id);
    }
}
