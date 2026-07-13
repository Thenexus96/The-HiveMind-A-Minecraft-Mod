# Entity and Content Index

## Entity

| Content | Identifier / implementation | Notes |
| --- | --- | --- |
| Drone | `DroneEntity` | Main autonomous and directly controllable entity |

## Items

| Content | Implementation | Purpose |
| --- | --- | --- |
| Drone Core | `DroneCoreItem` | Creates or deploys drones |
| HiveMind Access | `HiveMindAccessItem` | Grants HiveMind access |

## Blocks

| Content | Registration | Notes |
| --- | --- | --- |
| Hive Core | `ModBlock` | Registered content; no separate HiveCore behavior architecture is documented as implemented |
| Hive Material Block | `ModBlock` | Registered content |

See `src/main/java/net/sanfonic/hivemind/entity/`, `item/`, and `block/` for the authoritative implementation.

