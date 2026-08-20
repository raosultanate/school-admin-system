package com.schooladmin.system.service;

import com.schooladmin.system.domain.Person;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

// @Service: a @Component specialization -- tells Spring's component scan "build one of
// these and put it in the container." No new PersonService(...) exists anywhere in this
// codebase; Spring calls the constructor below itself.
@Service
public class PersonService {

    private static final Logger log = LoggerFactory.getLogger(PersonService.class);

    private final List<Person> people;

    // Constructor injection: Spring sees this is the only constructor, sees it needs a
    // List<Person>, and looks in its container for a bean of that exact type -- the one
    // SchoolAdminSystemApplication.initialPeople() produced. No @Autowired needed here;
    // that's only required when a class has more than one constructor and Spring needs to
    // be told which one to use for injection.
    public PersonService(List<Person> people) {
        this.people = people;
        log.info("PersonService constructed with {} people", people.size());
    }

    // @PostConstruct: Spring calls this automatically, exactly once, right after the
    // constructor finishes AND every dependency is already injected -- but before this bean
    // is handed to anything that depends on it (PersonServiceStartupRunner, in our case).
    // With constructor injection like ours, the constructor itself could do this same setup
    // work -- this hook matters more for field/setter injection, where the constructor
    // alone can't guarantee dependencies are set yet. Used here just to show the hook exists
    // and exactly when it fires relative to the constructor.
    @PostConstruct
    void logInitialization() {
        log.info("PersonService ready ({} people loaded)", people.size());
    }

    // @PreDestroy: the mirror image -- called once, right before Spring destroys this bean.
    // Happens on graceful shutdown (Ctrl-C into ./mvnw spring-boot:run) as the
    // ApplicationContext closes. Nothing to release for a plain in-memory list, but this is
    // where you'd close a database connection, flush a cache, stop a background thread, etc.
    @PreDestroy
    void logShutdown() {
        log.info("PersonService shutting down");
    }

    public List<String> describeAll() {
        return people.stream().map(Person::describe).toList();
    }
}
