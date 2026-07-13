# HiveMind Data Architecture

**Status:** Implemented foundation  
**Persistence model:** Minecraft NBT and `PersistentState`

## Data ownership

HiveMind keeps persistent world-level data in separate stores rather than a single generic container.

| Data | Owner | Persistent representation |
| --- | --- | --- |
| Hive membership, resources, research state | `Hive` and `HiveManager` | `PersistentState` / NBT |
| Drone-to-owner links | `HiveMindLinkManager` | `PersistentState` / NBT |
| Per-owner drone codes | `HiveCodeManager` | `PersistentState` / NBT |
| Drone telemetry | `DroneTelemetryStore` | `PersistentState` / NBT |
| Player access and membership metadata | `HiveMindData` / `PlayerHiveComponent` | Player NBT |
| Entity-local state | `DroneEntity` | Entity NBT / tracked state as applicable |

The server is authoritative. World-level stores are obtained from the server's Overworld persistent-state manager.

## Lifecycle and cleanup

`HiveMindServerEvents` coordinates server lifecycle handling and cleanup of stale linked drones, HiveCodes, and telemetry. Any new persisted data must define its owner, serialization format, load path, cleanup behavior, and compatibility plan before implementation.

## Compatibility note

`HiveMindDataManager` is retained as a backward-compatibility delegator. New work should use the dedicated managers identified above rather than extending that legacy facade.

## Primary code

- `src/main/java/net/sanfonic/hivemind/data/HiveMindData/`
- `src/main/java/net/sanfonic/hivemind/data/DroneData/DroneTelemetryStore.java`
- `src/main/java/net/sanfonic/hivemind/data/player/`

