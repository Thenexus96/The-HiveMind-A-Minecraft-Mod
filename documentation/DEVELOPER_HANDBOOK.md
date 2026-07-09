# Living Systems Project — Developer Handbook

**Project:** Living Systems Project (LSP)  
**Primary Module:** HiveMind  
**Minecraft Version:** 1.20.1  
**Mod Loader:** Fabric  
**Documentation Version:** 1.0  
**Status:** Active  
**Last Updated:** YYYY-MM-DD

---

# 1. Purpose

The Developer Handbook is the primary guide for understanding, developing, and contributing to the Living Systems Project.

This document combines project philosophy, development workflow, technical expectations, and contribution practices into one reference.

The goal is to ensure that all development remains aligned with the project's long-term vision.

---

# 2. Project Overview

The Living Systems Project (LSP) is a modular Minecraft development project focused on creating intelligent, autonomous, and interconnected systems.

The first major module is:

## HiveMind

HiveMind focuses on:

- Autonomous drones
- Hive intelligence
- AI behavior systems
- Colony development
- Player interaction
- Emergent gameplay

---

# 3. Project Philosophy

Before writing code, understand the principles behind the project.

The project prioritizes:

## Systems Over Features

Features should contribute to larger systems.

---

## Emergence Over Scripts

Create rules and relationships that produce behavior naturally.

---

## Player Influence Over Player Control

Players guide systems without replacing them.

---

## Expansion Over Shortcuts

Design systems that can grow.

---

## Documentation As Development

Important decisions must be recorded.

---

# 4. Repository Organization

The project is organized into several major areas.

```
LivingSystemsProject/

├── documentation/
│
│   ├── Project documentation
│   ├── Architecture documentation
│   ├── Development guides
│   └── Historical records
│
├── specs/
│
│   ├── Feature specifications
│   └── System designs
│
├── mods/
│
│   └── HiveMind/
│
└── README.md
```

---

# 5. Development Workflow

Development follows a documentation-first approach.

The general workflow:

```
Idea

↓

Specification

↓

Design Review

↓

Implementation

↓

Testing

↓

Documentation Update

↓

Release
```

---

# 6. Feature Development Process

Before implementing a major feature:

## Step 1 — Define Purpose

Document:

- What problem does this solve?
- Why does this belong in the project?
- How does it support the vision?

---

## Step 2 — Create Specification

Major systems should receive a specification.

Example:

```
specs/

0001-drone-framework.md
```

Specifications should include:

- Goals
- Requirements
- Design
- Limitations
- Future considerations

---

## Step 3 — Architecture Review

Determine:

- Where the system belongs
- What systems it interacts with
- What dependencies it requires

---

## Step 4 — Implementation

Write the code while maintaining:

- Clean structure
- Documentation
- Testing

---

## Step 5 — Record Changes

Update:

- Implementation Log
- Changelog
- Related documentation

---

# 7. Architecture Guidelines

## Modular Design

Systems should have clear responsibilities.

Example:

Drone AI:

Responsible for:

- Behavior
- Decision making
- Actions

Hive Core:

Responsible for:

- Organization
- Communication
- Colony management

---

## Avoid Tight Coupling

Systems should communicate through defined interfaces.

Avoid:

- Direct dependency chains
- Hidden relationships
- Single-use solutions

---

# 8. Coding Standards

## Naming

Use clear descriptive names.

Prefer:

```
HiveCoreManager
DroneTaskController
```

Avoid:

```
Manager2
ThingHandler
```

---

## Code Organization

Classes should have focused responsibilities.

A class should answer:

"What does this own?"

---

## Comments

Comments should explain:

- Why something exists
- Why a decision was made

Avoid explaining obvious code.

---

# 9. Documentation Requirements

Major changes require documentation updates.

Examples:

New system:

Required:

- Specification
- Architecture documentation
- Implementation notes

Major decision:

Required:

- ADR

Completed feature:

Required:

- Changelog update

---

# 10. Architecture Decision Records

ADRs document important decisions.

Examples:

```
ADR-0001-project-origin.md

ADR-0002-fabric-selection.md

ADR-0003-drone-renaming.md
```

An ADR should explain:

- Context
- Options considered
- Decision
- Reasoning
- Consequences

---

# 11. Testing Philosophy

Testing should ensure systems behave reliably.

Testing should consider:

- Functionality
- Performance
- Multiplayer behavior
- Edge cases

---

# 12. Debugging Philosophy

Debug tools are considered part of development.

HiveMind will eventually include tools for:

- Drone state visualization
- Hive information
- AI debugging
- Influence visualization

Debugging systems should make complex behavior understandable.

---

# 13. Git Workflow

Development should maintain clear history.

Commits should:

- Describe meaningful changes
- Avoid mixing unrelated changes
- Reference documentation when appropriate

Example:

```
Add drone navigation foundation

Related:
SPEC-0001
ADR-0007
```

---

# 14. Contribution Guidelines

Before contributing:

Understand:

- Project philosophy
- Architecture
- Development standards

Contributions should:

- Follow existing patterns
- Include documentation
- Explain major decisions

---

# 15. Development Priorities

When choosing between options, prioritize:

1. Stability
2. Maintainability
3. Performance
4. Expansion potential
5. Complexity reduction

---

# 16. Current Development Focus

Current priority:

## HiveMind Phase 2 — Drone Framework

Focus areas:

- Drone architecture
- AI systems
- Entity behavior
- Hive foundations

---

# 17. Future Developer Expectations

Future contributors should understand:

The goal is not simply to add content.

The goal is to create systems.

Every addition should contribute toward building a world that feels alive.

---

# Related Documents

- PROJECT_CHARTER.md
- PROJECT_BIBLE.md
- PROJECT_OVERVIEW.md
- PROJECT_VISION.md
- DESIGN_PHILOSOPHY.md
- CORE_PRINCIPLES.md
- ROADMAP.md
- SYSTEM_ARCHITECTURE.md

---

# Document History

| Version | Date       | Changes |
|---|------------|---|
| 1.0 | 2026-07-01 | Initial Developer Handbook |