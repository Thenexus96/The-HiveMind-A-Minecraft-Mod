package net.sanfonic.hivemind.data.HiveMindData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.sanfonic.hivemind.Hivemind;
import net.sanfonic.hivemind.data.DroneData.DroneTelemetryStore;
import net.sanfonic.hivemind.entity.DroneEntity;

public class HiveMindServerEvents {
  public static void register() {
    // Register server start event
    ServerLifecycleEvents.SERVER_STARTED.register(HiveMindServerEvents::onServerStarted);

    // Register world load event
    ServerWorldEvents.LOAD.register(HiveMindServerEvents::onWorldLoad);

    // Register entity load event (when entities are loaded from chunks)
    ServerEntityWorldChangeEvents.AFTER_ENTITY_CHANGE_WORLD.register(
        HiveMindServerEvents::onEntityChangeWorld);
  }

  private static void onServerStarted(MinecraftServer server) {
    // Server started, data manager is now available
    Hivemind.LOGGER.info("Server started, data manager initialized");
  }

  private static void onWorldLoad(MinecraftServer server, ServerWorld world) {
    Hivemind.LOGGER.debug("World loaded: {}", world.getRegistryKey().getValue());
    // Restore drone connections for this world
    restoreDroneConnections(server, world);
  }

  private static void onEntityChangeWorld(
      Entity originalEntity, Entity newEntity, ServerWorld origin, ServerWorld destination) {
    // Handle drone dimension changes
    if (newEntity instanceof DroneEntity drone) {
      Hivemind.LOGGER.debug("Drone changing worlds, restoring connection");
      drone.restoreHiveMindConnection();
    }
  }

  private static void restoreDroneConnections(MinecraftServer server, ServerWorld world) {
    Hivemind.LOGGER.debug("Restoring drone connections");

    // Find all drone entities in the world and restore their connections
    List<DroneEntity> drones = new ArrayList<>();

    for (Entity entity : world.iterateEntities()) {
      if (entity instanceof DroneEntity drone) {
        drones.add(drone);
      }
    }

    Hivemind.LOGGER.debug("Found {} drones to restore", drones.size());

    // Restore connections for all drones
    for (DroneEntity drone : drones) {
      Hivemind.LOGGER.debug("Restoring drone: {}", drone.getUuid());
      drone.restoreHiveMindConnection();

      // Debug: Check if restoration worked
      if (drone.hasHiveMindOwner()) {
        String hiveCode = drone.getHiveCode();
        Hivemind.LOGGER.debug("Drone restored with HiveCode: {}", hiveCode);
      } else {
        Hivemind.LOGGER.debug("Drone restoration found no owner data");
      }
    }

    // Clean up data for non-existent drones
    if (!drones.isEmpty()) {
      HiveMindLinkManager linkManager = HiveMindLinkManager.getInstance(server);
      HiveCodeManager codeManager = HiveCodeManager.getInstance(server);
      DroneTelemetryStore telemetry = DroneTelemetryStore.getInstance(server);

      List<UUID> existingDroneUUIDs = new ArrayList<>();

      // Collect all existing drone UUIDs from all worlds
      for (ServerWorld serverWorld : server.getWorlds()) {
        for (Entity entity : serverWorld.iterateEntities()) {
          if (entity instanceof DroneEntity) {
            existingDroneUUIDs.add(entity.getUuid());
          }
        }
      }

      Hivemind.LOGGER.debug("Cleaning up data for {} existing drones", existingDroneUUIDs.size());
      if (linkManager != null) linkManager.cleanupNonExistentDrones(existingDroneUUIDs);
      if (telemetry != null) telemetry.cleanupNonExistentDrones(existingDroneUUIDs);
      codeManager.cleanupInvalidCodes(existingDroneUUIDs);
    }

    Hivemind.LOGGER.debug(
        "Restored connections for {} drones in world: {}",
        drones.size(),
        world.getRegistryKey().getValue());
  }
}
