package net.sanfonic.hivemind.data.HiveMindData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

/**
 * Represents a player's hive: owner UUID, members, resources, and research state. Immutable data
 * class persisted to NBT.
 */
public class Hive {

  private final UUID id;
  private final UUID ownerUuid;
  private final Set<UUID> members;
  private final Map<String, Integer> resources;
  private final NbtCompound researchState;

  public Hive(UUID id, UUID ownerUuid) {
    this.id = id;
    this.ownerUuid = ownerUuid;
    this.members = new HashSet<>();
    this.members.add(ownerUuid);
    this.resources = new HashMap<>();
    this.researchState = new NbtCompound();
  }

  public UUID getId() {
    return id;
  }

  public UUID getOwnerUuid() {
    return ownerUuid;
  }

  public Set<UUID> getMembers() {
    return new HashSet<>(members);
  }

  public boolean isMember(UUID uuid) {
    return members.contains(uuid);
  }

  public boolean addMember(UUID uuid) {
    return members.add(uuid);
  }

  public boolean removeMember(UUID uuid) {
    if (uuid.equals(ownerUuid)) {
      return false; // Cannot remove owner
    }
    return members.remove(uuid);
  }

  public Map<String, Integer> getResources() {
    return new HashMap<>(resources);
  }

  public int getResourceValue(String resourceType) {
    return resources.getOrDefault(resourceType, 0);
  }

  public void setResourceValue(String resourceType, int value) {
    if (value <= 0) {
      resources.remove(resourceType);
    } else {
      resources.put(resourceType, value);
    }
  }

  public void addResource(String resourceType, int amount) {
    int current = getResourceValue(resourceType);
    setResourceValue(resourceType, current + amount);
  }

  public NbtCompound getResearchState() {
    return researchState.copy();
  }

  public void setResearchState(String nodeId, NbtCompound state) {
    researchState.put(nodeId, state);
  }

  public boolean hasResearchNode(String nodeId) {
    return researchState.contains(nodeId);
  }

  // NBT Serialization
  public NbtCompound toNbt() {
    NbtCompound nbt = new NbtCompound();
    nbt.putUuid("id", id);
    nbt.putUuid("ownerUuid", ownerUuid);

    // Serialize members
    NbtList membersList = new NbtList();
    for (UUID member : members) {
      NbtCompound memberNbt = new NbtCompound();
      memberNbt.putUuid("uuid", member);
      membersList.add(memberNbt);
    }
    nbt.put("members", membersList);

    // Serialize resources
    NbtCompound resourcesNbt = new NbtCompound();
    for (Map.Entry<String, Integer> entry : resources.entrySet()) {
      resourcesNbt.putInt(entry.getKey(), entry.getValue());
    }
    nbt.put("resources", resourcesNbt);

    // Serialize research state
    nbt.put("researchState", researchState.copy());

    return nbt;
  }

  public static Hive fromNbt(NbtCompound nbt) {
    UUID id = nbt.getUuid("id");
    UUID ownerUuid = nbt.getUuid("ownerUuid");

    Hive hive = new Hive(id, ownerUuid);

    // Deserialize members
    if (nbt.contains("members")) {
      NbtList membersList = nbt.getList("members", NbtElement.COMPOUND_TYPE);
      for (int i = 0; i < membersList.size(); i++) {
        NbtCompound memberNbt = membersList.getCompound(i);
        UUID memberUuid = memberNbt.getUuid("uuid");
        if (!memberUuid.equals(ownerUuid)) {
          hive.addMember(memberUuid);
        }
      }
    }

    // Deserialize resources
    if (nbt.contains("resources")) {
      NbtCompound resourcesNbt = nbt.getCompound("resources");
      for (String key : resourcesNbt.getKeys()) {
        hive.resources.put(key, resourcesNbt.getInt(key));
      }
    }

    // Deserialize research state
    if (nbt.contains("researchState")) {
      hive.researchState.copyFrom(nbt.getCompound("researchState"));
    }

    return hive;
  }

  @Override
  public String toString() {
    return String.format(
        "Hive{id=%s, owner=%s, members=%d, resources=%d}",
        id, ownerUuid, members.size(), resources.size());
  }
}
