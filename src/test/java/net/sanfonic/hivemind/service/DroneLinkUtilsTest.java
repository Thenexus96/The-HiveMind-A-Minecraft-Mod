package net.sanfonic.hivemind.service;

import net.minecraft.server.MinecraftServer;
import net.sanfonic.hivemind.data.DroneData.DroneTelemetryStore;
import net.sanfonic.hivemind.data.HiveMindData.HiveCodeManager;
import net.sanfonic.hivemind.data.HiveMindData.HiveMindLinkManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DroneLinkUtilsTest {

    private HiveMindService originalProvider;

    @BeforeEach
    public void setup() {
        // preserve original
        originalProvider = HiveMindServiceManager.get();
    }

    @AfterEach
    public void teardown() {
        HiveMindServiceManager.setProvider(originalProvider);
    }

    @Test
    public void testLinkDrone_callsManagersAndReturnsHiveCode() {
        // Simple concrete test doubles (avoid Mockito due to final Mojang classes)
        HiveMindLinkManager testLink = new HiveMindLinkManager() {
            private boolean linked = false;
            @Override
            public void linkDroneToOwner(UUID droneUUID, UUID ownerUUID) {
                linked = true;
                super.linkDroneToOwner(droneUUID, ownerUUID);
            }
        };

        DroneTelemetryStore testTelemetry = new DroneTelemetryStore() {
            public boolean updated = false;
            @Override
            public void updateDroneData(UUID droneUUID, UUID ownerUUID, double x, double y, double z, String dimensionKey, double health, double maxHealth) {
                this.updated = true;
                super.updateDroneData(droneUUID, ownerUUID, x, y, z, dimensionKey, health, maxHealth);
            }
        };

        HiveCodeManager testCode = new HiveCodeManager() {
            @Override
            public String generateHiveCode(UUID droneUUID, UUID ownerUUID) {
                return "D-123";
            }
        };

        HiveMindService testService = new HiveMindService() {
            @Override
            public HiveMindLinkManager getLinkManager(MinecraftServer server) {
                return testLink;
            }

            @Override
            public DroneTelemetryStore getTelemetryStore(MinecraftServer server) {
                return testTelemetry;
            }

            @Override
            public HiveCodeManager getHiveCodeManager(MinecraftServer server) {
                return testCode;
            }
        };

        HiveMindServiceManager.setProvider(testService);

        UUID drone = UUID.randomUUID();
        UUID owner = UUID.randomUUID();

        String code = DroneLinkUtils.linkDroneWithService(testService, drone, owner, 1.0, 2.0, 3.0, "overworld", 10.0, 20.0);

        assertEquals("D-123", code);
        assertTrue(testLink.isDroneLinked(drone));
        assertNotNull(testTelemetry.getDroneData(drone));
    }

    @Test
    public void testGetDroneOwner_delegatesToLinkManager() {
        HiveMindLinkManager testLink = new HiveMindLinkManager() {
            @Override
            public UUID getDroneOwner(UUID droneUUID) {
                return UUID.fromString("00000000-0000-0000-0000-000000000001");
            }
        };

        HiveMindService testService = new HiveMindService() {
            @Override
            public HiveMindLinkManager getLinkManager(MinecraftServer server) {
                return testLink;
            }

            @Override
            public DroneTelemetryStore getTelemetryStore(MinecraftServer server) {
                return null;
            }

            @Override
            public HiveCodeManager getHiveCodeManager(MinecraftServer server) {
                return null;
            }
        };

        HiveMindServiceManager.setProvider(testService);

        UUID drone = UUID.randomUUID();
        UUID result = DroneLinkUtils.getDroneOwnerWithService(testService, drone);
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), result);
    }
}
