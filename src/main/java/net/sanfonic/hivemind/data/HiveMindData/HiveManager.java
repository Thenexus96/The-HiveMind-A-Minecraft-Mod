package net.sanfonic.hivemind.data.HiveMindData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.sanfonic.hivemind.Hivemind;

/**
 * Server-side singleton managing all player hives. Persists hive data to world NBT. Access via
 * HiveManager.getInstance(server).
 */
public class HiveManager extends PersistentState {

  private static final String ID = "hivemind_hive_manager";
  private final Map<UUID, Hive> hives = new HashMap<>();

  public HiveManager() {
    super();
  }

  public HiveManager(NbtCompound nbt) {
    this();
    if (nbt != null && nbt.contains("hives")) {
      NbtList hivesList = nbt.getList("hives", NbtElement.COMPOUND_TYPE);
      for (int i = 0; i < hivesList.size(); i++) {
        Hive hive = Hive.fromNbt(hivesList.getCompound(i));
        this.hives.put(hive.getId(), hive);
      }
      if (Hivemind.LOGGER != null) {
        Hivemind.LOGGER.debug("Loaded {} hives from NBT", this.hives.size());
      }
    }
  }

  public static HiveManager getInstance(MinecraftServer server) {
    if (server == null) {
      return new HiveManager();
    }
    return server
        .getWorld(net.minecraft.world.World.OVERWORLD)
        .getPersistentStateManager()
        .getOrCreate(
            nbt -> {
              if (nbt == null) {
                return new HiveManager();
              }
              return new HiveManager(nbt);
            },
            HiveManager::new,
            ID);
  }

  /**
   * Create or get a hive for a player (owner). If the player already owns a hive, returns the
   * existing one.
   */
  public Hive getOrCreateHive(UUID ownerUuid) {
    // Check if this player already owns a hive
    for (Hive hive : hives.values()) {
      if (hive.getOwnerUuid().equals(ownerUuid)) {
        return hive;
      }
    }

    // Create new hive
    UUID hiveId = UUID.randomUUID();
    Hive hive = new Hive(hiveId, ownerUuid);
    hives.put(hiveId, hive);

    markDirty();
    if (Hivemind.LOGGER != null) {
      Hivemind.LOGGER.debug("Created new hive {} for owner {}", hiveId, ownerUuid);
    }

    return hive;
  }

  /** Get a hive by ID */
  public Hive getHive(UUID hiveId) {
    return hives.get(hiveId);
  }

  /** Get a player's hive if they own one */
  public Hive getHiveByOwner(UUID ownerUuid) {
    for (Hive hive : hives.values()) {
      if (hive.getOwnerUuid().equals(ownerUuid)) {
        return hive;
      }
    }
    return null;
  }

  /** Get all hives */
  public Map<UUID, Hive> getAllHives() {
    return new HashMap<>(hives);
  }

  /**
   * Add a member to a hive. Returns true if successful, false if member already in hive or hive not
   * found.
   */
  public boolean addMemberToHive(UUID hiveId, UUID memberUuid) {
    Hive hive = hives.get(hiveId);
    if (hive == null) {
      return false;
    }

    boolean added = hive.addMember(memberUuid);
    if (added) {
      markDirty();
      if (Hivemind.LOGGER != null) {
        Hivemind.LOGGER.debug("Added member {} to hive {}", memberUuid, hiveId);
      }
    }
    return added;
  }

  /**
   * Remove a member from a hive. Returns true if successful, false if member is owner or hive not
   * found.
   */
  public boolean removeMemberFromHive(UUID hiveId, UUID memberUuid) {
    Hive hive = hives.get(hiveId);
    if (hive == null) {
      return false;
    }

    boolean removed = hive.removeMember(memberUuid);
    if (removed) {
      markDirty();
      if (Hivemind.LOGGER != null) {
        Hivemind.LOGGER.debug("Removed member {} from hive {}", memberUuid, hiveId);
      }
    }
    return removed;
  }

  /** Add resources to a hive */
  public void addResourcesToHive(UUID hiveId, String resourceType, int amount) {
    Hive hive = hives.get(hiveId);
    if (hive != null) {
      hive.addResource(resourceType, amount);
      markDirty();
      if (Hivemind.LOGGER != null) {
        Hivemind.LOGGER.debug("Added {} {} to hive {}", amount, resourceType, hiveId);
      }
    }
  }

  // NBT Serialization
  @Override
  public NbtCompound writeNbt(NbtCompound nbt) {
    NbtList hivesList = new NbtList();
    for (Hive hive : hives.values()) {
      hivesList.add(hive.toNbt());
    }
    nbt.put("hives", hivesList);
    return nbt;
  }
}
