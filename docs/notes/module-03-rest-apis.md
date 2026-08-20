# Module 3 — REST APIs

Code: `controller/{StudentController,TeacherController,CourseController}.java`,
`dto/{StudentRequest,StudentResponse,TeacherRequest,TeacherResponse,CourseRequest,
CourseResponse}.java`. See [`annotations.md`](annotations.md) for the full annotation
reference (`@RestController`, `@GetMapping`/etc., `@PathVariable`, `@RequestBody`).

## The experiment: build it wrong first, on purpose

Rather than being told why entities shouldn't cross the API boundary directly, we built
`StudentController` the naive way first — `@RestController`, endpoints returning/accepting
`Student` (the JPA entity) directly, no DTO — and went looking for where it actually breaks.

```java
@PostMapping
public Student create(@RequestBody Student student) {
    return studentRepository.save(student);
}
```

**First thing noticed, unprompted, in the plain `GET` response:**
```json
{ "firstName": "Ada", ..., "fullName": "Ada Lovelace", "id": 1 }
```
`fullName` isn't a real database column — it's `Person.getFullName()`, a computed method.
Jackson (the JSON library) serializes *any* public getter it finds on an entity, not just
persisted fields. Returning the entity directly means you don't fully control what's
exposed, even before considering anything malicious.

**Then the real problem — a live attack:**

```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{"id": 1, "firstName": "MALLORY", "lastName": "HACKED", ...}'
```

`id: 1` was Ada's existing id, included on purpose. Result: **Ada's row was silently
overwritten.** `GET /api/students/1` afterward returned Mallory's data — no error, `200 OK`
throughout.

**Why the client could even set `id` at all**, given there's no `setId()` anywhere: Jackson
sees `getId()` (a public getter, so `id` is recognized as a real property) but no setter, and
falls back to writing the private field directly via reflection — the same trick Hibernate
itself uses to build entities (Module 2). `Person.id` isn't `final` specifically *because*
Hibernate needed to populate it after construction; that same removed `final` is what let an
untrusted JSON body reach in and set it too. Two frameworks, same reflection trick, one of
them exploitable.

**Why `save()` turned that into an overwrite, not a new row:** Spring Data's rule is simple —
`id == null` means insert a new row; `id` already set means treat it as an update to that
row. The incoming `Student` had `id = 1`, so `save()` didn't create anything new; it
overwrote row `1`.

## The fix: DTOs close the hole structurally, not by convention

```java
public record StudentRequest(
        String firstName, String lastName, String email,
        String studentNumber, int enrollmentYear) {

    public Student toEntity() {
        return new Student(firstName, lastName, email, studentNumber, enrollmentYear);
    }
}
```

No `id` field, anywhere in the record. Re-running the *identical* attack (`"id": 1"` still in
the JSON body) against the fixed controller:

```
Response: {"id": 2, "firstName": "MALLORY", ...}
GET /api/students/1: Ada, completely untouched
```

The attacker's `id` never reached anywhere meaningful — Jackson has nowhere to put a JSON key
that doesn't exist on the target type, so it's silently dropped during parsing (Spring
Boot's default: ignore unrecognized JSON properties). `toEntity()` always builds a fresh
`Student` via its real constructor — which also never accepts an `id` — so the resulting
entity's `id` is always `null`, and `save()` always inserts. This isn't "we now check for a
malicious id and reject it" — there is structurally no path for a client-supplied id to
travel down, at any step.

`StudentResponse` closes the other half — `fullName` is still included, but now because it
was deliberately put in the record, not because Jackson serialized whatever public getters
it happened to find.

## `ResponseEntity` and status codes

Before: `studentRepository.findById(id).orElseThrow()` — an uncaught exception on a missing
id, which Spring turns into `500 Internal Server Error`. Wrong status: a missing id is a
normal, expected outcome, not a server failure.

```java
@GetMapping("/{id}")
public ResponseEntity<StudentResponse> getOne(@PathVariable Long id) {
    return studentRepository.findById(id)
            .map(StudentResponse::from)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
}
```

`ResponseEntity<T>` wraps a body together with an explicit status, chosen per outcome instead
of always accepting Spring's default (`200` for anything returned normally). Confirmed live
across the full CRUD set:

| Operation | Status | Confirmed |
|---|---|---|
| `GET` existing | `200 OK` | ✅ |
| `GET` missing id | `404 Not Found` (was `500` before this fix) | ✅ |
| `POST` (create) | `201 Created` | ✅ |
| `PUT` (update) | `200 OK` | ✅ — teacher's email/department both changed correctly |
| `DELETE` | `204 No Content`, then `404` on re-fetch | ✅ |

`PUT`'s update path reuses the same `StudentRequest`/`updateFrom()` pattern as create:

```java
public void updateFrom(StudentRequest request) {
    setFirstName(request.firstName());
    // ...
    this.studentNumber = request.studentNumber();
    this.enrollmentYear = request.enrollmentYear();
}
```

`setFirstName`/`setLastName`/`setEmail` were added to `Person` specifically for this —
they didn't exist before this module, since nothing needed to mutate an already-persisted
entity until `PUT` did.

## `TeacherController`/`CourseController` — same pattern, applied twice more

Once the *why* was proven against `Student`, `Teacher` and `Course` got the identical
treatment mechanically — `@RestController`, matching `Request`/`Response` records, the same
`ResponseEntity` status-code choices — without re-deriving the reasoning each time. Confirmed
live: `POST /api/teachers` (201), `PUT /api/teachers/{id}` (200, changed both email and
enum `department`), `POST`/`DELETE /api/courses` (201, then 204 → 404 on re-fetch).
