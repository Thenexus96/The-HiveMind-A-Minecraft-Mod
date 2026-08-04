# ADR-0003 — Golem to Drone Naming Change

**Project:** Living Systems Project (LSP)  
**Module:** HiveMind  
**Status:** Accepted  
**Date:** YYYY-MM-DD

---

# Context

Early development of HiveMind used the term:

```
Golem
```

to describe the primary autonomous entity.

The original concept was based around a constructed helper entity capable of performing tasks for the player.

As the project vision developed, the role of this entity expanded.

The system moved away from creating a single helper creature and toward creating a network of autonomous units connected through a larger intelligence structure.

The original terminology no longer represented the intended design.

---

# Decision

The primary entity was renamed from:

```
Golem
```

to:

```
Drone
```

The term Drone better represents the intended role within HiveMind.

---

# Reasoning

## Represents Collective Intelligence

A golem is typically understood as:

- A constructed guardian
- A singular entity
- A servant or protector

A drone represents:

- A specialized unit
- A member of a larger organization
- A component of collective intelligence

This better matches HiveMind's goals.

---

## Supports Future Expansion

The term Drone allows additional roles and variations.

Examples:

```
Worker Drone

Combat Drone

Scout Drone

Builder Drone

Research Drone
```

This creates a scalable naming structure.

---

## Better Fits Hive Architecture

HiveMind is designed around:

```
Hive

↓

Drones

↓

Tasks

↓

Collective Behavior
```

The Drone terminology reinforces that entities are connected parts of a larger system.

---

# Alternatives Considered

## Golem

Rejected.

Reason:

The term implies:

- A singular constructed being
- Limited autonomy
- Individual purpose

This conflicts with the intended hive-based architecture.

---

## Worker

Considered.

Reason Not Selected:

Too restrictive.

The project requires support for multiple drone roles beyond labor.

---

## Servitor

Considered.

Reason Not Selected:

The term implies obedience rather than autonomous participation.

---

# Consequences

## Positive

- Better reflects the project's philosophy.
- Creates clearer terminology.
- Supports future unit expansion.
- Strengthens the HiveMind identity.

---

## Negative

- Requires updating documentation.
- Requires updating code references.
- Older discussions may contain outdated terminology.

---

# Terminology Standard

From this point forward:

Use:

```
Drone
```

instead of:

```
Golem
```

unless specifically discussing historical development.

---

# Related Documents

- PROJECT_BIBLE.md
- TERMINOLOGY.md
- ENTITY_ARCHITECTURE.md
- HIVE_CORE.md
- AI_ARCHITECTURE.md