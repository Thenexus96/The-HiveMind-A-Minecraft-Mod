# HiveMind — Project Status

**This is the single source of truth for "where things actually are."** If another
document in this repo disagrees with this one, this one wins — update the other
document or archive it.

**Last updated:** 2026-07-10
**Maintained by:** Nick, with Claude as ongoing dev/documentation partner.

---

## 1. What this project is

HiveMind is a Fabric mod (Java 17, Minecraft 1.20.1) where players link to a
HiveMind and command autonomous Drone entities. It's also Nick's structured
vehicle for learning software development — real OOP concepts (interfaces,
inheritance, generics, service-layer patterns) via a project he actually wants
to finish, not tutorial exercises.

**Success looks like:** a well-architected, maintainable mod, and genuine
programming skill growth along the way.

**Long-term idea, explicitly parked:** extracting a reusable "HiveCore" library
for a multi-mod ecosystem. This is a real possibility worth remembering, but it
is **not current work**. Nothing should be restructured into a monorepo or
multi-module layout to chase this until Nick raises it again. See
[`VISION.md`](VISION.md) for where this lives.

---

## 2. Current focus: Ownership architecture formalized (ADR-0007)

**Status Update (2026-08-04):** Ownership architecture has been formalized and decided.

See **ADR-0007: Ownership System Architecture** (`documentation/history/ADR/ADR-0007-ownership-system.md`) for the complete decision.

### Decision Summary

**HiveMindLinkManager is the canonical authority for drone-owner relationships.**

The mod has three ownership systems, each with distinct responsibility (no consolidation needed):

| System | Purpose | Status |
|---|---|---|
| `HiveMindLinkManager` | Drone → owner mapping (canonical) | ✓ Actively used in gameplay; routed through `HiveMindServiceManager` for testability |
| `PlayerHiveComponent` | Per-player access control (file persistence) | ✓ Correct; independent from ownership queries |
| `Hive` / `HiveManager` | Collective mechanics (guild model, research) | ✓ Intentionally isolated from drone ownership; for Tier 2+ feature expansion |

**Why this design?**
1. HiveMindLinkManager is already the de facto authority in practice
2. Each system solves a different problem (no overlap, no conflict)
3. Enables Tier 1 MVP mental power gating: "does player X own drone Y?"
4. Formalizing it clarifies all call sites and makes testing possible
5. Future consolidation (Tier 2+) is now possible without risk of divergence

### Concrete fixed issues

- ✓ **HIV-92:** `DroneEntity.restoreHiveMindConnection()` and `removeHiveMindConnection()` now route through `HiveMindServiceManager.get()` (testable seam)
- ✓ **HIV-89 & HIV-90:** `DroneLinkUtilsTest` refactored to use `@BeforeEach` fixtures (no duplicate variable declarations)

### Next work (HIV-94 Phase 2+)

- Code audit: verify no remaining direct `getInstance()` calls outside service layer
- Add integration tests for mental power gating (Tier 1 MVP use case)
- Document Hive/HiveManager javadoc to clarify they're not orphaned, just isolated

---

## 3. Other active architectural items

- **`DroneEntity` god-object (~600 lines).** Needs splitting. The old plan
  (from an imported ChatGPT "Phase 2 Drone Framework Taskboard") proposed
  `DroneEntity` / `DroneEntityData` / `DroneState` + a `DroneAIController` +
  Goal classes. **The `DroneAIController` piece is superseded** — the
  `DroneRoleBehavior` / `RoleRegistry` strategy pattern that's already in the
  codebase (`ScoutRole`, `SoldierRole`, `IdleRole`, `RoleRegistry`) is cleaner
  and is the pattern to keep. The split target should be updated to reflect
  that instead of resurrecting `DroneAIController`.

- **SpotBugs cleanup**, batched:
  - ✅ Batch 1 — null-safety on `getInstance()` across five manager classes. Done.
  - ⬜ Batch 2 — `UUID.equals(String)` logic bug
  - ⬜ Batch 3 — unread test fields
  - ⬜ Batch 4 — EI/EI2 mutable state exposure
  - ⬜ Batch 5 — `ModConfig` singleton thread safety
  - ⬜ Batch 6 — `ModConfig` public primitive fields
  - ⬜ Batch 7 — misc: ignored return values, dead stores

- **Deferred, not dropped: the "Rank" progression system.** Early planning
  docs (including `SPEC-0002-DRONE_LIFECYCLE.md`'s A→Z rank concept)
  proposed this, and it's not implemented in current code. This is
  intentional sequencing, not a rejection: foundational systems (ownership
  reconciliation, the `DroneEntity` split) come first, then a proper
  categorization system for role/progression flexibility gets designed on
  top of that solid foundation. Don't implement Rank yet, but don't treat
  the idea as dead either — it's roadmapped, just not specified.

---

## 4. Working principles (earned the hard way)

- **Model entities and relationships before writing managers.** Skipping this
  is literally why section 2 above exists.
- **One source of truth for ownership.** Don't let an entity cache a
  relationship that a separate manager also tracks independently.
- **`HiveMindServiceManager` is the testable seam.** Any code that reaches for
  `.getInstance(server)` directly instead of going through the service manager
  is bypassing testability — treat that as a bug, not a style nit.
- **Cross-reference imported planning docs against the actual repo before
  trusting them.** The ChatGPT-era taskboard predates a lot of current code
  and gets specific things wrong (see `DOCS_CLEANUP.md` for the fuller
  reasoning on this).
- **Tautological tests don't prove anything.** An internal flag that can never
  be false doesn't verify behavior — assert on observable public API.
- **Small, executable next steps over roadmap documents.** If a plan can't be
  stated as "do X to file Y, verify with Z," it's not ready to work on yet.

---

## 5. Stack reference

- Java 17, Fabric Loader, Minecraft 1.20.1, Gradle 8.14.4, Fabric Loom 1.11.1
- Checkstyle 10.12.7, SpotBugs, Spotless
- Cardinal Components API is **not used** (`PlayerHiveComponent` was built
  instead — see `ADR-0004`) — the one exception is worth double-checking
  against current code since this predates recent work.
- Dev environment: Windows locally, Linux x86_64 in CI.

---

## Related documents

- [`VISION.md`](VISION.md) — the long-term "why," including the parked HiveCore idea
- [`ROADMAP.md`](ROADMAP.md) — realistic near-term plan
- [`DOCS_CLEANUP.md`](DOCS_CLEANUP.md) — what happened to the old documentation set and why