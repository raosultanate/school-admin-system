# Module 2 — Exception Handling

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

## Next: custom exceptions (part 2)

Still to build: `StudentNotFoundException`, `DuplicateEnrollmentException` — custom
exceptions wired into actual project domain logic (a small `StudentRegistry`), plus
exception chaining and a decision on checked vs. unchecked for our own exception types.
This file will get a second half once that's done.
