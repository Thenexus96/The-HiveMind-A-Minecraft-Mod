# Gitea CI/CD Guide — HiveMind

This guide is optimized for **Gitea self-hosted runners** (your primary platform). If you also use GitHub, see `documentation/CI.md` for GitHub Actions guidance.

## Overview

Your Gitea setup uses **Docker containers** with pre-installed Gradle and Java, which provides:
- ✅ No dependency on GitHub's action ecosystem
- ✅ Reproducible builds across runners
- ✅ Self-contained execution in containers
- ✅ Easy scaling across multiple self-hosted runners

## Gitea Workflows

### Primary Workflow: `ci.yml`

**Location**: `.gitea/workflows/ci.yml`

**Purpose**: Standard build, test, and integration test pipeline for all branches

**Triggers**:
- `push` to: `main`, `creation`, `testing`, `pre-release`
- `pull_request` to: `main`, `creation`, `testing`, `pre-release`

**What it does**:
1. Checks out code
2. Verifies Gradle and Java environment
3. Ensures gradlew is executable
4. Runs unit tests: `./gradlew test`
5. Builds project: `./gradlew build`
6. Runs integration tests (only on `main` branch)
7. Archives all reports and artifacts
8. Creates downloadable archive (tar.gz or zip)

**Runtime**: ~5-15 minutes (depends on your server)

### Advanced Workflow: `ci-advanced.yml` (Optional)

**Location**: `.gitea/workflows/ci-advanced.yml`

**Purpose**: Parallel job execution for faster CI (optional, advanced)

**When to use**:
- You have multiple self-hosted runners
- You want to parallelize tests and build
- You need separate integration test job

**Jobs**:
1. `cache-warmup` — Pre-downloads dependencies
2. `test` — Runs unit tests in parallel
3. `build` — Runs build in parallel
4. `integration-test` — Integration tests after build+test pass

**To switch to advanced workflow**:
```bash
# Backup the current workflow
cp .gitea/workflows/ci.yml .gitea/workflows/ci-simple.yml

# Rename the advanced workflow
mv .gitea/workflows/ci-advanced.yml .gitea/workflows/ci.yml
```

## Container Image

**Image**: `gradle:8.3-jdk17`

This includes:
- ✅ Gradle 8.3
- ✅ Java 17 (Temurin)
- ✅ Standard build tools (tar, zip, wget, curl, git)
- ✅ ~1.5GB, downloaded once and cached

**Why this image**:
- Includes all build dependencies
- No need to install or configure Java
- Standard for Java/Gradle projects
- Official Gradle image maintained by Docker

## Running Builds Locally

### Run the exact same build as CI

```bash
# Clone/pull your repo
cd /path/to/hivemind

# Run tests
./gradlew --no-daemon --console=plain --warning-mode all test

# Run build
./gradlew --no-daemon --console=plain --warning-mode all build

# Run integration tests (optional)
./gradlew integrationTest -PintegrationTest
```

### Or use the local check script

```bash
chmod +x scripts/check-ci.sh
./scripts/check-ci.sh
```

### Run in Docker (exact CI environment)

```bash
docker run --rm -v $(pwd):/workspace -w /workspace gradle:8.3-jdk17 \
  ./gradlew --no-daemon --console=plain --warning-mode all test build
```

## Troubleshooting

### Build fails in CI but works locally

**Problem**: Gradle cache issues or environment differences

**Solutions**:
1. Clear Gradle cache on runner:
   ```bash
   rm -rf ~/.gradle/caches/fabric-loom/
   ```

2. Check disk space:
   ```bash
   df -h
   ```

3. Run with verbose output:
   ```bash
   ./gradlew build --info --debug
   ```

### CI is slow

**Problem**: First run of CI job is always slow (downloads dependencies)

**Solutions**:
1. The `cache-warmup` job in `ci-advanced.yml` helps
2. Run locally once to populate your cache
3. Use `ci-advanced.yml` to parallelize jobs

### Permission denied: ./gradlew

**Problem**: Workflow runner doesn't have execute permission

**Solution**: Already handled in workflow with `chmod +x gradlew`

### Container image not found

**Problem**: Runner can't pull `gradle:8.3-jdk17` image

**Solutions**:
1. Ensure runner has internet access
2. Pre-pull image on runner: `docker pull gradle:8.3-jdk17`
3. Configure runner to use a private registry if needed

### Out of memory (OOM) during build

**Problem**: Build fails with memory errors

**Solutions**:
1. Increase Docker container memory limit in Gitea runner config
2. Reduce parallel tasks:
   ```bash
   ./gradlew build --max-workers=1
   ```
3. Clear cache before build:
   ```bash
   ./gradlew clean
   ```

## Self-Hosted Runner Setup

### Prerequisites

- Docker installed on runner machine
- Gitea Actions runner installed and registered
- Sufficient disk space (~10GB recommended)
- Network access to pull Docker images

### Register a Gitea Actions Runner

```bash
# Download the runner
wget https://dl.gitea.io/act_runner/act_runner-linux-amd64

chmod +x act_runner-linux-amd64

# Register runner (interactive)
./act_runner-linux-amd64 register

# Run the runner
./act_runner-linux-amd64 daemon
```

### Configure Runner for HiveMind

Edit your runner config to:
- Set labels: `linux,docker,gradle` (or similar)
- Allocate enough memory: ~4GB minimum, 8GB+ recommended
- Enable Docker support (required for container jobs)

### Best Practices for Self-Hosted Runners

1. **Use labels** to route jobs to appropriate runners:
   ```yaml
   runs-on: self-hosted  # or: self-hosted,docker,linux
   ```

2. **Run multiple runners** for parallelization

3. **Keep runners updated**:
   ```bash
   # Periodically update act_runner
   ./act_runner-linux-amd64 upgrade
   ```

4. **Monitor disk space**:
   ```bash
   df -h /
   docker system prune -a  # Clean up unused images
   ```

5. **Clean up Docker images**:
   ```bash
   docker image prune -a --filter "until=720h"  # Delete images > 30 days old
   ```

## Artifacts and Outputs

Each successful build creates archives in the workflow:
- `build_artifacts.tar.gz` — TAR archive of all outputs
- `build_artifacts.zip` — ZIP archive of all outputs

**Contents**:
- `build/libs/*.jar` — Compiled mod JAR files
- `build/reports/tests/` — Unit test reports
- `build/reports/integrationTest/` — Integration test reports
- `.qodana/` — Code quality reports (if Qodana runs)

### Download Artifacts

In Gitea:
1. Go to your repository
2. Click "Actions" tab
3. Select a workflow run
4. Download artifacts from the run page

### Access Artifact Reports

After download:
```bash
# Extract
tar -xzf build_artifacts.tar.gz
cd build_artifacts

# View test reports (open in browser)
open build/reports/tests/test/index.html
```

## Environment Variables

Available in workflows:
- `JAVA_HOME` — Points to Java runtime
- `GRADLE_USER_HOME` — Gradle cache directory (`.gradle` by default)
- `CI` — Always set to `true` in CI environment

### Custom Secrets

To use secrets in workflows:
1. Go to Gitea repository settings
2. Add secret: `Settings > Secrets`
3. Use in workflow:
   ```yaml
   - name: Build
     env:
      MY_SECRET: ${{ secrets.MY_SECRET }}
     run: ./gradlew build
   ```

## Performance Tuning

### Speed Up Build

**In workflow**:
```yaml
- name: Build
  run: ./gradlew build \
    --parallel \
    --build-cache \
    --no-daemon
```

**Locally**:
```bash
export _JAVA_OPTIONS="-Xmx4g -Xms1g"
./gradlew build --parallel --build-cache
```

### Reduce Memory Usage

```bash
./gradlew build \
  --max-workers=1 \
  -Xmx2g \
  --no-daemon
```

## Qodana Code Quality (Gitea)

Gitea support for Qodana is limited. Options:

1. **Run Qodana locally**:
   ```bash
   docker run --rm -v $(pwd):/data jetbrains/qodana-jvm-community
   ```

2. **Optional Gitea Qodana workflow** (`.gitea/workflows/qodana_code_quality.yml`):
   - Limited PR comments and annotations
   - Reports uploaded as artifacts
   - Use for CI tracking, not inline PR feedback

3. **Skip Qodana in Gitea** and rely on GitHub Actions mirror for code quality

## Monitoring and Logging

### View Logs in Gitea

1. Go to repository
2. Click "Actions" tab
3. Select a workflow run
4. Click a job to view logs
5. Logs auto-scroll as job runs

### Export Logs

Gitea allows you to download full logs from completed runs.

### Local Runner Logs

On the runner machine:
```bash
# View recent runner logs
journalctl -u act_runner -f

# Or if running in Docker
docker logs <runner-container-id> -f
```

## Next Steps

1. **Test locally**: Run `./scripts/check-ci.sh` before pushing
2. **Monitor first runs**: Watch Gitea Actions as workflows execute
3. **Optimize**: If builds are slow, switch to `ci-advanced.yml`
4. **Scale**: Add more self-hosted runners if needed
5. **Document**: Update your runner setup guide for team members

## FAQs

**Q: Why Docker containers instead of GitHub actions?**
A: Gitea doesn't have access to the GitHub actions marketplace. Docker provides a portable, self-contained environment that works anywhere.

**Q: Can I use a different Java version?**
A: Yes—use a different Docker image like `gradle:8.3-jdk21` for Java 21.

**Q: How do I run Qodana in Gitea?**
A: Run locally with Docker or keep Qodana on GitHub Actions if you mirror your repo there.

**Q: What if I have multiple runners?**
A: Use labels in `runs-on:` to route jobs. Switch to `ci-advanced.yml` for parallelization.

**Q: How much disk space do I need?**
A: ~10GB minimum. Docker caches images (~2GB), Gradle cache (~1-3GB), build output (~500MB).

---

**For GitHub Actions guidance**, see `documentation/CI.md`
**For general CI questions**, see `documentation/REPO_CLEANUP_SUMMARY.md`
