# Entity and Content Index

Quick-reference table. For narrative, player-readable entries (also the
source content for the in-game Codex), see `reference/items/`,
`reference/blocks/`, `reference/entities/` where one exists.

## Entity

| Content | Identifier / implementation | Notes |
| --- | --- | --- |
| Drone | `DroneEntity` | Main autonomous and directly controllable entity |

## Items

| Content | Implementation | Purpose | Deep context |
| --- | --- | --- | --- |
| Drone Core | `DroneCoreItem` | Creates or deploys drones | [drone-core.md](items/drone-core.md) |
| HiveMind Access | `HiveMindAccessItem` | Grants HiveMind access | |

## Blocks

| Content | Registration | Notes |
| --- | --- | --- |
| Hive Core | `ModBlock` | Registered content; no separate HiveCore behavior architecture is documented as implemented |
| Hive Material Block | `ModBlock` | Registered content |

See `src/main/java/net/sanfonic/hivemind/entity/`, `item/`, and `block/` for the authoritative implementation.