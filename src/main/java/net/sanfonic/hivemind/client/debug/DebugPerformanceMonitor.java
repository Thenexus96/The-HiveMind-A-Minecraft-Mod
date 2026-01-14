package net.sanfonic.hivemind.client.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.sanfonic.hivemind.config.ModConfig;
import net.sanfonic.hivemind.entity.DroneEntity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Monitors and displays performance metrics for the HiveMind mod
 */
@Environment(EnvType.CLIENT)
public class DebugPerformanceMonitor {

    private static final int SAMPLE_SIZE = 60; // 60 ticks = 3 seconds
    // Performance metrics
    private static final Queue<Long> frameTimeSamples = new LinkedList<>();
    private static final Queue<Integer> droneCountSamples = new LinkedList<>();
    private static boolean enabled = false;
    private static int totalDrones = 0;
    private static int linkedDrones = 0;
    private static int controlledDrones = 0;

    // Statistics
    private static double avgFrameTime = 0;
    private static double avgDroneCount = 0;
    private static long lastUpdateTime = 0;

    public static void register() {
        HudRenderCallback.EVENT.register(DebugPerformanceMonitor::renderPerformanceHud);
    }

    public static void toggle() {
        enabled = !enabled;
        if (enabled) {
            reset();
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    private static void reset() {
        frameTimeSamples.clear();
        droneCountSamples.clear();
        avgFrameTime = 0;
        avgDroneCount = 0;
    }

    /**
     * Update performance metrics - call this every tick
     */
    public static void tick(MinecraftClient client) {
        ModConfig config = ModConfig.getInstance();

        if (!config.isDebugModeEnabled() || !enabled) {
            return;
        }

        long currentTime = System.nanoTime();

        // Calculate frame time
        if (lastUpdateTime > 0) {
            long frameTime = currentTime - lastUpdateTime;
            frameTimeSamples.add(frameTime);
            if (frameTimeSamples.size() > SAMPLE_SIZE) {
                frameTimeSamples.poll();
            }

            // Calculate average
            avgFrameTime = frameTimeSamples.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0.0) / 1_000_000.0; // Convert to milliseconds
        }

        lastUpdateTime = currentTime;

        // Count drones
        if (client.world != null) {
            totalDrones = 0;
            linkedDrones = 0;
            controlledDrones = 0;

            for (Entity entity : client.world.getEntities()) {
                if (entity instanceof DroneEntity drone) {
                    totalDrones++;
                    if (drone.hasHiveMindOwner()) {
                        linkedDrones++;
                    }
                    if (drone.isBeingControlled()) {
                        controlledDrones++;
                    }
                }
            }

            droneCountSamples.add(totalDrones);
            if (droneCountSamples.size() > SAMPLE_SIZE) {
                droneCountSamples.poll();
            }

            avgDroneCount = droneCountSamples.stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);
        }
    }

    private static void renderPerformanceHud(DrawContext context, float tickDelta) {
        ModConfig config = ModConfig.getInstance();

        if (!config.isDebugModeEnabled() || !enabled) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return;
        }

        // Update metrics
        tick(client);

        // Calculate FPS from frame time
        double fps = avgFrameTime > 0 ? 1000.0 / avgFrameTime : 0;

        // Create display text
        List<Text> lines = new ArrayList<>();

        lines.add(Text.literal("=== HiveMind Performance ===").formatted(Formatting.GOLD, Formatting.BOLD));
        lines.add(Text.literal(String.format("FPS: %.1f (%.2fms)", fps, avgFrameTime))
                .formatted(getFpsColor(fps)));
        lines.add(Text.literal(""));
        lines.add(Text.literal("Drones:").formatted(Formatting.YELLOW));
        lines.add(Text.literal(String.format("  Total: %d (avg: %.1f)", totalDrones, avgDroneCount))
                .formatted(Formatting.WHITE));
        lines.add(Text.literal(String.format("  Linked: %d", linkedDrones))
                .formatted(Formatting.GREEN));
        lines.add(Text.literal(String.format("  Controlled: %d", controlledDrones))
                .formatted(Formatting.AQUA));

        // Memory info
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / 1024 / 1024; // MB
        long totalMemory = runtime.totalMemory() / 1024 / 1024;
        long freeMemory = runtime.freeMemory() / 1024 / 1024;
        long usedMemory = totalMemory - freeMemory;

        lines.add(Text.literal(""));
        lines.add(Text.literal("Memory:").formatted(Formatting.YELLOW));
        lines.add(Text.literal(String.format("  Used: %dMB / %dMB", usedMemory, maxMemory))
                .formatted(getMemoryColor(usedMemory, maxMemory)));

        // Render the HUD
        int x = 10;
        int y = 10;
        int lineHeight = 10;
        int padding = 4;

        // Calculate background size
        int maxWidth = 0;
        for (Text line : lines) {
            int width = client.textRenderer.getWidth(line);
            if (width > maxWidth) {
                maxWidth = width;
            }
        }

        int bgWidth = maxWidth + (padding * 2);
        int bgHeight = (lines.size() * lineHeight) + (padding * 2);

        // Draw background
        context.fill(x, y, x + bgWidth, y + bgHeight, 0xC0000000);
        context.drawBorder(x - 1, y - 1, bgWidth + 2, bgHeight + 2, 0xFF404040);

        // Draw text
        int textY = y + padding;
        for (Text line : lines) {
            context.drawTextWithShadow(
                    client.textRenderer,
                    line,
                    x + padding,
                    textY,
                    0xFFFFFF
            );
            textY += lineHeight;
        }
    }

    private static Formatting getFpsColor(double fps) {
        if (fps >= 60) {
            return Formatting.GREEN;
        } else if (fps >= 30) {
            return Formatting.YELLOW;
        } else {
            return Formatting.RED;
        }
    }

    private static Formatting getMemoryColor(long used, long max) {
        double percentage = (double) used / max;
        if (percentage < 0.7) {
            return Formatting.GREEN;
        } else if (percentage < 0.9) {
            return Formatting.YELLOW;
        } else {
            return Formatting.RED;
        }
    }
}