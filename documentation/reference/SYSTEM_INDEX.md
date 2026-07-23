# System Index

Quick-reference table: concept → primary code → deeper docs. For narrative,
player-readable explanations of individual systems (also the source content
for the in-game Codex), see the per-topic entries under `reference/systems/`
where one exists for a row below.

| System | Primary implementation | Documentation |
| --- | --- | --- |
| Drone entity and roles | `entity/` | [Entity Architecture](../architecture/ENTITY_ARCHITECTURE.md), [AI Architecture](../architecture/AI_ARCHITECTURE.md), [Role/Behavior deep-dive](../architecture/ROLE_BEHAVIOR_SYSTEM.md), [Drone Roles (player-facing)](systems/drone-roles.md) |
| Hive membership and resources | `data/HiveMindData/Hive*` | [Data Architecture](../architecture/DATA_ARCHITECTURE.md), [Ownership deep-dive](../architecture/OWNERSHIP_SYSTEM.md) |
| HiveCode assignment | `HiveCodeManager` | [Data Architecture](../architecture/DATA_ARCHITECTURE.md) |
| Drone linking and telemetry | `HiveMindLinkManager`, `DroneTelemetryStore` | [Data Architecture](../architecture/DATA_ARCHITECTURE.md), [Ownership deep-dive](../architecture/OWNERSHIP_SYSTEM.md) |
| Direct drone control | `control/`, `network/` | [Event and Networking Architecture](../architecture/EVENT_ARCHITECTURE.md), [Networking deep-dive](../architecture/NETWORKING.md) |
| Service layer / testable seams | `service/` | [Service Layer](../architecture/SERVICE_LAYER.md) |
| Commands | `commands/` | [Commands](../wiki/Commands.md) |
| Configuration and debug tooling | `config/`, `client/debug/` | [Configuration](../wiki/Configuration.md) |