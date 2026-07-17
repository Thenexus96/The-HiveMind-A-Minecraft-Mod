# Phase 2 — Sprint 1 (2 weeks)

Sprint dates: Immediately (start) — 2 weeks

## Goal
Implement Debug Mode, create the base progression/research skeleton, implement per-player hive join/unjoin, and prototype node-claiming/resource flow. These unblock iterative testing and give a playable prototype.

## Acceptance Criteria (Sprint)
- Debug Mode toggles on/off and displays entity/hive info
- Player can unlock a single research node and get a persistent hive modifier
- Guest player can join another player's hive and later leave with state consistent
- Node claiming: a player can claim a node and receive a resource increment
- Automated tests covering basic serialization for Hive and DroneRole saved state

## Tasks

### A. Debug Mode (Primary — 3 days)
- Implement `DebugMode` class and keybind/command registration
- HUD overlay: shows hovered entity id, role, health, owner, hive id
- Console subcommand `hive debug` to toggle and dump selected entity data (e.g., `/hive debug toggle`, `/hive debug dump`)
- Visual link rendering for hive nodes (basic lines between nodes)
- Tests: integration test that toggles Debug Mode and verifies HUD state

Owner: Developer A

### B. Hive Manager & Per-player Hive (Primary — 3 days)
- Implement `HiveManager` server-side singleton
- Create `Hive` data structure: owner UUID, members, resources, research tree state
- RPC/command to `joinHive(hostPlayerUUID)` and `leaveHive()`
- Persist hive data to world save (NBT)
- Tests: unit test for create/join/leave and persistence

Owner: Developer B

### C. Progression Skeleton / Research Tree (Primary — 3 days)
- Design minimal research tree data model (nodes, cost, effects)
- Implement server-side unlock flow and persist to Hive
- Implement a simple UI/command to unlock a node (for test)
- Add one example node: +5% drone damage for Soldiers
- Tests: unit test for unlocking and applying modifier

Owner: Developer C

### D. Node Claiming & Resource Flow Prototype (Secondary — 3 days)
- Implement `Node` entity or tile placeholder with ownership state
- Implement claim mechanic: player interacts → starts claim → after short time ownership flips
- Provide simple resource value per node; increment hive resource on claim
- Tests: integration test verifying claim flow and resource update

Owner: Developer A

### E. Documentation & Debugging tools (Ongoing)
- Update `documentation/DESIGN_DECISIONS.md` (done)
- Add Debug Mode usage to `documentation/QUICK_START.md`
- Add server config options to `gradle.properties` or a config template

Owner: Developer Docs

## Deliverables
- Source files: `HiveManager`, `Hive` serialization, `DebugMode`, `DroneRole` interface (skeleton), `Node` prototype
- Tests: unit + integration tests described above
- Docs: Sprint notes in `documentation/roadmap/phase2-sprint1.md` and usage in `documentation/QUICK_START.md`

## Risks & Mitigation
- Pathfinding & AI cost: mitigate by keeping simulation frequency low for prototype and using LOD
- Data migration: version NBT tag; start with simple format and plan migration hooks

## Next steps after Sprint 1
- Sprint 2: Drone roles baseline, combat tuning, integrate progression bonuses into drone behavior
- Sprint 3: Performance profiling, load-testing and optimization
