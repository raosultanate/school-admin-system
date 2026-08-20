package com.schooladmin.system.repository;

import com.schooladmin.system.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// @Repository isn't written here on purpose -- Spring Data JPA adds it automatically to
// every interface that extends JpaRepository, plus wraps database exceptions into Spring's
// own hierarchy the same way a hand-written @Repository would.
//
// JpaRepository<Student, Long>: two type parameters, same shape as Module 4's hand-rolled
// InMemoryRepository<T, ID> in java-refresher -- T is the entity type, ID is its primary
// key's type (Student's id is a Long). No method bodies below because there are no methods
// below: extending JpaRepository already provides save(), findById(), findAll(),
// deleteById(), count(), and more, all implemented for us.
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Derived query method: Spring Data parses this method's NAME -- "findBy" + the field
    // "StudentNumber" -- and generates the query from that alone, at startup. No SQL, no
    // JPQL, no method body at all; this is exactly the lookup StudentRegistry.
    // findByStudentNumber() (java-refresher, Module 3) did by hand with a linear List scan.
    // Here the database does the lookup directly instead.
    Optional<Student> findByStudentNumber(String studentNumber);
}
