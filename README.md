# School Admin System

A Spring Boot learning project: a small admin system for managing `Teacher`s, `Student`s,
`Course`s, and enrollments. Built incrementally, module by module, as a hands-on way to
learn Spring Boot (and refresh core Java) for an interview.

## Docs

- [`docs/ROADMAP.md`](docs/ROADMAP.md) — the module-by-module learning plan and progress
  checklist. Start here to see what's done and what's next.
- [`docs/architecture.md`](docs/architecture.md) — layered architecture and domain model
  diagrams. Living documents, redrawn as the app grows.
- [`docs/notes/`](docs/notes/) — one write-up per module, explaining each concept against
  the actual code written for it.

## Stack

- Java 21 (Temurin, via [SDKMAN](https://sdkman.io/))
- Spring Boot 4.1, Maven
- See [`docs/notes/dependencies.md`](docs/notes/dependencies.md) for what each `pom.xml`
  dependency does and why it's there

## Running

```bash
./mvnw spring-boot:run
```

New machine and don't have Java/Maven set up yet? See
[`docs/notes/module-00-environment-setup.md`](docs/notes/module-00-environment-setup.md)
for exactly how this environment (and the project itself) was set up.

### Playground demos

Early modules are plain Java, run standalone (no Spring context, no web server) rather
than through the app above:

```bash
./mvnw -q compile
java -cp target/classes com.schooladmin.system.playground.OopDemo             # Module 1
java -cp target/classes com.schooladmin.system.playground.ExceptionBasicsDemo # Module 3
```

## Status

Early — see [`docs/ROADMAP.md`](docs/ROADMAP.md) for current module progress.
