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

### Code Style Guidelines

- **Classes**: PascalCase (DroneEntity, HiveCodeManager)
- **Methods**: camelCase (setHiveMindOwner, generateHiveCode)
- **Constants**: UPPER_SNAKE_CASE (MAX_DRONES, HIVE_CODE_PREFIX)
- **Packages**: lowercase (net.sanfonic.hivemind.entity)

### Commit Message Format

Types: Add, Fix, Change, Remove, Refactor, Docs, Test

## Project Structure

See [Developer Guide](docs/wiki/Developer-Guide.md) for architecture details.

## Questions?

Open a [discussion](discussions-link) or join our [Discord](discord-link).