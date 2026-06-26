package net.sanfonic.hivemind.service;

import net.minecraft.server.MinecraftServer;
import net.sanfonic.hivemind.data.DroneData.DroneTelemetryStore;
import net.sanfonic.hivemind.data.HiveMindData.HiveCodeManager;
import net.sanfonic.hivemind.data.HiveMindData.HiveMindLinkManager;

import java.util.UUID;

/**
 * Small helper utilities to perform common link/telemetry operations.
 * Extracted from DroneEntity logic so it can be unit-tested without constructing entities.
 */
public final class DroneLinkUtils {
    private DroneLinkUtils() {}

    /**
     * Link a drone to an owner and update telemetry + generate a hive code.
     * Returns the generated hive code (or null if none generated).
     */
    public static String linkDrone(MinecraftServer server, UUID droneUuid, UUID ownerUuid,
                                   double x, double y, double z, String dimensionKey,
                                   double health, double maxHealth) {
        if (server == null) return null;

        HiveMindLinkManager linkManager = HiveMindServiceManager.get().getLinkManager(server);
        if (linkManager != null) {
            linkManager.linkDroneToOwner(droneUuid, ownerUuid);

            DroneTelemetryStore telemetry = HiveMindServiceManager.get().getTelemetryStore(server);
            if (telemetry != null) {
                telemetry.updateDroneData(droneUuid, ownerUuid, x, y, z, dimensionKey, health, maxHealth);
            }
        }

        HiveCodeManager codeManager = HiveMindServiceManager.get().getHiveCodeManager(server);
        if (codeManager != null) {
            return codeManager.generateHiveCode(droneUuid, ownerUuid);
        }

        return null;
    }

    // Test-friendly counterparts that accept an explicit HiveMindService to avoid mocking MinecraftServer
    public static String linkDroneWithService(HiveMindService service, UUID droneUuid, UUID ownerUuid,
                                              double x, double y, double z, String dimensionKey,
                                              double health, double maxHealth) {
        if (service == null) return null;

        HiveMindLinkManager linkManager = service.getLinkManager(null);
        if (linkManager != null) {
            linkManager.linkDroneToOwner(droneUuid, ownerUuid);
            DroneTelemetryStore telemetry = service.getTelemetryStore(null);
            if (telemetry != null) {
                telemetry.updateDroneData(droneUuid, ownerUuid, x, y, z, dimensionKey, health, maxHealth);
            }
        }

        HiveCodeManager codeManager = service.getHiveCodeManager(null);
        if (codeManager != null) {
            return codeManager.generateHiveCode(droneUuid, ownerUuid);
        }

        return null;
    }

    public static UUID getDroneOwnerWithService(HiveMindService service, UUID droneUuid) {
        if (service == null) return null;
        HiveMindLinkManager linkManager = service.getLinkManager(null);
        if (linkManager == null) return null;
        return linkManager.getDroneOwner(droneUuid);
    }
}
