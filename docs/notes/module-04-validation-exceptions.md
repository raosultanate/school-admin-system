# Module 4 — Validation & Exception Handling in Spring

Code: `dto/ErrorResponse.java`, `controller/GlobalExceptionHandler.java`,
`domain/exception/NotFoundException.java`, `service/{Student,Teacher,Course}Service.java`,
Bean Validation annotations on the three `*Request` records. See
[`annotations.md`](annotations.md) for `@RestControllerAdvice`/`@ExceptionHandler`/`@Valid`
and friends.

## The experiment: prove the gap exists first

Same approach as Module 3's DTO lesson — before writing any fix, two real problems were
demonstrated live against the Module 3 code as it stood.

**Problem 1 — a duplicate `studentNumber` produced `500`, with a full stack trace leaked to
the client:**

```bash
curl -X POST http://localhost:8080/api/students \
  -d '{"firstName":"Bob","studentNumber":"S-1001", ...}'   # S-1001 already existed
```
```json
{
  "status": 500,
  "trace": "org.springframework.dao.DataIntegrityViolationException: could not execute statement [Unique index or primary key violation...] ... at org.hibernate... at org.h2.jdbc...",
  ...
}
```
The response body included the entire Hibernate/H2 call stack, the raw failed SQL, and the
database's internal constraint name — real information disclosure, not just an ugly error.
Root cause: nothing was catching `DataIntegrityViolationException` (Spring's translated form
of the database's unique-constraint rejection), so Spring's own default fallback handler
dumped everything it had.

**Problem 2 — a course with a blank title and negative capacity was silently accepted:**

```bash
curl -X POST http://localhost:8080/api/courses -d '{"title":"","capacity":-500}'
# -> 201 Created, garbage data saved exactly as sent
```
No validation existed anywhere on the DTOs.

## A detour first: should `studentNumber` be auto-generated?

Mid-module, a real design question came up: now that `Student` has an auto-generated `id`
(the primary key), is `studentNumber` redundant? No — they're different *kinds* of
identifier, a genuinely common pattern:

- **`id`** — a surrogate key. Meaningless, database-generated, exists purely for internal
  references (joins, URLs). Nobody outside the system should need to know it.
- **`studentNumber`** — a natural/business key. The real-world identifier the school
  actually uses. Needs to stay stable *because of business rules*, independent of whatever
  the database's internal primary key happens to be (which could change if data were ever
  migrated or merged).

Given that, `studentNumber` moved from being client-supplied to **server-generated** — a
realistic design (many real schools auto-issue numeric student IDs):

```java
// StudentService
private String generateUniqueStudentNumber() {
    String candidate;
    do {
        candidate = String.valueOf(100_000_000 + RANDOM.nextInt(900_000_000));
    } while (studentRepository.existsByStudentNumber(candidate));
    return candidate;
}
```

9 digits, checked against the database before use, retried on the (extremely rare) chance of
a collision. `StudentRequest` no longer has a `studentNumber` field at all — the same "no
field to attack" fix pattern as `id` in Module 3, applied to a second field. Confirmed live:
creating two students back-to-back produced different server-generated numbers
(`717901728`, `662318045`), and neither request supplied one.

## Introducing `StudentService` — and where it converged with exception handling

Generating a unique number with a retry loop is genuine business logic, not "translate
HTTP" (controller's job) or "talk to the database" (repository's job) — so it moved into a
new `StudentService` (`@Service`), the same pattern as `PersonService` from Module 1. This
is earlier than the roadmap's dedicated Module 5 (Service Layer), done deliberately small
and honest for this one real need rather than waiting.

This converged naturally with the exception-handling work already planned: `StudentService`
became the natural place to *throw* `StudentNotFoundException`, instead of the controller
manually building a `404` `ResponseEntity`:

```java
public Student findById(Long id) {
    return studentRepository.findById(id)
            .orElseThrow(() -> new StudentNotFoundException("No student found with id " + id));
}
```

The controller simplified accordingly — no more `.map(...).orElseGet(() ->
ResponseEntity.notFound().build())` anywhere in `StudentController`:

```java
@GetMapping("/{id}")
public StudentResponse getStudentById(@PathVariable Long id) {
    return StudentResponse.from(studentService.findById(id));
}
```

A plain return now always means success — the exception (if any) is handled entirely
elsewhere, by `GlobalExceptionHandler`.

## Going further: making the pattern consistent across all three resources

`Teacher`/`Course` still had the old manual `.map(...).orElseGet(() ->
ResponseEntity.notFound().build())` pattern at this point — working correctly, but a
different *mechanism* from `Student`'s new throw-based one. Asked directly about it: "I want
to follow best practice while building this, that way I learn all this well" — so the
inconsistency got fixed rather than left as two valid-but-different styles.

`TeacherService`/`CourseService` were added, mirroring `StudentService`'s shape (minus the
number generation, which is Student-specific). Rather than three separate, near-identical
`@ExceptionHandler` methods (one per resource's not-found exception), an abstract
`NotFoundException` base class was introduced:

```java
public abstract class NotFoundException extends RuntimeException {
    protected NotFoundException(String message) { super(message); }
    protected NotFoundException(String message, Throwable cause) { super(message, cause); }
}
```

`StudentNotFoundException`/`TeacherNotFoundException`/`CourseNotFoundException` all extend
it — a deliberate divergence from `StudentNotFoundException`'s frozen `java-refresher`
ancestor (which extends `RuntimeException` directly), made because this project's copy needs
to plug into shared handling that `java-refresher`'s standalone version has no reason to
support. `abstract` on purpose: always throw a specific subtype, never the base directly —
the subtype is what makes a stack trace or log line self-explanatory.

Confirmed live across all three resources and multiple verbs (`GET`/`PUT`/`DELETE` on
missing ids) — identical `ErrorResponse` shape every time, e.g.
`{"status":404,"message":"No teacher found with id 999",...}`.

## `GlobalExceptionHandler` — one place, every controller

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Matches StudentNotFoundException, TeacherNotFoundException, CourseNotFoundException,
    // and any future NotFoundException subtype -- one method, not one per resource.
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException e, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }
    // ... DuplicateEnrollmentException -> 409, InvalidStudentDataException -> 400,
    //     MethodArgumentNotValidException -> 400, DataIntegrityViolationException -> 409
}
```

`@RestControllerAdvice` applies to *every* `@RestController` in the app automatically —
`Teacher`/`Course` benefit from the `MethodArgumentNotValidException`/
`DataIntegrityViolationException` handlers without either controller referencing this class
at all, same as before; now their not-found cases are covered the identical way too.

**Honesty note on two of the three pre-existing exceptions:** `DuplicateEnrollmentException`
and `InvalidStudentDataException` are wired into the handler (satisfying the roadmap's "wire
the existing custom exceptions" goal), but neither currently has a natural trigger in this
REST flow — `studentNumber` is server-generated with a pre-check now, so a client can't
supply a duplicate; nothing currently does the kind of low-level-parsing-failure translation
`InvalidStudentDataException` exists for. Registered and ready rather than forced into an
artificial use.

Every response, regardless of cause, takes the same shape (`ErrorResponse`) — deliberately
**without** a stack trace or any internal detail, the direct opposite of the leaked response
from Problem 1:

```java
public record ErrorResponse(Instant timestamp, int status, String message, String path) {}
```

## Bean Validation — rejecting garbage before it reaches a repository

```java
public record CourseRequest(
        @NotBlank(message = "must not be blank") String title,
        @Min(value = 1, message = "must be at least 1") int capacity) { ... }
```

```java
@PostMapping
public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request) { ... }
```

`@Valid` on the parameter triggers validation *before* the method body runs at all — a
failure throws `MethodArgumentNotValidException` automatically, caught by
`GlobalExceptionHandler`. Confirmed live, re-running Problem 2's exact request:

```
Before: 201 Created, garbage data saved as-is
After:  400 Bad Request, "capacity: must be at least 1; title: must not be blank"
```

## Full before/after, confirmed live

| Scenario | Before | After |
|---|---|---|
| Duplicate `studentNumber` on create | `500`, full stack trace leaked | No longer reachable via the API at all — `studentNumber` is server-generated with a pre-check |
| Blank title / negative capacity | `201`, garbage silently saved | `400`, clean validation message |
| `GET` a missing student | (worked correctly since Module 3's `ResponseEntity` fix) | Same `404`, now via `StudentNotFoundException` + `GlobalExceptionHandler` instead of a manual check in the controller |
| Invalid email format | Silently accepted | `400`, `"email: must be a valid address"` |
| `GET`/`PUT`/`DELETE` a missing teacher/course | Manual `.orElseGet(() -> ResponseEntity.notFound().build())` in each controller — worked, but a different mechanism from `Student` | Same throw-based pattern as `Student`, via `TeacherService`/`CourseService` + the shared `NotFoundException` base — confirmed live across `GET`, `PUT`, and `DELETE` |
