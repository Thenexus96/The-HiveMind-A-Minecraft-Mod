# Project Context

## Identity

- **Project:** Living Systems Project (LSP)
- **Primary module:** HiveMind
- **Platform:** Minecraft 1.20.1
- **Mod loader:** Fabric
- **Status:** Active development

HiveMind is a Minecraft mod about a collective consciousness that coordinates autonomous drone entities. Its goal is to create living, interconnected systems rather than isolated mechanics.

## Architectural orientation

- The Hive defines collective goals and global strategy.
- Drones own local execution, state, navigation, and immediate decisions.
- Systems should have clear ownership and communicate through controlled interfaces or events.
- Core identity and persistent-state systems should remain purpose-built and owned by HiveMind; Cardinal Components is not a core dependency.

## Read next

- `documentation/PROJECT_BIBLE.md` — vision and philosophy
- `documentation/00_Project/PROJECT_OVERVIEW.md` — project and module overview
- `documentation/architecture/SYSTEM_ARCHITECTURE.md` — system boundaries and communication
- `documentation/architecture/ENTITY_ARCHITECTURE.md` — Hive and Drone responsibilities

