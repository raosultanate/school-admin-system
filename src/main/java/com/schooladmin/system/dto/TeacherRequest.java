package com.schooladmin.system.dto;

import com.schooladmin.system.domain.Department;
import com.schooladmin.system.domain.Teacher;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** What a client sends to create or update a {@link Teacher}. */
public record TeacherRequest(
        @NotBlank(message = "must not be blank") String firstName,
        @NotBlank(message = "must not be blank") String lastName,
        @NotBlank(message = "must not be blank") @Email(message = "must be a valid address") String email,
        @NotNull(message = "must be provided") Department department) {

    public Teacher toEntity() {
        return new Teacher(firstName, lastName, email, department);
    }
}
