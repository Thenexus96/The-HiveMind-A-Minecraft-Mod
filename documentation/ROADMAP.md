# HiveMind — Roadmap

Supersedes: `RoadMap v1`, `Road Map v2`, `Road Map v3`, the old `ROADMAP.md`,
and the phase docs under `roadmap/`. Those disagreed with each other and with
the actual code (several marked ownership/hive systems "✅ complete" — they
aren't; see `PROJECT_STATUS.md`). This version only lists what's real.

Format is deliberately flat, not a multi-quarter phase chart — those didn't
hold up last time and encouraged treating aspirational plans as done work.

## Now

1. **Fix `DroneEntity` untestable methods.** Route
   `restoreHiveMindConnection()` and `removeHiveMindConnection()` through
   `HiveMindServiceManager.get()` instead of calling `.getInstance(server)`
   directly. (Detailed in `PROJECT_STATUS.md` §2.)
2. **ERD pass.** Actually draw out Player / Hive / HiveCore / Drone and how
   ownership and control relate, *before* touching the four-system
   reconciliation. This was the step that got skipped originally.
3. **Reconcile the four ownership/persistence systems** into one source of
   truth, informed by the ERD above.

## Next

4. **Split `DroneEntity`** into smaller pieces along the lines already proven
   out by the `DroneRoleBehavior` / `RoleRegistry` pattern — extend that
   pattern rather than introducing the old `DroneAIController` concept.
5. **SpotBugs batches 2–7** (see `PROJECT_STATUS.md` for the list).
6. **`HiveController` interface** — abstraction to support delegating
   ownership from a player to a `HiveCoreEntity` later, and to support
   NPC-generated hives with no player owner. Ownership registry stays a
   single UUID→UUID mapping regardless of controller type; automation is a
   mode flag, not a parallel system.

## Later / not scheduled

- Drone possession polish, formation/group commands, research & progression
  tree, additional roles beyond Scout/Soldier/Idle — all real ideas from the
  older roadmap docs, genuinely worth doing, but blocked on the architecture
  work above and not worth detailing until then.
- HiveCore library extraction — see `VISION.md`. Not started, not planned
  until explicitly revisited.
- "Rank" progression system (A→Z, from `SPEC-0002-DRONE_LIFECYCLE.md`) —
  deliberately sequenced after foundational systems and a proper role/
  progression categorization pass. Not rejected, just not up yet.

## Explicitly not doing

- Monorepo / multi-module restructuring (deprioritized; revisit only if Nick
  raises it).
- Cardinal Components as a dependency (decided against in `ADR-0004`; still
  holds unless something's changed).