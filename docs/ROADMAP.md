# School Admin System — Learning Roadmap

**Goal:** learn Spring Boot well enough for an interview by building a real app, while
refreshing core Java (inheritance, polymorphism, exception handling) along the way.

**How this works:** each module below pairs a Java concept refresher with a Spring Boot
concept, both applied directly to a feature of the School Admin System. We check items off
as we go. Architecture diagrams ([architecture.md](architecture.md)) and per-module
write-ups ([notes/](notes/)) live alongside this file and get updated as our understanding
grows — don't expect them to be "finished" early.

**Domain we're building:** an admin can manage `Teacher`s and `Student`s (both are kinds of
`Person`), organize them into `Course`s, and `Enroll` students into courses. Small enough to
finish, big enough to hit every mainstream Spring Boot concept.

**Java refresher order (Modules 1–4):** OOP → Enums → Exceptions → Collections & Generics.
This follows how Java's own language tutorials sequence things: enums are really just a
constrained class, so they belong right after inheritance/polymorphism while that's fresh;
exceptions need the class hierarchy to make sense (an exception type is just a subclass);
collections and generics come last because collection methods routinely throw the
exceptions just covered, and a typed collection is easiest to appreciate once you've felt
the pain of *not* having one.

---

## Module 0 — Environment & Project Setup ✅
- [x] Install JDK 21 (via SDKMAN, no sudo needed)
- [x] Install Maven (via SDKMAN)
- [x] Install VS Code Java + Spring Boot extensions
- [x] Generate project skeleton via Spring Initializr (Spring Boot 4.1, Maven, Java 21)
- [x] Confirm `./mvnw compile` works
- [x] git init this project, publish to GitHub
- **Notes:** [notes/module-00-environment-setup.md](notes/module-00-environment-setup.md)

## Module 1 — Java OOP Refresher: Inheritance & Polymorphism ✅
*No Spring yet — plain Java, so the OOP concepts aren't tangled up with framework magic.*
- [x] Classes vs objects, constructors, `this`
- [x] Inheritance: `Person` (abstract) → `Student`, `Teacher`
- [x] Method overriding vs overloading, `@Override`, `super`
- [x] Polymorphism: treating `Student`/`Teacher` as `Person`, dynamic dispatch
- [x] Abstract classes (as encountered here) — interfaces vs abstract classes still to
      revisit when we hit Module 9 (Strategy pattern)
- [x] Encapsulation (private final fields + getters) and abstraction (`abstract describe()`)
      — noticed as a bonus since the same code already demonstrates all four OOP pillars
- **Build:** plain Java domain classes (`Person`, `Student`, `Teacher`) with a `main()` to
  exercise them, no database yet. ✅ `domain/{Person,Student,Teacher}.java`,
  `playground/OopDemo.java`.
- **Notes:** [notes/module-01-inheritance-polymorphism.md](notes/module-01-inheritance-polymorphism.md)
- **Interview payoff:** "explain the four pillars of OOP with an example" answered from
  your own code.

## Module 2 — Java Refresher: Enums
*Still plain Java. An enum is really just a class with a fixed, known set of instances —
the natural next step right after inheritance/polymorphism, not a separate topic.*
- [ ] What an enum actually is under the hood (a class; each constant is a singleton
      instance of it) — not just "a fancy list of constants"
- [ ] Enums with fields, a constructor, and methods — not just bare names
- [ ] `switch` over an enum
- [ ] An enum implementing an interface — polymorphism again, in a new shape
- [ ] `EnumMap`/`EnumSet` — worth knowing exist; explained properly once Module 4 covers
      `Map`/`Set` in general
- **Build:** replace `Admin`'s raw `String accessLevel` with a proper `AccessLevel` enum,
  and consider a `Department` enum for `Teacher` — strengthens Module 1's domain model
  instead of starting a disconnected new example.
- **Interview payoff:** "why use an enum instead of a bunch of String/int constants" — and
  being able to say *why* from your own refactor, not just recite the textbook answer.

## Module 3 — Java Refresher: Exception Handling ✅
- [x] Checked vs unchecked exceptions, the exception hierarchy
- [x] `try`/`catch`/`finally`, try-with-resources
- [x] Custom exceptions (`StudentNotFoundException`, `DuplicateEnrollmentException`,
      `InvalidStudentDataException`)
- [x] Exception chaining, when to catch vs rethrow
- **Build:** ✅ `domain/exception/{StudentNotFoundException,DuplicateEnrollmentException,
  InvalidStudentDataException}.java`, `playground/StudentRegistry.java`,
  `playground/ExceptionHandlingDemo.java`.
- **Notes:** [notes/module-03-exception-handling.md](notes/module-03-exception-handling.md)
- **Interview payoff:** "checked vs unchecked, when would you use a custom exception?"

## Module 4 — Java Refresher: Collections & Generics
*Still plain Java. This is what makes Spring Data's `JpaRepository<T, ID>` (Module 6) and
every `List<...>`/`Map<...>` we've been about to write actually make sense, instead of
being copy-pasted syntax.*
- [ ] The Collections Framework map: `List`, `Set`, `Map` — what each guarantees
      (order? uniqueness? key lookup?) and which our domain will actually need
- [ ] `ArrayList` vs `LinkedList` — underlying structure (resizable array vs doubly-linked
      nodes), what that means for `get(i)` vs `add`/`remove` at the ends, and why `ArrayList`
      is the default choice almost every time in modern Java
- [ ] `HashMap` vs `LinkedHashMap` vs `TreeMap` — Java's "dictionary": key → value lookup,
      what "average O(1) lookup" actually depends on (`hashCode()`), and when ordering
      (insertion order / sorted order) matters enough to pay for it
- [ ] The `equals()`/`hashCode()` contract — why `HashMap`/`HashSet` silently misbehave if
      you override one but not the other, and why our `Person` hierarchy doesn't need this
      yet (no `Person` has gone into a `HashSet` or been a `HashMap` key so far)
- [ ] Generics: why (`List<Student>` vs a raw `List` full of casts), a bounded type
      parameter (`<T extends Comparable<T>>`), and reading a generic interface signature
      like `JpaRepository<T, ID>` before we actually meet it
- **Build:** a small in-memory `StudentRoster` — a `List<Student>` for ordered iteration, a
  `Map<String, List<Student>>` grouping students by something (e.g. year), and a tiny
  hand-rolled generic `InMemoryRepository<T, ID>` (foreshadowing Module 6's real one).
- **Interview payoff:** "ArrayList vs LinkedList", "how does HashMap work", "what are
  generics for", "what's the equals/hashCode contract" — four very common questions, one
  small project.

## Module 5 — Spring Boot Fundamentals: IoC & Dependency Injection
- [ ] What `@SpringBootApplication` actually does (auto-config, component scan)
- [ ] The IoC container / `ApplicationContext`
- [ ] `@Component`, `@Service`, `@Repository`, `@Autowired`, constructor injection
- [ ] Bean lifecycle basics
- **Build:** turn Module 1's domain classes into Spring-managed services, wire a simple
  `PersonService` via constructor injection, run it in the app context.
- **Diagram:** `docs/architecture.md` gets its first layered-architecture sketch.

## Module 6 — Persistence: JPA, Hibernate, Spring Data
- [ ] `@Entity`, `@Id`, `@GeneratedValue`, column mapping
- [ ] Spring Data JPA repositories (`JpaRepository`), derived query methods
- [ ] H2 in-memory DB + H2 console for poking at data
- **Build:** `Student`/`Teacher`/`Course` become JPA entities; `StudentRepository`,
  `TeacherRepository`, `CourseRepository`.
- **Diagram:** first ER diagram in `docs/architecture.md`.

## Module 7 — REST APIs
- [ ] `@RestController`, `@RequestMapping`/`@GetMapping`/etc.
- [ ] DTOs vs entities — why you don't return entities directly
- [ ] `ResponseEntity`, status codes
- **Build:** CRUD REST endpoints for Students and Teachers, tested with `curl`/Postman.

## Module 8 — Validation & Exception Handling in Spring
*Ties directly back to Module 3.*
- [ ] Bean Validation (`@Valid`, `@NotNull`, `@Size`, etc.)
- [ ] `@ControllerAdvice` + `@ExceptionHandler` — mapping custom exceptions to HTTP status
- [ ] Consistent error response shape
- **Build:** wire Module 3's custom exceptions into a global exception handler.

## Module 9 — Service Layer, Transactions & Interfaces
- [ ] `@Transactional` and why it matters
- [ ] Interfaces + Strategy pattern (e.g. a `GradingStrategy`) — more polymorphism, this
  time framework-flavored
- **Build:** `EnrollmentService` with real business rules (course capacity, duplicate
  enrollment checks) using Module 3's exceptions.

## Module 10 — Relationships & Advanced JPA
- [ ] `@OneToMany`, `@ManyToMany` (Student ⇄ Course via Enrollment)
- [ ] Lazy vs eager loading, cascading, N+1 query problem
- **Build:** wire up Enrollment as the join entity between Student and Course.
- **Diagram:** ER diagram in `docs/architecture.md` gets updated with relationships.

## Module 11 — Testing
- [ ] JUnit 5 basics
- [ ] Mockito for mocking dependencies
- [ ] `@SpringBootTest`, `@WebMvcTest`, `@DataJpaTest` — what each actually boots
- **Build:** unit tests for services, slice tests for controllers and repositories.

## Module 12 — Spring Security
- [ ] Authentication vs authorization
- [ ] Roles (`ADMIN`, `TEACHER`) securing endpoints
- [ ] Password hashing, basics of JWT (if time allows)
- **Build:** lock down admin-only endpoints (e.g. only admins can delete a student).

## Module 13 — Polish: Pagination, Logging, Config Profiles
- [ ] Pagination & sorting (`Pageable`)
- [ ] SLF4J logging
- [ ] `application-dev.properties` vs `application-prod.properties`, switching H2 → Postgres
- **Diagram:** `docs/architecture.md` gets a "deployment view" (dev vs prod config).

## Module 14 — API Docs & Packaging
- [ ] springdoc-openapi (Swagger UI)
- [ ] Building an executable jar, basic Dockerfile
- **Stretch:** `docker-compose.yml` with Postgres.

## Module 15 — Interview Wrap-Up
- [ ] Walk back through each module and answer "why did we do it this way" out loud
- [ ] Map each module to likely interview questions
- [ ] Mock Q&A session

---

### Ground rules for how we work
- We build features in small, runnable increments — every module ends with something you
  can actually run and poke at, not just theory.
- Java refreshers happen *just before* the Spring concept that needs them, not all up front.
- `docs/` diagrams are living documents — expect to redraw them as the app grows.
