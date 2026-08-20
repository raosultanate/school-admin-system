package com.schooladmin.system;

import com.schooladmin.system.domain.Student;
import com.schooladmin.system.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

// Same pattern as PersonServiceStartupRunner: @Component gets it built, implementing
// CommandLineRunner gets run() called automatically after startup. Spring calls every
// CommandLineRunner bean it finds -- both this one and PersonServiceStartupRunner will fire.
@Component
class StudentRepositoryStartupRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StudentRepositoryStartupRunner.class);

    private final StudentRepository studentRepository;

    StudentRepositoryStartupRunner(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public void run(String... args) {
        Student ada = new Student("Ada", "Lovelace", "ada@school.edu", "S-1001", 2024);
        log.info("Before save: id = {}", ada.getId());

        Student saved = studentRepository.save(ada);
        log.info("After save: id = {}", saved.getId());

        Optional<Student> found = studentRepository.findById(saved.getId());
        log.info("findById({}): {}", saved.getId(), found.map(Student::describe).orElse("not found"));

        // Derived query method -- no SQL written anywhere, just a method name.
        Optional<Student> byNumber = studentRepository.findByStudentNumber("S-1001");
        log.info("findByStudentNumber(\"S-1001\"): {}", byNumber.map(Student::describe).orElse("not found"));

        Optional<Student> missing = studentRepository.findByStudentNumber("S-9999");
        log.info("findByStudentNumber(\"S-9999\"): {}", missing.map(Student::describe).orElse("not found"));

        log.info("Total students in database: {}", studentRepository.count());
    }
}
