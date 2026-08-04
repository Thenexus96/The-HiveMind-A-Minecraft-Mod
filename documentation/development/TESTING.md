# Testing Guide

## Current automated coverage

The repository includes unit and integration coverage for Hive persistence, linking, telemetry, drone utility behavior, and DroneEntity integration.

| Area | Test location |
| --- | --- |
| Hive and link persistence | `src/test/java/net/sanfonic/hivemind/data/HiveMindData/` |
| Drone telemetry persistence | `src/test/java/net/sanfonic/hivemind/data/DroneData/` |
| Drone linking utilities | `src/test/java/net/sanfonic/hivemind/service/` |
| Drone entity integration | `src/test/java/net/sanfonic/hivemind/entity/` |
| Fabric integration | `src/integrationTest/java/net/sanfonic/hivemind/integration/` |

## Before merging a change

1. Run the relevant automated tests.
2. For persistence changes, verify save/load and stale-data cleanup behavior.
3. For networking or direct-control changes, verify server validation and a multiplayer path.
4. For player-facing changes, perform an in-game smoke test.
5. Update the related specification or architecture document when the system boundary or behavior changes.

Testing evidence belongs in the implementation log or pull request context; do not mark a planned behavior as verified.

