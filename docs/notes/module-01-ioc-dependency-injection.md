# Module 1 — Spring Boot Fundamentals: IoC & Dependency Injection

Code: `SchoolAdminSystemApplication.java` (the `initialPeople()` bean),
`service/PersonService.java`, `PersonServiceStartupRunner.java`. See also
[`annotations.md`](annotations.md) for a quick-reference table of every annotation used here.

## Bean vs. component vs. annotation — get this straight first

These three get used almost interchangeably in casual talk, but they're not the same kind
of thing:

- **A bean** is an *object* — any object that ends up managed inside Spring's container,
  however it got there. It's the noun: "that object is a bean."
- **`@Component`** (and its specializations `@Service`/`@Repository`/`@Controller`) is *one
  way* of telling Spring to create a bean: annotate a class you wrote, and component
  scanning finds it and builds one.
- **An annotation** (`@Service`, `@Bean`, etc.) is never itself a bean — it's just a label.
  `@Service` sitting on `PersonService`'s class declaration is not a bean; the one
  `PersonService` *object* Spring builds because it saw that label is the bean.

`PersonService` is both a bean and a component (it's a bean *because* it's a component).
The `List<Person>` bean below is a bean but **not** a component — nothing was annotated, a
method just produced it.

## The IoC container, in one sentence

Normally, `PersonService` would create its own `List<Person>` — either `new` one itself or
fetch it from somewhere. **Inversion of Control** flips that: the *container* decides what
`PersonService` gets and hands it in, rather than `PersonService` going and getting it.
Search this whole codebase — `new PersonService(...)` does not exist anywhere. The
container calls that constructor, once, at startup.

The container itself is the `ApplicationContext`. `SpringApplication.run(...)` in `main()`
builds one; everything below is really just describing how it decides what to build and how
to wire it together.

## Component scanning and stereotype annotations

```java
@Service
public class PersonService { ... }
```

`@SpringBootApplication` triggers **component scanning**: Spring walks every class in this
package and its subpackages looking for `@Component`/`@Service`/`@Repository`/etc., and
registers each one it finds as a bean — automatically, no manual registration list. That's
*why* `PersonService` and `PersonServiceStartupRunner` only need the right annotation to be
picked up; nothing else references them by name anywhere.

`@Repository` (not used yet) is worth naming here: same mechanism as `@Service`, but it also
adds automatic translation of database-specific exceptions into Spring's own consistent
exception hierarchy — real added behavior, not just a label. No repository exists yet since
there's no persistence layer to wrap; that's Module 2.

## `@Bean` methods — the other way to register a bean

```java
@Bean
List<Person> initialPeople() {
    return List.of(
            new Student("Ada", "Lovelace", "ada@school.edu", "S-1001", 2024),
            new Teacher("Alan", "Turing", "alan@school.edu", Department.COMPUTER_SCIENCE),
            new Admin("Grace", "Hopper", "grace@school.edu", AccessLevel.SUPER_ADMIN)
    );
}
```

Component scanning only finds classes *you* wrote and annotated. `List` is a JDK interface —
there's no file of ours to put `@Component` on. For a case like this (or wiring up any type
you don't own), a `@Bean` method inside a `@Configuration` class is the other way to add
something to the container: call the method once, register whatever it returns.

`@SpringBootApplication` is itself meta-annotated with `@Configuration` — so `@Bean` methods
can live directly on the main class, no separate `@Configuration` class needed for one small
seed bean like this.

**The rule for choosing between the two:** annotate the class directly if you own it and a
plain constructor call is enough (`@Service` on `PersonService`). Reach for a `@Bean` method
when you don't own the type, or building it needs more than a bare constructor call
(`initialPeople()` needs both — `List` isn't ours, and it needs a method body to assemble
the specific seed data).

## Constructor injection

```java
public PersonService(List<Person> people) {
    this.people = people;
    log.info("PersonService constructed with {} people", people.size());
}
```

No `@Autowired` here, and none is needed: since Spring 4.3, if a class has **exactly one
constructor**, Spring uses it automatically, resolving each parameter from the container by
type. `@Autowired` on a constructor is only required when a class has *multiple*
constructors and Spring needs to be told which one to use.

Constructor injection means `people` can be `private final` — the object is fully built
with everything it needs in one step, no window where it exists with a missing dependency.
It's also trivially testable without Spring at all: `new PersonService(List.of(...))` works
in a plain JUnit test.

## Proving it end-to-end: `CommandLineRunner`

```java
@Component
class PersonServiceStartupRunner implements CommandLineRunner {
    private final PersonService personService;

    PersonServiceStartupRunner(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public void run(String... args) {
        personService.describeAll().forEach(log::info);
    }
}
```

Two separate things are happening on this class, easy to conflate: `@Component` controls
whether Spring **builds** this bean at all. `implements CommandLineRunner` controls whether
Spring **calls anything** on it afterward. Drop the `implements` and this class would still
get built — it would just sit there unused, same as any bean nothing asks for.

`SpringApplication.run(...)` does its work in two phases: first it builds the
`ApplicationContext` (every bean above, wired together), then — once that's done — it asks
the container "give me every bean that implements `CommandLineRunner`" and calls `.run(...)`
on each one. Spelled out, that second phase is roughly:

```java
// what SpringApplication.run() does internally, roughly
for (CommandLineRunner runner : context.getBeansOfType(CommandLineRunner.class)) {
    runner.run(args);
}
```

Running `./mvnw spring-boot:run` produces, among the framework's own startup logging:

```
... PersonService              : PersonService constructed with 3 people
... PersonService              : PersonService ready (3 people loaded)
... SchoolAdminSystemApplication : Started SchoolAdminSystemApplication in 0.25 seconds
... PersonServiceStartupRunner : Ada Lovelace is a student (#S-1001, enrolled 2024)
... PersonServiceStartupRunner : Alan Turing teaches in Computer Science
... PersonServiceStartupRunner : Grace Hopper administers the system (SUPER_ADMIN)
```

Tracing that backward: `PersonServiceStartupRunner` got a real, working `PersonService` —
which means `PersonService`'s constructor ran with a real `List<Person>` — which means
`initialPeople()` ran — and none of it required a line of manual wiring. That's dependency
injection actually happening, not just defined.

## Bean lifecycle basics

```java
@PostConstruct
void logInitialization() {
    log.info("PersonService ready ({} people loaded)", people.size());
}

@PreDestroy
void logShutdown() {
    log.info("PersonService shutting down");
}
```

Every singleton bean (the default scope — one shared instance per `ApplicationContext`,
all that's used so far here) goes through the same lifecycle: **constructed** (dependencies
injected) → **`@PostConstruct`** (runs once, right after the constructor, before this bean
is handed to anything depending on it — confirmed live: the "ready" log line appears
immediately after the "constructed" line, in that order, every run) → **in use** for as
long as the app runs → **`@PreDestroy`** on graceful shutdown (`Ctrl-C` into
`./mvnw spring-boot:run`), before the `ApplicationContext` closes. Confirmed live too: the
"shutting down" line appears right after Tomcat's graceful shutdown completes and right
before the JPA `EntityManagerFactory` closes.

With constructor injection like ours, the constructor itself could have done the
`@PostConstruct` work just as correctly — this hook matters more when a bean uses
field/setter injection, where the constructor alone can't guarantee dependencies are already
set. It's used here specifically to show the hook exists and exactly when it fires relative
to the constructor.
