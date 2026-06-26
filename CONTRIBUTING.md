# Contributing to HiveMind

Thank you for considering contributing to HiveMind!

## How to Contribute

### Reporting Bugs

Use the [bug report template](.github/ISSUE_TEMPLATE/bug_report.md) and include:

- Minecraft version
- Mod version
- Steps to reproduce
- Expected vs actual behavior
- Logs/screenshots

### Suggesting Features

Use the [feature request template](.github/ISSUE_TEMPLATE/feature_request.md) and describe:

- The problem you're trying to solve
- Your proposed solution
- Any alternatives considered

### Code Contributions

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/your-feature`
3. **Follow code style**:
    - Use 4 spaces for indentation
    - Follow existing naming conventions
    - Add comments for complex logic
4. **Test thoroughly**
5. **Commit with clear messages**: `git commit -m "Add: drone formation system"`
6. **Push and create a PR**

### Development Setup

```bash
git clone https://github.com/your-username/hivemind.git
cd hivemind
./gradlew build
```

### CI & PR checks

This project uses **Gitea Actions for CI** (self-hosted). Before opening a pull request, please run the local CI check script to verify everything passes:

```bash
chmod +x scripts/check-ci.sh
./scripts/check-ci.sh
```

Or manually run these steps:

```bash
./gradlew --no-daemon --console=plain --warning-mode all test
./gradlew --no-daemon --console=plain --warning-mode all build
```

Additional checks:
- If your change includes runtime assets/datagen, document how to regenerate them and run any datagen tasks locally.
- For integration tests (if applicable): `./gradlew integrationTest -PintegrationTest`.
- Make sure lints / code analysis pass. See `docs/GITEA_CI.md` for details on the project's Gitea CI workflows.

The repository uses Gitea Actions with Docker containers. For full CI documentation, see:
- **Primary CI guide**: `docs/GITEA_CI.md` (Gitea self-hosted)
- **Secondary reference**: `docs/CI.md` (GitHub Actions, if used)

### Code Style Guidelines

- **Classes**: PascalCase (DroneEntity, HiveCodeManager)
- **Methods**: camelCase (setHiveMindOwner, generateHiveCode)
- **Constants**: UPPER_SNAKE_CASE (MAX_DRONES, HIVE_CODE_PREFIX)
- **Packages**: lowercase (net.sanfonic.hivemind.entity)

### Code Quality & Formatting

We use automated tools to maintain consistent code quality:

**Before committing**, run:
```bash
./gradlew spotlessApply  # Auto-fix formatting
./gradlew checkstyleMain # Check style violations
./gradlew spotbugsMain   # Find potential bugs
```

Or use the pre-commit hook:
```bash
chmod +x scripts/pre-commit-hook.sh
cp scripts/pre-commit-hook.sh .git/hooks/pre-commit
```

**Tools used**:
- **Spotless** — Auto-formats code to Google Java Style
- **Checkstyle** — Enforces style rules
- **SpotBugs** — Detects potential bugs

See [CODE_QUALITY.md](docs/CODE_QUALITY.md) for detailed guide.

### Commit Message Format

Types: Add, Fix, Change, Remove, Refactor, Docs, Test

## Project Structure

See [Developer Guide](docs/wiki/Developer-Guide.md) for architecture details.

## Questions?

Open a [discussion](discussions-link) or join our [Discord](discord-link).