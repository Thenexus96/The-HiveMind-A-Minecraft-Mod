# HiveMind Commands

Commands reflect the currently registered implementation. Permission requirements are enforced by the command code and may change during development.

## Player commands

| Command | Purpose |
| --- | --- |
| `/hivemind control <HiveCode>` | Begin direct control of a linked drone |
| `/hivemind release` | End the active drone-control session |
| `/hivemind list` | List the caller's drones |
| `/hivemind controlnearest` | Begin control of a nearby eligible drone |
| `/hivemind_link` | Link a drone in view or nearby to the caller; requires permission level 2 |
| `/hivemind_link_all` | Link eligible nearby drones; requires permission level 2 |
| `/hivemind_list` | List linked drones; requires permission level 2 |
| `/hive join` | Create or retrieve the caller's Hive |
| `/hive leave` | Leave a Hive where the caller is a non-owner member |

## Debug commands

`/debug` provides development tooling, including toggling debug mode, spawning, inspecting, assigning roles, teleporting, healing, damaging, diagnosing, and dumping drone/Hive state. These commands are intended for development and server administration, not normal player progression.

Use in-game command completion to inspect the exact syntax available in the current build.

