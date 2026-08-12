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
        // Scan every already-registered student first. If any of them already has this
        // student number, this is a duplicate -- throw immediately, before adding anything,
        // so a rejected registration never partially succeeds.
        for (Student existing : students) {
            if (existing.getStudentNumber().equals(student.getStudentNumber())) {
                throw new DuplicateEnrollmentException(
                        "Student " + student.getStudentNumber() + " is already registered");
            }
        }
        // No duplicate found in the loop above -- safe to add.
        students.add(student);
    }

    public Student findByStudentNumber(String studentNumber) {
        // Linear search: return the first (and, thanks to register()'s duplicate check,
        // only) student whose number matches.
        for (Student student : students) {
            if (student.getStudentNumber().equals(studentNumber)) {
                return student;
            }
        }
        // Looped through everyone without a match -- this is the "not found" case, thrown
        // only after exhausting the list, never partway through.
        throw new StudentNotFoundException("No student found with number " + studentNumber);
    }
}
