# Configuration and Debugging

`ModConfig` stores HiveMind configuration under the mod configuration directory. The current settings cover logging, drone tuning, and debug behavior.

## Normal settings

- Debug, drone-linking, and command logging switches
- Log cooldown
- Drone follow range
- Drone health
- Drone speed

## Debug-mode settings

- Overlay visibility
- Instant spawning
- Access bypass
- Unlimited drones
- Automatic linking
- Debug spawn radius

Debug mode is intended for development and troubleshooting. Do not rely on debug bypasses for normal gameplay or compatibility behavior.

## Related systems

- In-game debug commands: [Commands](Commands.md)
- Client debug controls: `src/main/java/net/sanfonic/hivemind/client/debug/`
- Debug network handlers: `src/main/java/net/sanfonic/hivemind/network/DebugNetworkHandler.java`

