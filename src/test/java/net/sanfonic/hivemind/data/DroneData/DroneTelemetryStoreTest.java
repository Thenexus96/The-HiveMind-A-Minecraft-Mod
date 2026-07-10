package net.sanfonic.hivemind.data.DroneData;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.NbtCompound;
import org.junit.jupiter.api.Test;

public class DroneTelemetryStoreTest {

  @Test
  public void testUpdateAndPersistence() {
    DroneTelemetryStore store = new DroneTelemetryStore();
    UUID drone = UUID.randomUUID();
    UUID owner = UUID.randomUUID();

    store.updateDroneData(drone, owner, 1.0, 2.0, 3.0, "overworld", 10.0, 20.0);
    DroneTelemetryStore.DroneData data = store.getDroneData(drone);
    assertNotNull(data);
    assertEquals(owner, data.ownerUUID);
    assertEquals(1.0, data.x);

    NbtCompound nbt = new NbtCompound();
    store.writeNbt(nbt);

    DroneTelemetryStore loaded = DroneTelemetryStore.createFromNbt(nbt);
    Map<UUID, DroneTelemetryStore.DroneData> all = loaded.getAllDroneData();
    assertTrue(all.containsKey(drone));

    loaded.cleanupNonExistentDrones(java.util.List.of());
    assertTrue(loaded.getAllDroneData().isEmpty());
  }
}
