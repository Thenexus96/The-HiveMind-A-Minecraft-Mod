# ADR-0007: Ownership System Architecture - Three-System Reconciliation

**Date:** 2026-08-04  
**Status:** Accepted  
**Decision Owner:** Nick Sanford (with Copilot analysis)  
**Affects:** Tier 1 MVP (Mental Power + Task Loops), Future Tier 2-5

---

## Problem Statement

Three distinct ownership systems coexist in the HiveMind codebase, each with different persistence strategies, access patterns, and integration levels:

1. **Hive/HiveManager** — Guild-like collective ownership (world NBT, command-level only)
2. **HiveMindLinkManager** — Drone-to-owner mapping (world NBT, actively used in gameplay)
3. **PlayerHiveComponent** — Per-player access control (file persistence, access item gating)

**Architectural Risk:** No single canonical authority for ownership decisions. Uncertainty about which system to query for "who owns this drone?" and "what drones does this player own?"

**Blocking Issue:** Tier 1 MVP requires clear mental power gating logic that must reliably check "does player X own drone Y?" — cannot implement with ambiguous ownership.

---

## Decision

**HiveMindLinkManager is the canonical authority for drone-owner relationships.**

All gameplay decisions about drone ownership must query HiveMindLinkManager, routed through HiveMindServiceManager for testability.

---

## Rationale

**Why HiveMindLinkManager?**

1. **Actively Used in Gameplay**
    - `DroneEntity` queries it for ownership verification
    - `DroneLinkUtils` uses it to establish ownership on spawn
    - `DroneEntity.removeHiveMindConnection()` uses it for cleanup
    - The system is already the de facto authority

2. **Service-Managed Abstraction**
    - Routed through `HiveMindServiceManager.get().getLinkManager(server)`
    - Makes testing possible (mock service provider)
    - Enables future implementation swaps (e.g., database backend)

3. **Persistent Storage**
    - World NBT via `PersistentState`
    - Survives server restarts
    - Bidirectional lookup: drone→owner and owner→drones

4. **Architectural Clarity**
    - It's already being used this way in practice
    - Formalizing it reduces confusion
    - Clear separation of concerns

---

## Alternative Approaches Considered

### 1. Use Hive/HiveManager as canonical
**Rejected:** Hive/HiveManager is defined but orphaned from core gameplay. It's used only for `/hive join/leave` commands and research tracking. Making it canonical would require:
- Rewiring all DroneEntity ownership checks
- Adding resource flow from drone claims to hive
- Merging duplicate data (now maintaining two ownership sources)
- High risk of divergence

### 2. Use PlayerHiveComponent as canonical
**Rejected:** PlayerHiveComponent gates initial access (HiveMindAccessItem) but doesn't track drone relationships. It's file-based per-player persistence, not world state. Making it canonical would require:
- Changing persistence model (world NBT vs. file)
- Adding bidirectional lookups for owner→drones
- Duplicating data across systems

### 3. Merge all three into one unified system
**Deferred:** Future work (Tier 2+). For now, separation is acceptable because each serves distinct concerns:
- HiveMindLinkManager: drone ownership
- PlayerHiveComponent: access control / gating
- Hive/HiveManager: collective mechanics / research

---

## Architecture After This Decision
Player joins HiveMind
→ PlayerHiveComponent.initializeNewMember() [file persist]
→ Player can interact with HiveMindAccessItem

Player claims drone
→ DroneLinkUtils.linkDroneToOwner() [calls LinkManager via ServiceManager]
→ HiveMindLinkManager tracks: player UUID → drone UUID

Player mental power gating check (Tier 1 MVP)
→ HiveCore.getMentalPower(playerUUID) [checks HiveCore data]
→ HiveMindServiceManager.getLinkManager().getOwnerDrones(playerUUID) [checks LinkManager]
→ Gate drone behavior if power < required

Drone death
→ DroneEntity.removeHiveMindConnection()
→ HiveMindServiceManager.getLinkManager().unlinkDrone() [cleanup via LinkManager]

**Separation of Concerns:**
- **HiveMindLinkManager:** "Which drones does this player own?"
- **PlayerHiveComponent:** "Does this player have HiveMind access?"
- **Hive/HiveManager:** "What are the collective's shared resources and research?"

---

## Implementation Plan

### Tier 1 Blocking (Now)
1. **Document** decision in DESIGN_DECISIONS.md
2. **Verify** all ownership checks already use LinkManager (they do)
3. **Add ADR** to codebase (this file)

### Tier 1 MVP (Immediate)
- Mental power gating uses LinkManager to verify ownership
- No code changes needed; LinkManager already in place

### Tier 2+ (Future)
- **Option A:** Migrate Hive/HiveManager into LinkManager (if collective features expand)
- **Option B:** Migrate PlayerHiveComponent access state into LinkManager (consolidation)
- **Option C:** Add reconciliation task to detect orphaned state

---

## Consequences

### Positive
✅ **Clarity:** All developers know LinkManager is the authority  
✅ **Testability:** Service-managed access enables mocking  
✅ **Low Risk:** No code changes needed; formalizes existing pattern  
✅ **MVP Unblocked:** Tier 1 can build mental power gating confidently

### Negative / Accepted Debt
⚠️ **Hive/HiveManager Orphaned:** Defined but not wired to core gameplay (acceptable: kept for `/hive` commands)  
⚠️ **No Synchronization:** Three systems persist independently (acceptable: they track separate concerns, no conflicts observed)  
⚠️ **Future Consolidation Needed:** PlayerHiveComponent access + HiveMindLinkManager could be merged (deferred to Tier 2+)

---

## Open Questions / Future Decisions

1. **Should PlayerHiveComponent access state be consolidated into LinkManager?**
    - Currently separate (file persistence vs. world NBT)
    - Consolidation would create single "player → hive membership" record
    - Deferred: post-Tier-1 architecture decision

2. **Should Hive/HiveManager be integrated or retired?**
    - Currently unused for core gameplay
    - `/hive` commands still work and could expand
    - Deferred: depends on collective mechanics roadmap (Tier 3+)

3. **Should a reconciliation task detect orphaned state?**
    - E.g., "drone has no owner in LinkManager"
    - E.g., "player's drone listed but LinkManager disagrees"
    - Deferred: Tier 3 quality task if needed

---

## References

- **Audit:** `/mnt/user-data/uploads/OWNERSHIP_AUDIT_HIV-91.md` (Copilot analysis, 2026-08-04)
- **Linear Issues:** HIV-81, HIV-93, HIV-94 (ownership reconciliation epic)
- **Related Code:**
    - `HiveMindLinkManager.java` — canonical system
    - `HiveMindServiceManager.java` — abstraction layer
    - `DroneLinkUtils.java` — primary call site
    - `DroneEntity.java` — ownership verification

---

**Status:** ✅ **ACCEPTED** — Documented, formalized, blocking issues unblocked.