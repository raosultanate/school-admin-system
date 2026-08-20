package com.schooladmin.system.domain;

import com.schooladmin.system.dto.TeacherRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/**
 * A {@link Person} who teaches at the school, affiliated with a single {@link Department}.
 */
@Entity
@Table(name = "teachers")
public class Teacher extends Person {

    // Department, not a raw String -- same reasoning as Admin.accessLevel: a fixed, known
    // set of valid values belongs in an enum, not a String that can hold anything.
    //
    // @Enumerated(EnumType.STRING): without this, Hibernate's default is EnumType.ORDINAL --
    // storing the enum's declaration *position* (0, 1, 2...) as a plain integer. That breaks
    // silently the moment someone reorders or inserts a new Department constant -- row data
    // that meant COMPUTER_SCIENCE (position 0) could suddenly mean MATHEMATICS after a code
    // change, with no error, just wrong data. STRING stores the constant's actual name
    // ("COMPUTER_SCIENCE") -- self-describing in the database, and safe against reordering.
    @Enumerated(EnumType.STRING)
    private Department department;

    protected Teacher() {
    }

    /**
     * @param firstName  teacher's first name
     * @param lastName   teacher's last name
     * @param email      teacher's contact email
     * @param department department the teacher belongs to
     */
    public Teacher(String firstName, String lastName, String email, Department department) {
        super(firstName, lastName, email);
        this.department = department;
    }

    public Department getDepartment() {
        return department;
    }

    public void updateFrom(TeacherRequest request) {
        setFirstName(request.firstName());
        setLastName(request.lastName());
        setEmail(request.email());
        this.department = request.department();
    }

    @Override
    public String describe() {
        return getFullName() + " teaches in " + department.label();
    }
}
