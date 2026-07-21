package net.sanfonic.hivemind.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.sanfonic.hivemind.data.HiveMindData.Hive;
import net.sanfonic.hivemind.data.HiveMindData.HiveManager;
import net.sanfonic.hivemind.data.research.ResearchManager;

public class ResearchCommands {

  public static void addResearchCommands(LiteralArgumentBuilder<ServerCommandSource> hiveCommand) {
    hiveCommand.then(
        CommandManager.literal("research")
            .then(
                CommandManager.literal("unlock")
                    .then(
                        CommandManager.argument("node", StringArgumentType.word())
                            .executes(ResearchCommands::unlockNode))));
  }

  private static int unlockNode(CommandContext<ServerCommandSource> context) {
    try {
      ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
      ServerCommandSource source = context.getSource();
      var server = source.getServer();

      HiveManager manager = HiveManager.getInstance(server);
      Hive hive = manager.getHiveByOwner(player.getUuid());
      if (hive == null) {
        source.sendError(Text.literal("You do not own a hive. Use /hive join first."));
        return 0;
      }

      String nodeId = StringArgumentType.getString(context, "node");
      boolean ok = ResearchManager.unlockForHive(hive, nodeId);
      if (!ok) {
        source.sendError(Text.literal("Failed to unlock node. Already unlocked or unknown node."));
        return 0;
      }

      source.sendFeedback(
          () ->
              Text.literal("Unlocked node \"" + nodeId + "\" for your hive.")
                  .formatted(Formatting.GREEN),
          false);
      return 1;
    } catch (Exception e) {
      context.getSource().sendError(Text.literal("Error: " + e.getMessage()));
      e.printStackTrace();
      return 0;
    }
  }
}
