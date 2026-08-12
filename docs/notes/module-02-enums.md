# Module 2 — Enums

Code: `domain/{HasLabel,AccessLevel,Department}.java` (the enum types), `domain/Admin.java`
and `domain/Teacher.java` (refactored to use them instead of raw `String`s),
`playground/EnumDemo.java` (the demo).

## An enum is a class, not "a fancy list of constants"

```java
public enum AccessLevel implements HasLabel {
    SUPER_ADMIN(3, "Full access to all system functions"),
    ADMIN(2, "Can manage students, teachers, and courses"),
    SUPPORT(1, "Read-only access for support purposes");

    private final int rank;
    private final String description;

    AccessLevel(int rank, String description) {
        this.rank = rank;
        this.description = description;
    }
    ...
}
```

`javac` compiles this into an actual class file. Each line in the constant list
(`SUPER_ADMIN(3, "...")`) runs the constructor below it exactly once and produces one
`public static final AccessLevel` instance — a genuine singleton. That's the whole reason
enums can have fields, constructors, and methods like any other class: they *are* one.

**Enum constructors are implicitly `private`.** Java won't even let you write `public
AccessLevel(...)` — there's no `new AccessLevel(...)` anywhere outside the enum's own body;
the constants declared inside it are the only instances that will ever exist.

## Why this replaced `String accessLevel` on `Admin`

Before: `private final String accessLevel;` — legal to construct with `"SUPER_ADMIN"`,
`"SUPRE_ADMIN"` (typo), `"anything at all"`, and the compiler would accept every one of
them identically. The typo becomes a runtime bug, discovered whenever that code path
actually runs — if it runs.

After: `private final AccessLevel accessLevel;` — there are exactly three valid values,
and the compiler enforces it. `new Admin(..., AccessLevel.SUPRE_ADMIN)` fails to compile;
there's no way to construct an `Admin` with an invalid access level at all. Same principle
as inheritance forcing every `Person` subtype to implement `describe()` (Module 1) —
push a constraint into the type system so the compiler catches violations, instead of
relying on nobody ever mistyping a string.

`Teacher.department` got the identical treatment with a new `Department` enum.

## `switch` over an enum — two ways, compared

```java
// Classic (colon-based) switch STATEMENT
String note;
switch (level) {
    case SUPER_ADMIN:
        note = "can do anything";
        break;
    case ADMIN:
        note = "day-to-day management";
        break;
    case SUPPORT:
        note = "read-only";
        break;
    default:
        throw new IllegalStateException("Unhandled AccessLevel: " + level);
}
```
```java
// Modern (arrow-based) switch EXPRESSION, Java 14+
String note = switch (level) {
    case SUPER_ADMIN -> "can do anything";
    case ADMIN -> "day-to-day management";
    case SUPPORT -> "read-only";
};
```

Both produced identical output for all three constants. The real difference is what
happens if a fourth `AccessLevel` constant gets added later and this `switch` isn't
updated:

- **Classic statement:** compiles fine either way. Without the `default` branch throwing,
  `note` would simply never get assigned for the new constant — the kind of silent bug this
  whole conversation about exceptions has been warning about. The `default: throw` here is
  a deliberate defensive pattern, not boilerplate.
- **Switch expression:** the compiler checks exhaustiveness itself. Proven directly —
  removing the `SUPPORT` case from a switch expression over the same three-constant enum
  produces `error: the switch expression does not cover all possible input values` at
  compile time, no `default` required. Forgetting to handle a new constant becomes
  impossible to ship, not just impossible to notice.

Both forms are worth recognizing — plenty of existing code and interview whiteboards still
use the classic form — but the switch expression is the stronger default going forward on
a codebase targeting Java 21, as this one is.

## An enum implementing an interface

```java
public interface HasLabel {
    String label();
}

public enum AccessLevel implements HasLabel { ... }
public enum Department implements HasLabel { ... }
```

```java
private static void printLabel(HasLabel item) {
    System.out.println("Label: " + item.label());
}

printLabel(AccessLevel.ADMIN);       // Label: Can manage students, teachers, and courses
printLabel(Department.COMPUTER_SCIENCE); // Label: Computer Science
```

`AccessLevel` and `Department` share no inheritance relationship, model completely
different concepts, and yet the exact same `printLabel` method works on both. This is
polymorphism again (Module 1), just via an interface instead of an abstract class —
`printLabel` only depends on the `HasLabel` contract, never on which concrete enum it's
actually holding.

## `EnumMap` / `EnumSet` — a preview

```java
Map<AccessLevel, Integer> userCountByLevel = new EnumMap<>(AccessLevel.class);
userCountByLevel.put(AccessLevel.SUPER_ADMIN, 1);
userCountByLevel.put(AccessLevel.ADMIN, 4);
// EnumMap: {SUPER_ADMIN=1, ADMIN=4}

EnumSet<AccessLevel> elevated = EnumSet.of(AccessLevel.SUPER_ADMIN, AccessLevel.ADMIN);
// EnumSet: [SUPER_ADMIN, ADMIN]
```

`EnumMap`/`EnumSet` are `Map`/`Set` implementations specifically for enum keys/elements,
internally backed by a plain array indexed by each constant's declaration order — not a
hash table. Faster and more memory-compact than `HashMap`/`HashSet` for this one case, but
that comparison only means something once Module 4 covers `Map`/`Set`/`HashMap` in general.
Noted here as "this exists," explained properly there.
