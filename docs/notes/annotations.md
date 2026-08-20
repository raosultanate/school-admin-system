# Annotations — what each one does and where it's used

A running reference, not tied to one module — annotations get added here the first time
they show up in the code, and this file is where to look them up later instead of
re-deriving "wait, what does this one actually do again?"

**The one distinction that matters more than any single annotation:** an annotation is just
a label on your code — it isn't a bean. The *object Spring creates* because it saw the
label is the bean. `@Service` itself is never a bean; the one `PersonService` object Spring
builds because of `@Service` is.

## `@Component` is the umbrella

`@Component`/`@Service`/`@Repository`/`@Controller` aren't four separate mechanisms —
`@Service`, `@Repository`, and `@Controller` are all `@Component` underneath, each just
carrying a more specific meaning (and, for `@Repository`, one real bit of extra behavior)
on top:

```
@Component  (the umbrella — "Spring, build and manage this")
   ├── @Service     business logic lives here (PersonService)
   ├── @Repository  data access lives here, + auto exception translation (StudentRepository)
   └── @Controller  handles HTTP requests here (StudentController, via @RestController)
```

Anything done with `@Service` could technically be done with plain `@Component` instead —
Spring would still find and build it the same way via component scanning. The specific ones
exist purely so the code's intent is obvious at a glance; `@Repository` is the one exception
where the label also changes real behavior (translating database-specific exceptions into
Spring's own consistent hierarchy).

| Annotation | What it does | Where it's used | First appeared |
|---|---|---|---|
| `@SpringBootApplication` | A bundle of three annotations at once: `@Configuration` (allows `@Bean` methods on this class), `@ComponentScan` (makes Spring go looking for `@Component`/`@Service`/etc. classes at all), `@EnableAutoConfiguration` (auto-starts Tomcat, H2, etc. based on what's on the classpath). Without this, none of the annotations below would do anything — nothing would be scanning for them. | `SchoolAdminSystemApplication` class | Module 0 (project skeleton) |
| `@Bean` | Put on a method (inside a `@Configuration` class, or — like here — a `@SpringBootApplication` class, which already is one). Spring calls the method once at startup and registers whatever it returns as a bean. The only option for types you don't own and can't annotate directly (e.g. `List`, a JDK interface). | `initialPeople()` method in `SchoolAdminSystemApplication` | Module 1 |
| `@Component` | The base "Spring, build one of these and manage it" annotation for a class *you wrote*. Found automatically by component scanning — no manual registration. `@Service`/`@Repository`/`@Controller` are all secretly `@Component` underneath, with a more specific name. | `PersonServiceStartupRunner` class | Module 1 |
| `@Service` | A `@Component` specialization signaling "business logic lives here." Functionally identical to plain `@Component` for scanning purposes — the difference is purely about communicating intent to whoever reads the code. | `PersonService` class | Module 1 |
| `@PostConstruct` | Not Spring's own annotation (it's `jakarta.annotation`, standard Java), but Spring honors it: calls the annotated method once, automatically, right after the constructor runs and every dependency is already injected — before the bean is handed to anything that depends on it. | `PersonService.logInitialization()` | Module 1 |
| `@PreDestroy` | The mirror of `@PostConstruct` — calls the annotated method once, right before the container destroys the bean (e.g. on graceful shutdown). Where you'd release a resource (close a connection, stop a thread) if there were one to release. | `PersonService.logShutdown()` | Module 1 |
| `@Repository` | Not written explicitly anywhere — Spring Data JPA applies it automatically to every interface extending `JpaRepository`. Same `@Component` mechanism as `@Service`, plus translates database-specific exceptions into Spring's own consistent hierarchy. | `StudentRepository`/`TeacherRepository`/`CourseRepository` (applied automatically) | Module 2 |
| `@RestController` | A `@Controller` (itself a `@Component`) that also assumes every method's return value should be written directly into the HTTP response body as JSON — no separate step needed to say "send this back." | `StudentController`/`TeacherController`/`CourseController` classes | Module 3 |

## Web / REST annotations — routing an HTTP request to a method

These control *which* method handles a given HTTP request, once `@RestController` has
already gotten the class built as a bean. A different job again from either table above —
not "is this a bean," but "which bean method does this specific request go to."

| Annotation | What it does | Where it's used | First appeared |
|---|---|---|---|
| `@RequestMapping("/api/students")` | A base path every endpoint in the class is relative to — `@GetMapping` inside this class means `GET /api/students`, not just `GET /`. | `StudentController` class | Module 3 |
| `@GetMapping`/`@PostMapping`/`@PutMapping`/`@DeleteMapping` | Maps one method to one HTTP verb (+ optional path suffix, e.g. `@GetMapping("/{id}")`). Same idea as `@RequestMapping`, specialized per verb — `@GetMapping` is shorthand for `@RequestMapping(method = GET)`. | All four `Student`/`Teacher`/`Course` controller methods | Module 3 |
| `@PathVariable` | Pulls a `{...}` segment out of the URL and passes it as the annotated method parameter — `@GetMapping("/{id}")` + `@PathVariable Long id` means whatever's in that URL slot becomes `id`. | `getStudentById(@PathVariable Long id)` | Module 3 |
| `@RequestBody` | Deserializes the incoming HTTP request body (JSON) into the annotated parameter's type, via Jackson. What made the DTO/entity-binding vulnerability (and its fix) possible to demonstrate at all. | `createStudent(@RequestBody StudentRequest request)` | Module 3 |
| `@RestControllerAdvice` | A `@Component` that intercepts exceptions thrown by *any* `@RestController` in the whole app, not just one — applies to `Student`/`Teacher`/`Course` controllers automatically, none of them reference it. Combines `@ControllerAdvice` (the interception) with `@RestController`'s "write the return value straight into the response body as JSON" behavior. | `GlobalExceptionHandler` class | Module 4 |
| `@ExceptionHandler(SomeException.class)` | Marks a method inside a `@RestControllerAdvice` (or a controller itself) as the handler for one specific exception type. When that exception reaches Spring uncaught, this method runs instead of Spring's own default error handling — confirmed live: replaced a `500` with a full leaked stack trace with a clean `404`/`409`/`400` and a consistent body shape. | Every method in `GlobalExceptionHandler` | Module 4 |

**Not an annotation, but essential to this module:** `ResponseEntity<T>` is a regular Java
class that wraps a response body together with an explicit HTTP status code and headers —
`ResponseEntity.ok(body)` (200), `ResponseEntity.status(HttpStatus.CREATED).body(body)`
(201), `ResponseEntity.notFound().build()` (404, no body), `ResponseEntity.noContent().build()`
(204). Without it, every endpoint just gets Spring's default (200 for anything returned
normally), which is wrong for a missing resource (should be 404) or a fresh creation
(should be 201) — confirmed live in `StudentController.getStudentById()`, which returned 500 before
this was introduced and 404 after.

## JPA / Hibernate annotations — a different system entirely

Everything in the table above is read by **Spring's** container (component scanning, bean
wiring). The table below is read by **Hibernate**, a completely separate framework, for a
completely different job: mapping Java objects to database tables and columns. Same `@`
syntax, two unrelated annotation systems — nothing here creates a Spring bean, and none of
these classes are `@Component`s just because they're entities.

| Annotation | What it does | Where it's used | First appeared |
|---|---|---|---|
| `@MappedSuperclass` | This class is never its own database table — but its fields become real columns on whatever `@Entity` extends it. The JPA analog of "only ever subclassed, never instantiated directly," which was already true of this class in plain OOP terms. | `Person` class | Module 2 |
| `@Entity` | This class gets a real database table, created automatically from its fields (and any inherited from a `@MappedSuperclass`). | `Student`, `Teacher`, `Course` classes | Module 2 |
| `@Id` | Marks the primary key field/column — every entity needs exactly one. | `Person.id` | Module 2 |
| `@GeneratedValue(strategy = GenerationType.IDENTITY)` | The database auto-generates this field's value on insert (an auto-increment column) — the application never sets it. `IDENTITY` specifically means "let the database's own auto-increment feature handle it," the simplest strategy and the one most databases support identically. | `Person.id` | Module 2 |
| `@Column(nullable = false, unique = true)` | Explicit column constraints, enforced by the database itself — here, `studentNumber` must be present and unique on every row. `@Column` is optional on most fields (Hibernate infers a sensible default column); used explicitly when a real constraint needs stating. | `Student.studentNumber` | Module 2 |
| `@Table(name = "...")` | Names the table explicitly. Without it, Hibernate's default naming strategy derives a name from the class name (typically the singular, lowercased class name) — being explicit avoids depending on that default. | `Student`, `Teacher`, `Course` classes | Module 2 |
| `@Enumerated(EnumType.STRING)` | Controls how an enum field is stored. Without it, Hibernate defaults to `EnumType.ORDINAL` — storing the constant's *declaration position* (0, 1, 2...) as a plain integer, which silently breaks if the enum's order ever changes. `STRING` stores the constant's actual name instead — self-describing, safe against reordering. | `Teacher.department` | Module 2 |

## Bean Validation annotations — a fourth, separate system

Yet another distinct annotation system (`jakarta.validation`, Hibernate Validator is the
implementation on the classpath) — nothing to do with Spring's container, HTTP routing, or
JPA/Hibernate's table mapping. These are read by the Bean Validation framework, triggered
specifically by `@Valid` appearing on a method parameter.

| Annotation | What it does | Where it's used | First appeared |
|---|---|---|---|
| `@Valid` | Put on a `@RequestBody` parameter -- tells Spring to run Bean Validation on the deserialized object *before* the controller method body executes. A failure throws `MethodArgumentNotValidException` automatically; the method body never runs at all. | `createStudent(@Valid @RequestBody StudentRequest request)` and the `Teacher`/`Course` equivalents | Module 4 |
| `@NotBlank` | Fails if the field is `null`, empty, or all whitespace. | `StudentRequest.firstName`/`lastName`/`email`, `TeacherRequest`'s equivalents, `CourseRequest.title` | Module 4 |
| `@Email` | Fails if the field isn't shaped like a valid email address. Stacks with `@NotBlank` on the same field — both must pass. | `StudentRequest.email`, `TeacherRequest.email` | Module 4 |
| `@Min`/`@Max` | Fails if a numeric field is below/above the given bound. | `StudentRequest.enrollmentYear` (2000-2100), `CourseRequest.capacity` (`@Min(1)`) | Module 4 |
| `@NotNull` | Fails if the field is `null` -- for non-`String` types where `@NotBlank` doesn't apply. | `TeacherRequest.department` | Module 4 |

Confirmed live: `POST /api/courses` with `{"title": "", "capacity": -500}` went from a
silently-accepted `201` (no validation existed) to a `400` with
`"capacity: must be at least 1; title: must not be blank"` once `@Valid` + these annotations
were added.

## Rule going forward

Every time a new annotation gets used for the first time, it gets a row here — what it
does, in plain terms, and where to see it actually being used in this codebase, in whichever
table it belongs to (Spring DI, Web/REST, JPA/Hibernate, or Bean Validation — four separate
systems, same `@` syntax).
