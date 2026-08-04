# HiveMind AI Architecture

**Status:** Implemented foundation; expansion planned  
**Scope:** Current drone behaviors and control boundaries

## Current model

HiveMind uses a lightweight, role-based behavior model. `DroneEntity` owns local entity state and execution. A `DroneRole` selects a `DroneRoleBehavior` through `RoleRegistry`; the current roles are Idle, Scout, and Soldier. `FollowHiveMindPlayerGoal` provides a concrete Minecraft goal for following the linked player.

```text
Hive/player intent
        ↓
DroneEntity (local state and lifecycle)
        ↓
DroneRole → RoleRegistry → DroneRoleBehavior
        ↓
Minecraft goals, navigation, and movement
```

## Ownership boundary

- The Hive supplies collective membership, resources, and research state.
- A drone owns its current role, local behavior, navigation, and direct-control state.
- Direct player control pauses or replaces normal AI execution for the controlled drone.

This preserves the project principle that the Hive sets broad intent while drones perform local execution. See [ENTITY_ARCHITECTURE.md](ENTITY_ARCHITECTURE.md).

## Current limits

The repository does not yet implement a general task-assignment engine, shared memory system, strategic HiveCore AI, or a complete behavior-planning framework. Those are planned architecture areas and must not be represented as implemented behavior.

## Primary code

- `src/main/java/net/sanfonic/hivemind/entity/DroneEntity.java`
- `src/main/java/net/sanfonic/hivemind/entity/custom/role/`
- `src/main/java/net/sanfonic/hivemind/entity/custom/goal/FollowHiveMindPlayerGoal.java`
- `src/main/java/net/sanfonic/hivemind/control/`

