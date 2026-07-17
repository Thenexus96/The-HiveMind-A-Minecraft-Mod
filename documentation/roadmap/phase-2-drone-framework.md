## Phase 2 — Core HiveMind Mod Features (Active Phase)

This is the currently active phase where the gameplay systems, core logic, and mod mechanics are being built. The team selected a hybrid approach to development: implement an in-game Debug Mode quickly to accelerate iteration while finalizing design decisions for roles, combat, progression, and hive ownership.

✔ Accomplished

- Completed the Game Progression System Draft (multiple models evaluated)
- Designed system categories (biological, technological, hybrid)
- Created Debug Mode feature concept and added it to the roadmap
- Updated roadmap to record technical decisions (July 2026)

🔧 Remaining — Prioritized

Priority A — Developer tooling & unblockers
1. Implement Debug Mode system in code (blocking)
   - Toggle system (configurable keybind + command)
   - Debug overlay (on-screen HUD) and console output for devs
   - Entity scan (inspect drone internals), block scan, and HiveMind node visibility
   - Short acceptance criteria: toggle on/off, display selected entity data, display network node links

Priority B — Progression & Research
2. Finalize progression structure
   - Decide core progression model: hybrid XP + Research/Skill-tree for Hive-level bonuses
   - Implement base progression engine (node-unlock + research tree skeleton)
   - Acceptance: player can unlock one hive research node and receive a persistent modifier

Priority C — First gameplay mechanics (playable features)
3. Infection / node-claiming prototype
   - Basic mechanics for claiming nodes (ownership transfer, infection ticks)
   - Simple resource flow placeholder (prototype values) to verify gameplay
   - Acceptance: claim a node, it changes ownership and provides a resource increment to the hive

Priority D — Hive ownership & multiplayer model
4. Implement per-player hives with join mechanics
   - Default: each player has their own HiveMind instance
   - Implement join: guest hive goes dormant but its resources are accessible while joined
   - Acceptance: a player can join another hive and later unjoin restoring previous state

Priority E — Role & Combat baseline
5. Implement class-based drone roles + player research tree
   - Create role classes (Scout, Worker, Soldier) with baseline stats and capability hooks
   - Roles have built-in combat capability; Soldiers tuned for combat
   - Acceptance: spawn drones with a role and verify role-specific stats/skills

Priority F — Tests, profiling, and release preparation
6. Performance profiling, SpotBugs run, and load test (50+ drones)
   - Start with safe default (50 drones). Add config for server admins to increase limit (including near-infinite option with warnings)
   - Acceptance: core systems stable under default load; documented server/client trade-offs

Implementation notes
- Approach: work in small iterations; each priority has clear acceptance criteria and tests
- Use Debug Mode to visualize and validate progression, hive ownership, and drone behaviors
- Keep third-party mod integration deferred until Phase 3+ to avoid scope creep

Prototype & Sprint Plan
- Sprint 1 (2 weeks): Implement Debug Mode, basic progression skeleton, per-player hive join/unjoin, simple node-claim prototype
- Sprint 2 (2 weeks): Drone roles baseline, simple combat tuning, integration with progression bonuses
- Sprint 3 (2 weeks): Performance profiling, load tests, refine progression and research tree

Files to update during work
- `src/main/java/...` — new DebugMode classes, Drone role classes, Hive ownership manager
- `documentation/DESIGN_DECISIONS.md` — capture decisions, server/client trade-offs, API notes
- `documentation/roadmap/phase2-sprint1.md` — sprint tasks and owners

First internal prototype test
- Run a dedicated test world with Debug Mode enabled
- Verify node claiming and resource flow prototype
