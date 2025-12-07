# Phase 2-2 Debug System Testing Checklist

## Pre-Test Setup

### 1. Verify Debug Mode Configuration

- [x] Check that `config/hivemind.json` exists
- [x] Open the config and verify these fields exist:
  ```json
  {
    "debugModeEnabled": false,
    "debugShowOverlay": true,
    "debugInstantSpawn": true,
    "debugBypassAccess": true,
    "debugUnlimitedDrones": true,
    "debugAutoLink": true,
    "debugSpawnRadius": 5
  }
  ```

### 2. Enable Debug Mode

**Method 1 - Command:**

```
/hivemind_config debug_mode true
```

**Method 2 - Direct Edit:**

- Close Minecraft
- Edit `config/hivemind.json`
- Set `"debugModeEnabled": true`
- Save and restart

---

## Test Suite 1: Debug Commands

### A. Toggle Command

```
/hive debug toggle
```

- [x] Should show "Debug Mode: ENABLED" or "DISABLED"
- [x] Check config file updates
- [x] Toggle again to verify it switches

### B. Status Command

```
/hive debug status
```

- [x] Shows all debug settings
- [x] Values match config file
- [x] Display is readable and formatted

### C. Grant Access Command

```
/hive debug grantaccess
```

- [x] Message: "✓ HiveMind access granted! (Debug)"
- [x] Can now use HiveMind features without Access Item
- [x] Check with `/hivemind_list` to verify access
- [x] If already had access, should show warning

### D. Spawn Commands

**Single Drone:**

```
/hive debug spawn
```

- [ ] Spawns 1 drone near player
- [ ] Shows "✓ Spawned 1 debug drone(s)"
- [ ] Drone appears with particles
- [ ] Sound plays (beacon power select)
- [ ] Drone is auto-linked (if `debugAutoLink` is true)
- [ ] Drone has a HiveCode (D-001, D-002, etc.)

**Multiple Drones:**

```
/hive debug spawn 5
```

- [ ] Spawns 5 drones in circle around player
- [ ] Each at ~5 blocks away (debugSpawnRadius)
- [ ] All spawn successfully
- [ ] All have unique HiveCodes
- [ ] Shows "✓ Spawned 5 debug drone(s)"

**Maximum Test:**

```
/hive debug spawn 10
```

- [ ] Spawns 10 drones (max allowed)
- [ ] No crashes or errors
- [ ] All drones are functional

### E. Drone Info Command

**First, target a drone:**

- Look at a drone
- Note its HiveCode (e.g., D-001)

```
/hive debug info @e[type=hivemind:drone,limit=1,sort=nearest]
```

- [ ] Shows "=== Drone Debug Info ==="
- [ ] Displays HiveCode in aqua color
- [ ] Shows full UUID
- [ ] Shows Health (e.g., 40.0/40.0)
- [ ] Shows Role (e.g., "Idle")
- [ ] Shows Position (x, y, z coordinates)
- [ ] Shows Owner UUID (or "None")
- [ ] Shows "AI Paused: false"

### F. Role Change Command

```
/hive debug setrole @e[type=hivemind:drone,limit=1,sort=nearest] scout
```

- [ ] Message: "✓ Changed drone role from Idle to Scout"
- [ ] Drone's role actually changes (check with info command)
- [ ] Drone behavior changes (scouts wander more)

**Try other roles:**

```
/hive debug setrole @e[type=hivemind:drone,limit=1,sort=nearest] soldier
/hive debug setrole @e[type=hivemind:drone,limit=1,sort=nearest] worker
/hive debug setrole @e[type=hivemind:drone,limit=1,sort=nearest] guard
```

- [ ] Each role change works
- [ ] No errors in console

### G. Teleport Commands

**Teleport Drone to Player:**

```
/hive debug teleport tome @e[type=hivemind:drone,limit=1,sort=nearest]
```

- [ ] Drone appears in front of you
- [ ] Purple portal particles at old position
- [ ] Purple portal particles at new position
- [ ] Enderman teleport sound
- [ ] Message shows HiveCode

**Teleport Player to Drone:**

```
/hive debug teleport todrone @e[type=hivemind:drone,limit=1,sort=nearest]
```

- [ ] You teleport to drone's location
- [ ] Portal particles at both locations
- [ ] Teleport sound plays
- [ ] Message confirms teleport

**Teleport Nearest Drone:**

```
/hive debug teleport nearest
```

- [ ] Finds nearest drone
- [ ] Teleports it to you
- [ ] Proper particles and sound
- [ ] Works even if you're not looking at a drone

### H. Health Modification Commands

**Heal Drone:**

```
/hive debug heal @e[type=hivemind:drone,limit=1,sort=nearest]
```

- [ ] Drone heals to full health (40/40)
- [ ] Pink heart particles appear
- [ ] Level-up sound plays
- [ ] Message confirms healing

**Damage Drone:**

```
/hive debug damage @e[type=hivemind:drone,limit=1,sort=nearest] 10
```

- [ ] Drone takes 10 damage
- [ ] Red damage indicator particles
- [ ] Message shows damage dealt
- [ ] Health decreases (verify with info command)

**Kill Test:**

```
/hive debug damage @e[type=hivemind:drone,limit=1,sort=nearest] 50
```

- [ ] Drone dies (over 40 damage)
- [ ] Death animation plays
- [ ] Drone entity removed
- [ ] HiveCode removed from system

### I. Kill Commands

**Kill All (default radius):**

```
/hive debug killall
```

- [ ] All drones within 500 blocks removed
- [ ] Shows count: "✗ Removed X drone(s)"
- [ ] Poof particles at each drone location
- [ ] No drones remain nearby

**Kill All with Radius:**

```
/hive debug spawn 3
/hive debug killall 10
```

- [ ] Only drones within 10 blocks removed
- [ ] Drones further away remain
- [ ] Count is accurate

---

## Test Suite 2: Debug Keybindings

### Setup

- [ ] Debug mode is enabled
- [ ] Spawn some drones to test with

### A. Toggle Overlay (F3 - or your configured key)

**Press the overlay keybind:**

- [ ] Message in action bar: "Debug Overlay: §aON"
- [ ] Info appears above drones:
    - HiveCode (gold, bold)
    - Health with color (green/yellow/red)
    - Role (yellow)
    - Owner (green if yours, gray if none)
    - Distance in meters
    - "AI PAUSED" if applicable
    - "CONTROLLED" if being controlled

**Press again:**

- [ ] Message: "Debug Overlay: §cOFF"
- [ ] Info disappears from above drones

**Test with multiple drones:**

- [ ] Each drone shows its own info
- [ ] Info is readable from ~16 blocks away
- [ ] Info scales with distance
- [ ] No performance issues with 5+ drones

### B. Spawn Drone (Numpad +)

**Press Numpad +:**

- [ ] Message: "§aSpawning debug drone..."
- [ ] One drone spawns nearby
- [ ] Works without typing commands

**Hold and rapid press:**

- [ ] Multiple drones spawn
- [ ] No crashes or lag spikes

### C. Kill Nearby (Numpad -)

**Press Numpad -:**

- [ ] Message: "§cKilling nearby drones..."
- [ ] Drones within 10 blocks removed
- [ ] Particles appear

### D. Teleport Nearest (Numpad *)

**Press Numpad *:**

- [ ] Message: "§bTeleporting nearest drone..."
- [ ] Closest drone teleports to you
- [ ] Portal effects and sound

---

## Test Suite 3: Debug Overlay Visual Tests

### Enable Overlay

```
/hive debug toggle  (if needed)
```

Press overlay keybind (F3)

### A. Different Drone States

**Unlinked Drone:**

- [ ] Spawn with `/summon hivemind:drone`
- [ ] Shows "Owner: §cNone"
- [ ] HiveCode is gray or shows "???"

**Linked Drone:**

- [ ] Spawn with debug spawn command
- [ ] Shows "Owner: §a<YourName>"
- [ ] HiveCode is gold

**Low Health Drone:**

```
/hive debug spawn
/hive debug damage @e[type=hivemind:drone,limit=1,sort=nearest] 30
```

- [ ] Health shows red (10/40 HP)

**Medium Health:**

```
/hive debug heal @e[type=hivemind:drone,limit=1,sort=nearest]
/hive debug damage @e[type=hivemind:drone,limit=1,sort=nearest] 20
```

- [ ] Health shows yellow (20/40 HP)

**Full Health:**

- [ ] Health shows green (40/40 HP)

### B. Distance Testing

- [ ] Walk away from drone
- [ ] Info remains visible up to ~64 blocks
- [ ] Info scales smaller as distance increases
- [ ] Disappears beyond render distance

### C. Multiple Drones

```
/hive debug spawn 10
```

- [ ] All 10 drones show their info
- [ ] No overlapping text issues
- [ ] No performance problems
- [ ] Each shows unique HiveCode

---

## Test Suite 4: Config File Integration

### A. Config Persistence

```
/hivemind_config debug_mode true
```

- [ ] Check `config/hivemind.json`
- [ ] `debugModeEnabled` is now true
- [ ] Restart Minecraft
- [ ] Debug mode still enabled

### B. Individual Debug Settings

```
/hivemind_config drone_debug true
/hivemind_config command_debug true
```

- [ ] Settings save to config
- [ ] Logs appear for drone events
- [ ] Logs appear for commands

### C. Debug Spawn Radius

```
/hivemind_config debug_mode true
```

Edit config: `"debugSpawnRadius": 10`

```
/hive debug spawn 5
```

- [ ] Drones spawn ~10 blocks away
- [ ] Circle pattern maintained

---

## Test Suite 5: Error Handling

### A. Commands Without Debug Mode

**Disable debug mode:**

```
/hivemind_config debug_mode false
```

**Try debug commands:**

```
/hive debug spawn
```

- [ ] Should show error or warning
- [ ] Doesn't crash
- [ ] Clear message that debug mode is disabled

### B. Invalid Arguments

```
/hive debug spawn 999
```

- [ ] Rejects (max is 10)
- [ ] Shows helpful error

```
/hive debug setrole @e[type=hivemind:drone,limit=1] invalidrole
```

- [ ] Falls back to IDLE role
- [ ] Doesn't crash

### C. No Drones Present

```
/hive debug killall
/hive debug teleport nearest
```

- [ ] Shows "No drones found"
- [ ] Doesn't crash or error

### D. Network Packet Tests

**On multiplayer/LAN:**

- [ ] Client keybinds send packets to server
- [ ] Server executes commands
- [ ] Results sync back to client
- [ ] No packet errors in console

---

## Test Suite 6: Integration Tests

### A. Debug + Normal Gameplay

**With debug mode ON:**

- [ ] Normal drone spawning still works
- [ ] `/hivemind_link` command works
- [ ] `/hivemind_list` shows all drones
- [ ] Drone control works
- [ ] No conflicts

### B. HiveCode System

```
/hive debug spawn 3
/hivemind_list
```

- [ ] Debug drones show in list
- [ ] HiveCodes are sequential (D-001, D-002, D-003)
- [ ] Each drone has unique code
- [ ] Codes persist after restart

### C. Multiple Players (if testing on server)

**Player 1:**

```
/hive debug spawn 3
```

**Player 2:**

```
/hive debug spawn 3
```

- [ ] Player 1 has D-001, D-002, D-003
- [ ] Player 2 has D-001, D-002, D-003 (their own set)
- [ ] No HiveCode conflicts
- [ ] Each player sees only their drones in `/hivemind_list`

---

## Performance Tests

### A. Spawn Stress Test

```
/hive debug spawn 10
/hive debug spawn 10
/hive debug spawn 10
```

- [ ] 30 drones in world
- [ ] FPS remains playable (>30)
- [ ] No memory leaks
- [ ] Overlay still works
- [ ] Commands still responsive

### B. Rapid Command Spam

**Rapidly execute:**

```
/hive debug spawn
/hive debug killall
/hive debug spawn
/hive debug killall
```

(Repeat 10 times)

- [ ] No crashes
- [ ] No duplicate drones
- [ ] No orphaned data
- [ ] Memory doesn't spike

### C. Long-Running Session

- [ ] Enable debug overlay
- [ ] Spawn 10 drones
- [ ] Play for 10+ minutes
- [ ] No memory increase
- [ ] No FPS degradation
- [ ] No console spam

---

## Bug Checks

### Common Issues to Look For:

- [ ] Console errors when using debug commands
- [ ] Drones spawning at 0,0,0
- [ ] HiveCodes not showing
- [ ] Overlay text overlapping
- [ ] Keybinds not responding
- [ ] Config not saving
- [ ] Drones not auto-linking
- [ ] Memory leaks with overlay enabled
- [ ] Packet spam in console
- [ ] Debug mode not persisting

---

## Final Verification

### A. Clean State Test

1. [ ] Delete `config/hivemind.json`
2. [ ] Restart Minecraft
3. [ ] Enable debug mode
4. [ ] All features work from scratch

### B. Disable Debug Mode

```
/hivemind_config debug_mode false
```

- [ ] Overlay disappears
- [ ] Keybinds don't work
- [ ] Commands show disabled message
- [ ] No errors in console
- [ ] Normal gameplay unaffected

### C. Documentation Check

- [ ] All commands are documented
- [ ] Keybinds are explained
- [ ] Config options are clear
- [ ] Players can easily enable/disable

---

## Test Results Summary

**Date Tested:** _______________

**Minecraft Version:** 1.20.1

**Fabric Version:** _______________

**Mod Version:** _______________

### Pass/Fail Count:

- Commands: ____ / ____
- Keybindings: ____ / ____
- Overlay: ____ / ____
- Config: ____ / ____
- Integration: ____ / ____
- Performance: ____ / ____

### Critical Issues Found:

1.
2.
3.

### Minor Issues Found:

1.
2.
3.

### Notes:

---

**Phase 2-2 Testing Status:** ⬜ Not Started | ⬜ In Progress | ⬜ Completed | ⬜ Issues Found