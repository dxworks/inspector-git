# AGENTS.md

## Purpose
This document is for coding agents operating in this repository.
It defines build/test commands, single-test execution patterns, and code-style conventions.

## Scope
- Applies to the whole repository.
- Main tech stack:
  - Kotlin (Maven multi-module): `gitclient`, `core`, `utils`, `chr-helper`
  - Node.js wrapper scripts in `lib/` and package publishing via `npm`

## Repository Layout
- `pom.xml` (root aggregator)
- `gitclient/` (git extraction + parsers + tests)
- `core/` (model/transformers + tests)
- `utils/` (shared utilities)
- `chr-helper/` (chronos helper CLI jar)
- `lib/` (Node wrappers that execute built jars)
- `.github/workflows/` (CI/release pipelines)

## Toolchain and Runtime
- Java: minimum 11 (project compiles to JVM target 11)
- CI currently runs Java 21 (Temurin)
- Maven wrapper available: `./mvnw` / `mvnw.cmd`
- Node required for packaging and publishing npm artifacts

## Build Commands (Primary)
Run from repo root unless stated otherwise.

### Maven (all modules)
- Build all modules:
  - `./mvnw clean package`
- Build without tests:
  - `./mvnw clean package -DskipTests`
- Run all tests:
  - `./mvnw test`
- Build one module (and required dependencies):
  - `./mvnw -pl gitclient -am clean package`
  - `./mvnw -pl core -am clean package`
  - `./mvnw -pl chr-helper -am clean package`
  - `./mvnw -pl utils -am clean package`

### Node packaging
- Install deps:
  - `npm ci`
- Build distributable `dist/` (copies JS + built jars):
  - `npm run build`
- Clean dist:
  - `npm run clean`

## Test Commands (Including Single Test)
Maven Surefire is used (no dedicated Failsafe configuration found).

### All tests
- `./mvnw test`
- Module-only:
  - `./mvnw -pl gitclient test`
  - `./mvnw -pl core test`

### Single test class
- `./mvnw -pl gitclient -Dtest=LineOperationsMetaExtractorTest test`
- `./mvnw -pl core -Dtest=IssueTrackerTransformerTest test`

### Single test method
- `./mvnw -pl gitclient -Dtest=LineOperationsMetaExtractorTest#readModify test`
- `./mvnw -pl core -Dtest=IssueTrackerTransformerTest#test\ task\ regex test`

### Pattern-based selection
- `./mvnw -pl gitclient -Dtest=*ParserTest test`
- `./mvnw -pl core -Dtest=*TransformerTest test`

### Notes on integration-style tests
- Some tests are named `*IT` (for example `ModelTestIT`) but no dedicated integration-test plugin config is present.
- Treat `*IT` tests as potentially environment-heavy and run explicitly when needed:
  - `./mvnw -pl core -Dtest=ModelTestIT test`

## Lint/Formatting Commands
No explicit lint or formatter task is configured in Maven or npm scripts.
Operational guidance:
- Use IntelliJ Kotlin formatting defaults already used by the codebase.
- Keep changes minimal and style-consistent with surrounding files.
- Do not introduce new lint frameworks unless requested.

## Running the CLI Locally
After building jars and npm dist:
- `node lib/iglog.js <path-to-repo>`
- `node lib/ig-chr-helper.js <path-to-folder-containing-iglogs>`

## Code Style Guidelines

### General
- Follow existing module/package organization; avoid cross-module shortcuts.
- Prefer small, composable functions over long procedural blocks.
- Keep public APIs stable unless task explicitly requires API changes.

### Kotlin Formatting
- 4-space indentation, no tabs.
- Keep one top-level declaration per conceptual unit when practical.
- Use trailing commas in multiline argument lists where surrounding code uses them.
- Keep line wrapping readable over strict compactness.

### Imports
- Order:
  1. project imports
  2. third-party imports
  3. JDK/Kotlin imports
- Avoid unnecessary imports.
- Wildcard imports exist in some files; prefer explicit imports for new/updated code unless wildcard keeps parity with the file's existing style.

### Naming
- Classes/objects/interfaces: `PascalCase`
- Functions/properties/locals: `camelCase`
- Constants:
  - `UPPER_SNAKE_CASE` for global constants and env-var-like values
  - `camelCase` constants inside companion objects are also common here; preserve local style in touched files
- Test names:
  - Descriptive names; backtick test names are acceptable and common

### Types and Nullability
- Prefer `val` over `var`; use `var` only for required mutability.
- Model immutable data with `data class` where appropriate.
- Use Kotlin null-safety idioms (`?.`, `?:`, `?.let`) instead of defensive null checks.
- Avoid `!!` unless logically guaranteed; if used, keep it tightly scoped and justified.

### Error Handling
- Use domain-specific exceptions for domain failures (for example patterns like `NoChangeException`).
- Catch specific exceptions, not broad `Exception`, unless truly boundary-level handling.
- Log meaningful context with SLF4J (`LoggerFactory`) before returning fallback/null.
- Prefer fail-fast for invalid invariant states; prefer graceful continuation for partial parsing/extraction issues.

### Logging
- Use `private val LOG = LoggerFactory.getLogger(...)` in companion objects/classes.
- Keep log messages actionable and contextual (entity id, file name, commit id).
- Use `debug` for noisy loops, `info` for progress milestones, `warn/error` for anomalies.

### Collections and Transformations
- Prefer Kotlin collection operators (`map`, `filter`, `mapNotNull`, `firstOrNull`) over manual mutable loops where readable.
- Use sequences/streams only when needed for large datasets or parallel operations.
- Keep recursion (`tailrec`) only where it improves clarity and remains safe.

### Tests
- Use JUnit 5 (`@Test`) with Kotlin/JUnit assertions.
- Keep test fixtures local and explicit.
- Prefer deterministic test data; avoid hardcoded machine-specific paths in new tests.
- For parser/transformer behavior, assert both size and content-level expectations.

### JavaScript (lib wrappers)
- CommonJS style (`require`, `module.exports`) is current standard here.
- Keep wrappers thin: argument passthrough + Java caller orchestration.
- Do not add heavy logic in Node wrappers when Kotlin modules own behavior.

## Agent Operating Checklist
- Before coding: identify target module (`gitclient`, `core`, `utils`, `chr-helper`).
- After coding: run the smallest relevant test scope first.
- Before finalizing: run module-level tests if change spans multiple files.
- If packaging-related change: run `npm run build` after Maven package succeeds.
- Do not add new build systems or style tools unless explicitly requested.

## CI and Release Notes (for context)
- CI build workflow runs `mvn clean package`.
- Release workflows also run npm packaging (`npm ci`, `npm run build`).
- `gitclient/pom.xml` contains `$tag_version` replacement logic in release flow; avoid altering this unless release automation is being updated.

## Cursor/Copilot Rules Discovery
- `.cursor/rules/`: not found
- `.cursorrules`: not found
- `.github/copilot-instructions.md`: not found

If such files are later added, merge their guidance into this document and treat them as higher-priority local agent instructions.
