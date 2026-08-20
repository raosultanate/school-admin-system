package com.schooladmin.system.dto;

import com.schooladmin.system.domain.Department;
import com.schooladmin.system.domain.Teacher;

public record TeacherResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String fullName,
        Department department) {

    public static TeacherResponse from(Teacher teacher) {
        return new TeacherResponse(
                teacher.getId(),
                teacher.getFirstName(),
                teacher.getLastName(),
                teacher.getEmail(),
                teacher.getFullName(),
                teacher.getDepartment());
    }
}
