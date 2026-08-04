package net.sanfonic.hivemind.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.sanfonic.hivemind.data.DroneData.DroneTelemetryStore;
import net.sanfonic.hivemind.data.HiveMindData.HiveCodeManager;
import net.sanfonic.hivemind.data.HiveMindData.HiveMindLinkManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DroneLinkUtilsTest {

  private HiveMindService originalProvider;
  private HiveCodeManager testCode;
  private HiveMindLinkManager testLink;
  private DroneTelemetryStore testTelemetry;
  private HiveMindService testService;

  @BeforeEach
  public void setup() {
    // Preserve original
    originalProvider = HiveMindServiceManager.get();

    // Initialize test objects
    testCode =
        new HiveCodeManager() {
          @Override
          public String generateHiveCode(UUID droneUUID, UUID ownerUUID) {
            return "D-123";
          }
        };

    testLink = new HiveMindLinkManager();
    testTelemetry = new DroneTelemetryStore();

    testService =
        new HiveMindService() {
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
  }

  @AfterEach
  public void teardown() {
    HiveMindServiceManager.setProvider(originalProvider);
  }

  @Test
  public void testLinkDroneWithService() {
    UUID drone = UUID.randomUUID();
    UUID owner = UUID.randomUUID();

    String code =
        DroneLinkUtils.linkDroneWithService(
            testService, drone, owner, 1.0, 2.0, 3.0, "overworld", 10.0, 20.0);

    assertEquals("D-123", code);
    assertTrue(testLink.isDroneLinked(drone));
    assertNotNull(testTelemetry.getDroneData(drone));
  }

  @Test
  public void testGetDroneOwner_delegatesToLinkManager() {
    // Override testLink for this specific test
    testLink =
        new HiveMindLinkManager() {
          @Override
          public UUID getDroneOwner(UUID droneUUID) {
            return UUID.fromString("00000000-0000-0000-0000-000000000001");
          }
        };

    // Recreate testService to use the overridden testLink
    testService =
        new HiveMindService() {
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
