# HiveMind Event and Networking Architecture

**Status:** Implemented foundation

## Communication paths

HiveMind currently communicates through Fabric lifecycle and command callbacks, Minecraft entity state, and explicit client/server packets.

```text
Client input or command
        ↓
Fabric callback / packet receiver
        ↓
Server-side validation and manager/entity update
        ↓
Entity state, response packet, or client feedback
```

## Network channels

`NetworkHandler` defines the main control channels:

- `hivemind:drone_control`
- `hivemind:drone_movement`

`DebugNetworkHandler` defines debug-only channels for spawning, removing nearby drones, and teleporting. Debug traffic must remain separate from normal gameplay behavior.

## Rules

- Validate sender, ownership, and entity identity on the server before changing state.
- Keep packet payloads small and purpose-specific.
- Prefer an explicit packet or defined Fabric callback to hidden cross-system coupling.
- Treat client-side state as presentation or input; the server remains authoritative for gameplay and persistence.

## Primary code

- `src/main/java/net/sanfonic/hivemind/network/`
- `src/main/java/net/sanfonic/hivemind/control/`
- `src/main/java/net/sanfonic/hivemind/data/HiveMindData/HiveMindServerEvents.java`

