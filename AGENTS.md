# Inspector Git

## Project Overview

Inspector Git is a CLI tool for extracting and analysing metadata from Git repositories. It produces an intermediate representation called **iglog** — a compact, structured log format that captures commits, changes, hunks, and line-level operations. The tool also supports author anonymization (incognito mode), commit graph extraction, and Chronos helper metrics.

The tool is also packaged as a [Voyager](https://github.com/dxworks/voyager) instrument (`iglog-voyager.zip`).

## Build & Run

- **Language:** Kotlin (JVM target 21)
- **Build tool:** Maven (wrapper included: `./mvnw`)
- **Build:** `./mvnw clean package`
- **Output:** `gitclient/target/iglog.jar` (fat JAR — main tool), `chr-helper/target/ig-chr-helper.jar` (Chronos helper)
- **Run:** `java -jar gitclient/target/iglog.jar <repo-path> [flags]`
- **Main class:** `org.dxworks.inspectorgit.gitclient.MainKt`

### Configuration

Configuration is passed via environment variables (prefixed `IG_`):

| Variable | Default | Description |
|---|---|---|
| `IG_IGLOG` | `false` | Generate iglog output |
| `IG_GITLOG` | `true` | Generate git log output |
| `IG_INCOGNITO` | `false` | Enable author anonymization |
| `IG_RECURSIVE` | `false` | Recursively process sub-repositories |

## Project Structure

```
gitclient/                          — Main iglog extraction tool
  src/main/kotlin/org/dxworks/inspectorgit/gitclient/
    main.kt                         — CLI entry point
    GitClient.kt                    — Git process wrapper
    parsers/                         — Git log parsers
    iglog/                           — IGLog format readers/writers
    incognito/                       — Author anonymization
    dto/                             — Data transfer objects
  src/main/resources/
    instrument.yml                   — Voyager instrument descriptor

core/                               — Core domain model and transformers
  src/main/kotlin/org/dxworks/inspectorgit/
    model/                           — Git, issue tracker, remote models
    transformers/                    — Data transformation logic
    factories/                       — Project factory implementations
    registries/                      — Registry pattern implementations

utils/                              — Shared utility classes
chr-helper/                         — Chronos helper tool

lib/                                — Node.js CLI wrappers (npm package)
scripts/
  build.sh                          — Build script (wraps mvnw)
  prepare-release.sh                — Package standard release ZIP
  prepare-release-voyager.sh        — Package Voyager instrument ZIP
  regression-test.sh                — Compare output against latest release
releaseNotes/                       — Per-version release notes
```

## Dependencies

- `jackson-module-kotlin`, `jackson-dataformat-csv` — JSON/CSV serialization
- `kotlin-reflect`, `kotlin-stdlib-jdk8` — Kotlin runtime
- `logback-classic` — Logging (SLF4J)
- `zip4j` — ZIP file handling (gitclient)
- `java-diff-utils` — Diff generation (gitclient)
- `groovy` — Groovy scripting support (core)

## Docker

- **Image:** `dxworks/inspector-git` on Docker Hub
- **Base:** `eclipse-temurin:21-jre-alpine`
- **Build:** `docker build -t inspector-git-test .` (requires `gitclient/target/iglog.jar` — run `./mvnw clean package` first)
- **Run:** `docker run -v /path/to/repo:/repo -e IG_IGLOG=true inspector-git-test /repo`

## CI/CD

- **Build:** `build.yml` runs on every push (build verification)
- **Release:** Tag `v*` triggers `release.yml` — parse-tag → gate → archive + npm + docker (parallel) → GitHub Release
- **Voyager release:** Tag `v*-voyager` triggers `release-voyager.yml` — parse-tag → gate → archive → GitHub Release
- **Security:** `trivy-security-scan.yml` runs on PRs to `develop` (filesystem + Docker image scan); `trivy-daily-scan.yml` runs daily
- **Regression:** `regression-test.yml` runs on PRs (non-blocking) and manual dispatch

All release workflows use reusable pipelines from `dxworks/pipelines@v1`.

## Branching Strategy

- Main development branch: `develop`
- Release tags: `v<semver>` (standard) and `v<semver>-voyager` (Voyager instrument)

## Output Format

iglog produces output files in the results directory:
- `.iglog` files — compact intermediate representation of git history
- `.gitlog` files — structured git log output
- Commit graph data (when requested)
