# ADR-0002 — Fabric Selection

**Project:** Living Systems Project (LSP)  
**Module:** HiveMind  
**Status:** Accepted  
**Date:** YYYY-MM-DD

---

# Context

The Living Systems Project required a Minecraft modding foundation that supported long-term development, modular architecture, and direct control over systems.

Because HiveMind is intended to become a framework for autonomous entities, AI systems, and interconnected gameplay mechanics, the selected mod loader needed to support:

- Flexible development
- Strong community support
- Lightweight architecture
- Long-term maintainability
- Access to core Minecraft systems

The project was designed around Minecraft version:

```
1.20.1
```

---

# Decision

The Living Systems Project selected:

```
Fabric
```

as the primary modding framework.

Fabric was chosen as the foundation for HiveMind development.

---

# Reasons for Selection

## Lightweight Architecture

Fabric provides a lightweight foundation that allows the project to control its own architecture rather than relying on a large abstraction layer.

This aligns with the project's philosophy:

- Modular systems
- Clear responsibilities
- Minimal unnecessary dependencies

---

## Strong Development Flexibility

Fabric provides direct access to Minecraft systems while allowing developers to create custom frameworks.

This is important for systems such as:

- Drone AI
- Hive management
- Custom entity behavior
- Future ecosystem systems

---

## Compatibility With Project Goals

The project requires flexibility rather than a predefined gameplay structure.

Fabric allows HiveMind to build:

- Custom systems
- Custom behaviors
- Custom interactions

without unnecessary restrictions.

---

# Alternatives Considered

## Forge

### Consideration

Forge provides a large ecosystem and extensive modding support.

### Reason Not Selected

While Forge is a capable platform, the project's goals favored:

- A lighter framework
- Greater architectural control
- Reduced dependency overhead

---

## NeoForge

### Consideration

NeoForge provides a modern continuation of Forge-based development.

### Reason Not Selected

The project was established around the Fabric ecosystem and Minecraft 1.20.1 development.

Changing frameworks would introduce unnecessary complexity.

---

# Consequences

## Positive

- Lightweight development foundation.
- Strong control over custom systems.
- Good alignment with modular architecture.
- Supports long-term experimentation.

---

## Negative

- Requires more custom implementation.
- Some systems may require additional development effort.
- Less reliance on large existing frameworks.

---

# Long-Term Impact

Choosing Fabric reinforces the project's architectural philosophy:

> Build the systems we need instead of depending on systems that already exist.

This decision supports the long-term goal of creating a flexible living systems framework.

---

# Related Documents

- PROJECT_CHARTER.md
- PROJECT_BIBLE.md
- SYSTEM_ARCHITECTURE.md
- DEVELOPMENT_GUIDE.md