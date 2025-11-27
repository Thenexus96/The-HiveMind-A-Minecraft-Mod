package net.sanfonic.hivemind.data.HiveMindData;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages HiveCode assignment for drones with PER-PLAYER counters
 * Each player has their own drone numbering D-001, D-002, D-003, etc.
 * Numbers are never reused - if D-002 dies, the next drone is D-004
 */
public class HiveCodeManager extends PersistentState {
    private static final String DATA_NAME = "hivemind_hivecodes";
    private static final String HIVECODE_PREFIX = "D-";

    // Per-player counter: ownerUUID -> next available drone number
    private final Map<UUID, Integer> playerNextDroneNumber = new HashMap<>();

    // Maps: droneUUID <-> HiveCode (permanent assignments)
    private final Map<UUID, String> droneToCode = new HashMap<>();
    private final Map<String, UUID> codeToDrone = new HashMap<>();

    // Maps: HiveCode -> ownerUUID (for quick ownership lookup)
    private final Map<String, UUID> codeToOwner = new HashMap<>();

    /**
     * Get or create the HiveCodeManager instance
     */
    public static HiveCodeManager getInstance(MinecraftServer server) {
        ServerWorld overworld = server.getWorld(World.OVERWORLD);
        PersistentStateManager stateManager = overworld.getPersistentStateManager();

        return stateManager.getOrCreate(
                HiveCodeManager::createFromNbt,
                HiveCodeManager::new,
                DATA_NAME
        );
    }

    /**
     * Create manager from saved NBT data
     */
    public static HiveCodeManager createFromNbt(NbtCompound nbt) {
        HiveCodeManager manager = new HiveCodeManager();

        // Load per-player counters
        NbtCompound countersNbt = nbt.getCompound("PlayerCounters");
        for (String key : countersNbt.getKeys()) {
            try {
                UUID ownerUUID = UUID.fromString(key);
                int nextNumber = countersNbt.getInt(key);
                manager.playerNextDroneNumber.put(ownerUUID, nextNumber);
            } catch (IllegalArgumentException e) {
                System.err.println("[HiveMind] Invalid UUID in PlayerCounters: " + key);
            }
        }

        // Load drone code mappings
        NbtList mappingsList = nbt.getList("CodeMappings", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < mappingsList.size(); i++) {
            NbtCompound mapping = mappingsList.getCompound(i);
            UUID droneUUID = mapping.getUuid("DroneUUID");
            String hiveCode = mapping.getString("HiveCode");
            UUID ownerUUID = mapping.getUuid("OwnerUUID");

            manager.droneToCode.put(droneUUID, hiveCode);
            manager.codeToDrone.put(hiveCode, droneUUID);
            manager.codeToOwner.put(hiveCode, ownerUUID);
        }
        return manager;
    }

    /**
     * Format a drone number into a HiveCode
     *
     * @param number The drone number (1, 2, 3, etc.)
     * @return Formatted HiveCode (D-001, D-002, etc.)
     */
    public static String formatHiveCode(int number) {
        return HIVECODE_PREFIX + String.format("%03d", number);
    }

    /**
     * Parse a HiveCode to get the drone number
     *
     * @param hiveCode The HiveCode string
     * @return The drone number, or -1 if invalid
     */
    public static int parseHiveCodeNumber(String hiveCode) {
        if (hiveCode == null || !hiveCode.startsWith(HIVECODE_PREFIX)) {
            return -1;
        }

        try {
            String numberPart = hiveCode.substring(HIVECODE_PREFIX.length());
            return Integer.parseInt(numberPart);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        // Save per-player counters
        NbtCompound countersNbt = new NbtCompound();
        for (Map.Entry<UUID, Integer> entry : playerNextDroneNumber.entrySet()) {
            countersNbt.putInt(entry.getKey().toString(), entry.getValue());
        }
        nbt.put("PlayerCounters", countersNbt);

        // Save drone code mappings
        NbtList mappingsList = new NbtList();
        for (Map.Entry<UUID, String> entry : droneToCode.entrySet()) {
            NbtCompound mapping = new NbtCompound();
            UUID droneUUID = entry.getKey();
            String hiveCode = entry.getValue();

            mapping.putUuid("DroneUUID", droneUUID);
            mapping.putString("HiveCode", hiveCode);
            mapping.putUuid("OwnerUUID", codeToOwner.get(hiveCode));
            mappingsList.add(mapping);
        }
        nbt.put("CodeMappings", mappingsList);
        return nbt;
    }

    /**
     * Generate a new HiveCode for a drone
     * Uses the player's personal counter
     * @param droneUUID The Drone's UUID
     * @param ownerUUID The Owner's UUID
     * @return The generated HiveCode (e.g., "D-001")
     */
    public String generateHiveCode(UUID droneUUID, UUID ownerUUID) {
        // Check if drone already has a code
        if (droneToCode.containsKey(droneUUID)) {
            return droneToCode.get(droneUUID);
        }
        // Generate new code
        int droneNumber = playerNextDroneNumber.getOrDefault(ownerUUID, 1);
        //Increment counter for next drone
        playerNextDroneNumber.put(ownerUUID, droneNumber + 1);
        // Generate code
        String hiveCode = HIVECODE_PREFIX + String.format("%03d", droneNumber);
        // Store Mappings
        droneToCode.put(droneUUID, hiveCode);
        codeToDrone.put(hiveCode, droneUUID);
        codeToOwner.put(hiveCode, ownerUUID);

        System.out.println("[HiveMind] Generated HiveCode " + hiveCode + " for player " +
                ownerUUID.toString().substring(0, 8) + " (player's drone #" + droneNumber + ")");

        markDirty();
        return hiveCode;
    }

    /**
     * Get the HiveCode for a Drone
     *
     * @param droneUUID The drone's UUID
     * @return The HiveCode, or null if not assigned
     */
    public String getHiveCode(UUID droneUUID) {
        return droneToCode.get(droneUUID);
    }

    /**
     * Get the drone from UUID from a HiveCode
     * Note: Multiple players can have D-001, so you need to filter by owner
     * @param hiveCode The HiveCode (e.g., "D-001)
     * @return The drone's UUID, or null if not found
     */
    public UUID getDroneFromCode(String hiveCode, UUID ownerUUID) {
        UUID droneUUID = codeToDrone.get(hiveCode);
        if (droneUUID != null) {
            UUID codeOwner = codeToOwner.get(hiveCode);
            if (ownerUUID.equals(codeOwner)) {
                return droneUUID;
            }
        }
        return null;
    }

    /**
     * Get the owner of a drone by HiveCode
     *
     * @param hiveCode The HiveCode
     * @return The owner's UUID, or null if not assigned
     */
    public UUID getOwnerFromCode(String hiveCode) {
        return codeToOwner.get(hiveCode);
    }

    /**
     * Check if a HiveCode is already in use by this player
     * @param hiveCode The code to check
     * @param ownerUUID The player's UUID
     * @return true if the code exists for this player
     */
    public boolean isCodeInUseByPlayer(String hiveCode, UUID ownerUUID) {
        UUID codeOwner = codeToOwner.get(hiveCode);
        return codeOwner != null && codeOwner.equals(hiveCode);
    }

    /**
     * Remove a drone's HiveCode (called when drone dies or is removed)
     * Note: The player's counter is NOT decremented - numbers are never reused
     * @param droneUUID The drone's UUID
     */
    public void removeHiveCode(UUID droneUUID) {
        String hiveCode = droneToCode.remove(droneUUID);
        if (hiveCode != null) {
            codeToDrone.remove(hiveCode);
            codeToOwner.remove(hiveCode);
            System.out.println("[HiveMind] Removed HiveCode " + hiveCode + " (number not reused)");
            markDirty();
        }
    }

    /**
     * Get all HiveCodes owned by a player
     *
     * @param ownerUUID The owner's UUID
     * @return Map of HiveCode -> DroneUUID for this player
     */
    public Map<String, UUID> getOwnerDroneCodes(UUID ownerUUID) {
        Map<String, UUID> result = new HashMap<>();
        for (Map.Entry<String, UUID> entry : codeToOwner.entrySet()) {
            if (entry.getValue().equals(ownerUUID)) {
                String code = entry.getKey();
                result.put(code, codeToDrone.get(code));
            }
        }
        return result;
    }

    /**
     * Get the next drone number for a player (fir display purposes
     *
     * @param ownerUUID The player's UUID
     * @return The next drone number that will be assigned
     */
    public int getPlayerNextDroneNumber(UUID ownerUUID) {
        return playerNextDroneNumber.getOrDefault(ownerUUID, 1);
    }

    /**
     * Cleanup codes for non-existent drones
     *
     * @param existingDroneUUIDs List of currently existing drone UUIDs
     */
    public void cleanupInvalidCodes(java.util.List<UUID> existingDroneUUIDs) {
        droneToCode.keySet().removeIf(droneUUID -> !existingDroneUUIDs.contains(droneUUID));

        // Rebuild reverse mappings
        codeToDrone.clear();
        codeToOwner.clear();
        for (Map.Entry<UUID, String> entry : droneToCode.entrySet()) {
            codeToDrone.put(entry.getValue(), entry.getKey());
            // Note: owner mapping will need to be rebuilt from HiveMindDataManager
        }

        markDirty();
    }
}
