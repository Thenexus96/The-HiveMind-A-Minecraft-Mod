# HiveMind — Vision

This is intentionally short. Long philosophy documents don't help you ship
code, and the previous version of this project's docs proved that — pages of
manifesto material didn't prevent (and arguably encouraged) an unreconciled
four-system ownership bug. Keep this page honest and skip the ceremony.

## The mod, concretely

A Minecraft Fabric mod where a player joins a HiveMind and gains autonomous
Drone entities they can command, possess, and grow over time. The interesting
gameplay loop is: deploy drones → assign roles/behavior → command or possess
them → the hive grows.

## Why Nick is building it

Two goals, held simultaneously and neither one gets to override the other:

1. **A finished, well-architected mod** he'd actually want to play and be
   proud of the code behind.
2. **A vehicle for learning real software development** — interfaces,
   inheritance, generics, service layers, testable seams, and the discipline
   of fixing architecture before piling on features. The `HiveMindServiceManager`
   pattern and the ownership-system mess in `PROJECT_STATUS.md` are both, in
   their own way, exactly the kind of thing that's worth learning from.

## The long-term idea: HiveCore

There's a real, plausible future where the reusable parts of this mod (drone
ownership, role/behavior framework, command routing, persistence patterns) get
extracted into a standalone `HiveCore` library that other mods could depend on
— including a possible future "Divine Dungeon" crossover mod mentioned in
earlier planning.

**This is explicitly not current work.** It requires:
- The ownership/persistence architecture actually being fixed first (you can't
  extract a clean library from four overlapping systems).
- A second real consumer of the library to prove the abstraction is right,
  rather than guessing at boundaries with only one mod to test against.
- Nick deciding it's time — this doc records the idea so it isn't lost, not so
  it gets picked up automatically.

Until then: **single module, no monorepo restructuring, no premature
abstraction.** If a class could plausibly be reused later, that's fine to
notice, but it's not a reason to build a plugin system for it now.

## What "done" looks like for the current phase

Not a shipped 1.0 — just: one ownership system, `DroneEntity` split into
sane-sized pieces, the role/behavior strategy pattern used consistently, and a
SpotBugs report that isn't full of open items. That's the bar before "what's
next" gets interesting again.