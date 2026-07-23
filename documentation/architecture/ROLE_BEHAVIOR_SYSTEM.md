# Architecture: Drone Role/Behavior System

**Status:** Accurate as of this writing — this pattern works and is the
model to extend, not replace. Contrast with `PROJECT_STATUS.md`'s ownership
section, which documents a system that's *not* in a good state — this one is
a good example of what "done right" looks like here.

## What it is

A **strategy pattern**: `DroneEntity` doesn't contain role-specific logic
itself. Instead it holds a `DroneRoleBehavior` reference and delegates to it.

```
DroneEntity
  ├── currentRole: DroneRole (enum — just an identifier + display name)
  └── roleBehavior: DroneRoleBehavior (the actual strategy object)
```

## The interface

`DroneRoleBehavior` (`entity/custom/role/DroneRoleBehavior.java`):

```java
public interface DroneRoleBehavior {
  void applyGoals(DroneEntity drone);       // required
  void onRoleAssigned(DroneEntity drone);   // required
  default void tick(DroneEntity drone) {}          // optional, no-op default
  default void onRoleRemoved(DroneEntity drone) {}  // optional, no-op default
  DroneRole getRole();
  default int getGoalPriority() { return 5; }       // optional, sensible default
}
```

Worth noticing as a Java teaching point: two methods are required (no
default), and the rest have `default` implementations that do nothing. That's
a deliberate interface design choice — it means `IdleRole` (which genuinely
has nothing to tick) can skip overriding `tick()` entirely, while
`SoldierRole` (which needs cleanup logic) overrides `onRoleRemoved()`. The
interface doesn't force every implementer to write boilerplate no-ops.

## The registry

`RoleRegistry` is a simple static lookup: `DroneRole` enum value →
`DroneRoleBehavior` instance, built once in a static initializer block.
`getBehavior()` falls back to `IDLE` if a role somehow isn't registered,
rather than returning `null` or throwing — the entity always has *some*
usable behavior.

## Adding a new role — the actual steps

1. Create a class implementing `DroneRoleBehavior` (e.g. `WorkerRole`) in
   `entity/custom/role/`.
2. Add the enum value to `DroneRole`.
3. Register it in `RoleRegistry`'s static block: `register(new WorkerRole());`
4. That's it — `DroneEntity.setRole()` and `applyRoleBehavior()` already
   handle the swap generically; nothing in `DroneEntity` itself needs to
   change.

That last point is the actual payoff of the pattern: **the entity class
doesn't grow every time a role is added.** Compare this to the alternative —
a big `switch` statement over role types inside `DroneEntity` — which is
exactly the kind of god-object growth flagged as a problem in
`PROJECT_STATUS.md`.

## Why this replaced the earlier `DroneAIController` plan

An imported ChatGPT-era planning doc proposed a `DroneAIController` class as
part of the drone framework. In practice, `DroneRoleBehavior` +
`RoleRegistry` ended up solving the same problem more cleanly — behavior is
swappable per-role without a separate controller layer sitting on top. When
`DroneEntity` eventually gets split into smaller pieces (see roadmap), this
pattern is the one to build on, not the older controller concept.

## Known rough edges (honest, not hidden)

- `applyRoleBehavior()` clears *all* goal/target selectors and rebuilds base
  goals every role change, rather than only touching role-specific slots.
  Works, but is a bit heavier than it needs to be.
- Goal priority ranges (2–7 reserved for roles, base goals at 0–1 and 8–10)
  are a convention enforced by comments, not the compiler. Worth keeping in
  mind if a new role's `getGoalPriority()` collides with another role's.