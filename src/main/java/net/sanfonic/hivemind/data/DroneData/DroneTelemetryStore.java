package net.sanfonic.hivemind.data.DroneData;

import java.util.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

/** Stores optional telemetry (last-known position, health) for drones. */
public class DroneTelemetryStore extends PersistentState {
  private static final String DATA_NAME = "hivemind_telemetry";

  public static class DroneData {
    public UUID droneUUID;
    public UUID ownerUUID;
    public double x, y, z;
    public String dimensionKey;
    public double health;
    public double maxHealth;

    public DroneData(
        UUID droneUUID,
        UUID ownerUUID,
        double x,
        double y,
        double z,
        String dimensionKey,
        double health,
        double maxHealth) {
      this.droneUUID = droneUUID;
      this.ownerUUID = ownerUUID;
      this.x = x;
      this.y = y;
      this.z = z;
      this.dimensionKey = dimensionKey;
      this.health = health;
      this.maxHealth = maxHealth;
    }
  }

  private final Map<UUID, DroneData> droneDataMap = new HashMap<>();

  public static DroneTelemetryStore getInstance(MinecraftServer server) {
    ServerWorld overworld = server.getWorld(World.OVERWORLD);

    if (overworld == null) {
      throw new IllegalStateException("Overworld is not loaded; cannot access DroneTelemetryStore");
    }

    PersistentStateManager persistentStateManager = overworld.getPersistentStateManager();

    return persistentStateManager.getOrCreate(
        DroneTelemetryStore::createFromNbt, DroneTelemetryStore::new, DATA_NAME);
  }

  public static DroneTelemetryStore createFromNbt(NbtCompound nbt) {
    DroneTelemetryStore manager = new DroneTelemetryStore();

    NbtList list = nbt.getList("DroneData", NbtElement.COMPOUND_TYPE);
    for (int i = 0; i < list.size(); i++) {
      NbtCompound compound = list.getCompound(i);
      UUID droneUUID = compound.getUuid("DroneUUID");
      UUID ownerUUID = compound.getUuid("OwnerUUID");
      double x = compound.getDouble("X");
      double y = compound.getDouble("Y");
      double z = compound.getDouble("Z");
      String dimension = compound.getString("Dimension");
      double health = compound.getDouble("Health");
      double maxHealth = compound.getDouble("MaxHealth");

      DroneData data = new DroneData(droneUUID, ownerUUID, x, y, z, dimension, health, maxHealth);
      manager.droneDataMap.put(droneUUID, data);
    }

    return manager;
  }

  @Override
  public NbtCompound writeNbt(NbtCompound nbt) {
    NbtList list = new NbtList();
    for (DroneData data : droneDataMap.values()) {
      NbtCompound compound = new NbtCompound();
      compound.putUuid("DroneUUID", data.droneUUID);
      compound.putUuid("OwnerUUID", data.ownerUUID);
      compound.putDouble("X", data.x);
      compound.putDouble("Y", data.y);
      compound.putDouble("Z", data.z);
      compound.putString("Dimension", data.dimensionKey == null ? "" : data.dimensionKey);
      compound.putDouble("Health", data.health);
      compound.putDouble("MaxHealth", data.maxHealth);
      list.add(compound);
    }
    nbt.put("DroneData", list);
    return nbt;
  }

  public void updateDroneData(
      UUID droneUUID,
      UUID ownerUUID,
      double x,
      double y,
      double z,
      String dimensionKey,
      double health,
      double maxHealth) {
    DroneData droneData =
        new DroneData(droneUUID, ownerUUID, x, y, z, dimensionKey, health, maxHealth);
    droneDataMap.put(droneUUID, droneData);
    markDirty();
  }

  public DroneData getDroneData(UUID droneUUID) {
    return droneDataMap.get(droneUUID);
  }

  public Map<UUID, DroneData> getAllDroneData() {
    return new HashMap<>(droneDataMap);
  }

  public void cleanupNonExistentDrones(List<UUID> existingDroneUUIDs) {
    droneDataMap.keySet().removeIf(droneUUID -> !existingDroneUUIDs.contains(droneUUID));
    markDirty();
  }
}
