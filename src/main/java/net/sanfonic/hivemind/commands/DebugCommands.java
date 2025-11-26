package net.sanfonic.hivemind.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sanfonic.hivemind.config.ModConfig;
import net.sanfonic.hivemind.data.player.PlayerHiveComponent;
import net.sanfonic.hivemind.entity.DroneEntity;
import net.sanfonic.hivemind.entity.ModEntities;
import net.sanfonic.hivemind.entity.custom.role.DroneRole;

import java.util.List;
import java.util.UUID;

public class DebugCommands {

    public static void init() {
        CommandRegistrationCallback.EVENT.register(((
                dispatcher,
                registryAccess,
                environment) -> {
            registerCommands(dispatcher);
        }));
    }

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("hive")
                        .then(CommandManager.literal("debug")
                                .requires(source -> source.hasPermissionLevel(2)))

                        // Toggle debug mode
                        .then(CommandManager.literal("toggle")
                                .executes(DebugCommands::toggleDebugMode))
                        // Spawn a drone instantly
                        .then(CommandManager.literal("spawn")
                                .executes(DebugCommands::spawnDebugDrone)
                                .then(CommandManager.argument("count", IntegerArgumentType.integer(1, 10))
                                        .executes(DebugCommands::spawnMultipleDrones)))
                        // Get info about a drone
                        .then(CommandManager.literal("info")
                                .then(CommandManager.argument("drone", EntityArgumentType.entity())
                                        .executes(DebugCommands::showDroneInfo)))
                        // Change drone state/role
                        .then(CommandManager.literal("setrole")
                                .then(CommandManager.argument("drone", EntityArgumentType.entity())
                                        .then(CommandManager.argument("role", StringArgumentType.word())
                                                .executes(DebugCommands::setDroneRole))))
                        // Kill all drones
                        .then(CommandManager.literal("killall")
                                .executes(DebugCommands::killAllDrones)
                                .then(CommandManager.argument("radius", IntegerArgumentType.integer(1, 500))
                                        .executes(DebugCommands::killAllDronesInRadius)))
                        // Grant HiveMind access (bypass item)
                        .then(CommandManager.literal("grantaccess")
                                .executes(DebugCommands::grantAccess))
                        // Show debug status
                        .then(CommandManager.literal("status")
                                .executes(DebugCommands::showDebugStatus))
        );
    }

    private static int toggleDebugMode(CommandContext<ServerCommandSource> context) {
        ModConfig config = ModConfig.getInstance();
        config.debugModeEnabled = !config.debugModeEnabled;
        config.save();
        String status = config.debugModeEnabled ? "ENABLED" : "DISABLED";
        Formatting color = config.debugModeEnabled ? Formatting.GREEN : Formatting.RED;
        context.getSource().sendFeedback(() ->
                        Text.literal("⚙ Debug Mode: ").formatted(Formatting.GOLD)
                                .append(Text.literal(status).formatted(color, Formatting.BOLD)),
                true);
        if (config.debugModeEnabled) {
            context.getSource().sendFeedback(() ->
                            Text.literal("Debug features are now active!").formatted(Formatting.GRAY),
                    false);
        }

        return 1;

    }

    private static int spawnDebugDrone(CommandContext<ServerCommandSource> context) {
        return spawnDrones(context, 1);
    }

    private static int spawnMultipleDrones(CommandContext<ServerCommandSource> context) {
        int count = IntegerArgumentType.getInteger(context, "count");
        return spawnDrones(context, count);
    }

    private static int spawnDrones(CommandContext<ServerCommandSource> context, int count) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
            ServerWorld world = player.getServerWorld();
            ModConfig config = ModConfig.getInstance();
            if (!config.isDebugModeEnabled()) {
                context.getSource().sendError(Text.literal("Debug mode is not enabled!"));
                return 0;
            }

            Vec3d playerPos = player.getPos();
            int radius = config.debugSpawnRadius;
            int spawned = 0;
            for (int i = 0; i < count; i++) {
                // Calculate spawn position in a circle around player
                double angle = (i / (double) count) * Math.PI * 2;
                double offsetX = Math.cos(angle) * radius;
                double offsetZ = Math.sin(angle) * radius;
                BlockPos spawnPos = new BlockPos(
                        (int) (playerPos.x + offsetX),
                        (int) playerPos.y,
                        (int) (playerPos.z + offsetZ)
                );

                // Find safe spawn location
                while (!world.isAir(spawnPos) && spawnPos.getY() < world.getTopY()) {
                    spawnPos = spawnPos.up();
                }

                // Create drone
                DroneEntity drone = ModEntities.DRONE.create(world);
                if (drone != null) {
                    drone.refreshPositionAndAngles(
                            spawnPos.getX() + 0.5,
                            spawnPos.getY(),
                            spawnPos.getZ() + 0.5,
                            player.getYaw(),
                            0.0F
                    );

                    // Auto-link if enabled
                    if (config.shouldAutoLink()) {
                        drone.setHiveMindOwnerUuid(player.getUuid());
                    }

                    world.spawnEntity(drone);
                    spawned++;

                    // Spawn particles
                    world.spawnParticles(
                            ParticleTypes.ELECTRIC_SPARK,
                            spawnPos.getX() + 0.5,
                            spawnPos.getY() + 1,
                            spawnPos.getZ() + 0.5,
                            10, 0.3, 0.5, 0.3, 0.1
                    );
                }
            }

            // Play sound
            world.playSound(null, player.getBlockPos(),
                    SoundEvents.BLOCK_BEACON_POWER_SELECT,
                    SoundCategory.PLAYERS, 1.0F, 1.2F);
            final int finalSpawned = spawned;
            context.getSource().sendFeedback(() ->
                            Text.literal("✓ Spawned ").formatted(Formatting.GREEN)
                                    .append(Text.literal(String.valueOf(finalSpawned))
                                            .formatted(Formatting.GOLD, Formatting.BOLD))
                                    .append(Text.literal(" debug drone(s)").formatted(Formatting.GREEN)),
                    false);

            return spawned;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("Failed to spawn drones: " + e.getMessage()));
            e.printStackTrace();
            return 0;
        }
    }

    private static int showDroneInfo(CommandContext<ServerCommandSource> context) {
        try {
            Entity entity = EntityArgumentType.getEntity(context, "drone");
            if (!(entity instanceof DroneEntity drone)) {
                context.getSource().sendError(Text.literal("Target is not a drone!"));
                return 0;
            }

            context.getSource().sendFeedback(() ->
                    Text.literal("=== Drone Debug Info ===").formatted(Formatting.GOLD), false);
            String hivecode = drone.getHiveCode();
            context.getSource().sendFeedback(() ->
                    Text.literal("HiveCode: ").formatted(Formatting.GRAY)
                            .append(Text.literal(hivecode).formatted(Formatting.AQUA)), false);
            context.getSource().sendFeedback(() ->
                    Text.literal("UUID: ").formatted(Formatting.GRAY)
                            .append(Text.literal(drone.getUuid().toString())
                                    .formatted(Formatting.DARK_GRAY)), false);
            context.getSource().sendFeedback(() ->
                    Text.literal("Health: ").formatted(Formatting.GRAY)
                            .append(Text.literal(String.format("%.1f/%.1f",
                                            drone.getHealth(), drone.getMaxHealth()))
                                    .formatted(Formatting.RED)), false);
            context.getSource().sendFeedback(() ->
                    Text.literal("Role: ").formatted(Formatting.GRAY)
                            .append(Text.literal(drone.getRole().getDisplayName())
                                    .formatted(Formatting.YELLOW)), false);
            context.getSource().sendFeedback(() ->
                    Text.literal("Position: ").formatted(Formatting.GRAY)
                            .append(Text.literal(String.format("%.1f, %.1f, %.1f",
                                            drone.getX(), drone.getY(), drone.getZ()))
                                    .formatted(Formatting.WHITE)), false);
            if (drone.hasHiveMindOwner()) {
                UUID ownerUuid = drone.getHiveMindOwnerUuid();
                context.getSource().sendFeedback(() ->
                        Text.literal("Owner: ").formatted(Formatting.GRAY)
                                .append(Text.literal(ownerUuid.toString())
                                        .formatted(Formatting.GREEN)), false);
            } else {
                context.getSource().sendFeedback(() ->
                        Text.literal("Owner: ").formatted(Formatting.GRAY)
                                .append(Text.literal("None").formatted(Formatting.RED)), false);
            }

            context.getSource().sendFeedback(() ->
                    Text.literal("AI Paused: ").formatted(Formatting.GRAY)
                            .append(Text.literal(String.valueOf(drone.isAiControlPaused()))
                                    .formatted(drone.isAiControlPaused() ?
                                            Formatting.RED : Formatting.GREEN)), false);
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("Error: " + e.getMessage()));
            return 0;
        }
    }

    private static int setDroneRole(CommandContext<ServerCommandSource> context) {
        try {
            Entity entity = EntityArgumentType.getEntity(context, "drone");
            String roleId = StringArgumentType.getString(context, "role");
            if (!(entity instanceof DroneEntity drone)) {
                context.getSource().sendError(Text.literal("Target is not a drone!"));
                return 0;
            }

            DroneRole newRole = DroneRole.fromId(roleId);
            DroneRole oldRole = drone.getRole();
            drone.setRole(newRole);
            drone.applyRoleBehavior();
            context.getSource().sendFeedback(() ->
                            Text.literal("✓ Changed drone role from ").formatted(Formatting.GREEN)
                                    .append(Text.literal(oldRole.getDisplayName()).formatted(Formatting.YELLOW))
                                    .append(Text.literal(" to ").formatted(Formatting.GREEN))
                                    .append(Text.literal(newRole.getDisplayName()).formatted(Formatting.GOLD, Formatting.BOLD)),
                    false);
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("Error: " + e.getMessage()));
            return 0;
        }
    }

    private static int killAllDrones(CommandContext<ServerCommandSource> context) {
        return killAllDronesInRadius(context, 500);
    }

    private static int killAllDronesInRadius(CommandContext<ServerCommandSource> context) {
        int radius = IntegerArgumentType.getInteger(context, "radius");
        return killAllDronesInRadius(context, radius);
    }

    private static int killAllDronesInRadius(CommandContext<ServerCommandSource> context, int radius) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
            ServerWorld world = player.getServerWorld();
            List<DroneEntity> drones = world.getEntitiesByClass(
                    DroneEntity.class,
                    player.getBoundingBox().expand(radius),
                    drone -> true
            );

            int killed = 0;
            for (DroneEntity drone : drones) {
                drone.remove(Entity.RemovalReason.KILLED);
                killed++;
                // Death particles
                world.spawnParticles(
                        ParticleTypes.POOF,
                        drone.getX(), drone.getY() + 1, drone.getZ(),
                        20, 0.5, 0.5, 0.5, 0.05
                );
            }

            final int finalKilled = killed;
            context.getSource().sendFeedback(() ->
                            Text.literal("✗ Removed ").formatted(Formatting.RED)
                                    .append(Text.literal(String.valueOf(finalKilled)).formatted(Formatting.GOLD,
                                            Formatting.BOLD))
                                    .append(Text.literal(" drone(s)").formatted(Formatting.RED)),
                    false);
            return killed;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("Error: " + e.getMessage()));
            return 0;
        }
    }

    private static int grantAccess(CommandContext<ServerCommandSource> context) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
            if (PlayerHiveComponent.hasAccess(player)) {
                context.getSource().sendFeedback(() ->
                                Text.literal("⚠ You already have HiveMind access!").formatted(Formatting.YELLOW),
                        false);
                return 0;
            }
            PlayerHiveComponent.initializeNewMember(player);
            context.getSource().sendFeedback(() ->
                            Text.literal("✓ HiveMind access granted! (Debug)").formatted(Formatting.GREEN),
                    false);
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("Error: " + e.getMessage()));
            return 0;
        }
    }

    private static int showDebugStatus(CommandContext<ServerCommandSource> context) {
        ModConfig config = ModConfig.getInstance();
        context.getSource().sendFeedback(() ->
                Text.literal("=== HiveMind Debug Status ===").formatted(Formatting.GOLD), false);
        context.getSource().sendFeedback(() ->
                        Text.literal("Debug Mode: ")
                                .append(Text.literal(config.isDebugModeEnabled() ? "ENABLED" : "DISABLED")
                                        .formatted(config.isDebugModeEnabled() ? Formatting.GREEN : Formatting.RED)),
                false);
        if (config.isDebugModeEnabled()) {
            context.getSource().sendFeedback(() ->
                    Text.literal(" Show Overlay: " + config.debugShowOverlay)
                            .formatted(Formatting.GRAY), false);
            context.getSource().sendFeedback(() ->
                    Text.literal("  Instant Spawn: " + config.debugInstantSpawn)
                            .formatted(Formatting.GRAY), false);
            context.getSource().sendFeedback(() ->
                    Text.literal("  Bypass Access: " + config.debugBypassAccess)
                            .formatted(Formatting.GRAY), false);
            context.getSource().sendFeedback(() ->
                    Text.literal("  Unlimited Drones: " + config.debugUnlimitedDrones)
                            .formatted(Formatting.GRAY), false);
            context.getSource().sendFeedback(() ->
                    Text.literal("  Auto-Link: " + config.debugAutoLink)
                            .formatted(Formatting.GRAY), false);
            context.getSource().sendFeedback(() ->
                    Text.literal("  Spawn Radius: " + config.debugSpawnRadius)
                            .formatted(Formatting.GRAY), false);
        }
        return 1;
    }
}