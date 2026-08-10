# Living Systems Project — Architecture Decision Record

**Project:** Living Systems Project
**Document Type:** Architecture Decision Record
**Status:** Accepted
**Version:** 1.0

```yaml
---
title: ADR-0008-Mental-Power-Data-Ownership
type: Architecture Decision Record
status: Accepted
version: 1.0
module: HiveMind
created: 2026-08-09
last_updated: 2026-08-09
---
```

---

# ADR-0008 — Mental Power Data Ownership

## Status

Accepted

---

# Context

HIV-116 required adding a `mentalPower` stat (int, 0–10, clamped) representing
a player's connection strength to the HiveMind, gating how many drones they
can control.

Before implementation, it wasn't obvious which existing system should own
this field. The codebase has several candidate homes for player/hive state:

- `HiveMindLinkManager` — canonical drone-owner mapping (per ADR-0007)
- `PlayerHiveComponent` — per-player access control, file-persisted
- `Hive` / `HiveManager` — collective mechanics: membership, resources,
  research state, persisted via `PersistentState` NBT
- A hypothetical new standalone `MentalPower` class

During implementation, a standalone `MentalPower` class was drafted and then
discarded before landing on the final approach, which is why this decision is
worth recording rather than leaving implicit in the diff.

---

# Decision

`mentalPower` is a plain `int` field directly on `Hive`
(`src/main/java/net/sanfonic/hivemind/data/HiveMindData/Hive.java`), with a
clamped setter (`setMentalPower`, range 0–10) and a plain getter
(`getMentalPower`). It is serialized in `Hive.toNbt()` / deserialized in
`Hive.fromNbt()` using the same pattern already used for `resources`, and
persists automatically through the existing `HiveManager` `PersistentState`
save/load cycle. No new class or manager was introduced.

---

# Alternatives Considered

## Option 1: Standalone `MentalPower` class

Description: a dedicated object (e.g. `new MentalPower(ownerUuid, value)`)
owned separately from `Hive`.

Advantages:
- Would give mental power its own place to grow (e.g. per-source gain
  tracking) without touching `Hive`.

Disadvantages:
- No current requirement justifies the extra indirection — mental power
  today is a single clamped int with no independent lifecycle.
- Would need its own persistence wiring, duplicating what `Hive`'s NBT
  round-trip already does for free.
- Conflicts with `CORE_PRINCIPLES.md` #7 (complexity must provide value) and
  #11 (stable foundations before complexity) — this is premature structure
  for a feature that doesn't yet have gain mechanics defined.

## Option 2: Field on `HiveCore`

Description: place `mentalPower` on a `HiveCore` block/entity concept.

Reason not selected: no `HiveCore` data-model class exists in the current
codebase (only registered block content — see
`documentation/reference/ENTITY_INDEX.md`). Introducing one solely to hold
this field would be new architecture invented to serve a single stat, with
no other current use case driving it.

## Option 3: Field on `Hive` (Selected)

Description: add `mentalPower` alongside `resources` and `researchState` on
the existing `Hive` class.

Advantages:
- Mental power is collective/progression state, matching exactly what `Hive`
  already owns per ADR-0007 ("Hive/HiveManager: collective mechanics —
  intentionally isolated from drone ownership").
- Reuses existing, already-tested NBT persistence — no new serialization
  code path.
- Consistent with the "model entities and relationships before writing
  managers" working principle in `PROJECT_STATUS.md`.

---

# Consequences

## Positive Effects

- Zero new persistence machinery; mental power survives server restarts for
  free via `HiveManager`'s existing save/load.
- Keeps `Hive` as the single place to look for "stuff a hive collectively
  has" (members, resources, research, now mental power).
- Read path (`fromNbt`) routes through the setter, so the 0–10 invariant is
  enforced even against hand-edited or corrupted save data.

## Negative Effects

- If mental power's gain mechanics grow significantly more complex later
  (e.g. per-source contribution tracking, decay over time, multiple
  simultaneous gain categories), the current single-int representation may
  need to be revisited or expanded — deferred deliberately per
  `CORE_PRINCIPLES.md` #7/#11, not a design gap in this ADR.

## Future Impact

Future mental-power-related tickets (gain mechanics, drone spawning gating,
UI display) should read/write through `Hive.getMentalPower()` /
`setMentalPower()` rather than introducing a parallel tracking mechanism,
unless a future ADR supersedes this one with a documented reason (e.g. actual
multi-source gain requirements materializing).

---

# Implementation Notes

- `Hive.java`: field, getter, clamped setter, NBT read/write.
- `ModConfig.java`: `mentalPowerMax`, `mentalPowerGainRate`,
  `mentalPowerGainMechanism` — config only, not persisted per-hive state.
- `HiveTest.java`: `testMentalPowerPersistence`,
  `testMentalPowerClampsToMaxAfterPersistence`,
  `testMentalPowerClampsToMinAfterPersistence`.
- No migration required — new NBT key (`mentalPower`), old saves simply
  default to `0` via `nbt.contains()` guard in `fromNbt()`.

---

# Related Documentation

## Architecture

- `documentation/architecture/DATA_ARCHITECTURE.md`

## History

- `documentation/history/ADR/ADR-0007-ownership-system.md`

## Planning

- `documentation/DESIGN_DECISIONS.md`
- `documentation/00_Project/CORE_PRINCIPLES.md` (principles #7, #11)

---

# Decision Summary

> This decision establishes that `mentalPower` lives directly on `Hive` as a
> clamped int field, persisted via `Hive`'s existing NBT cycle, because it is
> collective/progression state that already belongs to `Hive` under
> ADR-0007's ownership boundaries — not because a new class was needed.

---

# Document History

| Version | Date       | Changes                        |
| ------- | ---------- | ------------------------------- |
| 1.0     | 2026-08-09 | Initial ADR for HIV-116 |