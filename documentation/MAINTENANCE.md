# Repository Maintenance & Cleanup Guide

This document lists recommended cleanup steps and maintenance tasks to keep the repository healthy, small, and efficient.

## 1. Remove Generated/Build Artifacts from Git

The project's `.gitignore` already lists `build/` and `run/` directories, but if these files are already in Git history they should be untracked. This reduces repository size and prevents build artifacts from cluttering version control.

To remove tracked build artifacts while keeping them locally:

```bash
git rm -r --cached build/ || true
git rm -r --cached run/ || true
git commit -m "chore: remove generated artifacts from version control"
git push
```

> **Note**: Removing files from entire git history (to shrink the repo significantly) requires `git filter-repo` or `git filter-branch`. Coordinate with all contributors if considering this.

## 2. Audit Large Files and Vendor Libraries

Check `devlibs/` and `build/libs/` for binary jars. Decide whether these should be stored in the repo or fetched via Gradle/Maven Central:

```bash
find devlibs/ build/libs/ -name "*.jar" -exec ls -lh {} \; | sort -k5 -h
```

**Recommendation**: Remove non-source jars from VCS and configure Gradle to fetch them from Maven Central or a private repository. Source jars can be kept for reference.

## 3. Keep `.gitignore` Up to Date

Ensure `.gitignore` includes at least:
- `/.gradle/`
- `/build/`
- `/run/`
- `out/`
- IDE directories (`.idea/`, `*.iml`)
- OS artifacts (`*.DS_Store`)
- Logs (`/logs/`, `hs_err_*.log`, `replay_*.log`)

## 4. Dependency Hygiene

Use Gradle's built-in tasks to identify unused or conflicting dependencies:

```bash
./gradlew dependencies
./gradlew dependencyInsight --configuration runtimeClasspath --dependency <group:artifact>
```

**Recommendation**: Review and remove unused dependencies quarterly. Dependabot (configured in `.github/dependabot.yml`) will automatically create PRs for updates.

## 5. Documentation and Roadmap Maintenance

- Update `documentation/roadmap/` with milestone progress and upcoming features.
- Link maintainers to `documentation/CI.md` and `documentation/MAINTENANCE.md` in `README.md`.
- Keep `CONTRIBUTING.md` current with the latest development practices.

## 6. CI Automation

The project now includes:
- **GitHub Actions**: `.github/workflows/` with build, tests, Qodana, and integration tests.
- **Dependabot**: `.github/dependabot.yml` for automatic dependency update PRs.
- **Local check script**: `scripts/check-ci.sh` for contributors to validate changes before pushing.

## 7. Regularly Scheduled Maintenance Tasks

Consider adding these to your personal or team schedule:

| Task | Frequency | Purpose |
|------|-----------|---------|
| Review Dependabot PRs | Weekly | Keep dependencies up to date |
| Check build reports in CI | Per PR | Catch issues early |
| Review Qodana reports | Monthly | Improve code quality |
| Clean up old artifacts | Quarterly | Free up space in Actions artifact storage |
| Archive old feature branches | Monthly | Reduce clutter in git branches |

## 8. Local Development Hygiene

After testing locally, clean up temporary build files:

```bash
./gradlew clean
```

To perform a deep clean (removes all Gradle caches):

```bash
rm -rf ~/.gradle/
./gradlew build
```

## Next Steps

If you want to implement additional automation or cleanup:

1. **Remove tracked build artifacts** (non-destructive):
   - Run the `git rm --cached` commands above.
   - Commit and push.

2. **Audit and move large binaries**:
   - Identify large jars in `devlibs/` and `build/libs/`.
   - Configure Gradle to fetch them instead of storing in repo.

3. **Add a scheduled cleanup workflow**:
   - Create a GitHub Action to archive old artifacts and clean up old branches weekly.

4. **Enable branch protection rules**:
   - Require PR checks to pass before merge.
   - Require at least one review.
   - Dismiss stale PR approvals.

Contact the maintainers if you want help with any of these tasks.
