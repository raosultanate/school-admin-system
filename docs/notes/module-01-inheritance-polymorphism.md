# Module 1 — Inheritance & Polymorphism (and Encapsulation & Abstraction, for free)

Code for this module: `src/main/java/com/schooladmin/system/domain/{Person,Student,Teacher}.java`
and `src/main/java/com/schooladmin/system/playground/OopDemo.java`. Deliberately plain
Java — no Spring annotations yet — so the OOP concepts aren't tangled up with framework
magic.

## `Person` — an abstract class

```java
public abstract class Person {
    private final String firstName;
    private final String lastName;
    private final String email;

    protected Person(String firstName, String lastName, String email) { ... }

    public String getFullName() { return firstName + " " + lastName; }

    public abstract String describe();
}
```

- `abstract class` means you can never write `new Person(...)` directly — it's a template,
  not a finished thing. Only concrete subclasses can be instantiated.
- The constructor is `protected`: only subclasses (or code in the same package) can call
  it. Outside code has no way to build a bare `Person`.
- `getFullName()` is a normal, concrete method — every subclass gets it for free without
  rewriting it.
- `describe()` has no body — it's `abstract`. The compiler *forces* every concrete subclass
  to implement it, or that subclass must also be declared `abstract`.

## `Student` / `Teacher` — inheritance

```java
public class Student extends Person {
    private final String studentNumber;

    public Student(String firstName, String lastName, String email, String studentNumber) {
        super(firstName, lastName, email);
        this.studentNumber = studentNumber;
    }

    @Override
    public String describe() {
        return getFullName() + " is a student (#" + studentNumber + ")";
    }
}
```

- `extends Person` — `Student` *is a* `Person`, plus its own `studentNumber` field.
- `super(firstName, lastName, email)` must be the **first line** of the constructor — it
  hands the shared fields up to `Person`'s constructor before `Student` adds its own field.
  This is constructor chaining.
- `@Override` on `describe()` isn't required by the compiler, but always write it: it makes
  the compiler double-check you're actually overriding something (typo the method
  signature and `@Override` turns that into a compile error instead of a silent bug).
- `Teacher` is the same shape, with `department` instead of `studentNumber`.

**Overriding vs overloading** — these sound similar but are different:
- *Overriding* (what we did here): a subclass provides its own body for a method that
  already exists in the parent, same signature. Resolved at runtime based on the actual
  object type.
- *Overloading*: multiple methods in the *same* class with the same name but different
  parameters (e.g. `describe()` and `describe(boolean verbose)`). Resolved at compile time
  based on the arguments you pass.

## `OopDemo` — where polymorphism actually shows up

```java
List<Person> people = List.of(
        new Student("Ada", "Lovelace", "ada@school.edu", "S-1001"),
        new Teacher("Alan", "Turing", "alan@school.edu", "Computer Science")
);

for (Person person : people) {
    System.out.println(person.describe());
}
```

Output:
```
Ada Lovelace is a student (#S-1001)
Alan Turing teaches in Computer Science
```

The list's declared type is `List<Person>`, and the loop variable's declared type is
`Person`. At compile time, that's *all* Java knows. But at runtime, each `describe()` call
dispatches to the actual object's class — `Student`'s version for the first element,
`Teacher`'s for the second. This is **runtime (dynamic) polymorphism**: the method that
runs is determined by the real object, not the variable's declared type.

Why this matters beyond the demo: this is the exact mechanism Spring leans on constantly —
e.g. you'll depend on an interface type, and Spring hands you a concrete implementation at
runtime. Understanding *this* small example is what makes that later behavior feel obvious
instead of magical.

## The other two pillars were already here

"The four pillars of OOP" — encapsulation, abstraction, inheritance, polymorphism — is a
near-guaranteed interview question. This one file of code happens to demonstrate all four,
so it's worth naming the two we used without calling out:

**Encapsulation** — hiding an object's internal state and only exposing it through
controlled methods. Look at `Person` again: every field is `private final`. There are no
setters anywhere. The only way to read a field from outside the class is through a getter
(`getFirstName()`, `getFullName()`, etc.) — nothing outside the class can reach in and
mutate `firstName` directly, and nothing can mutate it at all after construction, because
`final` fields can only be assigned once (in the constructor). The object fully owns and
protects its own state.

**Abstraction** — exposing *what* something can do without exposing *how*. `Person`
declares `abstract String describe()`: every `Person` is guaranteed to be describable, but
`Person` itself says nothing about *how* that description is built — that's each
subclass's private business. `OopDemo` only ever depends on the `Person` abstraction (`for
(Person person : people)`); it never needs to know it's holding a `Student` or a `Teacher`.
That's abstraction paying off directly as loose coupling.

| Pillar | Where it is here |
|---|---|
| Encapsulation | `private final` fields + getters, no setters, in `Person` |
| Abstraction | `abstract class Person` + `abstract describe()` — depend on the interface, not the detail |
| Inheritance | `Student extends Person`, `Teacher extends Person`, `super(...)` |
| Polymorphism | `OopDemo`'s loop: one call site, different behavior per actual object type |

## Try it yourself

Add a third `Person` subtype (e.g. `Admin`) with its own `describe()`, add one to the
`people` list in `OopDemo`, and re-run:

```
java -cp target/classes com.schooladmin.system.playground.OopDemo
```

No changes needed anywhere else — the `for` loop already works for any `Person` subtype,
because it only ever asks for behavior `Person` guarantees exists (`describe()`).
