# Documentation & Comment Conventions

Not a Java/Spring concept — just how this repo is organized, written down once so it stays
consistent as modules get added.

## Where things live

- `README.md` (root) — the one file that has to stay at root; GitHub renders it as the repo
  homepage. Kept short: what this is, stack, how to run, links into `docs/`.
- `docs/ROADMAP.md` — the module-by-module learning plan and progress checklist.
- `docs/architecture.md` — architecture/domain diagrams, redrawn as understanding grows.
- `docs/notes/` — one write-up per module, explaining concepts against the actual code.

## Package organization: by layer, not by feature

Two common ways to organize a Spring Boot app's packages:

- **By layer** (chosen here): `domain/`, `repository/`, `service/`, `controller/` —
  everything of one *kind* lives together.
- **By feature**: `student/`, `teacher/`, `course/`, each containing its own
  controller+service+repository+entity — everything about one *concept* lives together.

By-feature is a legitimate, often-preferred pattern on large, multi-team codebases, where
by-layer starts hurting cohesion once there are dozens of entities and the "everything of
one kind" packages become unwieldy grab-bags. That threshold doesn't apply here — this
project will only ever have a handful of entities (`Student`, `Teacher`, `Course`,
`Enrollment`) — so by-feature would be structure for a scale this app doesn't have.
By-layer is also what the overwhelming majority of Spring tutorials, official guides, and
typical interview-context codebases use, making it the most immediately recognizable
pattern to reach for here.

**Not organized by Java language construct** (no `enums/`, `interfaces/`, `classes/`
folders): `AccessLevel`, `Department`, and `HasLabel` live in `domain/` alongside `Person`,
`Student`, `Teacher` — they're domain concepts exactly as much as any class is; that Java
happens to implement them with the `enum`/`interface` keyword is an implementation detail,
not an organizing principle. Splitting by construct would scatter related domain concepts
across folders for no real benefit.

Current/planned layout as later modules add to it:
- `domain/` — entities, enums, interfaces, exceptions (`domain/exception/`) — inherited
  from the [`java-refresher`](../../java-refresher) project, where they were introduced
- `repository/` — Spring Data repositories — Module 2
- `service/` — business logic — Module 5
- `controller/` — REST controllers — Module 3

There is no `playground/` package in this project — the standalone, one-`main()`-per-module
learning demos live in `java-refresher` instead, alongside the Java-language concepts they
teach. Everything under `src/` here is meant to read as production-style code.

## Comment philosophy

Two different things get lumped together as "comments" — worth keeping them separate:

**Javadoc (`/** ... */`)** — documents a **public contract**: what a class represents,
what a method promises, its parameters/return value. Write it for:
- every public class (one summary sentence minimum — what *is* this thing)
- public constructors/methods whose parameters aren't self-explanatory from their names
  alone (e.g. `Student(String firstName, ..., String studentNumber)` — worth documenting
  the expected format, `"S-1001"`)
- abstract methods, since the Javadoc is the only place to describe the *contract*
  subclasses must fulfill (see `Person.describe()`)

Skip it for trivial getters (`getFirstName()`) — the method name already says everything
the Javadoc would, and restating it is noise, not documentation. (Some teams still require
it project-wide via a linter; this project doesn't.)

**Inline comments (`//`)** — reserved for the *why*, not the *what*. A comment explaining
what a line does is a sign the code should be renamed/restructured to be self-explanatory
instead. Only write one when there's a non-obvious reason behind a choice — a workaround, a
constraint, something that would genuinely surprise a reader (e.g. why an exception extends
`RuntimeException` rather than `Exception`). Everything under `src/` here is production-style
code and should read that way — the teaching-oriented, walkthrough-comment style belongs to
`java-refresher`'s `playground/` package, not this project.
