package net.sanfonic.hivemind.client.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.sanfonic.hivemind.client.gui.DroneDebugOverlay;
import net.sanfonic.hivemind.config.ModConfig;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class DebugKeyBindings {
    // Pakcet identifiers for server communication
    public static final Identifier DEBUG_SPAWN_PACKET =
            new Identifier("hivemind", "debug_spawn");
    public static final Identifier DEBUG_KILL_NEARBY_PACKET =
            new Identifier("hivemind", "debug_kill_nearby");
    public static final Identifier DEBUG_TELEPORT_PACKET =
            new Identifier("hivemind", "debug_teleport");
    // Debug keybindings
    private static KeyBinding debugToggleOverlay;
    private static KeyBinding debugSpawnDrone;
    private static KeyBinding debugKillNearby;
    private static KeyBinding debugTeleportDrone;

    public static void register() {
        ModConfig config = ModConfig.getInstance();
        // Only register debug keybinds if debug mode is enabled
        if (!config.isDebugModeEnabled()) {
            return;
        }
        // Register keybindings
        debugToggleOverlay = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hivemind.debug.toggle_overlay",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F3, // F3 + something is a good debug key
                "category.hivemind.debug"
        ));

        debugSpawnDrone = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hivemind.debug.spawn_drone",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_ADD, // Numpad +
                "category.hivemind.debug"
        ));

        debugKillNearby = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hivemind.debug.kill_nearby",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_SUBTRACT, // Numpad -
                "category.hivemind.debug"
        ));

        debugTeleportDrone = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hivemind.debug.teleport_drone",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_MULTIPLY, // Numpad *
                "category.hivemind.debug"
        ));

        // Register tick event to handle key process
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            // Toggle overlay
            while (debugToggleOverlay.wasPressed()) {
                DroneDebugOverlay.toggle();
            }
            // Spawn drone
            while (debugSpawnDrone.wasPressed()) {
                sendDebugSpawnPacket(1);
                client.player.sendMessage(Text.literal("§Spwning debug drone..."), true);
            }
            // Kill nearby drones
            while (debugKillNearby.wasPressed()) {
                sendDebugKillNearbyPacket(10);
                client.player.sendMessage(Text.literal("§cKilling nearby drones..."), true);
            }
            // Teleport nearest drone
            while (debugTeleportDrone.wasPressed()) {
                sendDebugTeleportPacket();
                client.player.sendMessage(Text.literal("§Teleporating nearest drone..."), true);
            }
        });
    }

    private static void sendDebugSpawnPacket(int count) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(count);
        ClientPlayNetworking.send(DEBUG_KILL_NEARBY_PACKET, buf);
    }

    private static void sendDebugKillNearbyPacket(int radius) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(radius);
        ClientPlayNetworking.send(DEBUG_KILL_NEARBY_PACKET, buf);
    }

    private static void sendDebugTeleportPacket() {
        PacketByteBuf buf = PacketByteBufs.create();
        // No data needed - server will find nearest drone
        ClientPlayNetworking.send(DEBUG_TELEPORT_PACKET, buf);
    }

    // Getters
    public static KeyBinding getDebugToggleOverlay() {
        return debugToggleOverlay;
    }

    public static KeyBinding getDebugSpawnDrone() {
        return debugSpawnDrone;
    }

    public static KeyBinding getDebugKillNearby() {
        return debugKillNearby;
    }

    public static KeyBinding getDebugTeleportDrone() {
        return debugTeleportDrone;
    }
}
