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
- Spring Web, Spring Data JPA, H2 (dev), Bean Validation, Lombok, DevTools

## Running

```bash
./mvnw spring-boot:run
```

## Status

Early — see [`docs/ROADMAP.md`](docs/ROADMAP.md) for current module progress.
