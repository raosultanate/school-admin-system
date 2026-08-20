package com.schooladmin.system.controller;

import com.schooladmin.system.domain.Course;
import com.schooladmin.system.dto.CourseRequest;
import com.schooladmin.system.dto.CourseResponse;
import com.schooladmin.system.repository.CourseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Same shape as StudentController/TeacherController -- see StudentController for the
// ResponseEntity/status code reasoning, not repeated here.
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public List<CourseResponse> listAll() {
        return courseRepository.findAll().stream().map(CourseResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getOne(@PathVariable Long id) {
        return courseRepository.findById(id)
                .map(CourseResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CourseResponse> create(@RequestBody CourseRequest request) {
        Course saved = courseRepository.save(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(CourseResponse.from(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> update(@PathVariable Long id, @RequestBody CourseRequest request) {
        return courseRepository.findById(id)
                .map(course -> {
                    course.updateFrom(request);
                    return courseRepository.save(course);
                })
                .map(CourseResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!courseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        courseRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
