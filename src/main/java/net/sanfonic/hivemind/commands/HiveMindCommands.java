package net.sanfonic.hivemind.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.sanfonic.hivemind.entity.DroneEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HiveMindCommands {
    // Add this flag to control debug output
    private static final boolean DEBUG_ENABLED = false;

    private static void debugLog(String message) {
        if (DEBUG_ENABLED) {
            System.out.println("[DEBUG] " + message);
        }
    }

    /**
     * Helper method to link a drone with proper effects and feedback
     */
    private static boolean linkDroneWithEffects(ServerPlayerEntity player, DroneEntity drone,
                                             ServerCommandSource source, String method) {
        // Check if drone is already linked to this player
        if (drone.hasHiveMindOwner()) {
            UUID currentOwner = drone.getHiveMindOwnerUuid();
            if (currentOwner != null && currentOwner.equals(player.getUuid())) {
                // Already linked to this player
                String hiveCode = drone.getHiveCode();
                source.sendFeedback(() ->
                                Text.literal("⚠ ").formatted(Formatting.YELLOW)
                                        .append(Text.literal("Drone ").formatted(Formatting.GRAY))
                                        .append(Text.literal(hiveCode != null ? hiveCode : "???").formatted(Formatting.GOLD))
                                        .append(Text.literal(" is already linked to you!").formatted(Formatting.GRAY)),
                        false
                );
                return false;
            } else {
                // Linked to someone else
                source.sendFeedback(() ->
                                Text.literal("⚠ ").formatted(Formatting.RED)
                                        .append(Text.literal("This drone belongs to another player!")
                                                .formatted(Formatting.GRAY)),
                        false
                );
                return false;
            }
        }

        // Link the drone
        drone.setHiveMindOwner(player.getUuid());

        // Get the HiveCode
        String hiveCode = drone.getHiveCode();

        // Play sound effect
        ServerWorld world = (ServerWorld) drone.getWorld();
        world.playSound(
                null,
                drone.getBlockPos(),
                SoundEvents.BLOCK_BEACON_POWER_SELECT,
                SoundCategory.NEUTRAL,
                0.8F,
                1.2F
        );

        spawnLinkingParticles(world, drone.getPos());

        // Spawn feedback message
        source.sendFeedback(() ->
                        Text.literal("✓ ").formatted(Formatting.GREEN)
                                .append(Text.literal("Linked Drone ").formatted(Formatting.GRAY))
                                .append(Text.literal(hiveCode).formatted(Formatting.GOLD, Formatting.BOLD))
                                .append(Text.literal(" to HiveMind! ").formatted(Formatting.GRAY))
                                .append(Text.literal("(" + method + ")").formatted(Formatting.DARK_GRAY)),
                false
        );

        return true;
    }

    /**
     * Spawn particles effects when linking a drone
     */
    private static void spawnLinkingParticles(ServerWorld world, Vec3d pos) {
        double x = pos.x;
        double y = pos.y + 1.0;
        double z = pos.z;

        // Central burst
        world.spawnParticles(
                ParticleTypes.ELECTRIC_SPARK,
                x, y, z,
                15, // Count
                0.3, 0.5, 0.3, // Spread
                0.1
        );

        // Ring effect
        for (int i = 0; i < 12; i++) {
            double angle = (i / 12.0) * Math.PI * 2;
            double radius = 1.0;

            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;

            world.spawnParticles(
                    ParticleTypes.END_ROD,
                    x + offsetX,
                    y,
                    z + offsetZ,
                    1,
                    0.0, 0.0, 0.0,
                    0.02
            );
        }
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register(((commandDispatcher,
                                                     commandRegistryAccess,
                                                     registrationEnvironment) -> {
            // Register hivemind_link command
            commandDispatcher.register(
                    CommandManager.literal("hivemind_link")
                            .requires(source -> source.hasPermissionLevel(2))
                            .executes(context -> {
                                ServerCommandSource source = context.getSource();
                                ServerPlayerEntity player = source.getPlayer();

                                // Check if player is null (command executed from console)
                                if (player == null) {
                                    source.sendFeedback(() -> Text.literal(
                                                    "This command can only be executed by a player!"),
                                            false);
                                    return 1;
                                }

                                debugLog("Player executing command: " + player.getName().getString());
                                debugLog("Player position: " + player.getPos());
                                debugLog("Player looking at: " + player.getRotationVector());

                                // Method 1: Try raycast
                                HitResult hitResult = player.raycast(20.0, 1.0f, false);
                                debugLog("Hit result type: " + hitResult.getType());

                                if (hitResult instanceof EntityHitResult entityHit) {
                                    Entity target = entityHit.getEntity();
                                    debugLog("Entity found via raycast: " + target.getClass().getName());
                                    if (target instanceof DroneEntity drone) {
                                        debugLog("Found DroneEntity via raycast, attempting to link...");
                                        if (linkDroneWithEffects(player, drone, source, "Direct Look")) {
                                            return 1;
                                        }
                                        // If linking failed (already linked), continue to search
                                    }
                                }

                                // Method 2: If raycast fails, try looking for nearby entities
                                debugLog("Raycast failed, trying nearby entity search...");
                                World world = player.getWorld();
                                Vec3d playerPos = player.getPos();
                                Vec3d lookDirection = player.getRotationVector();

                                Vec3d endPos = playerPos.add(lookDirection.multiply(10.0));
                                Box searchBox = new Box(playerPos, endPos).expand(2.0);

                                List<Entity> nearbyEntities = world.getOtherEntities(player, searchBox);
                                debugLog("Found " + nearbyEntities.size() + "nearby entities");

                                for (Entity entity : nearbyEntities) {
                                    debugLog("Nearby entity: " + entity.getClass().getSimpleName() +
                                            " at " + entity.getPos());
                                    if (entity instanceof DroneEntity drone) {
                                        // Skip if already linked
                                        if (drone.hasHiveMindOwner()) {
                                            debugLog("Skipping already linked drone");
                                            continue;
                                        }

                                        Vec3d toEntity = entity.getPos().subtract(playerPos).normalize();
                                        double dot = lookDirection.dotProduct(toEntity);
                                        debugLog("Dot product (looking direction): " + dot);

                                        if (dot > 0.5) {
                                            debugLog("Found DroneEntity via area search, Linking...");
                                            if (linkDroneWithEffects(player, drone, source, "Area Search")) {
                                                return 1;
                                            }
                                        }
                                    }
                                }

                                // Method 3: Find the closest drone within range
                                debugLog("Area search failed, trying closest drone search...");
                                DroneEntity closestDrone = null;
                                double closestDistance = Double.MAX_VALUE;

                                List<Entity> allNearbyEntites = world.getOtherEntities(player, Box.of(
                                        playerPos, 20, 20, 20));
                                for (Entity entity : allNearbyEntites) {
                                    if (entity instanceof DroneEntity drone) {
                                        // Skip if already linked
                                        if (drone.hasHiveMindOwner()) {
                                            continue;
                                        }

                                        double distance = player.distanceTo(drone);
                                        debugLog("Found drone at distance: " + distance);
                                        if (distance < closestDistance && distance < 10.0) {
                                            closestDistance = distance;
                                            closestDrone = drone;
                                        }
                                    }
                                }

                                if (closestDrone != null) {
                                    debugLog("Found closest DroneEntity, linking...");
                                    final double finalDistance = closestDistance;
                                    if (linkDroneWithEffects(player, closestDrone, source, "Closest - " +
                                            String.format("%.1fm", finalDistance))) {
                                        return 1;
                                    }
                                }

                                source.sendFeedback(() ->
                                                Text.literal("⚠ ").formatted(Formatting.RED)
                                                        .append(Text.literal("No Drone Found! " +
                                                                        "Look at a drone or stand near one.")
                                                                .formatted(Formatting.GRAY)),
                                        false
                                );
                                return 1;
                            })
            );

            // Register hivemind_link_all command - links all nearby unlinked drones
            commandDispatcher.register(
                    CommandManager.literal("hivemind_link_all")
                            .requires(source -> source.hasPermissionLevel(2))
                            .executes(context -> {
                                ServerCommandSource source = context.getSource();
                                ServerPlayerEntity player = source.getPlayer();

                                if (player == null) {
                                    source.sendFeedback(() -> Text.literal(
                                            "This command can only be executed by a player"), false);
                                    return 1;
                                }

                                World world = player.getWorld();
                                Vec3d playerPos = player.getPos();

                                // Search in 20 block radius
                                Box searchBox = Box.of(playerPos, 40, 40, 40);
                                List<Entity> allNearbyEntities = world.getOtherEntities(player, searchBox);
                                List<DroneEntity> unlinkedDrones = new ArrayList<>();

                                // Find all unlinked drones within range
                                for (Entity entity : allNearbyEntities) {
                                    if (entity instanceof DroneEntity drone) {
                                        if (!drone.hasHiveMindOwner() && player.distanceTo(drone) <= 20.0) {
                                            unlinkedDrones.add(drone);
                                        }
                                    }
                                }

                                if (unlinkedDrones.isEmpty()) {
                                    source.sendFeedback(() ->
                                                    Text.literal("⚠ ").formatted(Formatting.YELLOW)
                                                            .append(Text.literal("No unlinked drones found within 20 blocks.")
                                                                    .formatted(Formatting.GRAY)),
                                            false
                                    );
                                    return 1;
                                }

                                // Link all unlinked drones
                                int linkedCount = 0;
                                List<String> linkedCodes = new ArrayList<>();

                                for (DroneEntity drone : unlinkedDrones) {
                                    drone.setHiveMindOwner(player.getUuid());
                                    linkedCount++;

                                    String hiveCode = drone.getHiveCode();
                                    if (hiveCode != null) {
                                        linkedCodes.add(hiveCode);
                                    }

                                    // Spawn particles for each drone
                                    if (world instanceof ServerWorld serverWorld) {
                                        spawnLinkingParticles(serverWorld, drone.getPos());
                                    }
                                }

                                // Play sound
                                world.playSound(
                                        null,
                                        player.getBlockPos(),
                                        SoundEvents.BLOCK_BEACON_ACTIVATE,
                                        SoundCategory.PLAYERS,
                                        1.0F,
                                        1.0F
                                );

                                // Send success message
                                final int finalCount = linkedCount;
                                final String codesList = String.join(", ", linkedCodes);

                                source.sendFeedback(() ->
                                                Text.literal("✓ ").formatted(Formatting.GREEN)
                                                        .append(Text.literal("Linked ").formatted(Formatting.GRAY))
                                                        .append(Text.literal(String.valueOf(finalCount)).formatted(
                                                                Formatting.GOLD, Formatting.BOLD)),
                                        false
                                );

                                if (!codesList.isEmpty()) {
                                    source.sendFeedback(() ->
                                                    Text.literal(" Codes: ").formatted(Formatting.DARK_GRAY)
                                                            .append(Text.literal(codesList).formatted(Formatting.GOLD)),
                                            false
                                    );
                                }

                                return 1;
                            })
            );

            // Register hivemind_list command
            commandDispatcher.register(
                    CommandManager.literal("hivemind_list")
                            .requires(source -> source.hasPermissionLevel(0))
                            .executes(context -> {
                                ServerCommandSource source = context.getSource();
                                ServerPlayerEntity player = source.getPlayer();

                                if (player == null) {
                                    source.sendFeedback(() -> Text.literal(
                                                    "§cThis command can only be executed by a player!"),
                                            false);
                                    return 1;
                                }

                                // Get all entities in the world
                                World world = player.getWorld();
                                List<DroneEntity> connectedDrones = new ArrayList<>();
                                // Search in a large area around the player (or use world bounds)
                                Vec3d playerPos = player.getPos();
                                // Search in a large radius (1000 blocks) - adjust as needed
                                Box searchBox = Box.of(
                                        playerPos, 2000, 256, 2000
                                ); // 2000x256x2000 search area

                                List<Entity> allEntities = world.getOtherEntities(null, searchBox);

                                for (Entity entity : allEntities) {
                                    if (entity instanceof DroneEntity drone) {
                                        if (drone.hasHiveMindOwner()) {
                                            PlayerEntity owner = drone.getHiveMindOwnerPlayer();
                                            if (owner != null && owner.getUuid().equals(player.getUuid())) {
                                                connectedDrones.add(drone);
                                            }
                                        }
                                    }
                                }

                                // Display Results
                                if (connectedDrones.isEmpty()) {
                                    source.sendFeedback(() -> Text.literal(
                                            "§eYou have no connected Drones."), false);
                                } else {
                                    source.sendFeedback(() -> Text.literal(
                                            "§a=== Your Connected Drones ==="), false);
                                    source.sendFeedback(() -> Text.literal("§7Found " +
                                            connectedDrones.size() +
                                            " connected drone(s):"), false);

                                    for (int i = 0; i < connectedDrones.size(); i++) {
                                        DroneEntity drone = connectedDrones.get(i);
                                        Vec3d pos = drone.getPos();
                                        double distance = player.distanceTo(drone);
                                        String hiveCode = drone.getHiveCode();

                                        // Format position and distance
                                        String posStr = String.format("%.1f, %.1f, %.1f", pos.x, pos.y, pos.z);
                                        String distStr = String.format("%.1f", distance);
                                        // Health info (fixed the format string)
                                        String healthStr = String.format("%.1f/%.1f", drone.getHealth(),
                                                drone.getMaxHealth());

                                        final int droneNum = i + 1;
                                        final String finalPosStr = posStr;
                                        final String finalDistStr = distStr;
                                        final String finalHealthStr = healthStr;
                                        final String finalHiveCode = hiveCode != null ? hiveCode : "???";

                                        source.sendFeedback(() -> Text.literal(
                                                        "§b" + droneNum + ". §7Drone at §f" + finalPosStr +
                                                                " §7(§a" + finalDistStr + "m away§7) §7Health: §c" +
                                                                finalHealthStr),
                                                false);
                                    }
                                }

                                return 1;
                            })
            );
            DebugCommands.registerCommands(commandDispatcher);
        }));
    }
}