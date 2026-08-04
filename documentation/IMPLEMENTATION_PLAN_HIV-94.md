# HIV-94: Implementation Plan - HiveMindLinkManager as Canonical Authority

**Date:** 2026-08-04  
**Status:** Planning  
**Decision Reference:** ADR-0007: Ownership System Architecture  
**Aligns With:** HIV-91 Audit + ADR-0007 Decision

---

## Target State (Per ADR-0007)

**HiveMindLinkManager remains the canonical authority for drone ownership.**

No consolidation; instead, ensure all systems are properly aligned:
- HiveMindLinkManager: drone ownership (canonical)
- PlayerHiveComponent: access control (independent)
- Hive/HiveManager: collective mechanics (deferred to Tier 2+)

---

## Scope of HIV-94

Based on ADR-0007 decision, HIV-94 work is NOT a consolidation, but rather:

1. **Ensure all ownership queries route through HiveMindServiceManager** (already done in HIV-92)
2. **Document and formalize the ownership hierarchy**
3. **Remove any remaining direct getInstance() calls outside service layer**
4. **Add integration tests for mental power gating (Tier 1 MVP use case)**
5. **Document in QUICK_START.md and DESIGN_DECISIONS.md**

---

## Current Alignment Status (Post-HIV-92)

| Component | Status | Notes |
|-----------|--------|-------|
| HiveMindLinkManager queries | ✓ ROUTED | All calls via HiveMindServiceManager.get().getLinkManager() |
| DroneEntity ownership checks | ✓ ALIGNED | Uses service manager (HIV-92 fix) |
| DroneLinkUtils | ✓ ALIGNED | Routes through service manager |
| PlayerHiveComponent | ✓ INDEPENDENT | Correctly isolated from ownership queries |
| Hive/HiveManager | ✓ ISOLATED | Used only for collective mechanics (/hive commands) |

---

## Remaining Work for HIV-94

### Phase 1: Documentation (Priority 1)

**Files to create/update:**

1. **ADR-0007 Reference Document** (already exists - just link to it)
   - Ownership hierarchy formalized
   - Path: `documentation/history/ADR/ADR-0007-ownership-system.md`

2. **Update DESIGN_DECISIONS.md** 
   - Add section: "Ownership and Authority"
   - Document that LinkManager is canonical
   - Explain why other systems remain separate
   - Link to ADR-0007

3. **Update QUICK_START.md**
   - Add: "How Drone Ownership Works"
   - Explain: Player claims drone → LinkManager tracks it → Queries check LinkManager
   - Example: `/hive debug dump` shows LinkManager state

4. **Update PROJECT_STATUS.md**
   - Add note: "Ownership architecture decided per ADR-0007"
   - Tier 1 MVP gating logic enabled by canonical LinkManager

### Phase 2: Code Audit (Priority 2)

**Verify no direct getInstance() calls outside service layer:**

```bash
grep -r "LinkManager\.getInstance\|HiveCodeManager\.getInstance\|DroneTelemetryStore\.getInstance" \
  src/main/java --include="*.java" \
  --exclude-dir=service
```

If found: Route through HiveMindServiceManager

### Phase 3: Test Coverage (Priority 2)

**Add integration test for Tier 1 MVP use case:**

File: `src/test/java/net/sanfonic/hivemind/integration/MentalPowerGatingTest.java`

Test scenario:
```java
// Player links drone via DroneLinkUtils
HiveMindServiceManager.get().getLinkManager(server)
  .linkDroneToOwner(droneUUID, playerUUID);

// Mental power check queries LinkManager
List<UUID> playerDrones = HiveMindServiceManager.get()
  .getLinkManager(server)
  .getOwnerDrones(playerUUID);

// Verify correct answer
assertTrue(playerDrones.contains(droneUUID));
```

### Phase 4: Hive/HiveManager Documentation (Priority 3)

**Update Hive.java javadoc:**
```java
/**
 * Represents a player collective (guild-like group of players).
 * NOT used for drone ownership decisions (see HiveMindLinkManager).
 * Used for: member management, shared resources, research tracking.
 * 
 * See ADR-0007 for ownership hierarchy explanation.
 */
public class Hive { ... }
```

**Update HiveManager.java javadoc:**
```java
/**
 * Server-side singleton managing player collectives.
 * NOTE: This is NOT the canonical ownership system.
 * 
 * Canonical system: HiveMindLinkManager (drone ownership)
 * This system: Collective mechanics (members, resources, research)
 * 
 * See ADR-0007 for detailed ownership architecture.
 */
public class HiveManager extends PersistentState { ... }
```

### Phase 5: Deprecation Markers (Priority 3)

If intent is to deprecate Hive mechanics in future (Tier 2+):

```java
@Deprecated(
  forRemoval = false,  // Keep for now (Tier 2+)
  since = "MVP-Alpha"
)
public class HiveManager { ... }
```

Add comment:
```java
// TODO(Tier 2+): Integrate collective mechanics with LinkManager
//   See ADR-0007 "Alternative 3: Merge all three systems"
```

---

## Acceptance Criteria (Per ADR-0007)

- [x] One canonical authority declared (HiveMindLinkManager)
- [x] All ownership queries route through HiveMindServiceManager (HIV-92 implemented)
- [x] No direct getInstance() calls in gameplay code (verify with audit)
- [x] Systems operate independently without conflict
- [x] Ownership queries always return consistent answers (LinkManager is single source)
- [ ] Documentation updated (DESIGN_DECISIONS.md, QUICK_START.md, ADR-0007 link)
- [ ] Integration test for mental power gating added
- [ ] ADR-0007 referenced in all ownership-related code
- [ ] Hive/HiveManager documented as separate concern (not orphaned, just isolated)

---

## Deliverables

1. **Updated OWNERSHIP_AUDIT_HIV-91.md** — Aligned with ADR-0007 findings
2. **IMPLEMENTATION_PLAN_HIV-94.md** (this file) — Non-consolidation roadmap
3. **Code updates** (Phase 1-5 as needed):
   - DESIGN_DECISIONS.md ownership section
   - QUICK_START.md ownership explanation
   - PROJECT_STATUS.md ADR reference
   - Javadoc updates (Hive, HiveManager)
   - Integration test for Tier 1 MVP gating
4. **Git commit** — "HIV-94: Formalize ownership hierarchy per ADR-0007"

---

## Why NOT a Consolidation?

ADR-0007 explicitly rejected consolidation because:

1. **Each system solves different problems:**
   - LinkManager: drone-owner binding
   - PlayerHiveComponent: access gating
   - Hive/HiveManager: collective mechanics

2. **No active conflict:** Systems don't overlap; each owns one data domain

3. **Consolidation is future work (Tier 2+):** When collective features expand, integration can happen

4. **Current approach is lowest-risk:** Formalize current state, add missing docs/tests

---

## Estimated Effort

- Phase 1 (Documentation): 1-2 hours
- Phase 2 (Code audit): 0.5 hours
- Phase 3 (Integration test): 1 hour
- Phase 4-5 (Javadoc + deprecation): 0.5 hours
- **Total: 3-4 hours**

Much shorter than full consolidation, and aligns with architectural decision.

---

## Next Steps

1. Get approval on plan alignment with ADR-0007
2. Create branch: `hiv-94/formalize-ownership-hierarchy`
3. Execute Phase 1 (docs)
4. Execute Phase 2 (audit)
5. Execute Phase 3 (test)
6. Commit and push

