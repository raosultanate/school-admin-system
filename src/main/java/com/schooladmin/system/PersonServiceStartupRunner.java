package com.schooladmin.system;

import com.schooladmin.system.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// @Component gets this bean built by Spring, same mechanism as @Service. Separately,
// "implements CommandLineRunner" is what gets its run() method actually called --
// SpringApplication.run() looks up every CommandLineRunner bean after startup finishes and
// invokes each one. Two different things: @Component controls whether Spring builds it at
// all; implementing CommandLineRunner controls whether Spring calls anything on it
// afterward. Drop the "implements" and this class would still get built, just never used.
@Component
class PersonServiceStartupRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PersonServiceStartupRunner.class);

    private final PersonService personService;

    // Same constructor-injection pattern as PersonService -- Spring hands in the
    // PersonService it already built, matched by type.
    PersonServiceStartupRunner(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public void run(String... args) {
        personService.describeAll().forEach(log::info);
    }
}
