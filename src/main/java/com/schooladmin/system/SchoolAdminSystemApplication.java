package com.schooladmin.system;

import com.schooladmin.system.domain.AccessLevel;
import com.schooladmin.system.domain.Admin;
import com.schooladmin.system.domain.Department;
import com.schooladmin.system.domain.Person;
import com.schooladmin.system.domain.Student;
import com.schooladmin.system.domain.Teacher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Entry point. {@code @SpringBootApplication} triggers component scanning (finds every
 * {@code @Component}/{@code @Service}/{@code @Repository} under this package) and
 * auto-configuration (wires up the embedded server, JPA, etc. based on what's on the
 * classpath) — see {@code docs/notes/module-01-ioc-dependency-injection.md}.
 */
@SpringBootApplication
public class SchoolAdminSystemApplication {

	public static void main(String[] args) {
		// This one call does two things in sequence: (1) builds the ApplicationContext --
		// creates every bean below and wires their constructors together -- then (2) once
		// that's done, finds every bean that implements CommandLineRunner and calls its
		// run() method. PersonServiceStartupRunner gets invoked because of step (2), not
		// because anything here calls it directly.
		SpringApplication.run(SchoolAdminSystemApplication.class, args);
	}

	// @SpringBootApplication is itself meta-annotated with @Configuration, so @Bean methods
	// can live right here. This is how a type we don't own (List<Person> -- can't put
	// @Component on an interface) still gets into the container: call a method once, and
	// whatever it returns becomes a bean, matched to whoever asks for that exact type
	// (PersonService's constructor, in this case).
	@Bean
	List<Person> initialPeople() {
		return List.of(
				new Student("Ada", "Lovelace", "ada@school.edu", "S-1001", 2024),
				new Teacher("Alan", "Turing", "alan@school.edu", Department.COMPUTER_SCIENCE),
				new Admin("Grace", "Hopper", "grace@school.edu", AccessLevel.SUPER_ADMIN)
		);
	}

}
