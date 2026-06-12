package net.sanfonic.hivemind.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sanfonic.hivemind.config.ModConfig;
import net.sanfonic.hivemind.entity.DroneEntity;
import net.sanfonic.hivemind.entity.ModEntities;

import java.util.Comparator;
import java.util.List;

public class DebugNetworkHandler {

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(DebugNetworkConstants.DEBUG_SPAWN_PACKET,
                (server, player, handler, buf, responseSender) -> {
                    int count = buf.readInt();
                    server.execute(() -> handleDebugSpawn(player, count));
                });

        ServerPlayNetworking.registerGlobalReceiver(DebugNetworkConstants.DEBUG_KILL_NEARBY_PACKET,
                (server, player, handler, buf, responseSender) -> {
                    int radius = buf.readInt();
                    server.execute(() -> handleDebugKillNearby(player, radius));
                });

        ServerPlayNetworking.registerGlobalReceiver(DebugNetworkConstants.DEBUG_TELEPORT_PACKET,
                (server, player, handler, buf, responseSender) ->
                        server.execute(() -> handleDebugTeleport(player)));
    }

    private static void handleDebugSpawn(ServerPlayerEntity player, int count) {
        ModConfig config = ModConfig.getInstance();

        if (!config.isDebugModeEnabled()) {
            player.sendMessage(Text.literal("§cDebug mode is not enabled!"), false);
            return;
        }

        ServerWorld world = player.getServerWorld();
        Vec3d playerPos = player.getPos();
        int radius = config.debugSpawnRadius;
        int spawned = 0;

        for (int i = 0; i < count; i++) {
            double angle = (i / (double) count) * Math.PI * 2;
            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;

            BlockPos spawnPos = new BlockPos(
                    (int) (playerPos.x + offsetX),
                    (int) playerPos.y,
                    (int) (playerPos.z + offsetZ)
            );

            while (!world.isAir(spawnPos) && spawnPos.getY() < world.getTopY()) {
                spawnPos = spawnPos.up();
            }

            DroneEntity drone = ModEntities.DRONE.create(world);
            if (drone != null) {
                drone.refreshPositionAndAngles(
                        spawnPos.getX() + 0.5,
                        spawnPos.getY(),
                        spawnPos.getZ() + 0.5,
                        player.getYaw(),
                        0.0F
                );

                if (config.shouldAutoLink()) {
                    drone.setHiveMindOwner(player.getUuid());
                }

                world.spawnEntity(drone);
                spawned++;

                world.spawnParticles(
                        ParticleTypes.ELECTRIC_SPARK,
                        spawnPos.getX() + 0.5,
                        spawnPos.getY() + 1,
                        spawnPos.getZ() + 0.5,
                        10, 0.3, 0.5, 0.3, 0.1
                );
            }
        }

        world.playSound(null, player.getBlockPos(),
                SoundEvents.BLOCK_BEACON_POWER_SELECT,
                SoundCategory.PLAYERS, 1.0F, 1.2F);

        player.sendMessage(Text.literal("§aSpawned " + spawned + " debug drone(s)"), false);
    }

    private static void handleDebugKillNearby(ServerPlayerEntity player, int radius) {
        ModConfig config = ModConfig.getInstance();

        if (!config.isDebugModeEnabled()) {
            player.sendMessage(Text.literal("§cDebug mode is not enabled!"), false);
            return;
        }

        ServerWorld world = player.getServerWorld();

        List<DroneEntity> drones = world.getEntitiesByClass(
                DroneEntity.class,
                player.getBoundingBox().expand(radius),
                drone -> true
        );

        int killed = 0;
        for (DroneEntity drone : drones) {
            world.spawnParticles(
                    ParticleTypes.POOF,
                    drone.getX(), drone.getY() + 1, drone.getZ(),
                    20, 0.5, 0.5, 0.5, 0.05
            );

            drone.remove(Entity.RemovalReason.KILLED);
            killed++;
        }

        player.sendMessage(Text.literal("§cRemoved " + killed + " drone(s)"), false);
    }

    private static void handleDebugTeleport(ServerPlayerEntity player) {
        ModConfig config = ModConfig.getInstance();

        if (!config.isDebugModeEnabled()) {
            player.sendMessage(Text.literal("§cDebug mode is not enabled!"), false);
            return;
        }

        ServerWorld world = player.getServerWorld();

        DroneEntity nearestDrone = world.getEntitiesByClass(
                        DroneEntity.class,
                        player.getBoundingBox().expand(100),
                        drone -> true
                ).stream()
                .min(Comparator.comparingDouble(player::distanceTo))
                .orElse(null);

        if (nearestDrone == null) {
            player.sendMessage(Text.literal("§cNo drones found nearby!"), false);
            return;
        }

        Vec3d lookVec = player.getRotationVector().multiply(3.0);
        Vec3d teleportPos = player.getPos().add(lookVec);

        world.spawnParticles(
                ParticleTypes.PORTAL,
                nearestDrone.getX(), nearestDrone.getY() + 1, nearestDrone.getZ(),
                30, 0.5, 0.5, 0.5, 0.5
        );

        nearestDrone.teleport(teleportPos.x, teleportPos.y, teleportPos.z);
        nearestDrone.setVelocity(0, 0, 0);
        nearestDrone.velocityDirty = true;

        world.getChunkManager().sendToNearbyPlayers(nearestDrone,
                new net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket(nearestDrone));

        world.spawnParticles(
                ParticleTypes.PORTAL,
                teleportPos.x, teleportPos.y + 1, teleportPos.z,
                30, 0.5, 0.5, 0.5, 0.5
        );

        world.playSound(null, nearestDrone.getBlockPos(),
                SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                SoundCategory.NEUTRAL, 1.0F, 1.0F);

        String hiveCode = nearestDrone.getHiveCode();
        player.sendMessage(Text.literal("§bTeleported drone " + hiveCode + " to you"), false);
    }
}
