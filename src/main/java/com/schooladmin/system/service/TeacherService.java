package com.schooladmin.system.service;

import com.schooladmin.system.domain.Teacher;
import com.schooladmin.system.domain.exception.TeacherNotFoundException;
import com.schooladmin.system.dto.TeacherRequest;
import com.schooladmin.system.repository.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Same shape as {@link StudentService}, minus the student-number generation -- {@code
 * TeacherController} only translates HTTP ⇄ Java and delegates everything else here.
 */
@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    public Teacher findById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new TeacherNotFoundException("No teacher found with id " + id));
    }

    public Teacher create(TeacherRequest request) {
        return teacherRepository.save(request.toEntity());
    }

    public Teacher update(Long id, TeacherRequest request) {
        Teacher teacher = findById(id);
        teacher.updateFrom(request);
        return teacherRepository.save(teacher);
    }

    public void delete(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new TeacherNotFoundException("No teacher found with id " + id);
        }
        teacherRepository.deleteById(id);
    }
}
