# Module 1 — Inheritance & Polymorphism

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

## Try it yourself

Add a third `Person` subtype (e.g. `Admin`) with its own `describe()`, add one to the
`people` list in `OopDemo`, and re-run:

```
java -cp target/classes com.schooladmin.system.playground.OopDemo
```

No changes needed anywhere else — the `for` loop already works for any `Person` subtype,
because it only ever asks for behavior `Person` guarantees exists (`describe()`).
