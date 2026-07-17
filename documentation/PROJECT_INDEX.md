# Living Systems Project — Knowledge Base Index

**Project:** Living Systems Project (LSP)
**Primary Implementation:** HiveMind
**Minecraft Version:** 1.20.1
**Mod Loader:** Fabric
**Document Status:** Active
**Documentation Version:** 1.0

---

# Purpose

The Living Systems Project Knowledge Base is the central navigation system for all project documentation.

This index connects the project's:

* Vision
* Philosophy
* Architecture
* Specifications
* Implementation
* History
* Roadmap
* Reference Material

The goal is to ensure that the project remains understandable, maintainable, and expandable over time.

---

# Current Repository Structure

The structure below is the authoritative current layout. Earlier planning documents may use a numbered `docs/` hierarchy; that hierarchy is a planning model, not a filesystem map for this repository.

```text
The-HiveMind-A-Minecraft-Mod/

├── README.md
├── CHANGELOG.md
├── CONTRIBUTING.md
│
├── documentation/
│
│   ├── 00_Project/
│   ├── architecture/
│   ├── specs/
│   ├── development/
│   ├── history/
│   ├── roadmap/
│   ├── reference/
│   ├── wiki/
│   └── design/
│
└── src/
```

See [DOCUMENTATION_GAP_REGISTER.md](DOCUMENTATION_GAP_REGISTER.md) for planned document categories that have not yet been created because their underlying systems are not implemented or approved.

---

# Documentation Hierarchy

The Living Systems Project follows this documentation hierarchy:

```text
Vision

↓

Principles

↓

Architecture

↓

Specifications

↓

Implementation

↓

Testing

↓

History
```

Each layer has a specific purpose.

---

# 00 — Project Foundation

## Purpose

Defines why the project exists and establishes the guiding principles behind development.

Location:

```text
docs/00_Project/
```

Documents:

| Document                | Purpose                                |
| ----------------------- | -------------------------------------- |
| PROJECT_CONSTITUTION.md | Project governing principles and rules |
| PROJECT_CHARTER.md      | Project purpose and scope              |
| PROJECT_BIBLE.md        | Complete project identity reference    |
| PROJECT_OVERVIEW.md     | High-level project introduction        |
| PROJECT_VISION.md       | Long-term direction                    |
| SYSTEMS_MANIFESTO.md    | Core philosophy behind living systems  |
| DESIGN_PHILOSOPHY.md    | Design principles                      |
| CORE_PRINCIPLES.md      | Engineering principles                 |
| TERMINOLOGY.md          | Core project vocabulary                |

---

# 01 — Architecture

## Purpose

Defines how systems are organized and how they interact.

Location:

```text
docs/01_Architecture/
```

Documents:

| Document               | Purpose                            |
| ---------------------- | ---------------------------------- |
| SYSTEM_ARCHITECTURE.md | Overall system structure           |
| ENTITY_ARCHITECTURE.md | Entity design and responsibilities |
| AI_ARCHITECTURE.md     | Intelligence and behavior systems  |
| DATA_ARCHITECTURE.md   | Data ownership and persistence     |
| EVENT_ARCHITECTURE.md  | Communication and events           |
| HIVE_CORE.md           | Hive intelligence architecture     |
| TASK_SYSTEM.md         | Task and assignment framework      |
| MEMORY_SYSTEM.md       | Memory and information systems     |

---

# 02 — Specifications

## Purpose

Defines exact behavior before implementation.

Location:

```text
docs/02_Specifications/
```

Specifications describe:

* Purpose
* Requirements
* Data structures
* System behavior
* Interfaces
* Dependencies
* Testing requirements
* Acceptance criteria

Structure:

```text
02_Specifications/

├── Drone/
│
├── Hive/
│
├── AI/
│
├── Blocks/
│
├── Items/
│
├── World/
│
└── Player/
```

---

# 03 — Development

## Purpose

Defines how the project is developed and maintained.

Location:

```text
docs/03_Development/
```

Documents:

| Document                | Purpose                                      |
| ----------------------- | -------------------------------------------- |
| ENGINEERING_HANDBOOK.md | Engineering practices and decision framework |
| DEVELOPER_HANDBOOK.md   | Contributor workflow                         |
| CODING_STANDARDS.md     | Code conventions                             |
| TESTING.md              | Testing strategy                             |
| DEBUGGING.md            | Debugging practices                          |
| BUILD.md                | Build process                                |
| RELEASE.md              | Release workflow                             |

---

# 04 — History

## Purpose

Preserves the evolution of the project.

Location:

```text
docs/04_History/
```

Documents:

| Document              | Purpose                       |
| --------------------- | ----------------------------- |
| ADR/                  | Architecture Decision Records |
| SESSION_HISTORY.md    | Major development discussions |
| IMPLEMENTATION_LOG.md | Completed development work    |
| CHANGELOG.md          | User-facing changes           |
| PROJECT_TIMELINE.md   | Major milestones              |

---

# 05 — Roadmap

## Purpose

Defines planned development and future direction.

Location:

```text
docs/05_Roadmap/
```

Documents:

| Document    | Purpose                      |
| ----------- | ---------------------------- |
| ROADMAP.md  | Complete development roadmap |
| PHASE_01.md | Foundation phase             |
| PHASE_02.md | Drone Framework phase        |
| PHASE_03.md | World impact systems         |
| BACKLOG.md  | Future ideas and tasks       |

---

# 06 — Reference

## Purpose

Provides navigation and lookup information.

Location:

```text
docs/06_Reference/
```

Documents:

| Document              | Purpose                     |
| --------------------- | --------------------------- |
| LEXICON.md            | Official project vocabulary |
| GLOSSARY.md           | General terminology         |
| ENTITY_INDEX.md       | Entity catalog              |
| SYSTEM_INDEX.md       | System catalog              |
| DECISION_INDEX.md     | ADR navigation              |
| ARCHITECTURE_INDEX.md | Architecture navigation     |

---

# 07 — Platform

## Purpose

Documents reusable Living Systems concepts independent of Minecraft.

Location:

```text
docs/07_Platform/
```

Documents:

| Document                   | Purpose                       |
| -------------------------- | ----------------------------- |
| LIVING_SYSTEMS_THEORY.md   | Core theoretical concepts     |
| AUTONOMOUS_AGENT_MODEL.md  | Agent design principles       |
| COLLECTIVE_INTELLIGENCE.md | Group intelligence concepts   |
| EMERGENT_SYSTEMS.md        | Emergent behavior principles  |
| REFERENCE_ARCHITECTURE.md  | General platform architecture |

---

# 08 — HiveMind Implementation

## Purpose

Documents Minecraft-specific implementation details.

Location:

```text
docs/08_HiveMind/
```

Areas:

```text
HiveMind

├── Fabric Integration
├── Entities
├── Blocks
├── Items
├── Networking
├── Rendering
├── Commands
└── Player Systems
```

---

# Development Workflow

All significant features should follow this lifecycle:

```text
Idea

↓

Discussion

↓

ADR (if required)

↓

Architecture Review

↓

Specification

↓

Implementation

↓

Testing

↓

Implementation Log

↓

Changelog
```

---

# Traceability Model

Every major system should be traceable through:

```text
Project Vision

↓

Architecture

↓

Specification

↓

Source Code

↓

Tests

↓

Historical Record
```

This ensures that important decisions are never lost.

---

# Current Development Focus

## Phase 2 — Drone Framework

Current priority:

Building the foundation of autonomous HiveMind entities.

Focus areas:

* Drone Entity Framework
* Drone Lifecycle
* Drone States
* AI Goal System
* Task Framework
* Hive Preparation

---

# Navigation Guide

## New Contributors

Start with:

1. README.md
2. PROJECT_INDEX.md
3. PROJECT_CONSTITUTION.md
4. PROJECT_OVERVIEW.md
5. SYSTEM_ARCHITECTURE.md

---

## Developers

Start with:

1. ENGINEERING_HANDBOOK.md
2. DEVELOPER_HANDBOOK.md
3. Relevant Architecture Document
4. Relevant Specification
5. Source Code

---

## AI Assistants

Start with:

1. PROJECT_BIBLE.md
2. PROJECT_INDEX.md
3. ARCHITECTURE_INDEX.md
4. Current Roadmap
5. Implementation Log

---

# Knowledge Base Principles

The Living Systems Project documentation follows these principles:

## Documentation Is Part of Development

A feature is incomplete until it is documented.

---

## Decisions Must Be Preserved

Important decisions require permanent records.

---

## Architecture Comes Before Implementation

Systems should be understood before they are built.

---

## The Repository Is the Project Memory

Important knowledge should not exist only in conversations or individual memory.

---

# Document History

| Version | Date       | Changes                      |
| ------- |------------| ---------------------------- |
| 1.0     | 2026-07-09 | Initial Knowledge Base Index |
