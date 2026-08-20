package com.schooladmin.system.dto;

import com.schooladmin.system.domain.Department;
import com.schooladmin.system.domain.Teacher;

public record TeacherRequest(
        String firstName,
        String lastName,
        String email,
        Department department) {

    public Teacher toEntity() {
        return new Teacher(firstName, lastName, email, department);
    }
}
