# ADR-0004 — Cardinal Components Decision

**Project:** Living Systems Project (LSP)  
**Module:** HiveMind  
**Status:** Accepted  
**Date:** YYYY-MM-DD

---

# Context

As HiveMind development progressed, the project needed a method for storing additional data on Minecraft objects.

Potential data requirements included:

- Drone identity
- Hive association
- AI state information
- Progression data
- Relationships
- Persistent system information

A common approach within Fabric development is the use of:

```
Cardinal Components API
```

for attaching custom data to Minecraft objects.

The project evaluated whether this approach aligned with the long-term architecture of the Living Systems Project.

---

# Decision

The Living Systems Project will not use Cardinal Components as a core dependency.

HiveMind will develop internal data management systems where appropriate.

---

# Reasoning

## Maintain Architectural Ownership

HiveMind is intended to become a living systems framework.

Core systems such as:

- Entity identity
- Hive relationships
- Persistent state
- Progression

should be designed around the project's own architecture.

---

## Reduce Dependency Reliance

External dependencies introduce:

- Compatibility considerations
- Version maintenance concerns
- Architectural constraints

The project should minimize dependencies that directly influence core design.

---

## Encourage Purpose-Built Systems

The information HiveMind requires is not generic data storage.

Examples:

A Drone's data is not simply:

```
Key = Value
```

It represents:

- Identity
- Relationships
- Role
- Memory
- Purpose

The architecture should reflect that.

---

# Alternatives Considered

## Cardinal Components API

### Advantages

- Established Fabric ecosystem solution.
- Simplifies attaching data.
- Reduces initial development time.

### Reasons Not Selected

Potential drawbacks:

- Adds a core dependency.
- Encourages generic data attachment rather than system ownership.
- May limit future architectural decisions.

---

## Vanilla Persistent Data Systems

### Consideration

Using Minecraft's built-in persistence methods.

### Potential Use

May still be used where appropriate.

Examples:

- NBT storage
- Entity serialization
- World data storage

---

## Custom Data Architecture

### Selected

Develop internal systems designed specifically for HiveMind requirements.

---

# Consequences

## Positive

- Greater control over architecture.
- Better alignment with project philosophy.
- Reduced dependency requirements.
- More intentional system design.

---

## Negative

- Requires more development effort.
- Requires maintaining custom solutions.
- Some functionality must be implemented manually.

---

# Architectural Direction

Future systems should favor:

```
Purpose-Built Systems

over

Generic Data Containers
```

The goal is not to avoid all external tools.

The goal is to ensure that core identity systems remain controlled by HiveMind.

---

# Related Documents

- PROJECT_BIBLE.md
- DESIGN_PHILOSOPHY.md
- ENTITY_ARCHITECTURE.md
- HIVE_CORE.md
- SYSTEM_ARCHITECTURE.md