# Sprint 1 — Runbook

Last updated: 2026-07-02

This runbook explains how to run Sprint 1 locally and in CI, lists the sprint goals and acceptance criteria, and provides step-by-step developer onboarding commands, config examples, and the PR checklist so contributors can complete work reproducibly.

---

Sprint goal
 - Implement developer tooling and base systems required for Phase 2:
   1. Debug Mode: toggle + overlay + server dump command
   2. `HiveManager` and `Hive` server-side skeleton with world persistence
   3. Progression / Research skeleton (data model + unlock flow)
   4. Node-claiming and resource flow prototype
   5. Unit + integration tests for Hive persistence and DroneRole serialization

Sprint length: 2 weeks (start immediately). Owners: assign per-task in PRs (use branch prefix `sprint1/`).

Acceptance criteria (Definition of Done)
 - All tasks have at least one unit test covering the core happy path.
 - Debug Mode can be toggled in-game and the server-side dump command outputs Hive state to logs and a file in `run/logs/`.
 - `HiveManager` can create, retrieve and persist a `Hive` to the world save NBT (basic fields: id, owner UUID, resources map).
 - Research skeleton: data model (Node, Cost, Effect) exists, server unlock API exists and a sample node (+5% soldier damage) is implemented and tested.
 - Node-claiming prototype: player can claim a node in the world which increases hive resources and writes to persistent Hive data.
 - A PR with all code, tests and docs passes local `spotlessCheck`, `checkstyle`, `spotbugs` and `test` tasks.

Quick onboarding / prerequisites
 - Java 17 SDK installed (recommended: AdoptOpenJDK/Temurin 17).
 - Gradle wrapper present in repo (use it; do not rely on system Gradle).
 - Optional: Docker for containerized CI/emulation.
 - IDE: IntelliJ IDEA Community/Ultimate recommended. Import as Gradle project.

Important paths
 - Project root: repository top-level
 - Run data (dev client/server worlds): `run/`
 - Dev server config: `run/config/` (create `hivemind.json` if not present)
 - Docs for this sprint: `docs/roadmap/phase2-sprint1.md` and this runbook

Core commands (PowerShell / Windows)
Use PowerShell at project root; prefer the Gradle wrapper to ensure reproducible builds.

Run formatting & static analysis
```powershell
.\gradlew.bat spotlessCheck checkstyleMain spotbugsMain
# To auto-fix formatting:
.\gradlew.bat spotlessApply
```

Run unit tests and integration tests
```powershell
.\gradlew.bat test
# Integration tests (if implemented):
.\gradlew.bat integrationTest
```

Start the development client or server (Loom)
```powershell
# Start Minecraft client (local dev environment)
.\gradlew.bat runClient

# Start an integrated server for manual testing
.\gradlew.bat runServer
```

Run a single Gradle task in Docker (alternative reproducible environment)
```powershell
# From repository root (PowerShell). Adjust volume mount path quoting as needed.
docker run --rm -v "${PWD}:/workspace" -w /workspace gradle:8.3-jdk17 bash -lc "./gradlew build"
```

Debug Mode usage (developer tools)
 - Toggle in-game via provided command: `/hivemind_debug toggle` (server-op required).
 - Dump state: `/hivemind_debug dump` — writes HiveManager snapshot to `run/logs/hivemind-dump-<timestamp>.log` and to server log.
 - If you need a persistent default, create `run/config/hivemind.json` (example below).

Example `run/config/hivemind.json`
```json
{
  "max_drones_per_player": 25,
  "max_drones_per_server": 250,
  "lod_distance_blocks": 64,
  "debugMode_default": false
}
```

Recommended development flow for a task
 1. Create a feature branch: `sprint1/<short-task-name>` (e.g. `sprint1/debug-mode-overlay`).
 2. Implement code and add unit tests. Keep changes small and focused.
 3. Run `.\gradlew.bat spotlessApply` and `.\gradlew.bat test` locally.
 4. Push branch to remote Gitea and open a PR. Include this runbook link and fill the PR checklist.
 5. Address reviews and re-run CI until checks pass.

PR checklist (add to PR description)
 - [ ] Branch name uses `sprint1/` prefix
 - [ ] Added/updated unit tests covering new code (happy path)
 - [ ] All Gradle checks pass locally: `spotlessCheck`, `test`, `checkstyleMain`, `spotbugsMain`
 - [ ] Implementation matches acceptance criteria for its task
 - [ ] Updated docs: add any new developer-facing commands or config options to `docs/QUICK_START.md` or this runbook
 - [ ] Link to design decisions if applicable (`docs/DESIGN_DECISIONS.md`)

Task breakdown and owners (suggested)
 - Debug Mode (overlay + commands) — owner: @frontend-dev
   - Subtasks: toggle command, dump command, client overlay HUD, config & keybind
 - HiveManager + Hive persistence — owner: @backend-dev
   - Subtasks: basic model, create/retrieve, world NBT persist/load, tests
 - Research / Progression skeleton — owner: @design-dev
   - Subtasks: Node model, cost model, effect model, server unlock API, sample node
 - Node claiming & resource flow prototype — owner: @backend-dev
   - Subtasks: world interaction, resource increment, unit test for resource flow
 - Tests & CI integration — owner: @ci-dev
   - Subtasks: unit tests, integration tests, ensure Gradle tasks are incuded in Gitea workflow

How to run the sprint acceptance test locally (quick smoke)
 1. Start `runServer` in one terminal: `.\gradlew.bat runServer`.
 2. Start `runClient` in a second terminal or launch from IDE and join the local server.
 3. Toggle debug mode: `/hivemind_debug toggle` (verify server log shows state change).
 4. Create a Hive via the provided API/command or use the code path under test.
 5. Claim a sample node and verify the Hive's resources increased and persisted after server restart.

CI / Gitea workflow notes
 - Gitea CI uses containerized runners in our recommended configuration. Ensure `gradle:8.3-jdk17` image (or equivalent) is available on your runner host.
 - CI should run the following tasks: `spotlessCheck`, `checkstyleMain`, `spotbugsMain`, `test`, and `integrationTest` (integrationTest may be gated to scheduled or slower runners).
 - If CI fails with a style formatting error, run `.\gradlew.bat spotlessApply` and re-submit.

Troubleshooting
 - Gradle daemon issues: use `.\gradlew.bat --stop` to stop the daemon and retry.
 - If `runClient` fails due to missing mappings or resources, run `.\gradlew.bat --refresh-dependencies` and try again.
 - On Windows PowerShell, prefer `.\gradlew.bat` to avoid shell differences.

Risks & mitigations
 - Heavy server load when creating many drones: keep default conservative values and provide admin settings in `hivemind.json`.
 - Integration tests that require a running Minecraft environment: design integration tests to use small simulated environments or mock key subsystems where possible.

Next steps after Sprint 1
 - Implement DroneRole base classes and three baseline roles (Scout, Worker, Soldier) with simple behavior and tests.
 - Start performance profiling to determine safe default limits and LOD tuning.

References
 - `docs/roadmap/phase2-sprint1.md` — sprint plan and tasks
 - `docs/DESIGN_DECISIONS.md` — architecture decisions for Phase 2
 - `docs/QUICK_START.md` — developer quick start

---

If you want, I can also:
 - Create example skeleton classes and tests for HiveManager/Hive and add them to a `sprint1/` branch.
 - Add a small PR template file under `.gitea/PR_TEMPLATE.md` and update `CONTRIBUTING.md` to reference it.

End of runbook.

