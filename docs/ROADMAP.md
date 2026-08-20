# School Admin System — Learning Roadmap

**Goal:** learn Spring Boot well enough for an interview by building a real app.

**How this works:** each module below pairs a Spring Boot concept with a feature of the
School Admin System, applied directly to the real domain. We check items off as we go.
Architecture diagrams ([architecture.md](architecture.md)) and per-module write-ups
([notes/](notes/)) live alongside this file and get updated as our understanding grows —
don't expect them to be "finished" early.

**Domain we're building:** an admin can manage `Teacher`s and `Student`s (both are kinds of
`Person`), organize them into `Course`s, and `Enroll` students into courses. Small enough to
finish, big enough to hit every mainstream Spring Boot concept.

**Core Java refresher lives elsewhere:** OOP/polymorphism, enums, exception handling, and
collections/generics were originally modules here, but have been split out into their own
project — [`java-refresher`](../java-refresher) — so this roadmap can stay Spring-only and
that one can stay a standalone, no-framework language reference. The `Person`/`Student`/
`Teacher`/`Admin` domain classes and custom exceptions used below were introduced there
(see its `docs/ROADMAP.md` Modules 1–4) and are still exactly the classes living in
`domain/` here — this project just keeps building on them instead of teaching them from
scratch.

---

## Module 0 — Environment & Project Setup ✅
- [x] Install JDK 21 (via SDKMAN, no sudo needed)
- [x] Install Maven (via SDKMAN)
- [x] Install VS Code Java + Spring Boot extensions
- [x] Generate project skeleton via Spring Initializr (Spring Boot 4.1, Maven, Java 21)
- [x] Confirm `./mvnw compile` works
- [x] git init this project, publish to GitHub
- **Notes:** [notes/module-00-environment-setup.md](notes/module-00-environment-setup.md)

## Module 1 — Spring Boot Fundamentals: IoC & Dependency Injection ✅
- [x] What `@SpringBootApplication` actually does (auto-config, component scan)
- [x] The IoC container / `ApplicationContext`
- [x] `@Component`, `@Service`, `@Repository` (concept only — a real one arrives in Module
      2), `@Autowired` (and why it's not needed here), constructor injection
- [x] Bean lifecycle basics (`@PostConstruct`/`@PreDestroy`, singleton scope) — confirmed
      live: startup order (constructed → `@PostConstruct`) and shutdown order
      (`@PreDestroy` → resources closed)
- **Build:** ✅ `service/PersonService.java` (constructor-injected with a `List<Person>`
  `@Bean` from `SchoolAdminSystemApplication`), `PersonServiceStartupRunner.java` (a
  `CommandLineRunner` proving the whole graph gets wired and runs through the container).
- **Notes:** [notes/module-01-ioc-dependency-injection.md](notes/module-01-ioc-dependency-injection.md),
  [notes/annotations.md](notes/annotations.md)
- **Interview payoff:** "what is dependency injection / IoC, and why constructor injection
  over field injection" answered from your own wiring, not the textbook version.

## Module 2 — Persistence: JPA, Hibernate, Spring Data ✅
- [x] `@Entity`, `@Id`, `@GeneratedValue`, column mapping — `Person` (`@MappedSuperclass`),
      `Student`/`Teacher`/`Course` (`@Entity`), `@Enumerated` for `Teacher.department`
- [x] Spring Data JPA repositories (`JpaRepository`), derived query methods —
      `StudentRepository.findByStudentNumber(...)`, confirmed live (generated SQL, real
      `WHERE` clause, no query written by hand)
- [x] H2 in-memory DB + H2 console for poking at data — confirmed the database is
      genuinely temporary (random name every run, destroyed on stop); console reachable at
      `/h2-console`
- **Build:** ✅ `Student`/`Teacher`/`Course` are real JPA entities; `StudentRepository`,
  `TeacherRepository`, `CourseRepository` all exist. `StudentRepositoryStartupRunner.java`
  proves `save()`/`findById()`/derived queries end-to-end (watched `id` go `null` → `1` on
  save).
- **Diagram:** ✅ first ER diagram in `docs/architecture.md`.
- **Notes:** [notes/module-02-jpa-persistence.md](notes/module-02-jpa-persistence.md)
- **Interview payoff:** "what does `@Entity`/`@Id`/`@GeneratedValue` actually do," "why does
  my entity need a no-arg constructor," "how do derived query methods work" — answered from
  your own entities, not a tutorial's.

## Module 3 — REST APIs ✅
- [x] `@RestController`, `@RequestMapping`/`@GetMapping`/`@PostMapping`/`@PutMapping`/
      `@DeleteMapping`, `@PathVariable`, `@RequestBody`
- [x] DTOs vs entities — why you don't return entities directly. Proven with a real, live
      attack: built `StudentController` without DTOs first, then overwrote an existing
      student by including its `id` in a `POST` body. Fixed with `StudentRequest` (no `id`
      field — structurally impossible to repeat, not just guarded against); re-ran the
      identical attack against the fix and confirmed it now creates a new row instead.
- [x] `ResponseEntity`, status codes — `200`/`201`/`404`/`204` all confirmed live; fixed
      `getStudentById()` returning `500` on a missing id (should be, and now is, `404`)
- **Build:** ✅ full CRUD (`GET` all, `GET` one, `POST`, `PUT`, `DELETE`) for **Students,
  Teachers, and Courses** — went beyond the original Student/Teacher scope since the pattern
  was already proven. All exercised live with `curl`.
- **Notes:** [notes/module-03-rest-apis.md](notes/module-03-rest-apis.md)
- **Interview payoff:** "why use DTOs instead of returning entities" answered with a real
  security bug you caused and fixed yourself, not the textbook reasons.

## Module 4 — Validation & Exception Handling in Spring ✅
*Ties directly back to the custom exceptions already in `domain/exception/`
(`StudentNotFoundException`, `DuplicateEnrollmentException`, `InvalidStudentDataException`).*
- [x] Bean Validation (`@Valid`, `@NotBlank`, `@Email`, `@Min`/`@Max`, `@NotNull`) — proven
      live: garbage course data (blank title, negative capacity) went from a silently
      accepted `201` to a clean `400`
- [x] `@RestControllerAdvice` + `@ExceptionHandler` — mapping custom exceptions to HTTP
      status. Applies to all three controllers automatically, none reference it directly.
- [x] Consistent error response shape (`ErrorResponse`) — deliberately excludes stack traces/
      internal detail, replacing a real leaked-stack-trace `500` (confirmed live before the
      fix) with a clean `409`
- **Build:** ✅ `GlobalExceptionHandler` wires a shared abstract `NotFoundException` (404) —
  `StudentNotFoundException`/`TeacherNotFoundException`/`CourseNotFoundException` all extend
  it, one handler method covers all three (and any future subtype, automatically) —
  `DuplicateEnrollmentException` (409, registered though not currently triggered —
  `studentNumber` became server-generated mid-module), `InvalidStudentDataException` (400,
  also registered for completeness), `MethodArgumentNotValidException` (400), and
  `DataIntegrityViolationException` (409, defensive backstop). New `StudentService`/
  `TeacherService`/`CourseService` (earlier than Module 5, kept small and honest) throw the
  not-found exceptions and simplify all three controllers to pure HTTP ⇄ Java translation.
- **Notes:** [notes/module-04-validation-exceptions.md](notes/module-04-validation-exceptions.md)
- **Interview payoff:** "how do you handle validation and errors consistently across a
  Spring REST API" answered with a real leaked-stack-trace bug you caused and fixed
  yourself, not the textbook version.

## Module 5 — Service Layer, Transactions & Interfaces
- [ ] `@Transactional` and why it matters
- [ ] Interfaces + Strategy pattern (e.g. a `GradingStrategy`) — more polymorphism, this
  time framework-flavored
- **Build:** `EnrollmentService` with real business rules (course capacity, duplicate
  enrollment checks) using the existing custom exceptions.

## Module 6 — Relationships & Advanced JPA
- [ ] `@OneToMany`, `@ManyToMany` (Student ⇄ Course via Enrollment)
- [ ] Lazy vs eager loading, cascading, N+1 query problem
- **Build:** wire up Enrollment as the join entity between Student and Course.
- **Diagram:** ER diagram in `docs/architecture.md` gets updated with relationships.

## Module 7 — Testing
- [ ] JUnit 5 basics
- [ ] Mockito for mocking dependencies
- [ ] `@SpringBootTest`, `@WebMvcTest`, `@DataJpaTest` — what each actually boots
- **Build:** unit tests for services, slice tests for controllers and repositories.

## Module 8 — Spring Security
- [ ] Authentication vs authorization
- [ ] Roles (`ADMIN`, `TEACHER`) securing endpoints
- [ ] Password hashing, basics of JWT (if time allows)
- **Build:** lock down admin-only endpoints (e.g. only admins can delete a student).

## Module 9 — Polish: Pagination, Logging, Config Profiles
- [ ] Pagination & sorting (`Pageable`)
- [ ] SLF4J logging
- [ ] `application-dev.properties` vs `application-prod.properties`, switching H2 → Postgres
- **Diagram:** `docs/architecture.md` gets a "deployment view" (dev vs prod config).

## Module 10 — API Docs & Packaging
- [ ] springdoc-openapi (Swagger UI)
- [ ] Building an executable jar, basic Dockerfile
- **Stretch:** `docker-compose.yml` with Postgres.

## Module 11 — Interview Wrap-Up
- [ ] Walk back through each module and answer "why did we do it this way" out loud
- [ ] Map each module to likely interview questions
- [ ] Mock Q&A session

---

### Ground rules for how we work
- We build features in small, runnable increments — every module ends with something you
  can actually run and poke at, not just theory.
- `docs/` diagrams are living documents — expect to redraw them as the app grows.
