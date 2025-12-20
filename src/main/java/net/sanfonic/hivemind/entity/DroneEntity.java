package net.sanfonic.hivemind.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.sanfonic.hivemind.client.DroneClientHandler;
import net.sanfonic.hivemind.config.ModConfig;
import net.sanfonic.hivemind.data.HiveMindData.HiveCodeManager;
import net.sanfonic.hivemind.data.HiveMindData.HiveMindDataManager;
import net.sanfonic.hivemind.entity.custom.goal.FollowHiveMindPlayerGoal;
import net.sanfonic.hivemind.entity.custom.role.DroneRole;
import net.sanfonic.hivemind.entity.custom.role.DroneRoleBehavior;
import net.sanfonic.hivemind.entity.custom.role.RoleRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.UUID;

public class DroneEntity extends PathAwareEntity {
    // Tracked data for syncing linked status to client
    private static final TrackedData<Boolean> IS_LINKED =
            DataTracker.registerData(DroneEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<String> OWNER_NAME =
            DataTracker.registerData(DroneEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final Logger log = LoggerFactory.getLogger(DroneEntity.class);
    private DroneRole currentRole = DroneRole.IDLE;
    private DroneRoleBehavior roleBehavior = RoleRegistry.getBehavior(DroneRole.IDLE);
    private boolean aiControlPaused = false;
    private GoalSelector savedGoals;
    private GoalSelector savedTargetGoals;
    private static final TrackedData<Float> DRONE_YAW =
            DataTracker.registerData(DroneEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> DRONE_PITCH =
            DataTracker.registerData(DroneEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<String> HIVE_CODE =
            DataTracker.registerData(DroneEntity.class, TrackedDataHandlerRegistry.STRING);

    // Getters method for tracked data
    public static TrackedData<Float> getDroneYawTracker() {
        return DRONE_YAW;
    }
    public static TrackedData<Float> getDronePitchTracker() {
        return DRONE_PITCH;
    }
    private UUID hiveMindOwnerUuid;
    private UUID controllingPlayerId = null;
    private String hiveCode = null;
    public void setControllingPlayer(UUID playerId) {
        this.controllingPlayerId = playerId;
    }
    public boolean isBeingControlled() {
        return this.controllingPlayerId != null;
    }
    public UUID getControllingPlayerId() {
        return this.controllingPlayerId;
    }
    public void clearControllingPlayer() {
        this.controllingPlayerId = null;
    }

    // Add these fields to prevent console spam
    private UUID previousOwnerUuid = null;
    private long lastLogTime = 0;

    // Visual effect timing
    private int particleTimer = 0;

    // Constructor
    public DroneEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        System.out.println("DroneEntity created");
    }

    //FIXED: Method name matches what's called in ModEntites
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0); // Added for better AI
    }

    // Method to set the hivemind owner (called by the command)
    public void setHiveMindOwnerUuid(UUID ownerUUID) {
        if (ownerUUID == null) {
            throw new IllegalArgumentException("Owner UUID cannot be null");
        }

        this.hiveMindOwnerUuid = ownerUUID;

        if (!this.getWorld().isClient) {
            MinecraftServer server = this.getWorld().getServer();
            if (server != null) {
                HiveMindDataManager dataManager = HiveMindDataManager.getInstance(server);
                if (dataManager != null) {
                    dataManager.linkDroneToOwner(this.getUuid(), ownerUUID);

                    String dimensionKey = this.getWorld().getRegistryKey().getValue().toString();
                    dataManager.updateDroneData(
                            this.getUuid(),
                            ownerUUID,
                            this.getX(),
                            this.getY(),
                            this.getZ(),
                            dimensionKey,
                            this.getHealth(),
                            this.getMaxHealth()
                    );
                }

                // NEW: Generate and assign HiveCode
                HiveCodeManager codeManager = HiveCodeManager.getInstance(server);
                if (codeManager != null) {
                    this.hiveCode = codeManager.generateHiveCode(this.getUuid(), ownerUUID);
                    this.dataTracker.set(HIVE_CODE, this.hiveCode);
                    ModConfig.getInstance().droneLinkDebugLog(
                            "Assigned HiveCode " + this.hiveCode + " to drone"
                    );
                }
            }

            // Update tracked data after linking
            PlayerEntity owner = this.getWorld().getPlayerByUuid(ownerUUID);
            updateTrackedData(owner);
        }
    }

    // Method to check if this drone has a hivemind owner
    public boolean hasHiveMindOwner() {
        return this.hiveMindOwnerUuid != null;
    }

    // Get the PlayerEntity object of the owner
    public PlayerEntity getHiveMindOwnerPlayer() {
        if (this.hiveMindOwnerUuid == null || this.getWorld().isClient()) {
            return null;
        }

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            return serverWorld.getServer().getPlayerManager().getPlayer(this.hiveMindOwnerUuid);
        }

        return null;
    }

    // Pause AI control when player takes over
    public void pauseAIControl() {
        if (!aiControlPaused) {
            this.aiControlPaused = true;

            // Create new goal selectors to save current goals
            this.savedGoals = new GoalSelector(() -> this.getWorld().getProfiler());
            this.savedTargetGoals = new GoalSelector(() ->this.getWorld().getProfiler()); // Save current goals

            // In Fabric, we need to use reflection or a different approach to copy goals
            // For now, we'll just clear the goals and rely on resumeAIControl to re-add them

            // Clear current goals to stop AI
            this.goalSelector.getGoals().clear();
            this.targetSelector.getGoals().clear();

            // Stop current movement
            this.getNavigation().stop();
            this.setVelocity(this.getVelocity().multiply(0.8, 0.8, 0.8));
        }
    }

    // Resume AI control when player releases control
    public void resumeAIControl() {
        if (aiControlPaused) {
            this.aiControlPaused = false;

            // Re-register AI goals
            initGoals();

            // Clear saved goals
            this.savedGoals = null;
            this.savedTargetGoals = null;
        }
    }

    public boolean isAiControlPaused() {
        return aiControlPaused;
    }

    // Get the drone's flight speed for player control
    public float getFlySpeed() {
        return 0.2f; // Adjust based on your drone's characteristics
    }

    // Primary ability triggered by player
    public void triggerPrimaryAbility() {
        // Implement drone's primary ability (e.g., scanner, weapon, tool)
        // This could be mining, attacking, scanning, etc.

        // Example: Play sound effect
        // this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
        //     SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.NEUTRAL, 1.0F, 1.0F);
    }

    // Secondary ability triggered by player
    public void triggerSecondaryAbility() {
        // Implement drone's secondary ability
        // This could be a different tool or utility function

        // Example: Heal nearby entities
        // this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(5.0), null)
        //     .forEach(entity -> entity.heal(2.0F));
    }

    // Method to get the actual player object (if they're online, Alternative method that takes a world parameter
    public PlayerEntity getHiveMindOwnerPlayer(World world) {
        if (this.hiveMindOwnerUuid == null) return null;
        return world.getPlayerByUuid(this.hiveMindOwnerUuid);
    }

    /**
     * Restores the HiveMind Connection from persistent data
     * Called when the server loads and needs to restore drone-owner relationships
     */

    public void restoreHiveMindConnection() {
        if (this.getWorld().isClient) return; // Only run on server

        System.out.println("[HiveMind] Attempting to restore connection for drones: " + this.getUuid());

        MinecraftServer server = this.getWorld().getServer();
        if (server == null) {
            System.out.println("[HiveMind] x Server is null, cannot restore");
            return;
        }

        HiveMindDataManager dataManager = HiveMindDataManager.getInstance(server);
        if (dataManager == null) {
            System.out.println("[HiveMind] x DataManager is null, cannot restore");
            return;
        }

        UUID ownerUUID = dataManager.getDroneOwner(this.getUuid());

        if (ownerUUID != null) {
            System.out.println("[HiveMind] Found owner UUID: " + ownerUUID);

            // Set the owner UUID
            this.hiveMindOwnerUuid = ownerUUID;

            // Update the drone's data in the manager with current position/health
            String dimensionKey = this.getWorld().getRegistryKey().getValue().toString();
            dataManager.updateDroneData(
                    this.getUuid(),
                    ownerUUID,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    dimensionKey,
                    this.getHealth(),
                    this.getMaxHealth()
            );

            // Restore HiveCode
            HiveCodeManager codeManager = HiveCodeManager.getInstance(server);
            if (codeManager != null) {
                this.hiveCode = codeManager.getHiveCode(this.getUuid());
                if (this.hiveCode != null) {
                    System.out.println("[HiveMind] Restored HiveCode: " + this.hiveCode);
                    this.dataTracker.set(HIVE_CODE, this.hiveCode);
                } else {
                    System.out.println("[HiveMind] x HiveCode not found, generating new one");
                    // If no HiveCode exists, generate one
                    this.hiveCode = codeManager.generateHiveCode(this.getUuid(), ownerUUID);
                    this.dataTracker.set(HIVE_CODE, this.hiveCode);
                }
            } else {
                System.out.println("[HiveMind] x HiveCodeManager is null");
            }

            // Update tracked data - try to get player name from server
            PlayerEntity owner = this.getWorld().getPlayerByUuid(ownerUUID);
            if (owner != null) {
                System.out.println("[HiveMind] Found owner player: " + owner.getName().getString());
                updateTrackedData(owner);
            } else {
                // Owner is offline - get their name from player data
                System.out.println("[HiveMind] Owner is offline, trying to get name from player cache");
                String ownerName = getPlayerNameFromUUID(server, ownerUUID);
                if (ownerName != null) {
                    this.dataTracker.set(IS_LINKED, true);
                    this.dataTracker.set(OWNER_NAME, ownerName);
                    System.out.println("[HiveMind] Set owner name from cache: " + ownerName);
                } else {
                    // Fallback to "Unknown Owner"
                    this.dataTracker.set(IS_LINKED, true);
                    this.dataTracker.set(OWNER_NAME, "Unknown Owner");
                    System.out.println("[HiveMind] Could not find owner name, using Unknown Owner");
                }

                // Owner is offline, but we can still set the tracked data
                this.dataTracker.set(IS_LINKED, true);
            }
            System.out.println("[HiveMind] ✓ Successfully restored drone connection");
        } else {
            System.out.println("[HiveMind] x No owner found for drone");
        }
    }

    /**
     * Try to get player's name from thier UUID
     * Works even if the player is offline
     */
    private String getPlayerNameFromUUID(MinecraftServer server, UUID playerUUID) {
        try {
            // Try to get from online players first
            PlayerEntity player = server.getPlayerManager().getPlayer(playerUUID);
            if (player != null) {
                return player.getName().getString();
            }

            // Try to get from user cache (stores offline player names)
            com.mojang.authlib.GameProfile profile = server.getUserCache().getByUuid(playerUUID).orElse(null);
            if (profile != null && profile.getName() != null) {
                return profile.getName();
            }

            // Fallback - return shortened UUID
            return "Player-" + playerUUID.toString().substring(0, 8);
        } catch (Exception e) {
            System.out.println("[HiveMind] Error getting player name: " + e.getMessage());
            return null;
        }
    }

    /**
     * Gets the current HiveMind owner UUID
     */
    public UUID getHiveMindOwnerUuid() {
        return this.hiveMindOwnerUuid;
    }

    // Remove HiveMind connection
    public void removeHiveMindConnection() {
        if (!this.getWorld().isClient) {
            MinecraftServer server = this.getWorld().getServer();
            if (server != null) {
                HiveMindDataManager dataManager = HiveMindDataManager.getInstance(server);
                if (dataManager != null) {
                    dataManager.unlinkDrone(this.getUuid());
                }

                // NEW: Remove HiveCode
                HiveCodeManager codeManager = HiveCodeManager.getInstance(server);
                if (codeManager != null) {
                    codeManager.removeHiveCode(this.getUuid());
                }
            }
        }

        this.hiveMindOwnerUuid = null;
        this.hiveCode = null;
        this.dataTracker.set(HIVE_CODE, "");
        updateTrackedData(null);
    }

    // Validates a UUID to ensure it's not null and properly formatted
    private boolean isValidUUID(UUID uuid) {
        return uuid != null && !uuid.toString().equals("00000000-0000-0000-0000-000000000000");
    }

    // Updates tracked data for client-side visual effects
    private void updateTrackedData(PlayerEntity owner) {
        if (owner != null) {
            this.dataTracker.set(IS_LINKED, true);
            this.dataTracker.set(OWNER_NAME, owner.getName().getString());
            System.out.println("[HiveMind] Updated tracked data for owner: " + owner.getName().getString());
        } else if (this.hiveMindOwnerUuid != null) {
            // Owner is offline but drone is still linked
            this.dataTracker.set(IS_LINKED, true);

            // Try to get owner name from server cache
            if (!this.getWorld().isClient && this.getWorld().getServer() != null) {
                String ownerName = getPlayerNameFromUUID(this.getWorld().getServer(), this.hiveMindOwnerUuid);
                if (ownerName != null) {
                    this.dataTracker.set(OWNER_NAME, "Offline Owner");
                    System.out.println("[HiveMind] Updated tracked data for offline owner");
                }
            } else {
                this.dataTracker.set(IS_LINKED, false);
                this.dataTracker.set(OWNER_NAME, "");
                System.out.println("[HiveMind] Cleared tracked data");
            }
        }
    }

    // Client-side method to check if drone is linked (for visual effects)
    public boolean isLinkedForVisuals() {
        return this.dataTracker.get(IS_LINKED);
    }

    // Client-side method to get owner name for display
    public String getOwnerNameForDisplay() {
        return this.dataTracker.get(OWNER_NAME);
    }

    // Set the drone's role and apply the corresponding behavior
    public void setRole(DroneRole newRole) {
        if (newRole == null) {
            newRole = DroneRole.IDLE;
        }

        // Don't Change if already this role
        if (this.currentRole == newRole) {
            return;
        }

        // Clean up old role
        if (this.roleBehavior != null) {
            this.roleBehavior.onRoleRemoved(this);
        }

        // Set new role
        DroneRole oldRole = this.currentRole;
        this.currentRole = newRole;
        this.roleBehavior = RoleRegistry.getBehavior(newRole);

        // Apply new behavior
        applyRoleBehavior();

        // Log Change
        ModConfig.getInstance().droneLinkDebugLog(
                "Drone role changed from " + oldRole.getDisplayName() + " to " + newRole.getDisplayName()
        );
    }

    // Get the current role behavior
    public DroneRole getRole() {
        return this.currentRole;
    }

    /**
     * Apply the current role's behavior to this drone
     * This clears role specific goals and re-applies them
     */

    public void applyRoleBehavior() {
        // Clear all target selector (combat roles manage these)
        this.targetSelector.clear(goal -> {
        return true;
    });

        //Re-add base goals
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new FollowHiveMindPlayerGoal(this, 1.2D,
                6.0F, 2.0F));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(10, new LookAroundGoal(this));

        // Clear all target selector (combat roles manage this
        this.targetSelector.clear(goal -> true);

        // Apply new role behavior
        if (this.roleBehavior != null) {
            this.roleBehavior.applyGoals(this);
        }
    }

    // Helper method to clear target goals
    private void clearTargetGoals() {
        this.targetSelector.clear(goal -> true);
    }

    public void addGoal(int priority, Goal goal, boolean isTargetGoal) {
        if (isTargetGoal) {
            this.targetSelector.add(priority, goal);
        } else {
            this.goalSelector.add(priority, goal);
        }
    }

    public void removeGoal(Goal goal, boolean isTargetGoal) {
        if (isTargetGoal) {
        this.targetSelector.remove(goal);
        } else {
            this.goalSelector.remove(goal);
        }
    }

    @Override
    protected void initGoals() {
        // Basic survival goals (Highest priority)
        this.goalSelector.add(0, new SwimGoal(this));

        // HiveMind Specific behavior (High priority)
        this.goalSelector.add(1, new FollowHiveMindPlayerGoal(this,
                1.2D, 6.0F, 2.0F));

        // Priorities 2-7 are reserved for role-specific goals

        // Default wandering behavior (lower priority - will be overridden by roles)
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(10, new LookAroundGoal(this));

        // Apply initial role behavior
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(IS_LINKED, false);
        this.dataTracker.startTracking(OWNER_NAME, "");
        this.dataTracker.startTracking(DRONE_YAW, 0.0f);
        this.dataTracker.startTracking(DRONE_PITCH, 0.0f);
        this.dataTracker.startTracking(HIVE_CODE, ""); // NEW
        // No additional data to track
    }

    // Add getter method for HiveCode:
    public String getHiveCode() {
        if (this.getWorld().isClient) {
            // Client side: get from tracked data
            return this.dataTracker.get(HIVE_CODE);
        }
        // Server side: setHiveMindOwner to generate HiveCode:
        return this.hiveCode != null ? this.hiveCode : "";
    }

    // Modify setHiveMindOwner to generate HiveCode:
    public void setHiveMindOwner(UUID ownerUUID) {
        if (ownerUUID == null) {
            throw new IllegalArgumentException("Owner UUID cannot be null");
        }
        this.hiveMindOwnerUuid = ownerUUID;

        if (!this.getWorld().isClient) {
            MinecraftServer server = this.getWorld().getServer();
            if (server != null) {
                HiveMindDataManager dataManager = HiveMindDataManager.getInstance(server);
                if (dataManager != null) {
                    dataManager.linkDroneToOwner(this.getUuid(), ownerUUID);

                    String dimensionKey = this.getWorld().getRegistryKey().getValue().toTranslationKey();
                    dataManager.updateDroneData(
                            this.getUuid(),
                            ownerUUID,
                            this.getX(),
                            this.getY(),
                            this.getZ(),
                            dimensionKey,
                            this.getHealth(),
                            this.getMaxHealth()
                    );
                }

                //New: Generate and assign HiveCode
                HiveCodeManager codeManager = HiveCodeManager.getInstance(server);
                if (codeManager != null) {
                    this.hiveCode = codeManager.generateHiveCode(this.getUuid(), ownerUUID);
                    this.dataTracker.set(HIVE_CODE, this.hiveCode);
                    ModConfig.getInstance().droneLinkDebugLog(
                            "Assigned HiveCode " + this.hiveCode + " to drone"
                    );
                }
            }

            // Update tracked data after linking
            PlayerEntity owner = this.getWorld().getPlayerByUuid(ownerUUID);
            updateTrackedData(owner);
        }
    }

    // Override setYaw to sync to clients
    @Override
    public void setYaw(float yaw) {
        super.setYaw(yaw);

        // Force update head yaw as well for proper camera rotation
        this.setHeadYaw(yaw);
        this.bodyYaw = yaw;
        this.prevYaw = yaw;

        if (!this.getWorld().isClient) {
            this.dataTracker.set(DRONE_YAW, yaw);
        }
    }

    // Override setPitch to sync to clients
    @Override
    public void setPitch(float pitch) {
        super.setPitch(pitch);

        // Ensure previous pitch is also updated
        this.prevPitch = pitch;

        if (!this.getWorld().isClient) {
            this.dataTracker.set(DRONE_PITCH, pitch);
        }
    }

    // Client-side: Apply synced rotation ONLY if not locally controlled
    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);

            if (this.getWorld().isClient) {
                // Check if this drone is being controlled by the local player
                boolean isLocallyControlled = false;

                try {
                    isLocallyControlled = net.sanfonic.hivemind.client.DroneClientHandler.isControllingDrone() &&
                            DroneClientHandler.getControlledDrone() == this;
                } catch (Exception e) {
                // if client handler isn't available, assume not controlled
                isLocallyControlled = false;
            }

            // Only apply server rotation if we're NOT locally controlling this drone
            // This prevents the controlling client from having input overridden
            if (!isLocallyControlled()) {
                if (data.equals(DRONE_YAW)) {
                    float syncedYaw = this.dataTracker.get(DRONE_YAW);
                    this.setYaw(syncedYaw);
                    this.setHeadYaw(syncedYaw);
                    this.bodyYaw = syncedYaw;
                    this.prevYaw = syncedYaw;
                } else if (data.equals(DRONE_PITCH)) {
                    float syncedPitch = this.dataTracker.get(DRONE_PITCH);
                    this.setPitch(syncedPitch);
                    this.prevPitch = syncedPitch;
                }
            }
        }
    }

    // Improved rotation update method with better pitch clamping
    public void updateRotationFromInput(float yaw, float pitch) {
        // Clamp pitch to reasonable values for smooth movement
        pitch = net.minecraft.util.math.MathHelper.clamp(pitch, -89.9f, 89.9f);

        // Normalize yaw to prevent accumulation issues
        yaw = net.minecraft.util.math.MathHelper.wrapDegrees(yaw);

        // Set rotation values directly to avoid double-setting
        this.setYaw(yaw);
        this.setPitch(pitch);

        // Update related rotation values
        this.setHeadYaw(yaw);
        this.bodyYaw = yaw;
        this.prevYaw = yaw;
        this.prevPitch = pitch;

        // Update tracked data for client sync (only for other clients, not the controlling one)
        if (!this.getWorld().isClient) {
            this.dataTracker.set(DRONE_YAW, yaw);
            this.dataTracker.set(DRONE_PITCH, pitch);
        }
    }

    private boolean isLocallyControlled() {
        boolean isLocallyControlled = false;

        // You might need to import your client handler here
        try {
            // Check if we're controlling this specific drone
            isLocallyControlled = net.sanfonic.hivemind.client.DroneClientHandler.isControllingDrone() &&
                    DroneClientHandler.getControlledDrone() == this;
        } catch (Exception e) {
            // If client handler isn't available, assume not controlled
            isLocallyControlled = false;
        }
        return isLocallyControlled;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return super.damage(source, amount);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!this.getWorld().isClient) {
            String ownerInfo;
            if (hasHiveMindOwner()) {
                PlayerEntity owner = getHiveMindOwnerPlayer();
                if (owner != null) {
                    // Owner is online
                    ownerInfo = "Owner: " + owner.getName().getString();
                } else {
                    // Owner is offline - get name from cache
                    if (this.getWorld().getServer() != null) {
                        String ownerName = getPlayerNameFromUUID(this.getWorld().getServer(), this.hiveMindOwnerUuid);
                        if (ownerName != null) {
                            ownerInfo = "Owner: " + ownerName + " (offline)";
                        } else {
                            ownerInfo = "Owner: Unknown (offline)";
                        }
                    } else {
                        ownerInfo = "Owner: Unknown (offline)";
                    }
                }
            } else {
                ownerInfo = "No Owner";
            }

            String hiveCode = getHiveCode();
            String codeInfo = (hiveCode != null && !hiveCode.isEmpty()) ? "Code: " + hiveCode + " | " : "";

            Text message = Text.literal(codeInfo + "Health: " + (int) this
                            .getHealth() + "/" + (int) this.getMaxHealth())
                    .formatted(Formatting.AQUA)
                    .append(Text.literal(" | Role: " + this
                            .currentRole.getDisplayName()).formatted(Formatting.YELLOW))
                    .append(Text.literal(" | " + ownerInfo).formatted(
                            hasHiveMindOwner() ? Formatting.GREEN : Formatting.GRAY));
            player.sendMessage(message, false);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected void updatePostDeath() {
        super.updatePostDeath();
        // Remove the drone from the data manager when it dies
        if (!this.getWorld().isClient && hasHiveMindOwner()) {
            removeHiveMindConnection();
        }
    }

    // Don't forget to save/load the owner UUID to/from NBT
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.hiveMindOwnerUuid != null) {
            nbt.putUuid("HiveMindOwner", this.hiveMindOwnerUuid);
        }
        // Save Role
        nbt.putString("DroneRole", this.currentRole.getId());

        // New: Save HiveCode
        if (this.hiveCode != null) {
            nbt.putString("HiveCode", this.hiveCode);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        System.out.println("[HiveMind] DroneEntity reading from NBT...");

        if (nbt.containsUuid("HiveMindOwner")) {
            UUID loadedUuid = nbt.getUuid("HiveMindOwner");
            if (isValidUUID(loadedUuid)) {
                System.out.println("[HiveMind] Loaded owner UUID from NBT:" + loadedUuid);

                // Restore connection immediately when NBT is loaded
                if (!this.getWorld().isClient) {
                    // Schedule restoration for next tick to ensure server is ready
                    this.getWorld().getServer().execute(() -> {
                        restoreHiveMindConnection();
                    });
                }
            }
        }

        // Load Role
        if (nbt.contains("DroneRole")) {
            String roleId = nbt.getString("DroneRole");
            DroneRole loadedRole = DroneRole.fromId(roleId);
            setRole(loadedRole);
        }

        // Load HiveCode
        if (nbt.contains("HiveCode")) {
            this.hiveCode = nbt.getString("HiveCode");
            this.dataTracker.set(HIVE_CODE, this.hiveCode);
            System.out.println("[HiveMind] Loaded HiveCode from NBT: " + this.hiveCode);
        }
    }

    @Override
    public void tick() {
        if (isBeingControlled()) {
            // Store position before tick
            double preX = this.getX();
            double preY = this.getY();
            double preZ = this.getZ();
            float preYaw = this.getYaw();
            float prePitch = this.getPitch();

            super.tick();

            // Restore position if it changed unexpectedly (prevent camera jumps)
            if (!this.getWorld().isClient && isBeingControlled()) {
                // Only on server side, let client handle smooth movement
            }

            // Handle visual effects on client side
            if (this.getWorld().isClient) {
                handleClientVisualEffects();
                return;
            }

            // Server-side logic only below
            if (!this.getWorld().isClient) {
                //Only run AI Logic if not player controlled
                if (!aiControlPaused) {
                    runAITick();
                }

                if (this.hiveMindOwnerUuid != null) {
                    ModConfig config = ModConfig.getInstance();

                    // Only log if drone linking debug is enabled AND (owner changed OR enough time has passed)
                    if (config.isDroneLinkingDebugEnabled() &&
                            (!Objects.equals(this.previousOwnerUuid, this.hiveMindOwnerUuid) ||
                                    (System.currentTimeMillis() - this.lastLogTime > config.getLogCooldownMillis()))) {

                        config.droneLinkDebugLog("Drone is linked to: " + this.hiveMindOwnerUuid);
                        this.previousOwnerUuid = this.hiveMindOwnerUuid;
                        this.lastLogTime = System.currentTimeMillis();
                    }
                }

                // Role Specific tick behavior
                if (this.roleBehavior != null) {
                    this.roleBehavior.tick(this);
                }
            }
        }
    }

    public void runAITick() {
        // Move your existing AI logic here
        // This separates AI behavior from player-controlled behavior
    }

    // Override to prevent AI movement when player controlled
    @Override
    protected void mobTick() {
        if (!aiControlPaused) {
            super.mobTick();
        } else {
            // Minimal stepping for player-controlled drones
            this.updateWaterState();
        }
    }

    // Fabric-specific: Override canBreaththeUnderwater for flying drones
    @Override
    public boolean canBreatheInWater() {
        return true; // Drones don't need air
    }

    // Handles client-side visual effects for linked drones
    private void handleClientVisualEffects() {
        if (!isLinkedForVisuals()) {
            return;
        }

        // Increment particle timer
        particleTimer++;

        // Spawn particles every 10 ticks (0.5 seconds)
        if (particleTimer % 10 == 0) {
            spawnLinkParticles();
        }

        // Show floating name tag every 40 ticks (2 seconds)
        if (particleTimer % 40 == 0) {
            showOwnerNameTag();
        }
    }

    // Spawns particle effects to indicate the drone is linked
    private void spawnLinkParticles() {
        World world = this.getWorld();
        if (world == null) return;

        // Create a small circle of particles around the drone
        for (int i = 0; i < 3; i++) {
            double angle = (System.currentTimeMillis() / 1000.0 + i + 2.0943951) % (Math.PI * 2); // 120 degrees apart
            double radius = 0.8;
            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;

            world.addParticle(
                    ParticleTypes.ELECTRIC_SPARK,
                    this.getX() + offsetX,
                    this.getY() + this.getHeight() + 0.2,
                    this.getZ() + offsetZ,
                    0.0, 0.02, 0.0
            );
        }

        // Add a subtle glow effect
        world.addParticle(
                ParticleTypes.END_ROD,
                this.getX(),
                this.getY() + this.getHeight() + 0.5,
                this.getZ(),
                0.0, 0.0, 0.0
        );
    }

    // Shows a floating name tag above the drone
    private void showOwnerNameTag() {
        String ownerName = getOwnerNameForDisplay();
        if (ownerName.isEmpty()) return;

        World world = this.getWorld();
        if (world == null) return;

        // Create enchantment glint particles to simulate text
        for (int i = 0; i < 2; i++) {
            world.addParticle(
                    ParticleTypes.ENCHANT,
                    this.getX() + (world.random.nextDouble() - 0.5) * 0.5,
                    this.getY() + this.getHeight() + 1.0,
                    this.getZ() + (world.random.nextDouble() - 0.5) * 0.5,
                    0.0, 0.1, 0.0
            );
        }
    }

    // Returns the custom name for display, showing owner if linked
    @Override
    public Text getDisplayName() {
        String code = getHiveCode();

        if (hasHiveMindOwner() && !code.isEmpty()) {
            return Text.literal(code)
                    .formatted(Formatting.GOLD, Formatting.BOLD)
                    .append(Text.literal(" | ").formatted(Formatting.DARK_GRAY))
                    .append(Text.literal(getOwnerNameForDisplay()).formatted(Formatting.GREEN));
        } else if (!code.isEmpty()) {
            return Text.literal(code).formatted(Formatting.GRAY);
        }
        return Text.literal("Drone").formatted(Formatting.GRAY);
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public boolean isCustomNameVisible() {
        return true;
    }

    // Improved rotation update method
    public void updateRotationImmediate(float yaw, float pitch) {
        // Clamp values
        pitch = net.minecraft.util.math.MathHelper.clamp(pitch, -89.9f, 89.9f);
        yaw = net.minecraft.util.math.MathHelper.wrapDegrees(yaw);

        // Set rotation using proper methods
        this.setYaw(yaw);
        this.setPitch(pitch);
        this.setHeadYaw(yaw);
        this.setBodyYaw(yaw);

        // Force previous values to current to prevent interpolation
        try {
            this.prevYaw = yaw;
            this.prevPitch = pitch;
            this.prevHeadYaw = yaw;
            this.prevBodyYaw = yaw;
        } catch (Exception e) {
            // If directed access fails, force update through other means
            this.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), yaw, pitch);
        }
    }

    // Override to prevent position desync when being controlled
    @Override
    public void updatePosition(double x, double y, double z) {
        if (isBeingControlled()) {
            // Update position more carefully to prevent camera jumps
            double oldX = this.getX();
            double oldY = this.getY();
            double oldZ = this.getZ();

            super.updatePosition(x, y, z);

            // Force previous position to prevent interpolation artifacts
            this.prevX = x;
            this.prevY = y;
            this.prevZ = z;
        } else {
            super.updatePosition(x, y, z);
        }
    }
}