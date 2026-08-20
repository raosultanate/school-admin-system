package com.schooladmin.system.service;

import com.schooladmin.system.domain.Student;
import com.schooladmin.system.domain.exception.StudentNotFoundException;
import com.schooladmin.system.dto.StudentRequest;
import com.schooladmin.system.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

/**
 * Business logic for {@link Student}s: generating student numbers, and the actual create/
 * update/delete rules -- {@code StudentController} only translates HTTP ⇄ Java and delegates
 * everything else here.
 */
@Service
public class StudentService {

    // 9-digit student numbers: 100000000..999999999 -- never starts with a 0, so it's
    // always genuinely 9 digits, no zero-padding needed. SecureRandom over Random here has
    // no real security stakes for this specific value, but it's the conventional default
    // choice for "generate an identifier" in modern Java -- no reason to reach for the
    // weaker one.
    private static final SecureRandom RANDOM = new SecureRandom();

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    // Throws rather than returning Optional<Student> -- "no student with this id" is treated
    // as a definite failure at this boundary, not a case every caller has to remember to
    // handle. GlobalExceptionHandler (Module 4) maps this exception to 404 automatically, so
    // the controller method calling this doesn't need its own not-found branch at all.
    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("No student found with id " + id));
    }

    public Student create(StudentRequest request) {
        return studentRepository.save(request.toEntity(generateUniqueStudentNumber()));
    }

    public Student update(Long id, StudentRequest request) {
        Student student = findById(id);
        student.updateFrom(request);
        return studentRepository.save(student);
    }

    public void delete(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException("No student found with id " + id);
        }
        studentRepository.deleteById(id);
    }

    // Generate a candidate, check the database, retry on the (extremely rare, but real)
    // chance of a collision -- 900 million possible values, so this loop will essentially
    // always succeed on the first try, but "essentially always" isn't "always."
    private String generateUniqueStudentNumber() {
        String candidate;
        do {
            candidate = String.valueOf(100_000_000 + RANDOM.nextInt(900_000_000));
        } while (studentRepository.existsByStudentNumber(candidate));
        return candidate;
    }
}
