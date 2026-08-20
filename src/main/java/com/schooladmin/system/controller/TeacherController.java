package com.schooladmin.system.controller;

import com.schooladmin.system.domain.Teacher;
import com.schooladmin.system.dto.TeacherRequest;
import com.schooladmin.system.dto.TeacherResponse;
import com.schooladmin.system.repository.TeacherRepository;
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

// Same shape as StudentController throughout -- same annotations, same ResponseEntity/status
// code choices, same reasoning. Not repeated in comments here; see StudentController for the
// explanations.
@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherRepository teacherRepository;

    public TeacherController(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @GetMapping
    public List<TeacherResponse> listAll() {
        return teacherRepository.findAll().stream().map(TeacherResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponse> getOne(@PathVariable Long id) {
        return teacherRepository.findById(id)
                .map(TeacherResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TeacherResponse> create(@RequestBody TeacherRequest request) {
        Teacher saved = teacherRepository.save(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(TeacherResponse.from(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherResponse> update(@PathVariable Long id, @RequestBody TeacherRequest request) {
        return teacherRepository.findById(id)
                .map(teacher -> {
                    teacher.updateFrom(request);
                    return teacherRepository.save(teacher);
                })
                .map(TeacherResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!teacherRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        teacherRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
