package net.sanfonic.hivemind.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.sanfonic.hivemind.config.ModConfig;
import net.sanfonic.hivemind.entity.DroneEntity;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class DroneDebugOverlay {

    private static boolean enabled = false;

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(DroneDebugOverlay::renderDebugInfo);
    }

    public static void toggle() {
        enabled = !enabled;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(
                    Text.literal("Debug Overlay: " + (enabled ? "§aON" : "§cOFF")),
                    true // Action bar
            );
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    private static void renderDebugInfo(WorldRenderContext context) {
        ModConfig config = ModConfig.getInstance();

        // Check if debug mode and overlay are enabled
        if (!config.shouldShowDebugOverlay() && !enabled) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) {
            return;
        }

        // Find all drones in render distance
        List<DroneEntity> nearbyDrones = new ArrayList<>();
        for (Entity entity : client.world.getEntities()) {
            if (entity instanceof DroneEntity drone) {
                double distance = client.player.squaredDistanceTo(drone);
                if (distance < 4096) { // 64 block render distance
                    nearbyDrones.add(drone);
                }
            }
        }

        // Render info for each drone
        for (DroneEntity drone : nearbyDrones) {
            renderDroneInfo(context, drone);
        }
    }

    private static void renderDroneInfo(WorldRenderContext context, DroneEntity drone) {
        MinecraftClient client = MinecraftClient.getInstance();
        MatrixStack matrices = context.matrixStack();
        Camera camera = context.camera();

        // Get drone position
        Vec3d dronePos = drone.getPos();
        Vec3d cameraPos = camera.getPos();

        // Calculate relative position
        double relX = dronePos.x - cameraPos.x;
        double relY = dronePos.y - cameraPos.y + drone.getHeight() + 0.5; // Above drone
        double relZ = dronePos.z - cameraPos.z;

        // Calculate distance for scaling
        double distance = Math.sqrt(relX * relX + relY * relY + relZ * relZ);
        float scale = (float) (0.025 * Math.min(distance / 4.0, 1.0)); // Scale based on distance

        matrices.push();

        // Translate to drone position
        matrices.translate(relX, relY, relZ);

        // Rotate to face camera
        matrices.multiply(camera.getRotation());

        // Scale appropriately
        matrices.scale(-scale, -scale, scale);

        // Prepare text lines
        List<String> lines = buildDebugLines(drone, client);

        // Render background and text
        renderTextWithBackground(matrices, lines, client.textRenderer);

        matrices.pop();
    }

    private static List<String> buildDebugLines(DroneEntity drone, MinecraftClient client) {
        List<String> lines = new ArrayList<>();

        // Line 1: HiveCode
        String hiveCode = drone.getHiveCode();
        if (!hiveCode.isEmpty()) {
            lines.add("§6§l" + hiveCode);
        } else {
            lines.add("§7Drone");
        }

        // Line 2: Health
        float health = drone.getHealth();
        float maxHealth = drone.getMaxHealth();
        float healthPercent = (health / maxHealth) * 100;
        String healthColor;
        if (healthPercent > 66) {
            healthColor = "§a"; // Green
        } else if (healthPercent > 33) {
            healthColor = "§e"; // Yellow
        } else {
            healthColor = "§c"; // Red
        }
        lines.add(healthColor + String.format("%.1f/%.1f HP", health, maxHealth));

        // Line 3: Role
        lines.add("§7Role: §e" + drone.getRole().getDisplayName());

        // Line 4: Owner
        if (drone.hasHiveMindOwner()) {
            String ownerName = drone.getOwnerNameForDisplay();
            boolean isOwnedByPlayer = drone.getHiveMindOwnerUuid() != null &&
                    client.player != null &&
                    drone.getHiveMindOwnerUuid().equals(client.player.getUuid());
            String ownerColor = isOwnedByPlayer ? "§a" : "§7";
            lines.add("§7Owner: " + ownerColor + ownerName);
        } else {
            lines.add("§7Owner: §cNone");
        }

        // Line 5: Distance
        double distance = client.player.distanceTo(drone);
        lines.add("§7Distance: §b" + String.format("%.1fm", distance));

        // Line 6: AI State (if paused)
        if (drone.isAiControlPaused()) {
            lines.add("§c§lAI PAUSED");
        }

        // Line 7: Being Controlled
        if (drone.isBeingControlled()) {
            lines.add("§d§lCONTROLLED");
        }

        return lines;
    }

    private static void renderTextWithBackground(MatrixStack matrices, List<String> lines, TextRenderer textRenderer) {
        if (lines.isEmpty()) return;

        // Calculate dimensions
        int maxWidth = 0;
        for (String line : lines) {
            int width = textRenderer.getWidth(line);
            if (width > maxWidth) {
                maxWidth = width;
            }
        }

        int lineHeight = 10;
        int totalHeight = lines.size() * lineHeight;
        int padding = 2;

        // Center the text
        int startX = -maxWidth / 2;
        int startY = -totalHeight / 2;

        // Render background
        matrices.push();
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        // Background rectangle (semi-transparent black)
        int bgX1 = startX - padding;
        int bgY1 = startY - padding;
        int bgX2 = startX + maxWidth + padding;
        int bgY2 = startY + totalHeight + padding;

        buffer.vertex(matrix, bgX1, bgY2, 0).color(0, 0, 0, 128).next();
        buffer.vertex(matrix, bgX2, bgY2, 0).color(0, 0, 0, 128).next();
        buffer.vertex(matrix, bgX2, bgY1, 0).color(0, 0, 0, 128).next();
        buffer.vertex(matrix, bgX1, bgY1, 0).color(0, 0, 0, 128).next();

        tessellator.draw();
        RenderSystem.disableBlend();

        matrices.pop();

        // Render text lines
        matrices.push();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int y = startY + (i * lineHeight);

            // Center each line individually
            int lineWidth = textRenderer.getWidth(line);
            int x = -lineWidth / 2;

            // Render text with shadow
            textRenderer.draw(
                    line,
                    x,
                    y,
                    0xFFFFFF,
                    true, // Shadow
                    matrices.peek().getPositionMatrix(),
                    MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers(),
                    TextRenderer.TextLayerType.NORMAL,
                    0,
                    15728880 // Full brightness
            );
        }

        matrices.pop();
    }
}