---
id: drone-roles
title: Drone Roles
category: systems
related: [drone]
---

Roles determine how a Drone behaves on its own — what it prioritizes, how it
reacts to danger, and what it does when you're not directly commanding it.

## Details

Every Drone has exactly one role at a time. Available roles today:

- **Idle** — default behavior, minimal independent action.
- **Scout** — explores further and faster, keeps an eye on nearby players.
- **Soldier** — prioritizes combat, will engage threats and defend.

Changing a Drone's role swaps its behavior immediately — it's not a
permanent choice. Use `/hive debug setrole <drone> <role>` to change one
during testing.

## See also

- **Drone** — the entity roles are assigned to