# Annotations — what each one does and where it's used

A running reference, not tied to one module — annotations get added here the first time
they show up in the code, and this file is where to look them up later instead of
re-deriving "wait, what does this one actually do again?"

**The one distinction that matters more than any single annotation:** an annotation is just
a label on your code — it isn't a bean. The *object Spring creates* because it saw the
label is the bean. `@Service` itself is never a bean; the one `PersonService` object Spring
builds because of `@Service` is.

| Annotation | What it does | Where it's used | First appeared |
|---|---|---|---|
| `@SpringBootApplication` | A bundle of three annotations at once: `@Configuration` (allows `@Bean` methods on this class), `@ComponentScan` (makes Spring go looking for `@Component`/`@Service`/etc. classes at all), `@EnableAutoConfiguration` (auto-starts Tomcat, H2, etc. based on what's on the classpath). Without this, none of the annotations below would do anything — nothing would be scanning for them. | `SchoolAdminSystemApplication` class | Module 0 (project skeleton) |
| `@Bean` | Put on a method (inside a `@Configuration` class, or — like here — a `@SpringBootApplication` class, which already is one). Spring calls the method once at startup and registers whatever it returns as a bean. The only option for types you don't own and can't annotate directly (e.g. `List`, a JDK interface). | `initialPeople()` method in `SchoolAdminSystemApplication` | Module 1 |
| `@Component` | The base "Spring, build one of these and manage it" annotation for a class *you wrote*. Found automatically by component scanning — no manual registration. `@Service`/`@Repository`/`@Controller` are all secretly `@Component` underneath, with a more specific name. | `PersonServiceStartupRunner` class | Module 1 |
| `@Service` | A `@Component` specialization signaling "business logic lives here." Functionally identical to plain `@Component` for scanning purposes — the difference is purely about communicating intent to whoever reads the code. | `PersonService` class | Module 1 |
| `@PostConstruct` | Not Spring's own annotation (it's `jakarta.annotation`, standard Java), but Spring honors it: calls the annotated method once, automatically, right after the constructor runs and every dependency is already injected — before the bean is handed to anything that depends on it. | `PersonService.logInitialization()` | Module 1 |
| `@PreDestroy` | The mirror of `@PostConstruct` — calls the annotated method once, right before the container destroys the bean (e.g. on graceful shutdown). Where you'd release a resource (close a connection, stop a thread) if there were one to release. | `PersonService.logShutdown()` | Module 1 |

## Rule going forward

Every time a new annotation gets used for the first time, it gets a row here — what it
does, in plain terms, and where to see it actually being used in this codebase. `@Repository`
(a `@Component` specialization that also translates database exceptions — real behavior, not
just a label) is next, once Module 2 adds a real one.
