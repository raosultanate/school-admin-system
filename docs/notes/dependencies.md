# Dependencies — what's in `pom.xml` and why

Spring Boot projects are built from **starters** — dependency bundles that pull in
everything needed for one concern (web, database access, validation, ...) with compatible
versions, so you don't hand-pick individual jars. This is what got generated when the
project was created via Spring Initializr, requesting `web,data-jpa,h2,validation,lombok,
devtools`. Two extra ones showed up that we never asked for — noted below, because that's
worth noticing rather than shrugging off.

## Parent POM

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.1.0</version>
</parent>
```
Not a library — a shared Maven configuration. It pins compatible versions for every
`spring-boot-*` dependency below (so we never write a `<version>` on them ourselves) and
sets sensible defaults (Java version, encoding, plugin config).

## Runtime dependencies

| Artifact | What it does | We use it starting |
|---|---|---|
| `spring-boot-starter-webmvc` | Embedded Tomcat + Spring MVC — lets us write `@RestController`s and run a web server. Called `webmvc` (not the older `web`) in Boot 4 to distinguish it from the reactive stack, `spring-boot-starter-webflux`, which we're *not* using. | Module 7 (REST APIs) |
| `spring-boot-starter-data-jpa` | Spring Data JPA + Hibernate — ORM that maps Java objects to database rows, and lets us write repository interfaces instead of SQL. | Module 6 (Persistence) |
| `spring-boot-starter-validation` | Jakarta Bean Validation (Hibernate Validator) — annotations like `@NotNull`, `@Size` to validate incoming data declaratively. | Module 8 (Validation) |
| `h2` (`com.h2database`) | The actual H2 database engine — a real SQL database that runs in-memory or in a file, no separate server to install. Good for learning/dev; we'll swap to Postgres later (Module 13). `runtime` scope: our code never imports H2 classes directly, Spring Data JPA talks to it through JDBC. | Module 6 |
| `spring-boot-h2console` | The H2 web console (a browser UI at `/h2-console` to poke at the dev database directly). Split out as its own starter in Boot 4 — in older Spring Boot versions this was bundled differently. | Module 6 |
| `spring-boot-devtools` | Developer-only conveniences: auto-restart the app when you change code, disabled automatically in a production build (`optional=true` so it doesn't leak into apps that depend on this one). | Already active |
| `lombok` | Generates boilerplate (getters/setters/constructors) from annotations like `@Getter`, `@Data` at compile time, so you don't hand-write it. **Not used yet** — Module 1's `Person`/`Student`/`Teacher` were written by hand on purpose, so the constructor/getter mechanics were visible while learning them. We'll reach for Lombok once that's second nature (likely Module 6, on the JPA entities, where the boilerplate gets real). |  Module 6+ |
| `spring-boot-starter-actuator` | **Not requested** — Boot 4's Initializr appears to include it by default now. Exposes production monitoring endpoints (`/actuator/health`, `/actuator/metrics`, etc.). Harmless to leave in; we'll deliberately use it around Module 13 when we talk about running this in something closer to production. |  Module 13 |

## Test-scope dependencies

Spring Boot 4 splits test support per-concern instead of one catch-all
`spring-boot-starter-test`:

| Artifact | What it does |
|---|---|
| `spring-boot-starter-webmvc-test` | `MockMvc` and friends — test controllers without a real running server. |
| `spring-boot-starter-data-jpa-test` | `@DataJpaTest` support — test repositories against a real (in-memory) database, without loading the whole app. |
| `spring-boot-starter-validation-test` | Test utilities for validation. |

We'll use these in Module 11 (Testing).

## Build plugins

| Plugin | What it does |
|---|---|
| `spring-boot-maven-plugin` | Packages the app as an executable "fat jar" (all dependencies bundled in) and powers `./mvnw spring-boot:run`. |
| `maven-compiler-plugin` (configured with the Lombok annotation processor) | Standard Java compiler, wired so Lombok's compile-time code generation actually runs. |

## Rule going forward

Every time a new dependency gets added to `pom.xml`, it gets a row in this table before we
move on — so this file stays an up-to-date map of "why is this here," not something we
write once and forget.
