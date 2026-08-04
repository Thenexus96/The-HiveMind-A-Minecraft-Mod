# HIV-91.1: Audit Three Unreconciled Ownership Systems

**Audit Date:** 2026-08-04  
**Auditor:** Copilot  
**Status:** Complete

---

## Executive Summary

Three distinct ownership systems coexist in the HiveMind codebase with different purposes, persistence strategies, and call patterns:

1. **Hive/HiveManager** — Guild-like collective ownership (world NBT persistence, defined but underutilized)
2. **HiveMindLinkManager** — Drone-to-owner mapping (world NBT persistence, actively used for gameplay)
3. **PlayerHiveComponent** — Per-player access control (file persistence, wired into access item and player lifecycle)

**Architectural Gap:** These systems operate independently without clear hierarchy or synchronization. No single "canonical" authority exists for determining player drone ownership in gameplay decisions.

---

## SYSTEM 1: Hive & HiveManager (Guild/Collective Model)

### Data Model
```java
Hive {
  UUID id              // Unique hive identifier
  UUID ownerUuid       // Player who owns the hive (immutable)
  Set<UUID> members    // All members including owner
  Map<String, Integer> resources  // Shared resource pool
  NbtCompound researchState      // Collective research tree unlock state
}
```

### Persistence
- World NBT via `HiveManager` (extends `PersistentState`)
- Persists across server restarts
- Single hive per player (owner)

### Access Pattern
**Direct getInstance() calls (NOT routed through HiveMindServiceManager):**

| File | Line | Method | Purpose |
|------|------|--------|---------|
| `DebugCommands.java` | ~1001 | `HiveManager.getInstance(server)` | `/hive join` |
| `DebugCommands.java` | ~1032 | `HiveManager.getInstance(server)` | `/hive leave` |
| `ResearchCommands.java` | ~33 | `HiveManager.getInstance(server)` | `/hive research unlock` |

### Public API Methods
- `getOrCreateHive(UUID ownerUuid)` — Get player's hive or create new one
- `getHiveByOwner(UUID ownerUuid)` — Retrieve hive owned by a player
- `addMemberToHive(UUID hiveId, UUID memberUuid)` — Add member to collective
- `removeMemberFromHive(UUID hiveId, UUID memberUuid)` — Remove non-owner member
- `addResourcesToHive(UUID hiveId, ...)` — Increment hive resources
- `unlockResearchForHive(UUID hiveId, String nodeId)` — Persist research unlock

### Current Usage in Gameplay
- ✓ `/hive join` - Create/retrieve player's personal hive
- ✓ `/hive leave` - Remove player from other collective
- ✓ `/hive research unlock` - Track collective research progress
- ✗ **NOT connected to drone claiming** (no resource flow from node claims)
- ✗ **NOT used for drone ownership checks** (HiveMindLinkManager used instead)

### Gap Documentation
**Intended Purpose:** Implement collective decision-making and shared resources for player-aligned drones.

**Actual Usage:** Command-level guild mechanics only. Not integrated with core drone-owner relationships.

**Architectural Debt:** The Hive model exists and persists but is architecturally orphaned from the main drone ownership flow.

---

## SYSTEM 2: HiveMindLinkManager (Drone Ownership Authority)

### Data Model
```java
HiveMindLinkManager {
  Map<UUID, UUID> droneOwnerMap    // Drone UUID → Owner UUID
  Map<UUID, List<UUID>> ownerDroneMap  // Owner UUID → [Drone UUIDs]
}
```

### Persistence
- World NBT via `PersistentState`
- Persists across server restarts
- Bidirectional lookup (drone→owner and owner→drones)

### Access Pattern
**Service Manager Indirection (ROUTED through HiveMindServiceManager):**

```java
HiveMindServiceManager.get().getLinkManager(server)
  → HiveMindLinkManager.getInstance(server)
```

**Direct call sites (via HiveMindServiceManager):**

| File | Line | Method | Purpose | Access Route |
|------|------|--------|---------|--------------|
| `DroneEntity.java` | 331–334 | `unlinkDrone()` | Drone death cleanup | HiveMindServiceManager.getLinkManager() |
| `DroneLinkUtils.java` | 32–34 | `linkDroneToOwner()` | Link drone to player | HiveMindServiceManager.getLinkManager() |
| `DroneLinkUtils.java` | 84–88 | `getDroneOwner()` | Retrieve drone owner | HiveMindServiceManager.getLinkManager() |
| `DroneLinkUtilsTest.java` | Mock | All methods | Unit test via mock | HiveMindServiceManager.setProvider() |

### Public API Methods
- `linkDroneToOwner(UUID droneUUID, UUID ownerUUID)` — Create ownership link
- `unlinkDrone(UUID droneUUID)` — Remove ownership link
- `getDroneOwner(UUID droneUUID)` — Retrieve drone's owner
- `getOwnerDrones(UUID ownerUUID)` — Get all owned drones
- `isDroneLinked(UUID droneUUID)` — Check if linked

### Current Usage in Gameplay
- ✓ Drone spawning/claiming — drones linked to player via `DroneLinkUtils`
- ✓ Drone death — drones unlinked via `DroneEntity.removeHiveMindConnection()`
- ✓ Drone owner verification — `DroneEntity` checks owner for behavior decisions
- ✓ **PRIMARY AUTHORITY for drone-owner relationships in active gameplay**

### Architecture Note
This is the **de facto canonical system** for drone ownership despite Hive/HiveManager existing. All drone behavior decisions route through this system.

---

## SYSTEM 3: PlayerHiveComponent (Access Control & File Persistence)

### Data Model (Per-Player File Persistence)
```java
PlayerHiveData {
  boolean hasAccess        // Whether player has HiveMind access granted
  UUID hiveId              // Usually player's own UUID
  long joinTime            // Timestamp when access was first granted
}
```

### Persistence
- **Individual player NBT files** (under world player data)
- File I/O on player login/logout
- In-memory cache layer during session

### Access Pattern
**All static methods (no getInstance needed):**

| File | Line | Method | Purpose |
|------|------|--------|---------|
| `HiveMindAccessItem.java` | ~28 | `hasAccess()` | Check if player already has access |
| `HiveMindAccessItem.java` | ~41 | `initializeNewMember()` | Grant access when item used |
| `HiveMindServerEvents.java` | Lifecycle | `onPlayerJoin()` | Load player data from file |
| `HiveMindServerEvents.java` | Lifecycle | `onPlayerLeave()` | Save player data to file |

### Public API Methods
- `getData(PlayerEntity player)` — Get cached player hive data
- `hasAccess(PlayerEntity player)` — Check if player has access
- `setAccess(PlayerEntity player, boolean access)` — Grant/revoke and persist
- `getHiveId(PlayerEntity player)` — Get player's linked hive ID
- `initializeNewMember(PlayerEntity player)` — Wrapper for setAccess(true)
- `getPlayerStatus(PlayerEntity player)` — Formatted string for debug/UI

### Current Usage in Gameplay
- ✓ Access gating — HiveMindAccessItem checks before allowing interaction
- ✓ Player lifecycle — Load/save data on join/leave
- ✓ Access granting — Item gives initial access, component persists

### Architecture Note
This is the **actual gating mechanism** for player access but does NOT track drone relationships directly.

---

## Gameplay Feature Call Matrix

| Feature | Hive/HiveManager | LinkManager | PlayerComponent | Notes |
|---------|------------------|-------------|-----------------|-------|
| Player joins HiveMind | Create entry | — | ✓ File persist | Access item → `initializeNewMember()` |
| Player creates collective hive | ✓ Create new | — | — | `/hive join` |
| Player joins existing hive | ✓ Add member | — | — | Collective mechanics only |
| Player research unlock | ✓ Persist state | — | — | `/hive research unlock` |
| Drone spawns/claimed | — | ✓ Link drone | — | Core gameplay, via `DroneLinkUtils` |
| Drone dies | — | ✓ Unlink | — | Cleanup via `DroneEntity.removeHiveMindConnection()` |
| Drone behaves (checks owner) | — | ✓ Query owner | — | `DroneEntity` ownership verification |
| Player logout | — | — | ✓ File persist | Lifecycle save |
| Player login | — | — | ✓ File load | Lifecycle load |

---

## Architectural Findings

### Canonical Authority for Drone Ownership
**HiveMindLinkManager** is the de facto authority:
- Actively queried by `DroneEntity` for ownership checks
- Persists across restarts (world NBT)
- Routed through `HiveMindServiceManager` for testability
- Integrated with `DroneLinkUtils` service layer

### Architectural Debt: Unreconciled Collective Model
**Hive/HiveManager** exists but is architecturally orphaned:
- ✗ No integration with core drone-owner link
- ✗ No resource flow when nodes are claimed
- ✗ No player behavior consequences for research unlocks
- ✓ Commands exist and work but isolated from main gameplay

### Access Control System Bifurcation
**PlayerHiveComponent** gates initial access but does NOT track ownership:
- Independent file-based persistence (not world NBT)
- Used only for access item mechanics
- Does not affect drone claiming or research
- Could be unified with HiveMindLinkManager but currently separate

### Synchronization Issues (Not Currently Observed)
- HiveManager and LinkManager both persist to world NBT, independently
- If a player's Hive is deleted but their drone links remain, systems diverge
- No periodic reconciliation or validation

---

## Recommendations (Future Refactoring)

1. **Declare LinkManager canonical** for drone-owner relationships in documentation
2. **Migrate Hive collective mechanics** to use LinkManager as base (if keeping)
3. **Consider consolidating** PlayerHiveComponent access state into LinkManager
4. **Add reconciliation task** to detect and handle orphaned state (e.g., drone with no owner, owner with no drone link)
5. **Document in DESIGN_DECISIONS.md** which system owns each gameplay decision

---

## Deliverables Checklist

- [x] Map all call sites accessing player-to-drone ownership
- [x] Document which ownership system each feature uses (table provided)
- [x] Identify canonical system (HiveMindLinkManager)
- [x] Document gap: Hive defined but not wired to core gameplay
- [x] Document gap: PlayerHiveComponent wired to access but not ownership
- [x] Provide line-by-line call site mapping
- [x] Identify synchronization risks and architectural debt

---

**End of Audit Report**
