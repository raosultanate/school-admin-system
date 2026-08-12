# Module 0 — Environment Setup

How this machine went from nothing to a working Spring Boot dev environment, and how the
project itself was generated. Written down so it's reproducible on another machine (or
after a reinstall) without having to re-derive every step.

## Why SDKMAN instead of Homebrew casks

First attempt was `brew install --cask temurin@21` (Homebrew's usual way to install a JDK
on macOS). It failed: the cask installer needs `sudo`, and `sudo` needs an interactive
terminal password prompt — which isn't available when commands are run non-interactively.

Switched to [SDKMAN](https://sdkman.io/) instead: it installs JDKs and build tools into
`~/.sdkman/candidates/`, entirely in the user's home directory, no `sudo` required. Also
gives easy multi-version management later (`sdk install java <other-version>`) if a
different job/project needs a different Java version.

SDKMAN's own installer has one gotcha: it requires Bash ≥ 4, but macOS ships Bash 3.2 by
default at `/bin/bash` (Apple stopped updating it years ago for licensing reasons). Fix:
`brew install bash` first (a plain formula install, no `sudo` needed — only *casks*
required it), which puts Bash 5.x at `/opt/homebrew/bin/bash`, then run the SDKMAN
installer explicitly with that binary rather than relying on `PATH` (`/opt/homebrew/bin`
comes *after* `/bin` on `PATH` here, so plain `bash` still resolves to the old one).

## Commands that set up the toolchain

```bash
# 1. Modern bash (only needed so SDKMAN's installer will run)
brew install bash

# 2. SDKMAN itself
curl -s "https://get.sdkman.io" -o sdkman-install.sh
/opt/homebrew/bin/bash sdkman-install.sh

# 3. JDK 21 and Maven, via SDKMAN
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 21.0.5-tem
sdk install maven
```

SDKMAN's installer added an init snippet to `~/.zshrc` (and `~/.bash_profile`) that puts
`java`/`mvn` on `PATH` automatically in new interactive shells — no need to re-source
manually after opening a fresh terminal.

## VS Code

VS Code itself was already installed (`/Applications/Visual Studio Code.app`), but its
`code` CLI wasn't on `PATH`. Symlinked it in rather than reinstalling:

```bash
ln -sf "/Applications/Visual Studio Code.app/Contents/Resources/app/bin/code" \
    /opt/homebrew/bin/code
```

Then installed the Java + Spring extensions:

```bash
code --install-extension vscjava.vscode-java-pack          # Java language support, debugger, test runner, Maven/Gradle
code --install-extension vmware.vscode-spring-boot         # Spring Boot language support (bean/property autocomplete etc.)
code --install-extension vscjava.vscode-spring-initializr  # Generate/modify Spring Boot projects from within VS Code
code --install-extension vscjava.vscode-spring-boot-dashboard # View/start/stop Spring Boot apps from a sidebar
```

## Generating the project

Used the [Spring Initializr](https://start.spring.io) HTTP API directly rather than the
website, so the exact parameters are reproducible:

```bash
curl -s "https://start.spring.io/starter.zip" \
  -d type=maven-project \
  -d language=java \
  -d baseDir=school-admin-system \
  -d groupId=com.schooladmin \
  -d artifactId=school-admin-system \
  -d name=SchoolAdminSystem \
  -d description="School Admin System - Spring Boot learning project" \
  -d packageName=com.schooladmin.system \
  -d packaging=jar \
  -d javaVersion=21 \
  -d dependencies=web,data-jpa,h2,validation,lombok,devtools \
  -o school-admin-system.zip
unzip school-admin-system.zip
```

`bootVersion` was deliberately **not** pinned. First attempt pinned `3.3.5` (a version
common in tutorials) and Initializr rejected it: `"Spring Boot compatibility range is
>=4.0.0"` — by the time this project was created, Initializr's default had moved to Spring
Boot 4.1. Letting it default avoided fighting an already-retired version range. Worth
knowing if following an older tutorial that assumes Boot 3.x — package/annotation names
mostly carry over, but check `docs/notes/dependencies.md` for anything Boot-4-specific
(e.g. `spring-boot-starter-webmvc` replacing `spring-boot-starter-web`).

The generated `HELP.md` (generic Spring Initializr boilerplate) was deleted immediately —
this repo's own `README.md` + `docs/` replace it.

## Git & GitHub

```bash
git init
git add -A && git commit -m "Initial Spring Boot project skeleton, roadmap, and architecture docs"

# later, once ready to publish:
gh repo create school-admin-system --public --source=. --remote=origin \
    --description "School Admin System — Spring Boot learning project"
git branch -M main
git push -u origin main
```

Repo: https://github.com/raosultanate/school-admin-system (public).

## Verifying it all worked

```bash
java -version   # OpenJDK 21.0.5, Temurin
mvn -version    # Apache Maven 3.9.16
./mvnw -q compile
```
