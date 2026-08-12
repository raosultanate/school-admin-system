package com.schooladmin.system.playground;

import com.schooladmin.system.domain.Person;
import com.schooladmin.system.domain.Student;
import com.schooladmin.system.domain.Teacher;

import java.util.List;

/**
 * Standalone demo (no Spring, no database) of inheritance and polymorphism over the
 * {@link Person} hierarchy. Run directly:
 *
 * <pre>{@code
 * java -cp target/classes com.schooladmin.system.playground.OopDemo
 * }</pre>
 */
public class OopDemo {

    public static void main(String[] args) {
        List<Person> people = List.of(
                new Student("Ada", "Lovelace", "ada@school.edu", "S-1001"),
                new Teacher("Alan", "Turing", "alan@school.edu", "Computer Science")
        );

        for (Person person : people) {
            System.out.println(person.describe());
        }
    }
}
