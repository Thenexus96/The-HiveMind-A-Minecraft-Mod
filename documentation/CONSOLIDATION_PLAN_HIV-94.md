# HIV-94: Consolidation Plan - Hive as Unified Ownership Authority

**Date:** 2026-08-04  
**Status:** Planning  
**Approach:** Consolidate Hive into unified system

---

## Current State (from HIV-91 Audit)

Three separate systems exist:

1. **HiveMindLinkManager** (drone UUID → owner UUID mapping)
   - De facto active in gameplay
   - Directly queries drone ownership
   - World NBT persistence

2. **HiveManager** (collective model with members, resources, research)
   - Defined but isolated from core drone gameplay
   - Used only by /hive commands
   - No connection to drones

3. **PlayerHiveComponent** (per-player file persistence)
   - Access gating via HiveMindAccessItem
   - Independent lifecycle management

---

## Target State (After HIV-94)

**Single Unified Authority: Enhanced Hive Model**

The Hive class will become the single source of truth for:
- Player membership in collective
- Owned drones (replaced LinkManager drone list)
- Shared resources
- Research state
- Player access status (integrated from PlayerHiveComponent)

---

## Integration Plan

### Phase 1: Extend Hive Data Model

**Additions to Hive class:**
- `Map<UUID, List<UUID>> dronesByMember` — Track drones per member (not just owner)
- `boolean active` — Access flag (merged from PlayerHiveComponent.hasAccess)
- `long activatedAt` — Timestamp (merged from PlayerHiveComponent.joinTime)

```java
public class Hive {
  // Existing
  UUID id, ownerUuid, members, resources, researchState
  
  // New: Drone ownership (from LinkManager)
  Map<UUID, List<UUID>> dronesByMember  // member UUID → owned drone UUIDs
  
  // New: Access control (from PlayerHiveComponent)
  boolean active
  long activatedAt
}
```

### Phase 2: Refactor HiveManager

**HiveManager responsibilities expand:**
- Manage all Hive instances (already does)
- Provide drone query methods (new, wraps dronesByMember)
- Provide access control methods (new, wraps active flag)

**New HiveManager methods:**
```java
// Drone management (replaces HiveMindLinkManager)
void linkDroneToMember(UUID hiveId, UUID memberUuid, UUID droneUuid)
void unlinkDroneFromMember(UUID hiveId, UUID droneUuid)
UUID getDroneOwner(UUID droneUuid)  // Searches all hives
List<UUID> getMemberDrones(UUID hiveId, UUID memberUuid)
List<UUID> getAllHiveDrones(UUID hiveId)

// Access control (replaces PlayerHiveComponent.hasAccess)
void activateMember(UUID hiveId, UUID memberUuid)
void deactivateMember(UUID hiveId, UUID memberUuid)
boolean isMemberActive(UUID hiveId, UUID memberUuid)
```

### Phase 3: Migration of Call Sites

**DroneLinkUtils → HiveManager:**
- `linkDroneToOwner()` calls `HiveManager.linkDroneToMember(hiveId, ownerUuid, droneUuid)`
- `getDroneOwner()` calls `HiveManager.getDroneOwner(droneUuid)`

**HiveMindAccessItem → HiveManager:**
- `hasAccess()` calls `HiveManager.isMemberActive(hiveId, playerUuid)`
- `initializeNewMember()` calls `HiveManager.activateMember(hiveId, playerUuid)`

**DroneEntity → HiveManager:**
- `restoreHiveMindConnection()` uses `HiveManager.getDroneOwner()`
- `removeHiveMindConnection()` uses `HiveManager.unlinkDroneFromMember()`

### Phase 4: Retire Orphaned Systems

**After all call sites migrated:**
- Delete `HiveMindLinkManager.java` (functionality merged into Hive/HiveManager)
- Delete `PlayerHiveComponent.java` (access control merged into Hive/HiveManager)
- Update service layer (DroneLinkUtils, service manager if needed)

---

## Implementation Steps (Detailed)

### Step 1: Update Hive Data Model (non-breaking)
- Add drone tracking fields to Hive
- Add access control fields to Hive
- Implement new getter/setter methods
- Update toNbt() / fromNbt() for new fields

### Step 2: Extend HiveManager Public API
- Add drone management methods
- Add access control methods
- Implement search across all hives (e.g., getDroneOwner searches all)

### Step 3: Update HiveManager NBT Persistence
- Persist drone mappings (replaces LinkManager persistence)
- Persist access control flags (replaces PlayerHiveComponent file I/O)

### Step 4: Create Adapter/Bridge (HiveMindLinkManager)
- Keep HiveMindLinkManager class but delegate all calls to HiveManager
- Maintains backward compatibility during transition

### Step 5: Update Call Sites Incrementally
- DroneLinkUtils → HiveManager
- HiveMindAccessItem → HiveManager
- DroneEntity → HiveManager
- ResearchCommands (already using HiveManager, no change needed)
- DebugCommands (already using HiveManager, no change needed)

### Step 6: Update Service Layer
- HiveMindServiceManager may need to expose HiveManager instead of LinkManager
- Or keep LinkManager as facade

### Step 7: Remove PlayerHiveComponent
- Lifecycle events (ServerEvents) redirect to HiveManager
- File I/O replaced by world NBT persistence

### Step 8: Tests
- Update HiveTest to cover drone tracking
- Update HiveTest to cover access control
- Update DroneLinkUtilsTest to mock HiveManager instead of LinkManager
- Add integration tests for unified model

### Step 9: Documentation
- Update DESIGN_DECISIONS.md explaining unified model
- Create ADR-XXXX documenting consolidation decision
- Update QUICK_START.md if ownership behavior changed

---

## Risk Assessment

| Risk | Mitigation |
|------|-----------|
| Breaking drone ownership during migration | Use adapter bridge; test continuously |
| PlayerHiveComponent file I/O loss | Ensure NBT migration handles all cases |
| Performance regression (searching all hives) | Consider indexing by drone UUID if needed |
| Incomplete migration (old systems still called) | Complete grep sweep before deletion |

---

## Acceptance Criteria Check

- [ ] One ownership model governs all gameplay (Hive/HiveManager)
- [ ] All player-to-drone lookups use same system
- [ ] Call sites traceable (DroneLinkUtils → HiveManager → Hive)
- [ ] HiveMindLinkManager retired (or adapter bridge if needed for compatibility)
- [ ] PlayerHiveComponent retired
- [ ] Integration tests pass (drone linking, access control, research)
- [ ] Ownership queries consistent (no divergence between systems)
- [ ] ADR-XXXX documents decision and rationale

---

## Estimated Effort

- Phase 1-2 (Data model + HiveManager API): 2-3 hours
- Phase 3 (Call site migration): 2-3 hours  
- Phase 4-5 (Testing + docs): 1-2 hours
- **Total: 5-8 hours of focused development**

---

## Next Steps

1. Get approval on this plan
2. Create feature branch: `hiv-94/consolidate-ownership`
3. Implement Phase 1 (data model)
4. Implement Phase 2 (HiveManager API)
5. Add tests (Phase 8)
6. Migrate call sites (Phase 5)
7. Retire old systems (Phase 7)
8. Final integration testing
9. Document ADR (Phase 9)

