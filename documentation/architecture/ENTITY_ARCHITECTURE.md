# Living Systems Project — Entity Architecture

**Project:** Living Systems Project (LSP)
**Primary Module:** HiveMind
**Minecraft Version:** 1.20.1
**Mod Loader:** Fabric
**Documentation Version:** 1.0
**Status:** Active

---

# 1. Purpose

This document defines the architecture of all living entities within HiveMind.

It establishes:

- Entity responsibilities
- Lifecycle
- State management
- Relationships
- Communication
- Ownership
- Expansion strategy

Every future entity should be understandable through the architecture defined here.

---

# 2. Philosophy

HiveMind entities are not "mobs."

They are autonomous agents participating in a larger living system.

Each entity should possess:

- Identity
- Purpose
- Context
- Relationships
- Memory
- Goals

An entity should never exist solely to perform animations or scripted actions.

---

# 3. Entity Hierarchy

```
Minecraft Entity

        │

Living Entity

        │

Hive Entity

        │

Drone

        │

Specialized Drone
```

This hierarchy separates Minecraft functionality from HiveMind functionality.

---

# 4. Core Entity Principles

Every HiveMind entity should satisfy the following principles.

## Identity

Every entity has a persistent identity.

Identity should survive:

- Saving
- Loading
- World transfers
- Hive reassignment (where appropriate)

Identity should never depend on runtime object references.

---

## Purpose

Every entity exists for a reason.

Examples:

Worker Drone

Combat Drone

Scout Drone

Builder Drone

Research Drone

Future entities should define their purpose before implementation begins.

---

## Context

Behavior depends upon context.

Context includes:

- Nearby entities
- Hive state
- Assigned task
- World conditions
- Threat level
- Resource availability

Behavior should never operate in isolation.

---

## Autonomy

Each entity owns its own decisions.

The Hive provides goals.

The Drone determines execution.

---

# 5. Entity Lifecycle

Every entity progresses through several stages.

```
Creation

↓

Initialization

↓

Hive Assignment

↓

Task Assignment

↓

Active Operation

↓

Idle

↓

Task Reassignment

↓

Removal
```

Each stage represents explicit transitions rather than hidden state changes.

---

# 6. Entity Responsibilities

A Drone owns:

- Current state
- Current task
- Navigation
- Immediate decision making
- Local awareness

A Drone does **not** own:

- Colony management
- Global strategy
- Population control
- Hive identity

Those belong to the Hive Core.

---

# 7. State Model

A Drone should always exist in a clearly defined state.

Example states:

```
Initializing

Idle

Evaluating

Moving

Gathering

Building

Returning

Defending

Recovering

Disabled
```

States should be mutually exclusive whenever possible.

---

# 8. Role Model

Role describes *what the Drone is.*

State describes *what the Drone is currently doing.*

Example:

```
Role

Worker

State

Returning Resources
```

This distinction prevents unnecessary complexity.

---

# 9. Relationships

A Drone may maintain relationships with:

Hive

Task

Location

Resource

Other Drones

Player

Relationships should be explicit rather than inferred whenever practical.

---

# 10. Communication

Entities communicate indirectly.

Preferred:

```
Drone

↓

Task Complete Event

↓

Hive

↓

Assign New Task
```

Avoid:

```
Drone modifies Hive internals directly.
```

---

# 11. Memory

HiveMind distinguishes between two kinds of memory.

## Local Memory

Short-lived information.

Examples:

Last resource location.

Current threat.

Navigation target.

---

## Persistent Memory

Long-term information.

Examples:

Identity

Rank

Hive Membership

Role

Specialization

---

# 12. Future Expansion

The architecture should support future entity types without modification.

Possible future entities:

```
Queen

Worker Drone

Combat Drone

Scout Drone

Engineer Drone

Research Drone

Caretaker Drone

Logistics Drone

Player-controlled Units
```

Adding these should require configuration rather than architectural redesign.

---

# 13. Architecture Rules

Rule 1

Every entity has one clear responsibility.

Rule 2

Behavior belongs to AI.

Rule 3

Identity belongs to the entity.

Rule 4

Strategy belongs to the Hive.

Rule 5

Communication occurs through interfaces or events.

Rule 6

Relationships are explicit.

Rule 7

Expansion should never require replacing the hierarchy.

---

# 14. Future Documents

This document intentionally leaves implementation details to:

- AI_ARCHITECTURE.md
- HIVE_CORE.md
- DATA_ARCHITECTURE.md
- TASK_SYSTEM.md
- MEMORY_SYSTEM.md

Together these documents define the complete Drone Framework.

---

# Document History

| Version | Date       | Changes |
|---------|------------|----------|
| 1.0 | 2026-07-09 | Initial Entity Architecture |