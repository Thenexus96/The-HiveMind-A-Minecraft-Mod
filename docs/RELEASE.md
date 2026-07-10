# Release & Version Management — HiveMind

This guide explains how to manage releases, version bumping, and changelog for the HiveMind project.

## Version Format

HiveMind uses semantic versioning with Minecraft version suffix:

```
MAJOR.MINOR.PATCH-PRERELEASE+MINECRAFT_VERSION

Examples:
0.2.0-alpha+1.20.1      (pre-release alpha)
0.3.0+1.20.1            (stable release)
1.0.0+1.20.1            (major release)
```

**Format breakdown**:
- `MAJOR` — Breaking API/gameplay changes
- `MINOR` — New features, backwards compatible
- `PATCH` — Bug fixes only
- `PRERELEASE` — Optional: `-alpha`, `-beta`, `-rc1` (pre-release identifier)
- `MINECRAFT_VERSION` — Minecraft version this mod targets

## Current Version

**Location**: `gradle.properties`

```groovy
mod_version=0.2.0-alpha+1.20.1
```

---

## Release Process

### Step 1: Prepare Release

1. **Update CHANGELOG.md**
   ```bash
   # Add section for new version at top
   # List all changes since last release
   ```

2. **Run quality checks**
   ```bash
   ./scripts/check-ci.sh
   ```

3. **Update version in `gradle.properties`**
   ```bash
   # Use script: (see below for details)
   ./scripts/version-bump.sh
   ```

4. **Commit version change**
   ```bash
   git add gradle.properties CHANGELOG.md
   git commit -m "Docs: prepare release v0.3.0"
   ```

### Step 2: Create Release Tag

```bash
# Tag the commit
git tag -a v0.3.0 -m "Release HiveMind v0.3.0"

# Push tag to repository
git push origin v0.3.0
```

### Step 3: Build Release

```bash
# Clean and build
./gradlew clean build

# Verify artifacts in build/libs/
ls -lh build/libs/
```

### Step 4: Create Release in Gitea/GitHub

**In Gitea**:
1. Go to repository → Releases
2. Click "Create Release"
3. Select tag: `v0.3.0`
4. Title: `HiveMind v0.3.0`
5. Description: Copy from CHANGELOG.md
6. Upload JAR from `build/libs/`
7. Mark as pre-release if applicable

**In GitHub** (if mirrored):
1. Go to Releases
2. "Draft a new release"
3. Tag: `v0.3.0`
4. Release title: `v0.3.0 — Release Title`
5. Copy changelog from CHANGELOG.md
6. Upload JAR file
7. Publish

### Step 5: Announce Release

- Update project documentation
- Post release notes
- Notify users/community

---

## Version Bumping

### Automatic Version Bump Script

Use the provided script to bump versions:

```bash
./scripts/version-bump.sh [version] [minecraft-version]

# Examples:
./scripts/version-bump.sh 0.3.0          # Keep current Minecraft version
./scripts/version-bump.sh 0.3.0 1.21     # Change Minecraft version too
```

### Manual Version Bump

Edit `gradle.properties` directly:

```groovy
# Before
mod_version=0.2.0-alpha+1.20.1

# After
mod_version=0.3.0+1.20.1
```

### Version Bump Examples

**Fix (patch) release**:
```groovy
0.2.0-alpha+1.20.1  →  0.2.1+1.20.1
```

**Feature (minor) release**:
```groovy
0.2.0+1.20.1  →  0.3.0+1.20.1
```

**Breaking (major) release**:
```groovy
0.3.0+1.20.1  →  1.0.0+1.20.1
```

**Promote alpha to stable**:
```groovy
0.2.0-alpha+1.20.1  →  0.2.0+1.20.1
```

**Update for new Minecraft**:
```groovy
0.3.0+1.20.1  →  0.3.0+1.21
```

---

## Changelog Management

The project maintains a `CHANGELOG.md` file to track all changes.

### CHANGELOG Format

```markdown
# Changelog

All notable changes to HiveMind are documented here.

## [0.3.0] — 2026-06-26

### Added
- New drone AI behaviors
- Support for Minecraft 1.21
- Hive teleportation system

### Changed
- Improved drone control responsiveness
- Refactored entity serialization

### Fixed
- Fixed drone despawning on chunk unload
- Fixed HiveCode collision with other mods

### Deprecated
- Old drone command syntax (use `/hivemind control` instead)

### Removed
- Removed legacy config format

### Security
- Fixed potential data corruption on rapid saves

## [0.2.0] — 2026-05-15

### Added
- Initial Phase 2 implementation
- DroneEntity with basic AI
- Ownership and HiveCode systems

...
```

### Changelog Sections

- **Added** — New features
- **Changed** — Changes to existing features
- **Deprecated** — Features marked for removal
- **Removed** — Removed features
- **Fixed** — Bug fixes
- **Security** — Security issues fixed

### Update Changelog Before Release

```bash
# 1. Add new version section at top
# 2. List all changes since last release
# 3. Commit with version bump

git add CHANGELOG.md gradle.properties
git commit -m "Docs: prepare release v0.3.0"
```

---

## Release Workflow Automation

### (Optional) GitHub Actions Release Workflow

You can create an automated workflow (if using GitHub):

```yaml
name: Release

on:
  push:
    tags:
      - 'v*'

jobs:
  release:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Build release
        run: ./gradlew build
      
      - name: Create GitHub Release
        uses: actions/create-release@v1
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        with:
          tag_name: ${{ github.ref }}
          release_name: Release ${{ github.ref }}
```

For now, releases are **manual in Gitea** (recommended for control).

---

## Release Checklist

Use this checklist before each release:

- [ ] All PRs merged and tests passing
- [ ] `./scripts/check-ci.sh` passes locally
- [ ] Quality reports (code style, bugs) reviewed
- [ ] CHANGELOG.md updated with all changes
- [ ] Version bumped in `gradle.properties`
- [ ] Commit message uses: `Docs: prepare release vX.Y.Z`
- [ ] Tag created: `git tag -a vX.Y.Z -m "Release vX.Y.Z"`
- [ ] Tag pushed: `git push origin vX.Y.Z`
- [ ] Build successful: `./gradlew clean build`
- [ ] Artifacts verified: `ls build/libs/`
- [ ] Release created in Gitea with JAR uploaded
- [ ] Release notes copied from CHANGELOG.md
- [ ] README.md status line updated with new version
- [ ] Release announced (if applicable)

---

## Semantic Versioning Rules

### When to Bump MAJOR

- Breaking API changes (mods using your API break)
- Gameplay changes that affect existing saves
- Removing existing features

### When to Bump MINOR

- New features added (backwards compatible)
- New commands or mechanics
- New entity types or systems

### When to Bump PATCH

- Bug fixes only
- No new features
- No API changes

### Pre-release Versions

Use pre-release identifiers before release:

- `-alpha` — Early, unstable, many bugs expected
- `-beta` — Getting closer, most features done
- `-rc1` — Release candidate, expect final

Examples:
```
0.3.0-alpha → 0.3.0-beta → 0.3.0-rc1 → 0.3.0
```

---

## Minecraft Version Updates

When Minecraft releases a new version:

1. **Update yarn mappings** (in `gradle.properties`)
   ```
   yarn_mappings=1.21+build.X
   ```

2. **Update Fabric and APIs** (if needed)
   ```
   fabric_version=0.92.5+1.21
   ```

3. **Update mod version** (change Minecraft suffix)
   ```
   mod_version=0.3.0+1.21
   ```

4. **Test thoroughly**
   ```bash
   ./gradlew build test
   ```

5. **Create release for new Minecraft version**

---

## Managing Multiple Minecraft Versions

If you maintain multiple Minecraft versions:

1. Create separate branches: `release/1.20`, `release/1.21`
2. Tag releases with version: `v0.3.0-1.20`, `v0.3.0-1.21`
3. Maintain separate release notes

Or maintain single branch and update version string per Minecraft version.

---

## Distribution Channels

After creating a release, distribute via:

1. **Gitea Releases** (primary for self-hosted)
   - Upload JAR to Gitea release page

2. **GitHub Releases** (if mirrored)
   - GitHub Actions can auto-create releases
   - Or manually upload JAR

3. **CurseForge** (optional)
   - Upload JAR manually or via API

4. **Modrinth** (optional)
   - Maintain separate account
   - Upload JAR manually or via API

---

## Troubleshooting

### "Build fails before release"

**Solution**:
```bash
./gradlew clean build
./scripts/check-ci.sh
```

Fix any issues, then re-attempt.

### "Wrong version in CHANGELOG"

**Solution**: Update CHANGELOG.md before tagging.

### "Need to re-release (forgot something)"

**Solution**: Create new tag:
```bash
# Delete old tag (local)
git tag -d v0.3.0

# Delete old tag (remote)
git push origin --delete v0.3.0

# Create new tag
git tag -a v0.3.0 -m "Release v0.3.0"
git push origin v0.3.0
```

### "Accidentally released from wrong branch"

**Solution**: Delete release and tag, reset to correct branch:
```bash
# Delete in Gitea UI
# Then locally:
git tag -d v0.3.0
git push origin --delete v0.3.0
```

---

## Best Practices

1. **Always update CHANGELOG.md** before releasing
2. **Run quality checks** before creating tag
3. **Use semantic versioning** consistently
4. **Tag commits** for easy reference
5. **Keep changelog readable** with clear sections
6. **Test release** before announcing
7. **Document breaking changes** clearly
8. **Provide migration guide** for major versions

---

## Tools & Scripts

| Script | Purpose |
|--------|---------|
| `./scripts/version-bump.sh` | Bump version automatically |
| `./scripts/check-ci.sh` | Run all quality checks |
| `./gradlew build` | Build release JAR |

---

## References

- **Semantic Versioning**: https://semver.org/
- **Keep a Changelog**: https://keepachangelog.com/
- **Gradle Versioning**: https://docs.gradle.org/current/userguide/publishing_maven.html


