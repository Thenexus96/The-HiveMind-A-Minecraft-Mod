package net.sanfonic.hivemind.data.player;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.sanfonic.hivemind.Hivemind;

/**
 * Handles saving and loading player HiveMind data
 */
public class PlayerDataEvents {

    public static void register() {
        Hivemind.LOGGER.debug("Registering PlayerDataEvents");

        // Load data when player joins
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            Hivemind.LOGGER.debug("Player join event for {}", player.getName().getString());
            PlayerHiveComponent.onPlayerJoin(player);

            boolean hasAccess = PlayerHiveComponent.hasAccess(player);
            Hivemind.LOGGER.debug("Player {} has access: {}", player.getName().getString(), hasAccess);
        });


        // Save data when player disconnects
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            boolean hasAccess = PlayerHiveComponent.hasAccess(player);
            Hivemind.LOGGER.debug("Player disconnect event for {} (has access: {})",
                    player.getName().getString(), hasAccess);

            PlayerHiveComponent.onPlayerLeave(player);
        });

        // Handle respawn - Copy data to new entity
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            Hivemind.LOGGER.debug("Player respawn event for {}", newPlayer.getName().getString());
            // The data is store by UUID, so it should carry over automatically
            // Just force a save to be safe
            if (PlayerHiveComponent.hasAccess(newPlayer)) {
                PlayerHiveComponent.setAccess(newPlayer, true);
            }
        });

        Hivemind.LOGGER.debug("Player data events registered");
    }
}
