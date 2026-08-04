# Gitea Workflows Reference

This directory contains Gitea Actions workflows optimized for self-hosted runners.

## Recommended Workflows

### ✅ Primary: `ci.yml`

**Use this for standard builds.**

- Runs: build, tests, integration tests
- Container: `gradle:8.3-jdk17` (includes all dependencies)
- Triggers: push/PR to main, creation, testing, pre-release
- Runtime: ~5-15 minutes

**Best for**: Daily development, standard builds

---

### ⚡ Optional: `ci-advanced.yml`

**Use this if you have multiple runners and want parallelization.**

- Parallel jobs: cache-warmup → test + build → integration-test
- Faster overall runtime if jobs run on separate runners
- Same container and triggers as primary

**To use**:
```bash
mv ci.yml ci-simple.yml
mv ci-advanced.yml ci.yml
```

**Best for**: Large teams, multiple self-hosted runners, performance optimization

---

## Archive: Not Recommended (Legacy)

### ❌ `build.yml`

**Status**: Not optimized for Gitea  
**Reason**: Uses GitHub Actions syntax that may not be fully supported  
**Keep as**: Reference or backup only

---

### ❌ `build-split.yml`

**Status**: GitHub Actions focused  
**Reason**: Uses `actions/cache` and GitHub-specific features  
**Keep as**: Reference for advanced GitHub Actions setup only

---

### ❌ `qodana_code_quality.yml`

**Status**: Limited Gitea support  
**Reason**: Qodana action works better on GitHub Actions  
**Alternatives**:
1. Run Qodana locally: `docker run -v $(pwd):/data jetbrains/qodana-jvm-community`
2. Use GitHub Actions mirror (if you have one)
3. Skip and rely on manual code reviews

---

## How to Choose

| Situation | Workflow |
|-----------|----------|
| First time setup, standard builds | `ci.yml` |
| You have 3+ self-hosted runners | `ci-advanced.yml` |
| You need faster parallel execution | `ci-advanced.yml` |
| You want simplicity and reliability | `ci.yml` |

---

## Documentation

- **Full guide**: `docs/GITEA_CI.md`
- **General CI info**: `docs/CI.md` (includes GitHub Actions)
- **Troubleshooting**: `docs/GITEA_CI.md` (Troubleshooting section)

---

## Quick Links

- [Gitea Actions Documentation](https://docs.gitea.io/en-us/actions/act-runner-installation/)
- [Gradle Docker Image](https://hub.docker.com/_/gradle)
- [HiveMind Development](../../documentation/QUICK_START.md)

