# Architecture: Ownership & Persistence

**Status:** This documents a known-bad state, on purpose. Don't read this and
assume the design is intentional — it isn't. This is the accurate map of a
mess that needs fixing, written so nobody (human or AI) proposes a "fix" that
actually just adds a fifth overlapping system.

If you're about to touch anything in this area, read `PROJECT_STATUS.md` §2–3
first for the current plan and next concrete step.

## The core problem in one sentence

Four separate systems each partially answer "who owns this drone / who's in
this hive," built across different sessions, never reconciled, and
`DroneEntity` itself only talks to one of them directly.

## The four systems

### 1. `HiveMindLinkManager` (`data/HiveMindData/HiveMindLinkManager.java`)
- **What it tracks:** `droneUUID → ownerUUID`, plus the reverse
  `ownerUUID → List<droneUUID>`.
- **Persistence:** `PersistentState`, saved to world NBT under
  `hivemind_links`.
- **Who actually uses it:** This is the *real* path today.
  `DroneEntity.setHiveMindOwner()` routes through it (via
  `HiveMindServiceManager` → `DroneLinkUtils.linkDrone()`). Restore/remove
  paths touch it too, but bypass the service manager doing so (see the
  `DroneEntity` bug in `PROJECT_STATUS.md`).
- **Verdict:** Keep. This is the closest thing to a correct source of truth
  for drone→owner today.

### 2. `Hive` / `HiveManager` (`data/HiveMindData/Hive.java`, `HiveManager.java`)
- **What it tracks:** Player-centric. A `Hive` has an owner UUID, a member
  set, a resource map, and research state (NBT blob, unused so far).
- **Persistence:** `PersistentState`, saved under `hivemind_hive_manager`.
- **Who actually uses it:** Only `/hive join` and `/hive leave`
  (`DebugCommands.joinMyHive` / `leaveHive`). **Nothing else touches it.**
  `DroneEntity` has no reference to a `Hive` object at all.
- **Verdict:** This is the *intended* long-term shape (player-centric,
  supports membership, resources, research) but it's currently an island —
  wired into almost nothing.

### 3. `HiveMindData` (`data/HiveMindData/HiveMindData.java`)
- **What it tracks:** Player NBT flags — access boolean, a "Hive ID" (which
  defaults to the player's own UUID, not a real `Hive.id`), join timestamp.
- **Persistence:** Player NBT via `writeNbt`/`readNbt` round-trip — worth
  noting this pattern looks fragile (full NBT round-trip just to read/write
  one sub-compound) independent of the ownership question.
- **Verdict:** Overlaps almost entirely with #4 below. One of these two
  should not survive the reconciliation.

### 4. `PlayerHiveComponent` (`data/player/PlayerHiveComponent.java`)
- **What it tracks:** The same shape as #3 — access flag, hive id, join
  time — but stored differently: an in-memory `Map<UUID, PlayerHiveData>`
  cache backed by **individual files on disk**
  (`<world>/hivemind/<uuid>.dat`), not the standard `PersistentState`/NBT
  world-save path.
- **Who actually uses it:** This is the one actually wired into gameplay —
  `HiveMindAccessItem`, `DroneCoreItem`, `DebugCommands.grantAccess`,
  `PlayerDataEvents` (join/leave hooks) all call this, not `HiveMindData`.
- **Verdict:** This is the "real" player-access system in practice, even
  though `HiveMindData` looks like it should be. Custom file-based
  persistence outside the normal `PersistentState` system is itself worth
  questioning during reconciliation — it works, but it's a second
  persistence mechanism to reason about for no clear reason.

## Why this happened

Per project history: the ERD step (mapping out how Player, Hive, HiveCore,
and Drone relate before writing manager classes) was skipped. `HiveMindData`/
`PlayerHiveComponent` and `HiveMindLinkManager`/`Hive`/`HiveManager` came from
separate planning sessions that never got reconciled against each other.

## What reconciliation should produce (not yet done)

Not a full spec — that's the ERD work itself — but constraints any fix should
satisfy:

- **One persistence mechanism**, not two (`PersistentState`/world-NBT vs.
  custom per-player files). Prefer whichever integrates better with how
  `HiveMindLinkManager` already works, since that's the system to keep.
- **`DroneEntity` should reference hive membership through `Hive`**, not
  store `hiveMindOwnerUuid` as an independent fact that can drift from what
  `Hive.members` says.
- **`HiveMindData` and `PlayerHiveComponent` should become one system**,
  since they track identical data two different ways.
- Whatever comes out of this should go through `HiveMindServiceManager` —
  that's the established testable seam; a new manager that bypasses it
  reintroduces the exact problem already flagged in `DroneEntity`.

## Related

- `PROJECT_STATUS.md` — current plan and immediate next step
- `architecture/ROLE_BEHAVIOR_SYSTEM.md` — a system that *is* in good shape,
  useful as a contrast/reference for what "reconciled" should look like
- ADR-0004 (`decisions/`) — the decision that led to `PlayerHiveComponent`
  existing instead of using Cardinal Components