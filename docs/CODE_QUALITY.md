# Code Quality & Static Analysis — HiveMind

This guide explains the code quality tools integrated into the HiveMind project and how to use them.

## Overview

The project uses three complementary tools to maintain code quality:

| Tool | Purpose | Check | Fix |
|------|---------|-------|-----|
| **Spotless** | Code formatting | `./gradlew spotlessCheck` | `./gradlew spotlessApply` |
| **Checkstyle** | Style rules & conventions | `./gradlew checkstyleMain` | Manual (see violations) |
| **SpotBugs** | Bug detection | `./gradlew spotbugsMain` | Manual (see report) |

All three are integrated into the CI pipeline and run as part of `./gradlew build`.

---

## Quick Start

### Before Pushing

Always run this command before pushing:

```bash
./scripts/check-ci.sh
```

Or manually:

```bash
# Format code
./gradlew spotlessApply

# Check style
./gradlew checkstyleMain

# Check for bugs
./gradlew spotbugsMain

# Run tests
./gradlew test
```

### Install Git Pre-Commit Hook

Automatically run checks before each commit:

```bash
chmod +x scripts/pre-commit-hook.sh
cp scripts/pre-commit-hook.sh .git/hooks/pre-commit
```

Now every commit will run quality checks automatically.

---

## Tools in Detail

### 1. Spotless — Code Formatting

**Purpose**: Automatically format code consistently

**Style Guide**: Google Java Style Guide (with modifications for Minecraft)

**Features**:
- Removes trailing whitespace
- Ensures files end with newline
- Reformats code to standard style
- Removes unused imports
- Formats annotations

#### Running Spotless

```bash
# Check if formatting is correct
./gradlew spotlessCheck

# Automatically fix formatting
./gradlew spotlessApply

# Check specific file
./gradlew spotlessCheck --info
```

#### What Gets Formatted

- All Java files in `src/main/java/` and `src/test/java/`
- Excluded: `src/integrationTest/java/` (if needed, adjust `build.gradle`)

#### Spotless Configuration

- **File**: `build.gradle` (spotless block)
- **Formatter**: `googleJavaFormat('1.19.1')`
- **Max line length**: 100 characters (from Checkstyle)

#### IDE Integration

**IntelliJ/Android Studio**:
1. Go to Settings → Editor → Code Style
2. Set scheme to "Google Style"
3. Or run `./gradlew spotlessApply` after committing

**VSCode**:
1. Install "Prettier" or "Format Code Action" extension
2. Or run `./gradlew spotlessApply` before pushing

---

### 2. Checkstyle — Style Rules

**Purpose**: Enforce consistent coding conventions

**Configuration**: Google Java Style with Minecraft modding conventions

**Key Rules**:
- Import organization (java.*, javax.*, org.*, com.*)
- Naming conventions (classes, methods, constants, packages)
- Line length limit (100 characters)
- Proper spacing around operators and keywords
- Brace placement and indentation
- Method and class size limits
- Javadoc requirements

#### Running Checkstyle

```bash
# Run Checkstyle and generate report
./gradlew checkstyleMain

# View HTML report
open build/reports/checkstyle/main.html
```

#### Configuration

- **File**: `checkstyle.xml` (root of project)
- **Tool Version**: 10.12.7
- **Reports**: HTML and XML generated in `build/reports/checkstyle/`

#### Common Violations & Fixes

| Violation | Fix |
|-----------|-----|
| Line too long (>100 chars) | Break into multiple lines |
| Missing imports | Add import statement (Spotless helps) |
| Wrong brace placement | Use Spotless: `./gradlew spotlessApply` |
| Unused imports | Spotless removes automatically |
| Wrong naming convention | Rename to follow pattern (e.g., `CONSTANT_NAME` for constants) |
| Missing Javadoc | Add documentation comment (for public classes/methods) |

#### Customizing Rules

Edit `checkstyle.xml` to adjust:
- Line length (change `<property name="max" value="100"/>`)
- Method complexity limits
- Import grouping order
- Javadoc requirements

After changes, run: `./gradlew checkstyleMain --refresh-dependencies`

---

### 3. SpotBugs — Bug Detection

**Purpose**: Find potential bugs and code issues

**Detection Categories**:
- Null pointer dereferences
- Infinite loops
- Dead code
- Resource leaks
- Race conditions
- Type mismatches
- And many more...

#### Running SpotBugs

```bash
# Run SpotBugs analysis
./gradlew spotbugsMain

# View HTML report
open build/reports/spotbugs/main.html
```

#### Configuration

- **File**: `spotbugs-exclude.xml` (root of project)
- **Report Level**: Medium (balances detail vs noise)
- **Effort**: Default (good for most projects)
- **Reports**: HTML and XML generated in `build/reports/spotbugs/`

#### Understanding Bug Categories

| Category | Severity | Example |
|----------|----------|---------|
| **CORRECTNESS_ISSUE** | HIGH | Potential null pointer dereference |
| **STYLE_ISSUE** | LOW | Dead code, unused variables |
| **PERFORMANCE_ISSUE** | MEDIUM | Inefficient code patterns |
| **SECURITY_ISSUE** | HIGH | SQL injection, weak crypto |
| **I18N_ISSUE** | MEDIUM | Hardcoded strings |

#### Excluding False Positives

If you find a false positive or intentional pattern, exclude it in `spotbugs-exclude.xml`:

```xml
<Match>
  <Bug pattern="NP_NULL_ON_SOME_PATH"/>
  <Class name="com.example.MyClass"/>
  <Method name="myMethod"/>
</Match>
```

Then run: `./gradlew spotbugsMain`

---

## Quality Checks in CI

### GitHub Actions / Gitea

Quality checks run automatically on:
- **Every PR**: Tests and formatting checks
- **Every push to main**: Full analysis (tests, formatting, style, bugs)

**Workflow**: `.gitea/workflows/ci.yml` includes:
```yaml
./gradlew test                 # Unit tests
./gradlew build               # Build + Checkstyle + SpotBugs
./gradlew integrationTest     # Integration tests (main only)
```

### Local CI Check

Run the same checks locally:

```bash
./scripts/check-ci.sh
```

This runs tests and build (includes all quality checks).

---

## Pre-Commit Hook

The pre-commit hook automatically runs before each commit to catch issues early.

### Install Hook

```bash
chmod +x scripts/pre-commit-hook.sh
cp scripts/pre-commit-hook.sh .git/hooks/pre-commit
```

### What It Checks

1. Code formatting (Spotless)
2. Style rules (Checkstyle)
3. Bugs (SpotBugs)
4. Unit tests

### Skip Hook (If Necessary)

```bash
git commit --no-verify
```

> ⚠️ **Not recommended** — only use if the hook has a false positive

### Uninstall Hook

```bash
rm .git/hooks/pre-commit
```

---

## Fixing Quality Issues

### Step 1: Identify Issues

```bash
# Run all quality checks
./gradlew check

# View reports
open build/reports/
```

### Step 2: Auto-Fix Formatting

```bash
./gradlew spotlessApply
```

This fixes most common style issues automatically.

### Step 3: Fix Style Violations

Read the Checkstyle report:

```bash
open build/reports/checkstyle/main.html
```

Common fixes:
- Break long lines
- Add/fix imports
- Rename variables/methods
- Add documentation

### Step 4: Investigate Bugs

Open the SpotBugs report:

```bash
open build/reports/spotbugs/main.html
```

Investigate each issue:
- Is it a real bug?
- Is it a false positive?
- Can it be fixed?

If it's a false positive, exclude in `spotbugs-exclude.xml`.

### Step 5: Recheck

```bash
./gradlew check
```

Repeat until all issues are resolved.

---

## Best Practices

1. **Before pushing**, run:
   ```bash
   ./scripts/check-ci.sh
   ```

2. **Install pre-commit hook** to catch issues early:
   ```bash
   chmod +x scripts/pre-commit-hook.sh
   cp scripts/pre-commit-hook.sh .git/hooks/pre-commit
   ```

3. **Format code regularly**:
   ```bash
   ./gradlew spotlessApply
   ```

4. **Review quality reports** (not just passing/failing):
   ```bash
   open build/reports/checkstyle/main.html
   open build/reports/spotbugs/main.html
   ```

5. **Keep code clean**:
   - No dead code
   - No unused variables
   - No null pointer issues
   - Consistent style

6. **Write tests** to catch bugs early

---

## Troubleshooting

### "Spotless format violations"

**Fix**:
```bash
./gradlew spotlessApply
git add -A
git commit --amend
```

### "Checkstyle violations"

**Step 1**: View report
```bash
open build/reports/checkstyle/main.html
```

**Step 2**: Fix issues manually (break long lines, rename variables, etc.)

**Step 3**: Re-check
```bash
./gradlew checkstyleMain
```

### "SpotBugs issues"

**Step 1**: View report
```bash
open build/reports/spotbugs/main.html
```

**Step 2**: Evaluate each bug:
- Can it be fixed?
- Is it a false positive?
- Should it be excluded?

**Step 3**: Fix or exclude
```bash
# Fix in code, or
# Exclude in spotbugs-exclude.xml
./gradlew spotbugsMain
```

### Build takes too long

Quality checks add ~2-3 minutes to builds.

**Speed up locally**:
```bash
# Skip SpotBugs (faster)
./gradlew build -x spotbugsMain

# Skip Checkstyle (very fast)
./gradlew build -x checkstyleMain -x spotbugsMain
```

> ⚠️ **Warning**: CI always runs all checks. Only skip locally for faster iteration.

---

## IDE Configuration

### IntelliJ/Android Studio

1. **Import Google Style**:
   - Settings → Editor → Code Style
   - Select "Google Style" or import from `checkstyle.xml`

2. **Enable SpotBugs Plugin**:
   - Preferences → Plugins → Search "SpotBugs"
   - Install and restart

3. **Enable Checkstyle Plugin**:
   - Preferences → Plugins → Search "Checkstyle"
   - Install and restart

### VSCode

1. **Install Extensions**:
   - "Checkstyle for Java"
   - "SpotBugs for VS Code"
   - "Prettier" (for formatting)

2. **Configure Settings**:
   ```json
   "[java]": {
     "editor.formatOnSave": true,
     "editor.defaultFormatter": "Prettier"
   }
   ```

---

## Continuous Improvement

### Monthly

- Review quality metrics
- Check for recurring issues
- Update rules if needed

### Quarterly

- Update tool versions
- Re-assess rules
- Add new checks if beneficial

### When Adding Features

- Run full quality suite: `./gradlew check`
- Fix issues before merging
- Keep quality metrics stable or improving

---

## References

- **Spotless**: https://github.com/diffplug/spotless
- **Checkstyle**: https://checkstyle.org/
- **SpotBugs**: https://spotbugs.readthedocs.io/
- **Google Java Style**: https://google.github.io/styleguide/javaguide.html

---

## Need Help?

- **Spotless issues**: See `spotless` block in `build.gradle`
- **Checkstyle issues**: Edit `checkstyle.xml` or review report
- **SpotBugs issues**: Edit `spotbugs-exclude.xml` or investigate bug
- **General CI**: See `docs/GITEA_CI.md` or `docs/CI.md`


