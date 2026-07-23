# Project Context

## Identity

- **Project:** Living Systems Project (LSP)
- **Primary module:** HiveMind
- **Platform:** Minecraft 1.20.1
- **Mod loader:** Fabric
- **Status:** Active development

HiveMind is a Minecraft mod about a collective consciousness that coordinates
autonomous drone entities. It's built with two goals held simultaneously: a
finished, well-architected mod, and a structured vehicle for Nick to learn
real software development. See `documentation/VISION.md` for the full
reasoning — don't treat either goal as more important than the other.

## Architectural orientation

- The Hive defines collective goals and membership; Drones own local
  execution, state, navigation, and immediate decisions.
- Systems should have clear ownership and communicate through controlled
  interfaces or events.
- Core identity and persistent-state systems should remain purpose-built and
  owned by HiveMind; Cardinal Components is not a core dependency.
- **Known exception, not yet resolved:** this separation of concerns isn't
  actually true yet for ownership/persistence — see
  `documentation/architecture/OWNERSHIP_SYSTEM.md` for the real current state
  (four overlapping systems) before assuming the above is implemented.

## Read next

- `documentation/PROJECT_STATUS.md` — current state, always check this first
- `documentation/VISION.md` — vision and philosophy, including what's
  deliberately deferred (HiveCore extraction, monorepo split)
- `documentation/architecture/` — how systems actually work:
  `OWNERSHIP_SYSTEM.md`, `SERVICE_LAYER.md`, `ROLE_BEHAVIOR_SYSTEM.md`,
  `NETWORKING.md`