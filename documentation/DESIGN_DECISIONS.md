# Design Decisions — HiveMind (Recorded July 2, 2026)

This document records the high-level design decisions taken for Phase 2 and explains technical implications, API-level notes, and server/client performance trade-offs.

## 1. Drone Roles: Class-based + Hive Research (both)

Decision
- Drone behaviors and capabilities are implemented as concrete role classes or composable components (e.g., ScoutRole, SoldierRole, WorkerRole).
- Player/Hive progression is handled separately via a research/skill-tree system that grants hive-wide or player-wide bonuses, not by mixing per-drone skill-trees.

Rationale
- Class-based drone roles keep runtime logic simple and explicit (easier to serialize, test, and optimize).
- A separate research/skill-tree allows persistent progression, unlocks, and global modifiers without bloating drone runtime state.

Implementation notes
- DroneRole interface (or abstract class) with core hooks: onSpawn, onTick, onCommand, onDamage, onSerialize, onDeserialize.
- Roles store only minimal runtime state; heavy configuration lives in data-driven JSON or registry entries.
- Research tree represented as a server-side data structure persisted in Hive data (NBT) and replicated to clients when needed.

API Shape (Java pseudocode)
```java
public interface DroneRole {
    void onSpawn(DroneEntity drone);
    void onTick(DroneEntity drone, long tick);
    void onCommand(DroneEntity drone, CommandContext ctx);
    void onDamage(DroneEntity drone, DamageSource src, float amount);
    CompoundTag serialize();
    void deserialize(CompoundTag tag);
}
```

## 2. Combat: Role-weighted Hybrid

Decision
- All roles have base combat mechanics; combat-suited roles have higher base stats and special combat skills.

Rationale
- Keeps gameplay flexible while allowing specialization.
- Avoids rigid separation of combat vs utility and allows mixed-role squads.

Implementation notes
- Stats system: health, damage, accuracy, defense, aggression.
- Roles register passive & active skills (e.g., Soldier: explosive shot; Worker: weak defensive pulse).
- Balancing via config files and progression modifiers from research tree.

## 3. Multiplayer Hive Model: Per-player hives with joinable option

Decision
- Each player has a HiveMind instance by default.
- Players can join another player's HiveMind; the joining player's hive goes dormant but their resources remain accessible while joined.

Rationale
- Per-player hives preserve player agency and reduce contention.
- Join feature allows cooperative play and resource sharing.

Implementation notes
- Hive instance model: server-side object persisted in level data; references to owner UUID, member list, resources, research tree state.
- Joining mechanics: guest hive state marked dormant; guest's drones may be temporarily deactivated or reparented depending on choice (configurable behavior).
- Ensure save/load handles join/unjoin transitions safely.

API notes
- HiveManager: createHive(UUID owner), getHive(UUID owner), joinHive(UUID guest, UUID host), leaveHive(UUID guest), serialize/deserialize

## 4. Third-party Integrations

Decision
- Defer official mod integrations until Phase 3+.

Rationale
- Reduces scope during core gameplay development; avoids coupling early design to other mods' APIs.

## 5. Performance Targets & Server/Client Trade-offs

Decision
- Start with a conservative default (50 drones) and expose server-admin configuration options to increase limits. Offer a "near-infinite" option with warnings that it may cause severe performance degradation.

Rationale
- Ensures stable baseline for most servers while giving power users/admins control.

Server vs Client trade-offs

1. Entity Count & Authority
- Server is authoritative for AI, physics, and persistence. Increasing drone count increases server CPU and memory usage (AI ticks, pathfinding, entity updates).
- On the client side, rendering many drones increases GPU and CPU load. Clients with less powerful machines will struggle with high entity counts.

2. Network Traffic
- More entities = more network state to synchronize. Use delta updates and state compression for frequent small changes. Only replicate necessary fields to each client (visibility-based replication).

3. LOD & Visibility
- Implement Level-of-Detail (LOD) strategies for both simulation and rendering:
  - Server-side LOD: lower AI update frequency for distant/non-interacting drones.
  - Client-side LOD: reduce animation/detail or use simplified models for distant drones.

4. Culling & Interest Management
- Only synchronize entities within a player's interest radius. For joined hives, increase interest radius if necessary, but still apply culling for distant drones.

5. Configurable Limits & Settings
- Server config options:
  - `hivemind.max_drones_per_server` (default 50)
  - `hivemind.max_drones_per_player` (default 10)
  - `hivemind.lod.distance` (controls LOD thresholds)
  - `hivemind.experimental.infinite_entities = true/false` (admin-warning)

6. Profiling & Monitoring
- Provide debug metrics in Debug Mode (entity counts, tick times, avg pathfind cost) to tune settings.

Implementation recommendations
- Start with conservative defaults.
- Provide built-in LOD and interest management before enabling large counts.
- Document server/client trade-offs clearly in `documentation/CODE_QUALITY.md` and `documentation/GITEA_CI.md`.

## Migration & Backwards Compatibility
- Hive data format: include versioning in NBT so older/newer versions can detect and migrate.
- Role and research registries: data-driven (JSON) registrations so new roles can be added without hard migrations.

## Next Steps (Technical)
1. Add HiveManager and Hive data persistence classes.
2. Add DroneRole interface and implement three baseline roles (Scout, Worker, Soldier).
3. Implement Debug Mode hooks to visualize hive & drone state.
4. Add server config options for drone limits and LOD.
5. Create research tree data model and basic unlock logic (persisted to Hive).


---

Recorded by: implementation notes (July 2, 2026)

Refer to sprint plan: `documentation/roadmap/phase2-sprint1.md` for task breakdown and owners.
