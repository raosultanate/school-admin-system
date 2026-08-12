# Documentation & Comment Conventions

Not a Java/Spring concept — just how this repo is organized, written down once so it stays
consistent as modules get added.

## Where things live

- `README.md` (root) — the one file that has to stay at root; GitHub renders it as the repo
  homepage. Kept short: what this is, stack, how to run, links into `docs/`.
- `docs/ROADMAP.md` — the module-by-module learning plan and progress checklist.
- `docs/architecture.md` — architecture/domain diagrams, redrawn as understanding grows.
- `docs/notes/` — one write-up per module, explaining concepts against the actual code.

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
constraint from elsewhere in the system, something that would genuinely surprise a reader.
None of the code so far has needed one of these.
