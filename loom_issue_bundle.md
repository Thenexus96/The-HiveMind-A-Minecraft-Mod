Title: Fabric Loom devlaunchinjector deprecation: Invocation of Task.project at execution time

Summary
-------
During builds with Fabric Loom we observed a Gradle deprecation warning: "Invocation of Task.project at execution time has been deprecated." The issue points to net.fabricmc.devlaunchinjector.Main.main(). This file documents reproduction steps, environment, tested Loom versions, and the problems-report.

Repro steps
----------
1. Clone this repo and checkout the 'creation' branch.
2. Run: ./gradlew build --warning-mode all
3. Inspect build/reports/problems/problems-report.html

Environment
-----------
- OS: Windows_NT (development machine)
- Gradle: 8.14.4 (Gradle wrapper)
- Java: 17
- Fabric Loom plugin tested: 1.10.5, 1.11.1, 1.12.1, attempted 1.17 (failed plugin resolution)
- Repository: local The-HiveMind-A-Minecraft-Mod (branch: creation)

Observed behavior
-----------------
- With loom_version=1.10.5: deprecation warning present (problems-report contains "Invocation of Task.project at execution time has been deprecated" pointing to net.fabricmc.devlaunchinjector.Main.main()).
- With loom_version=1.11.1: deprecation warning absent; build completes without that deprecation.
- With loom_version=1.12.1: deprecation warning reappears.
- Attempt to use loom_version=1.17: plugin resolution failed due to changes in plugin publishing coordinates — not usable via the current plugin DSL in this project without updating plugin spec.

Files of interest
-----------------
- build/reports/problems/problems-report.html (contains the JSON/report entry with stack trace and file reference)
- build.gradle (shows plugin id 'fabric-loom' using loom_version property)
- gradle.properties (was modified during testing to pin loom_version to 1.11.1)

Suggested next steps for maintainers
-----------------------------------
1. Investigate devlaunchinjector.Main.main() usage of Task.project at execution time and migrate to a configuration-time safe API or defer invocation.
2. If impossible, document the deprecation and recommend a pinned Loom version (1.11.1 works for this repo as of 2026-06-12).
3. Add a CI check to run ./gradlew build --warning-mode all and fail when deprecation warnings are present (we added a Gitea CI workflow in this repo to help that).
4. Consider publishing a minimal reproduction case (small project) to help Loom maintainers reproduce.

Notes (what was tested here)
----------------------------
- Reproduced deprecation via Gradle problems-report. Pinning to 1.11.1 avoids the warning for this codebase.
- Full build commands and outputs are available in the repository build outputs and workflow artifacts when CI runs.

Contact / Reporter
------------------
- Reporter: local developer (branch: creation)
- Repository path: C:\Users\Nick's Main Rig\Projects\The-HiveMind-A-Minecraft-Mod

Appendix - problems report path
------------------------------
build/reports/problems/problems-report.html



