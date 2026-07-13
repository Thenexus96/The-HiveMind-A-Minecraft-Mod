package net.sanfonic.hivemind.service;

import net.minecraft.server.MinecraftServer;
import net.sanfonic.hivemind.data.DroneData.DroneTelemetryStore;
import net.sanfonic.hivemind.data.HiveMindData.HiveCodeManager;
import net.sanfonic.hivemind.data.HiveMindData.HiveMindLinkManager;

/**
 * Default provider for HiveMindService. Returns real manager instances.
 */
public class HiveMindServiceManager implements HiveMindService {
    private static HiveMindService provider = new HiveMindServiceManager();

    public static HiveMindService get() {
        return provider;
    }

    public static void setProvider(HiveMindService p) {
        provider = p;
    }

    @Override
    public HiveMindLinkManager getLinkManager(MinecraftServer server) {
        return HiveMindLinkManager.getInstance(server);
    }

    @Override
    public DroneTelemetryStore getTelemetryStore(MinecraftServer server) {
        return DroneTelemetryStore.getInstance(server);
    }

    @Override
    public HiveCodeManager getHiveCodeManager(MinecraftServer server) {
        return HiveCodeManager.getInstance(server);
    }
}
