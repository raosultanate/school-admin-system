package com.schooladmin.system.controller;

import com.schooladmin.system.dto.StudentRequest;
import com.schooladmin.system.dto.StudentResponse;
import com.schooladmin.system.service.StudentService;
import jakarta.validation.Valid;
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

// Thin now, on purpose: every not-found/status-code decision that used to live here moved
// into StudentService (throwing StudentNotFoundException) and GlobalExceptionHandler
// (translating that into 404). This controller's only job left is HTTP ⇄ Java translation.
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<StudentResponse> getAllStudents() {
        return studentService.findAll().stream().map(StudentResponse::from).toList();
    }

    // No ResponseEntity/404 branch needed anymore -- studentService.findById() throws
    // StudentNotFoundException on a miss, and GlobalExceptionHandler turns that into 404.
    // A plain return here always means success (200), same as getAllStudents() above.
    @GetMapping("/{id}")
    public StudentResponse getStudentById(@PathVariable Long id) {
        return StudentResponse.from(studentService.findById(id));
    }

    // 201 Created still needs ResponseEntity explicitly -- there's no exception involved on
    // the success path, just a status code that isn't Spring's 200 default.
    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentRequest request) {
        StudentResponse created = StudentResponse.from(studentService.create(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public StudentResponse updateStudent(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return StudentResponse.from(studentService.update(id, request));
    }

    // 204 still needs ResponseEntity explicitly, same reasoning as 201 above.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
