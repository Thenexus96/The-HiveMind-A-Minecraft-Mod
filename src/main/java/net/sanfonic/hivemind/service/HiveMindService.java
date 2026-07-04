package net.sanfonic.hivemind.service;

import net.minecraft.server.MinecraftServer;
import net.sanfonic.hivemind.data.DroneData.DroneTelemetryStore;
import net.sanfonic.hivemind.data.HiveMindData.HiveCodeManager;
import net.sanfonic.hivemind.data.HiveMindData.HiveMindLinkManager;

/**
 * Abstraction layer to retrieve hivemind-related managers given a MinecraftServer. This indirection
 * makes unit testing easier by allowing tests to inject mocks.
 */
public interface HiveMindService {
  HiveMindLinkManager getLinkManager(MinecraftServer server);

  DroneTelemetryStore getTelemetryStore(MinecraftServer server);

  HiveCodeManager getHiveCodeManager(MinecraftServer server);
}
