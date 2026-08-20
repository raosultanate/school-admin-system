package com.schooladmin.system.controller;

import com.schooladmin.system.domain.Student;
import com.schooladmin.system.dto.StudentRequest;
import com.schooladmin.system.dto.StudentResponse;
import com.schooladmin.system.repository.StudentRepository;
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

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream().map(StudentResponse::from).toList();
    }

    // ResponseEntity<T>: lets a method return a body AND explicitly choose the HTTP status,
    // instead of always getting Spring's default (200 OK for anything returned normally).
    // Before: findById(id).orElseThrow() -- an uncaught exception on a missing id, which
    // Spring turns into 500 Internal Server Error. Wrong status: a missing id is a normal,
    // expected outcome, not a server failure. .map(...).orElseGet(...) chooses the correct
    // response for both branches explicitly: 200 with a body, or 404 with none.
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        return studentRepository.findById(id)
                .map(StudentResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 201 Created, not the default 200 -- the correct status for "a new resource now exists"
    // per HTTP semantics. ResponseEntity.status(HttpStatus.CREATED) is how that's chosen
    // explicitly instead of accepting Spring's default.
    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@RequestBody StudentRequest request) {
        Student saved = studentRepository.save(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(StudentResponse.from(saved));
    }

    // PUT: fetch the existing entity, apply the incoming request onto it (updateFrom), save
    // the SAME managed entity back -- an update, not an insert, because its id is already
    // set. 404 if the id doesn't exist, same reasoning as getStudentById().
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Long id, @RequestBody StudentRequest request) {
        return studentRepository.findById(id)
                .map(student -> {
                    student.updateFrom(request);
                    return studentRepository.save(student);
                })
                .map(StudentResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // existsById() checked first, deliberately, rather than calling deleteById() blindly --
    // gives an honest 404 for a missing id instead of silently no-op'ing or relying on
    // deleteById()'s own (version-dependent) behavior for a nonexistent row.
    // 204 No Content: the correct status for "succeeded, nothing to send back."
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        if (!studentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        studentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
