# API Stability & Backward Compatibility — HiveMind

This document defines the HiveMind API stability guarantee, versioning strategy, and guidelines for maintaining backward compatibility.

## API Stability Promise

HiveMind aims to maintain backward compatibility within minor versions (X.Y.0) to allow mod developers to use the HiveMind API reliably.

- ✅ **Stable APIs** remain compatible within major versions
- ⚠️ **Breaking changes** only happen in major version upgrades
- 📢 **Deprecation period** provided before removal
- 📖 **Clear migration guides** for all breaking changes

---

## Semantic Versioning

HiveMind follows strict semantic versioning with Minecraft suffix:

```
MAJOR.MINOR.PATCH+MINECRAFT_VERSION
```

### MAJOR Version (0 → 1)

**When**: Significant API breaking changes, gameplay redesign

**What changes**:
- ❌ Entity IDs, ownership model changes
- ❌ Core command structures
- ❌ Entity data serialization format
- ✅ Old APIs fully removed (deprecation period passed)
- ✅ Major new features

**Migration**: Requires code rewrite, automatic migration not possible

**Example**: `0.3.0+1.20.1` → `1.0.0+1.20.1`

### MINOR Version (0.2 → 0.3)

**When**: New features, enhancements, non-breaking improvements

**What's guaranteed**:
- ✅ All public APIs remain compatible
- ✅ Existing mods continue to work
- ✅ Existing saves remain compatible
- ✅ New optional features/hooks added

**Breaking changes**: NONE

**Example**: `0.2.0+1.20.1` → `0.3.0+1.20.1`

### PATCH Version (0.2.0 → 0.2.1)

**When**: Bug fixes, performance improvements, security patches

**What's guaranteed**:
- ✅ Full backward compatibility
- ✅ No API changes
- ✅ No gameplay changes
- ✅ Existing saves fully compatible

**Breaking changes**: NONE (except security fixes)

**Example**: `0.2.0+1.20.1` → `0.2.1+1.20.1`

### Pre-release Versions

Pre-release versions (alpha, beta, RC) may include breaking changes:

- `-alpha` — Unstable, frequent breaking changes
- `-beta` — More stable, fewer breaking changes
- `-rc` (release candidate) — Final, no breaking changes expected

**Convention**: Treat pre-release as MAJOR version bump (assume breaking)

---

## What's Part of the Public API

### ✅ Public & Stable

These are part of the guaranteed API:

1. **Entity Classes**
   - `DroneEntity`
   - `EntityAttributes` and registrations
   - Public methods and fields

2. **Commands**
   - `/hivemind` command structure
   - Command arguments and return values

3. **Events** (if implemented)
   - Callback interfaces and hooks
   - Event firing mechanisms

4. **NBT Data Format**
   - Drone save data structure
   - HiveCode format
   - Ownership data persistence

5. **Config System**
   - Configuration file format
   - Config key names and types

### ❌ Internal & Not Stable

These can change without notice:

1. **Internal Classes**
   - Classes in `net.sanfonic.hivemind.internal.*`
   - Classes marked `@Internal` annotation

2. **Private/Package-Private Members**
   - Private fields and methods
   - Package-private implementations

3. **Build/Dev Utilities**
   - Test utilities
   - Development tools

### ⚠️ Deprecated (Marked for Removal)

These have deprecation period before removal:

- Marked with `@Deprecated` annotation
- Javadoc indicates removal timeline
- Replacement API provided
- Deprecation warnings in logs

---

## Deprecation Policy

### Before Removal

1. **Announce deprecation** in release notes
2. **Mark in code**: `@Deprecated` annotation with message
3. **Provide migration guide**: Document replacement
4. **Support period**: Minimum 2 minor versions
5. **Issue warnings**: Log when deprecated API used

### Deprecation Annotation Example

```java
/**
 * @deprecated Use {@link DroneEntity#getHiveCode()} instead.
 * Will be removed in HiveMind 0.5.0.
 * 
 * Migration: Replace getDroneId() with getHiveCode()
 */
@Deprecated(since = "0.3.0", forRemoval = true)
public String getDroneId() {
    return getHiveCode();
}
```

### Deprecation Timeline

**Minor version X.Y → X.Y+2**:
- Announce deprecation at X.Y
- Support until X.Y+2
- Remove in X.Y+3

**Example**:
- Deprecated in `0.2.0`
- Supported in `0.2.1`, `0.3.0`, `0.3.1`
- Removed in `0.4.0`

---

## Compatibility Matrix

Version compatibility for mods using HiveMind:

| Mod Built With | 0.2.0 | 0.2.1 | 0.3.0 | 0.3.1 | 1.0.0 |
|---|---|---|---|---|---|
| **HiveMind 0.2.0** | ✅ | ✅ | ⚠️* | ⚠️* | ❌ |
| **HiveMind 0.2.1** | ✅ | ✅ | ✅ | ✅ | ❌ |
| **HiveMind 0.3.0** | ❌ | ❌ | ✅ | ✅ | ❌ |
| **HiveMind 0.3.1** | ❌ | ❌ | ✅ | ✅ | ❌ |
| **HiveMind 1.0.0** | ❌ | ❌ | ❌ | ❌ | ✅ |

- ✅ Full compatibility
- ⚠️* Compatible (may have enhanced features)
- ❌ Not compatible

---

## Breaking Changes Policy

### Types of Breaking Changes

**Allowed in MAJOR versions only**:
- Rename public classes/interfaces
- Change method signatures
- Move/remove public methods
- Change behavior of existing methods
- Change data serialization format

**Not allowed in MINOR/PATCH**:
- Remove public API
- Change method signatures
- Break existing mods

### Before Breaking Change

1. **Check impact**: Identify affected mods/implementations
2. **Plan migration**: Provide replacement API
3. **Announce early**: In roadmap/discussion
4. **Deprecate first**: Give 2+ minor versions notice
5. **Document**: Create migration guide

### Migration Guide Template

```markdown
# Migration Guide: X.Y → X.Z

## Changed: DroneEntity Constructor

### Before
```java
new DroneEntity(world, owner, hiveCode);
```

### After
```java
DroneEntity drone = new DroneEntity(world);
drone.setOwner(owner);
drone.setHiveCode(hiveCode);
```

### Why
- Clearer API
- More flexible initialization

### Update Your Code
Replace constructor calls with setter methods.
```

---

## Stability Levels

Different parts of the API have different stability guarantees:

| Level | Stability | Breaking Changes | Example |
|-------|-----------|---|---|
| **Stable** | High | Only in major versions | Core entity classes, commands |
| **Experimental** | Low | May change frequently | New event system (if unstable) |
| **Internal** | None | No guarantee | Internal utilities, helpers |

### Mark Experimental APIs

```java
/**
 * @experimental This API is subject to change and may have breaking changes
 * in minor versions. Use at your own risk.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Experimental {
}
```

---

## Performance Compatibility

API calls should maintain similar performance characteristics:

- ✅ Method calls not dramatically slower
- ✅ Memory usage not significantly increased
- ✅ Serialization format compatible

---

## Data Compatibility

### NBT/Persistence Format

**Breaking changes allowed in**:
- MAJOR version bumps
- After deprecation period

**Non-breaking migration**:
- Automatic conversion on load
- Compatibility layer for old format

**Example** (0.2 → 0.3):
```java
if (data.contains("old_format")) {
    // Convert old format to new format
    convertOldData(data);
}
```

### Config Files

**Breaking changes**:
- Announce in release notes
- Provide migration script if needed
- Support both formats temporarily

---

## Testing Compatibility

### Unit Tests

- ✅ All public API methods tested
- ✅ Backward compatibility tests
- ✅ Migration scenarios tested

### Integration Tests

- ✅ Works with common mods
- ✅ Save file compatibility
- ✅ Command compatibility

---

## Roadmap & Planned Breaking Changes

### Known Breaking Changes (Future)

None currently planned. See `documentation/roadmap/` for future phases.

### Phases

| Phase | Version | Changes |
|-------|---------|---------|
| **Phase 2** | 0.2.x | Stable, no breaking changes |
| **Phase 3** | 0.3.x | New features, no breaking changes |
| **Phase 4** | 0.4.x | Control link, no breaking changes |
| **Phase 5** | 0.5.x | Major refactor, **breaking changes possible** |
| **Stable** | 1.0.0+ | Strict API stability |

---

## Reporting Compatibility Issues

Found a compatibility issue?

1. **Verify version**: What versions are affected?
2. **Test reproduction**: Can you reproduce consistently?
3. **Report issue**: Create GitHub/Gitea issue with:
   - HiveMind versions
   - Dependent mods/code
   - Error logs
   - Minimal reproduction

---

## API Reference

### Current Stable APIs

- ✅ `DroneEntity` class
- ✅ HiveMind commands (`/hivemind_link`, `/hivemind_control`)
- ✅ Entity ownership system
- ✅ HiveCode identifier system

### Experimental/Unstable

Currently, all APIs are considered stable for 0.2+ releases.

### Internal (Do Not Use)

- Internal utilities packages
- Development test classes
- Debug/profiling code

---

## Best Practices for Mod Developers

1. **Use stable APIs only**
   - Check if API is marked `@Experimental`
   - Avoid using internal classes

2. **Follow deprecation notices**
   - Update code when APIs deprecated
   - Don't wait until removal

3. **Version your mods**
   - Specify compatible HiveMind versions
   - Test with multiple versions

4. **Subscribe to changes**
   - Watch release notes
   - Read migration guides
   - Test pre-releases

5. **Report issues early**
   - Test pre-release versions
   - Report compatibility problems
   - Provide feedback

---

## Version Compatibility Declaration

**Format for mod developers**:

```gradle
dependencies {
    modImplementation "net.sanfonic.hivemind:hivemind:0.2.0+1.20.1"
}
```

Or with version constraint:

```gradle
dependencies {
    modImplementation "net.sanfonic.hivemind:hivemind:0.2.0+1.20.1" // Exact version
    modImplementation "net.sanfonic.hivemind:hivemind:0.2+" // Minor updates OK
}
```

---

## FAQ

### Q: Will old mods work with new HiveMind?

**A**: Yes, within major versions. A mod built for 0.2.0 works with 0.2.1 and 0.3.0. Major version 1.0.0 may require updates.

### Q: What if I use internal APIs?

**A**: You assume all risk. Internal APIs can change without notice. Use public APIs instead.

### Q: How long before deprecated APIs are removed?

**A**: Minimum 2 minor versions. Example: Deprecated in 0.2.0, removed in 0.4.0.

### Q: Can you extend the deprecation period?

**A**: Possibly. Open an issue explaining your use case.

### Q: What about pre-release versions (alpha, beta)?

**A**: Pre-release versions may have breaking changes. Not recommended for production use.

### Q: Can I depend on HiveMind 1.0.0 features if they come?

**A**: After 1.0.0 release, yes. Before then, use stable 0.x versions.

---

## References

- **Semantic Versioning**: https://semver.org/
- **Java Deprecation**: https://docs.oracle.com/javase/9/documentation/api/java/lang/Deprecated.html
- **API Evolution**: https://cr.openjdk.org/~darcy/patterns/P.005.html
