# Quick Start for HiveMind Development

## I want to...

### 🚀 Set up my development environment
1. Read [Getting Started](wiki/Getting-Started.md)
2. Read [Developer Guide](wiki/Developer-Guide.md)
3. Run: `./gradlew build`

### 🧪 Run tests before opening a PR
```bash
chmod +x scripts/check-ci.sh
./scripts/check-ci.sh
```

Or manually:
```bash
./gradlew test
./gradlew build
./gradlew integrationTest -PintegrationTest
```

### 📝 Understand the CI/CD pipeline
- **Primary (Gitea self-hosted)**: [GITEA_CI.md](GITEA_CI.md) ← START HERE
- Secondary (GitHub Actions): [CI.md](CI.md)
- Local CI check: `scripts/check-ci.sh`

### 🧹 Clean up my local repository
- Read: [Maintenance Guide](MAINTENANCE.md)
- Quick clean: `./gradlew clean`
- Full clean: `rm -rf ~/.gradle/ && ./gradlew build`

### 🐛 Report a bug
- Use the bug report template: `.github/ISSUE_TEMPLATE/bug_report.md`
- Read: [Contributing Guidelines](../CONTRIBUTING.md)

### ✨ Suggest a feature
- Use the feature request template: `.github/ISSUE_TEMPLATE/feature_request.md`
- Read: [Contributing Guidelines](../CONTRIBUTING.md)

### 🏗️ Understand the architecture
- Read: [Architecture Design](design/architecture.md)
- Read: [Data Flow Diagram](design/data-flow.md)
- Read: [API Documentation](wiki/API-Documentation.md)

### 📊 Check project progress
- Read: [Roadmap & Progress Tracking](roadmap/progress-tracking.md)
- Read: [Phase 2 Details](roadmap/phase-2-drone-framework.md)

### 📚 Understand what was recently done
- Read: [Repository Cleanup Summary](REPO_CLEANUP_SUMMARY.md)

---

## Developer Tools — Debug Mode

Use Debug Mode to speed development and inspect hive/drone state in-game. It is implemented as a toggleable HUD and a console command.

Commands / Usage:

```bash
# Toggle Debug Mode (in-game keybind will be documented in the developer guide)
# (Example console command)
/hivemind_debug toggle

# Dump selected entity data to server log
/hivemind_debug dump

# Use Debug Mode to view: hovered entity id, role, owner, hive id, and basic performance counters
```

Enable Debug Mode while running an internal test world to visualize node links, entity info, and simple performance metrics (tick time, entity counts).


## Important Paths

| Path | Purpose |
|------|---------|
| `src/main/java/` | Main mod source code |
| `src/test/java/` | Unit tests |
| `src/integrationTest/java/` | Integration tests |
| `docs/` | All documentation |
| `.github/workflows/` | GitHub Actions CI workflows |
| `scripts/check-ci.sh` | Local CI check script |
| `build.gradle` | Gradle build configuration |
| `gradle.properties` | Project version and dependencies |

---

## Common Commands

```bash
# Build the project
./gradlew build

# Run unit tests
./gradlew test

# Run integration tests
./gradlew integrationTest -PintegrationTest

# Run local CI checks
./scripts/check-ci.sh

# Clean everything
./gradlew clean

# Run Gradle help
./gradlew help

# Check dependencies
./gradlew dependencies
```

---

## CI Status

**Build**: [![Build Status](badge-url)](link)  
**Code Quality**: [![Qodana](badge-url)](link)  
**Tests**: Run on every PR and push

All workflows are documented in [CI.md](CI.md).

---

## Need Help?

- 📖 Check the [docs/](.) directory for comprehensive guides
- 🤝 Read [CONTRIBUTING.md](../CONTRIBUTING.md) for contribution guidelines
- 🔧 Review [CI.md](CI.md) for CI/troubleshooting
- 📋 See [MAINTENANCE.md](MAINTENANCE.md) for repository maintenance


