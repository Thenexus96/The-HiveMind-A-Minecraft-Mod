package net.sanfonic.hivemind.data.HiveMindData;

import java.util.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

/** Manages drone <-> owner link mappings and persists them separately from telemetry. */
public class HiveMindLinkManager extends PersistentState {
  private static final String DATA_NAME = "hivemind_links";

  // droneUUID -> ownerUUID
  private final Map<UUID, UUID> droneOwnerMap = new HashMap<>();
  // ownerUUID -> List<droneUUID>
  private final Map<UUID, List<UUID>> ownerDroneMap = new HashMap<>();

  public static HiveMindLinkManager getInstance(MinecraftServer server) {
    ServerWorld overworld = server.getWorld(World.OVERWORLD);
    if (overworld == null) {
      throw new IllegalStateException("Overworld is not loaded; cannot access HiveMindLinkManager");
    }

    PersistentStateManager persistentStateManager = overworld.getPersistentStateManager();

    return persistentStateManager.getOrCreate(
        HiveMindLinkManager::createFromNbt, HiveMindLinkManager::new, DATA_NAME);
  }

  public static HiveMindLinkManager createFromNbt(NbtCompound nbt) {
    HiveMindLinkManager manager = new HiveMindLinkManager();

    // Load mappings
    NbtList mappingList = nbt.getList("LinkMappings", NbtElement.COMPOUND_TYPE);
    for (int i = 0; i < mappingList.size(); i++) {
      NbtCompound mapping = mappingList.getCompound(i);
      UUID droneUUID = mapping.getUuid("DroneUUID");
      UUID ownerUUID = mapping.getUuid("OwnerUUID");
      manager.droneOwnerMap.put(droneUUID, ownerUUID);
      manager.ownerDroneMap.computeIfAbsent(ownerUUID, k -> new ArrayList<>()).add(droneUUID);
    }

    return manager;
  }

  @Override
  public NbtCompound writeNbt(NbtCompound nbt) {
    NbtList mappingList = new NbtList();
    for (Map.Entry<UUID, UUID> entry : droneOwnerMap.entrySet()) {
      NbtCompound mapping = new NbtCompound();
      mapping.putUuid("DroneUUID", entry.getKey());
      mapping.putUuid("OwnerUUID", entry.getValue());
      mappingList.add(mapping);
    }
    nbt.put("LinkMappings", mappingList);
    return nbt;
  }

  // Public APIs
  public void linkDroneToOwner(UUID droneUUID, UUID ownerUUID) {
    droneOwnerMap.put(droneUUID, ownerUUID);
    ownerDroneMap.computeIfAbsent(ownerUUID, k -> new ArrayList<>()).add(droneUUID);
    markDirty();
  }

  public void unlinkDrone(UUID droneUUID) {
    UUID ownerUUID = droneOwnerMap.remove(droneUUID);
    if (ownerUUID != null) {
      List<UUID> ownerDrones = ownerDroneMap.get(ownerUUID);
      if (ownerDrones != null) {
        ownerDrones.remove(droneUUID);
        if (ownerDrones.isEmpty()) {
          ownerDroneMap.remove(ownerUUID);
        }
      }
    }
    markDirty();
  }

  public UUID getDroneOwner(UUID droneUUID) {
    return droneOwnerMap.get(droneUUID);
  }

  public List<UUID> getOwnerDrones(UUID ownerUUID) {
    return ownerDroneMap.getOrDefault(ownerUUID, Collections.emptyList());
  }

  public boolean isDroneLinked(UUID droneUUID) {
    return droneOwnerMap.containsKey(droneUUID);
  }

  public void cleanupNonExistentDrones(List<UUID> existingDroneUUIDs) {
    droneOwnerMap.keySet().removeIf(droneUUID -> !existingDroneUUIDs.contains(droneUUID));

    // Clean up owner lists
    for (List<UUID> droneList : ownerDroneMap.values()) {
      droneList.removeIf(droneUUID -> !existingDroneUUIDs.contains(droneUUID));
    }
    ownerDroneMap.entrySet().removeIf(entry -> entry.getValue().isEmpty());

    markDirty();
  }
}
