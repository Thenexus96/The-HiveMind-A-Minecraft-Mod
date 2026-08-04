# Continuous Integration (CI) — HiveMind

**⚠️ Note**: This repository is **Gitea-first**. See `documentation/GITEA_CI.md` for the primary CI guide if you use Gitea self-hosted.

This document describes all CI platforms (Gitea, GitHub Actions) and how to run CI steps locally.

## Platform Overview

| Platform | Status | Use Case | Guide |
|----------|--------|----------|-------|
| **Gitea** (Self-hosted) | ✅ PRIMARY | Daily development, main deployment | `documentation/GITEA_CI.md` |
| **GitHub Actions** | ⏳ OPTIONAL | Secondary, mirror, or future migration | This page |

---

## Gitea CI (Primary) — Quick Start

**Your main CI platform:**

1. **Workflows**: `.gitea/workflows/ci.yml` (recommended)
2. **Container**: `gradle:8.3-jdk17`
3. **Triggers**: push/PR to `main`, `creation`, `testing`, `pre-release`
4. **Runtime**: ~5-15 minutes

**See full guide**: [`documentation/GITEA_CI.md`](GITEA_CI.md)

### Quick Gitea CI Commands

```bash
# Run tests locally (same as CI)
./gradlew test --no-daemon --console=plain --warning-mode all

# Run build (same as CI)
./gradlew build --no-daemon --console=plain --warning-mode all --stacktrace

# Run integration tests (same as CI on main)
./gradlew integrationTest -PintegrationTest
```

---

## GitHub Actions (Optional/Secondary)

If you also mirror your repo to GitHub or plan to use GitHub Actions later:
- `.github/workflows/build.yml` — Main build pipeline: sets up JDK 17, restores Gradle cache, runs unit tests, builds project, and uploads artifacts for `main` branch and tags only.
- `.github/workflows/qodana_code_quality.yml` — Qodana static analysis job for code quality. Runs on PR (limited scope) and full analysis on `push` to `main`/`creation`.
- `.github/workflows/integration-test.yml` — Dedicated integration test job for `main` branch and manual trigger. Uploads test results as artifacts.
- `.github/dependabot.yml` — Automated dependency update PRs for Gradle dependencies (weekly schedule).

### Gitea / Alternative Runners
- `.gitea/workflows/ci.yml` — Gitea self-hosted runner build pipeline (matches GitHub build.yml behavior).
- `.gitea/workflows/build.yml` — Gitea ubuntu-latest runner build pipeline (mirrors GitHub Actions).
- `.gitea/workflows/build-split.yml` — Advanced split-job workflow for parallel cache setup, build, and test (optional, for larger projects or self-hosted runners).

> **Note**: Standardize on GitHub Actions (primary) for day-to-day use. Keep Gitea workflows if you use a Gitea instance or self-hosted runners; otherwise, they can be archived or removed.

## How the GitHub Actions Build Pipeline Works

- **Triggers**: `push` and `pull_request` on branches: `creation`, `testing`, `pre-release`, `main`.
- **Steps**:
  1. Checkout repository (v4 action)
  2. Setup JDK 17 (Temurin) with Gradle cache enabled
  3. Validate Gradle wrapper
  4. Restore Gradle cache (fallback, with keys based on build.gradle and gradle-wrapper.properties)
  5. Run unit tests: `./gradlew --no-daemon --console=plain --warning-mode all test`
  6. Build: `./gradlew --no-daemon --console=plain --warning-mode all build --stacktrace`
  7. Upload artifacts (only for `main` branch or git tags, to avoid PR noise)

Running CI steps locally
- Quick build and tests:
```bash
./gradlew --no-daemon --console=plain --warning-mode all test
./gradlew --no-daemon --console=plain --warning-mode all build
```

- Run integration tests (if present):
```bash
./gradlew integrationTest -PintegrationTest
```

- Use the helper script to run all checks at once:
```bash
chmod +x scripts/check-ci.sh
./scripts/check-ci.sh
```

## Qodana (Code Quality)

- `qodana.yaml` config exists in the repo root. The GitHub Action uses `JetBrains/qodana-action@v2025.2`.
- **PR scans** use `pr-mode: true` to limit analysis scope to changed files (faster, cheaper).
- **Push/manual scans** use `pr-mode: false` for full codebase analysis.
- The action requires an optional `QODANA_TOKEN` secret in GitHub repository settings for premium features (quick-fixes, etc.).
- Results are uploaded as SARIF/artifacts for visibility in workflow runs.
- You can run a local Qodana scan using the Qodana CLI or Docker image.

## Integration Tests

- Dedicated GitHub Actions job in `.github/workflows/integration-test.yml`.
- **Triggers**: `push` to `main`, `pull_request` to `main`, and manual `workflow_dispatch`.
- Runs `./gradlew integrationTest -PintegrationTest` and uploads test results as artifacts.
- Recommended: run integration tests locally before pushing to `main` to catch issues early.

## Dependency Updates (Dependabot)

- `.github/dependabot.yml` configures automatic dependency update PRs for Gradle.
- **Schedule**: Weekly on Mondays at 04:00 UTC.
- **Limits**: Up to 5 open PRs at a time.
- **Labels**: Automatically tagged with `dependencies` and `java`.
- **Commits**: Use conventional commit format `chore(deps):`.
- Auto-merge is disabled; review updates before merging.

You can manually trigger Dependabot from the GitHub UI (Insights -> Dependency graph -> Dependabot).

## Secrets and Repository Settings

- `QODANA_TOKEN` — (optional) used by the Qodana Action. Set this in GitHub (Repository -> Settings -> Secrets and variables -> Actions).
  - Without this token, Qodana still runs but some features (quick-fixes, reporting) may be limited.
- No other secrets are required for basic build/test/integration test jobs.

## Best Practices for Pull Requests

1. Run local checks before opening a PR:
   ```bash
   ./scripts/check-ci.sh
   ```

2. Keep PRs focused and small to speed up CI and code review.

3. Qodana runs in `pr-mode` on PRs to limit the analysis scope (faster, cheaper).

4. If a CI job fails:
   - Open the workflow run in GitHub Actions for logs and details.
   - Check the "Logs" tab in the failed job step for error messages.
   - Review artifacts (test results, reports) uploaded by the job.

5. Ensure integration tests pass locally before pushing to `main`:
   ```bash
   ./gradlew integrationTest -PintegrationTest
   ```

## CI Troubleshooting

| Issue | Solution |
|-------|----------|
| Gradle fails with "Permission denied: ./gradlew" | Run `chmod +x gradlew` (Unix/macOS). On Windows, use `gradlew.bat`. |
| Gradle cache issues / timeout | Clear the cache from GitHub Actions UI: Actions -> cache -> delete. Retry the job. |
| Qodana token warnings | Optional—set `QODANA_TOKEN` in repo secrets if you want premium Qodana features. |
| Build out of memory (OOM) | Increase JVM memory: `export _JAVA_OPTIONS="-Xmx2g -Xms512m"` before running `./gradlew`. |
| Integration test failures | Run `./gradlew integrationTest -PintegrationTest --info` locally for detailed logs. |

## Next Steps

If you want to extend CI (e.g., add Java 21 matrix, run datagen on a schedule, add static analysis linters):
1. Open an issue or PR with the desired behavior.
2. Propose changes to the workflow files in `.github/workflows/`.
3. Test locally first using `gradlew` commands, then update workflow YAML.
