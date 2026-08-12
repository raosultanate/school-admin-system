# Module 3 — Exception Handling

Code for this module (part 1): `src/main/java/com/schooladmin/system/playground/ExceptionBasicsDemo.java`.
Plain Java, no Spring — the custom exceptions we wire into the project's domain come in
part 2, once these mechanics are solid.

## The exception hierarchy

Every throwable in Java forms one tree:

```
Throwable
├── Error              (JVM-level problems — OutOfMemoryError. Don't catch these.)
└── Exception
    ├── RuntimeException        ← "unchecked" — compiler doesn't force you to handle these
    │     (NullPointerException, ArithmeticException, IllegalArgumentException, ...)
    └── everything else          ← "checked" — compiler forces you to catch or declare these
          (IOException, SQLException, ...)
```

**Checked** means: if a method can throw it, the compiler won't let your code compile
unless you either `catch` it or add `throws X` to your own method signature and pass the
problem up the call stack. **Unchecked** (`RuntimeException` and its subclasses) means the
compiler doesn't force anything — you *can* catch it, but nothing stops you from ignoring
it; it just crashes the thread at runtime if nothing catches it.

## Unchecked exceptions + `finally`

```java
private static void demonstrateUnchecked() {
    try {
        int result = 10 / 0;          // throws ArithmeticException
        System.out.println(result);
    } catch (ArithmeticException e) {
        System.out.println("Caught unchecked exception: " + e.getMessage());
    } finally {
        System.out.println("finally always runs (unchecked demo)");
    }
}
```

Output:
```
Caught unchecked exception: / by zero
finally always runs (unchecked demo)
```

- `10 / 0` throws `ArithmeticException`, which extends `RuntimeException` — unchecked. The
  method signature has no `throws` clause; the compiler never demanded one. We chose to
  catch it because we wanted to handle it, not because we were forced to.
- `finally` runs **no matter what** — exception thrown and caught, thrown and uncaught, or
  no exception at all. It's for cleanup that must always happen (closing a file, releasing
  a lock), which is also why try-with-resources (below) mostly replaced writing `finally`
  blocks by hand.

## Checked exceptions — the obligation travels with the method signature

```java
private static void demonstrateCheckedHandled() {
    try {
        readFirstLine("does-not-exist.txt");
    } catch (IOException e) {
        System.out.println("Caught checked exception: " + e.getMessage());
    }
}

private static String readFirstLine(String path) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
        return reader.readLine();
    }
}
```

Output:
```
Caught checked exception: does-not-exist.txt (No such file or directory)
```

- `FileReader`'s constructor can throw `FileNotFoundException`, a subclass of
  `IOException` — checked. Because `readFirstLine` doesn't catch it internally, Java's
  compiler *requires* the `throws IOException` on its own signature. Remove that clause and
  `./mvnw compile` refuses to build — this isn't a warning, it's a compile error.
- Because `readFirstLine` declares `throws IOException`, its caller
  (`demonstrateCheckedHandled`) is now compiler-forced to either catch it or declare
  `throws` itself. That propagation *is* the entire mechanic of checked exceptions: the
  compiler makes "someone must handle this failure" travel up the call stack along with the
  method signature, until something actually catches it.

## Try-with-resources

```java
private static void demonstrateTryWithResources() {
    try (AutoCloseable resource = () -> System.out.println("resource closed automatically")) {
        System.out.println("using resource");
    } catch (Exception e) {
        System.out.println("error: " + e.getMessage());
    }
}
```

Output:
```
using resource
resource closed automatically
```

Anything declared inside the `try (...)` parentheses that implements `AutoCloseable` gets
`.close()` called automatically when the block ends — on success *or* on exception, and in
reverse order if there are multiple resources. `BufferedReader`/`FileReader` above are real
examples (both implement `AutoCloseable`); this demo uses a lambda as a minimal stand-in
just to make the auto-close moment visible in the output. This replaced the older pattern
of closing resources by hand in a `finally` block, which was easy to get subtly wrong
(forgetting to close on one exception path, or closing nested resources in the wrong order).

## Part 2 — custom exceptions

Code: `domain/exception/{StudentNotFoundException,DuplicateEnrollmentException,
InvalidStudentDataException}.java`, `playground/StudentRegistry.java`,
`playground/ExceptionHandlingDemo.java`.

### Why unchecked for our own exception types

All three custom exceptions extend `RuntimeException`, not `Exception` — a deliberate
choice, not a default. These represent business-rule/lookup failures (a duplicate student
number, a student that doesn't exist), the same *category* of problem as
`IllegalArgumentException` or `IllegalStateException` — both of which are themselves
unchecked. Making them checked would force `throws` declarations onto every method up the
call stack that might touch a `Student`, including, later, Spring service methods and
lambda-based code (checked exceptions don't cross most functional interfaces cleanly).
Spring's own exception hierarchy (`DataAccessException` and friends) is unchecked for the
same reason. This is a case where "the compiler won't force you to handle it" is the
*point*, not a loophole.

### Two constructors, or one — designed for how each is actually thrown

```java
public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(String message) { super(message); }
    public StudentNotFoundException(String message, Throwable cause) { super(message, cause); }
}
```
`StudentNotFoundException` and `DuplicateEnrollmentException` get both constructors —
they might be thrown standalone (a straightforward "this doesn't exist") or, in principle,
as a wrapper around some other failure.

```java
public class InvalidStudentDataException extends RuntimeException {
    public InvalidStudentDataException(String message, Throwable cause) { super(message, cause); }
}
```
`InvalidStudentDataException` only gets the `(message, cause)` constructor. It only ever
makes sense as a translation of some other failure (see chaining, below), so there's no
bare-message constructor to accidentally misuse — the class's shape enforces that at
compile time, not just by convention.

### Where they're thrown: no catching needed

```java
public void register(Student student) {
    for (Student existing : students) {
        if (existing.getStudentNumber().equals(student.getStudentNumber())) {
            throw new DuplicateEnrollmentException(
                    "Student " + student.getStudentNumber() + " is already registered");
        }
    }
    students.add(student);
}
```
`StudentRegistry.register()`/`findByStudentNumber()` just `throw` directly — there's no
lower-level exception here to translate, so there's nothing to catch internally. The
exception propagates straight up to wherever it's actually meaningful to handle it (in
`ExceptionHandlingDemo`, that's a `try`/`catch` around each call).

### Exception chaining — catching *in order to translate*

```java
private static int parseEnrollmentYear(String raw) {
    try {
        return Integer.parseInt(raw);
    } catch (NumberFormatException e) {
        throw new InvalidStudentDataException("Invalid enrollment year: '" + raw + "'", e);
    }
}
```

Output when called with `"not-a-year"`:
```
Caught: Invalid enrollment year: 'not-a-year'
Caused by: java.lang.NumberFormatException: For input string: "not-a-year"
```

This is the pattern worth internalizing. `NumberFormatException` is a **low-level,
technical** exception — it means nothing to whoever is registering a student. Catching it
and re-throwing it as `InvalidStudentDataException`, passing the original exception as the
second constructor argument (`e`), is **exception chaining**: the new exception's `cause`
is set to the original. Nothing is lost — `e.getCause()` still returns the original
`NumberFormatException` with its original message. In a real stack trace this shows up as
`Caused by: java.lang.NumberFormatException: ...` printed beneath the main trace, so the
root cause survives the translation instead of being thrown away.

**What NOT to do**, for contrast: `catch (NumberFormatException e) { throw e; }` — catching
only to rethrow the exact same exception unchanged accomplishes nothing; not catching it at
all would behave identically. Catching is worth it only when you're either genuinely
handling it right there, or translating it into something more meaningful for the caller
(as above).
