package com.schooladmin.system.playground;

import com.schooladmin.system.domain.Student;
import com.schooladmin.system.domain.exception.DuplicateEnrollmentException;
import com.schooladmin.system.domain.exception.StudentNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal in-memory student registry (no Spring, no database) used to exercise the custom
 * exceptions against real domain objects.
 */
public class StudentRegistry {

    private final List<Student> students = new ArrayList<>();

    public void register(Student student) {
        for (Student existing : students) {
            if (existing.getStudentNumber().equals(student.getStudentNumber())) {
                throw new DuplicateEnrollmentException(
                        "Student " + student.getStudentNumber() + " is already registered");
            }
        }
        students.add(student);
    }

    public Student findByStudentNumber(String studentNumber) {
        for (Student student : students) {
            if (student.getStudentNumber().equals(studentNumber)) {
                return student;
            }
        }
        throw new StudentNotFoundException("No student found with number " + studentNumber);
    }
}
