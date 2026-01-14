# HiveMind Debug System Usage Guide

## Overview

Your debug system now has three main components:

1. **Debug Logger** - Categorized logging with history
2. **Debug Console** - In-game log viewer
3. **Performance Monitor** - Real-time performance metrics

## How to Use Each Component

### 1. Debug Logger

The logger replaces your old `System.out.println()` calls with categorized logging:

```java
// Old way:
System.out.println("[HiveMind] Drone spawned");

// New way:
DebugLogger.info(DebugLogger.Category.DRONE_LIFECYCLE, "Drone spawned");
```

**Available Categories:**

- `DRONE_LIFECYCLE` - Spawning, death, creation
- `DRONE_AI` - AI behavior, goals, state changes
- `DRONE_CONTROL` - Player control, possession
- `NETWORKING` - Packet send/receive
- `DATA_MANAGEMENT` - NBT, persistence, data sync
- `COMMANDS` - Command execution
- `GENERAL` - Everything else

**Log Levels:**

```java
DebugLogger.debug(category, "Detailed debugging info");
DebugLogger.info(category, "General information");
DebugLogger.warn(category, "Warning message");
DebugLogger.error(category, "Error message");
DebugLogger.error(category, "Error with exception", exception);
```

### 2. Debug Console (F8)

**Opening the Console:**

- Press `F8` in-game (when debug mode is enabled)
- Or add it to your main GUI

**Features:**

- View all logs in real-time
- Filter by category (buttons on right)
- Scroll through history (mouse wheel or buttons)
- Auto-refreshes every second
- Shows up to 100 recent entries

**Example Integration in Your Code:**

```java
// In DroneEntity.java, replace your debug prints:

// Old:
System.out.println("[HiveMind] Drone linked to: " + ownerUUID);

// New:
DebugLogger.info(
    DebugLogger.Category.DRONE_LIFECYCLE,
    "Drone " + getHiveCode() + " linked to player " + ownerUUID.toString().substring(0, 8)
);

// Old:
System.out.println("[HiveMind] Found owner UUID from NBT:" + loadedUuid);

// New:
DebugLogger.debug(
    DebugLogger.Category.DATA_MANAGEMENT,
    "Loaded owner UUID from NBT: " + loadedUuid.toString().substring(0, 8)
);
```

### 3. Performance Monitor

**Toggling the Monitor:**
Add a keybind or command to toggle it:

```java
// In your keybind handler:
while (debugTogglePerformance.wasPressed()) {
    DebugPerformanceMonitor.toggle();
    client.player.sendMessage(
        Text.literal("Performance Monitor: " + 
            (DebugPerformanceMonitor.isEnabled() ? "§aON" : "§cOFF")),
        true
    );
}
```

**What It Shows:**

- FPS and frame time
- Total drones in world
- Average drone count
- Linked drones
- Controlled drones
- Memory usage

## Integration Steps

### Step 1: Replace Console Output

Go through your existing code and replace debug prints:

**In DroneEntity.java:**

```java
// Replace this:
if (config.isDroneLinkingDebugEnabled()) {
    config.droneLinkDebugLog("Drone is linked to: " + this.hiveMindOwnerUuid);
}

// With this:
DebugLogger.debug(
    DebugLogger.Category.DRONE_LIFECYCLE,
    "Drone " + getHiveCode() + " is linked to: " + 
    hiveMindOwnerUuid.toString().substring(0, 8)
);
```

**In HiveMindDataManager.java:**

```java
// Replace:
System.out.println("[HiveMind] Found " + drones.size() + " drones to restore");

// With:
DebugLogger.info(
    DebugLogger.Category.DATA_MANAGEMENT,
    "Found " + drones.size() + " drones to restore"
);
```

**In NetworkHandler.java:**

```java
// When sending packets:
DebugLogger.debug(
    DebugLogger.Category.NETWORKING,
    "Sent DroneControl packet to player " + player.getName().getString()
);

// When receiving:
DebugLogger.debug(
    DebugLogger.Category.NETWORKING,
    "Received DroneMovement packet from " + player.getName().getString()
);
```

### Step 2: Register Everything

In your `HivemindClient.java`:

```java
@Override
public void onInitializeClient() {
    // Existing registrations...
    
    // Add these:
    DebugPerformanceMonitor.register();
    
    // Update keybindings to include console opener
    DebugKeyBindings.register();
}
```

### Step 3: Add Console to Main GUI

Optionally add a button to your HiveMindMainGui to open the console:

```java
// In HiveMindMainGui.init():
if (ModConfig.getInstance().isDebugModeEnabled()) {
    adder.add(ButtonWidget.builder(
        Text.literal("Debug Console"),
        button -> this.client.setScreen(new DebugConsoleScreen(this))
    ).dimensions(0, 0, 200, 20).build());
}
```

### Step 4: Add Performance Toggle Command

In your DebugCommands.java:

```java
.then(CommandManager.literal("performance")
    .executes(context -> {
        // Toggle on server, need to notify client
        context.getSource().sendFeedback(() -> 
            Text.literal("Use F9 (or your keybind) to toggle performance monitor"),
            false
        );
        return 1;
    })
)
```

## Best Practices

### When to Use Each Log Level

**DEBUG** - Verbose, internal state changes

```java
DebugLogger.debug(Category.DRONE_AI, "Goal priority changed: 5 -> 3");
```

**INFO** - Important state changes

```java
DebugLogger.info(Category.DRONE_LIFECYCLE, "Drone D-001 spawned");
```

**WARN** - Potential issues

```java
DebugLogger.warn(Category.DATA_MANAGEMENT, "Owner UUID not found, using default");
```

**ERROR** - Actual problems

```java
DebugLogger.error(Category.NETWORKING, "Failed to send packet", exception);
```

### Performance Considerations

The logger only activates when debug mode is enabled, so there's minimal performance impact in production. However:

- Keep log messages concise
- Avoid logging in hot loops (every tick for every drone)
- Use cooldowns for repetitive logs:

```java
private long lastLogTime = 0;
private static final long LOG_COOLDOWN = 1000; // 1 second

if (System.currentTimeMillis() - lastLogTime > LOG_COOLDOWN) {
    DebugLogger.debug(Category.DRONE_AI, "Tick update");
    lastLogTime = System.currentTimeMillis();
}
```

## Testing Your Debug System

1. **Enable Debug Mode:**
   ```
   /hivemind_config debug_mode true
   ```

2. **Spawn Some Drones:**
   ```
   /hive debug spawn 5
   ```

3. **Open the Console (F8):**
    - You should see spawn logs
    - Try filtering by category
    - Scroll through the history

4. **Toggle Performance Monitor:**
    - Add the keybind and press it
    - Should see FPS, drone counts, memory usage

5. **Trigger Various Events:**
    - Link drones
    - Control a drone
    - Kill drones
    - Watch the logs populate in real-time

## Troubleshooting

**Console Won't Open:**

- Check debug mode is enabled
- Verify keybind is registered
- Check for keybind conflicts

**Logs Not Appearing:**

- Ensure debug mode is enabled in config
- Check that you're using the new logger, not println
- Verify the category filter isn't hiding your logs

**Performance Monitor Not Showing:**

- Call `DebugPerformanceMonitor.register()` in client init
- Toggle it with your keybind
- Check that HUD rendering isn't disabled

# HiveMind Debug System Usage Guide

## Overview

Your debug system now has three main components:

1. **Debug Logger** - Categorized logging with history
2. **Debug Console** - In-game log viewer
3. **Performance Monitor** - Real-time performance metrics

## How to Use Each Component

### 1. Debug Logger

The logger replaces your old `System.out.println()` calls with categorized logging:

```java
// Old way:
System.out.println("[HiveMind] Drone spawned");

// New way:
DebugLogger.info(DebugLogger.Category.DRONE_LIFECYCLE, "Drone spawned");
```

**Available Categories:**

- `DRONE_LIFECYCLE` - Spawning, death, creation
- `DRONE_AI` - AI behavior, goals, state changes
- `DRONE_CONTROL` - Player control, possession
- `NETWORKING` - Packet send/receive
- `DATA_MANAGEMENT` - NBT, persistence, data sync
- `COMMANDS` - Command execution
- `GENERAL` - Everything else

**Log Levels:**

```java
DebugLogger.debug(category, "Detailed debugging info");
DebugLogger.info(category, "General information");
DebugLogger.warn(category, "Warning message");
DebugLogger.error(category, "Error message");
DebugLogger.error(category, "Error with exception", exception);
```

### 2. Debug Console (F8)

**Opening the Console:**

- Press `F8` in-game (when debug mode is enabled)
- Or add it to your main GUI

**Features:**

- View all logs in real-time
- Filter by category (buttons on right)
- Scroll through history (mouse wheel or buttons)
- Auto-refreshes every second
- Shows up to 100 recent entries

**Example Integration in Your Code:**

```java
// In DroneEntity.java, replace your debug prints:

// Old:
System.out.println("[HiveMind] Drone linked to: " + ownerUUID);

// New:
DebugLogger.info(
    DebugLogger.Category.DRONE_LIFECYCLE,
    "Drone " + getHiveCode() + " linked to player " + ownerUUID.toString().substring(0, 8)
);

// Old:
System.out.println("[HiveMind] Found owner UUID from NBT:" + loadedUuid);

// New:
DebugLogger.debug(
    DebugLogger.Category.DATA_MANAGEMENT,
    "Loaded owner UUID from NBT: " + loadedUuid.toString().substring(0, 8)
);
```

### 3. Performance Monitor

**Toggling the Monitor:**
Add a keybind or command to toggle it:

```java
// In your keybind handler:
while (debugTogglePerformance.wasPressed()) {
    DebugPerformanceMonitor.toggle();
    client.player.sendMessage(
        Text.literal("Performance Monitor: " + 
            (DebugPerformanceMonitor.isEnabled() ? "§aON" : "§cOFF")),
        true
    );
}
```

**What It Shows:**

- FPS and frame time
- Total drones in world
- Average drone count
- Linked drones
- Controlled drones
- Memory usage

## Integration Steps

### Step 1: Replace Console Output

Go through your existing code and replace debug prints:

**In DroneEntity.java:**

```java
// Replace this:
if (config.isDroneLinkingDebugEnabled()) {
    config.droneLinkDebugLog("Drone is linked to: " + this.hiveMindOwnerUuid);
}

// With this:
DebugLogger.debug(
    DebugLogger.Category.DRONE_LIFECYCLE,
    "Drone " + getHiveCode() + " is linked to: " + 
    hiveMindOwnerUuid.toString().substring(0, 8)
);
```

**In HiveMindDataManager.java:**

```java
// Replace:
System.out.println("[HiveMind] Found " + drones.size() + " drones to restore");

// With:
DebugLogger.info(
    DebugLogger.Category.DATA_MANAGEMENT,
    "Found " + drones.size() + " drones to restore"
);
```

**In NetworkHandler.java:**

```java
// When sending packets:
DebugLogger.debug(
    DebugLogger.Category.NETWORKING,
    "Sent DroneControl packet to player " + player.getName().getString()
);

// When receiving:
DebugLogger.debug(
    DebugLogger.Category.NETWORKING,
    "Received DroneMovement packet from " + player.getName().getString()
);
```

### Step 2: Register Everything

In your `HivemindClient.java`:

```java
@Override
public void onInitializeClient() {
    // Existing registrations...
    
    // Add these:
    DebugPerformanceMonitor.register();
    
    // Update keybindings to include console opener
    DebugKeyBindings.register();
}
```

### Step 3: Add Console to Main GUI

Optionally add a button to your HiveMindMainGui to open the console:

```java
// In HiveMindMainGui.init():
if (ModConfig.getInstance().isDebugModeEnabled()) {
    adder.add(ButtonWidget.builder(
        Text.literal("Debug Console"),
        button -> this.client.setScreen(new DebugConsoleScreen(this))
    ).dimensions(0, 0, 200, 20).build());
}
```

### Step 4: Add Performance Toggle Command

In your DebugCommands.java:

```java
.then(CommandManager.literal("performance")
    .executes(context -> {
        // Toggle on server, need to notify client
        context.getSource().sendFeedback(() -> 
            Text.literal("Use F9 (or your keybind) to toggle performance monitor"),
            false
        );
        return 1;
    })
)
```

## Best Practices

### When to Use Each Log Level

**DEBUG** - Verbose, internal state changes

```java
DebugLogger.debug(Category.DRONE_AI, "Goal priority changed: 5 -> 3");
```

**INFO** - Important state changes

```java
DebugLogger.info(Category.DRONE_LIFECYCLE, "Drone D-001 spawned");
```

**WARN** - Potential issues

```java
DebugLogger.warn(Category.DATA_MANAGEMENT, "Owner UUID not found, using default");
```

**ERROR** - Actual problems

```java
DebugLogger.error(Category.NETWORKING, "Failed to send packet", exception);
```

### Performance Considerations

The logger only activates when debug mode is enabled, so there's minimal performance impact in production. However:

- Keep log messages concise
- Avoid logging in hot loops (every tick for every drone)
- Use cooldowns for repetitive logs:

```java
private long lastLogTime = 0;
private static final long LOG_COOLDOWN = 1000; // 1 second

if (System.currentTimeMillis() - lastLogTime > LOG_COOLDOWN) {
    DebugLogger.debug(Category.DRONE_AI, "Tick update");
    lastLogTime = System.currentTimeMillis();
}
```

## Testing Your Debug System

1. **Enable Debug Mode:**
   ```
   /hivemind_config debug_mode true
   ```

2. **Spawn Some Drones:**
   ```
   /hive debug spawn 5
   ```

3. **Open the Console (F8):**
    - You should see spawn logs
    - Try filtering by category
    - Scroll through the history

4. **Toggle Performance Monitor:**
    - Add the keybind and press it
    - Should see FPS, drone counts, memory usage

5. **Trigger Various Events:**
    - Link drones
    - Control a drone
    - Kill drones
    - Watch the logs populate in real-time

## Troubleshooting

**Console Won't Open:**

- Check debug mode is enabled
- Verify keybind is registered
- Check for keybind conflicts

**Logs Not Appearing:**

- Ensure debug mode is enabled in config
- Check that you're using the new logger, not println
- Verify the category filter isn't hiding your logs

**Performance Monitor Not Showing:**

- Call `DebugPerformanceMonitor.register()` in client init
- Toggle it with your keybind
- Check that HUD rendering isn't disabled