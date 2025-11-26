package net.sanfonic.hivemind.data.HiveMindData;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.sanfonic.hivemind.entity.DroneEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HiveMindServerEvents {
    public static void register() {
        // Register server start event
        ServerLifecycleEvents.SERVER_STARTED.register(HiveMindServerEvents::onServerStarted);

        // Register world load event
        ServerWorldEvents.LOAD.register(HiveMindServerEvents::onWorldLoad);

        // Register entity load event (when entities are loaded from chunks)
        ServerEntityWorldChangeEvents.AFTER_ENTITY_CHANGE_WORLD.register(HiveMindServerEvents::onEntityChangeWorld);
    }

    private static void onServerStarted(MinecraftServer server) {
        // Server started, data manager is now available
        System.out.println("[HiveMind] Server started, data manager initialized");
    }

    private static void onWorldLoad(MinecraftServer server, ServerWorld world) {
        System.out.println("[HiveMind] World loaded: " + world.getRegistryKey().getValue());
        // Restore drone connections for this world
        restoreDroneConnections(server, world);
    }

    private static void onEntityChangeWorld(Entity originalEntity, Entity newEntity,
                                            ServerWorld origin, ServerWorld destination) {
        // Handle drone dimension changes
        if (newEntity instanceof DroneEntity drone) {
            System.out.println("[HiveMind] Drone changing worlds, restoring connection...");
            drone.restoreHiveMindConnection();
        }
    }

    private static void restoreDroneConnections(MinecraftServer server, ServerWorld world) {
        System.out.println("[HiveMind] === Restoring Drone Connections ===");

        // Find all drone entities in the world and restore their connections
        List<DroneEntity> drones = new ArrayList<>();

        for (Entity entity : world.iterateEntities()) {
            if (entity instanceof DroneEntity drone) {
                drones.add(drone);
            }
        }

        System.out.println("[HiveMind] Found " + drones.size() + " drones to restore");

        // Restore connections for all drones
        for (DroneEntity drone : drones) {
            System.out.println("[HiveMind] Restoring drone: " + drone.getUuid());
            drone.restoreHiveMindConnection();

            // Debug: Check if restoration worked
            if (drone.hasHiveMindOwner()) {
                String hiveCode = drone.getHiveCode();
                System.out.println("[HiveMind] ✓ Drone restored with HiveCode: " + hiveCode);
            } else {
                System.out.println("[HiveMind] ✗ Drone restoration failed!");
            }
        }

        // Clean up data for non-existent drones
        if (!drones.isEmpty()) {
            HiveMindDataManager dataManager = HiveMindDataManager.getInstance(server);
            HiveCodeManager codeManager = HiveCodeManager.getInstance(server);

            List<UUID> existingDroneUUIDs = new ArrayList<>();

            // Collect all existing drone UUIDs from all worlds
            for (ServerWorld serverWorld : server.getWorlds()) {
                for (Entity entity : serverWorld.iterateEntities()) {
                    if (entity instanceof DroneEntity) {
                        existingDroneUUIDs.add(entity.getUuid());
                    }
                }
            }

            System.out.println("[HiveMind] Cleaning up data for " + existingDroneUUIDs.size() + " existing drones");
            dataManager.cleanupNonExistentDrones(existingDroneUUIDs);
            codeManager.cleanupInvalidCodes(existingDroneUUIDs);
        }

        System.out.println("[HiveMind] Restored connections for " + drones.size() +
                " drones in world: " + world.getRegistryKey().getValue());
    }
}
