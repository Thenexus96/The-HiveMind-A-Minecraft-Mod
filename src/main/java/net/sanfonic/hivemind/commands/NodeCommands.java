package net.sanfonic.hivemind.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.sanfonic.hivemind.data.node.NodeManager;

public class NodeCommands {
  public static void addNodeCommands(LiteralArgumentBuilder<ServerCommandSource> hiveCommand) {
    hiveCommand.then(
        CommandManager.literal("node")
            .then(
                CommandManager.literal("claim")
                    .then(
                        CommandManager.argument("nodeId", StringArgumentType.word())
                            .executes(NodeCommands::claimNode))));
  }

  private static int claimNode(CommandContext<ServerCommandSource> context) {
    try {
      ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
      ServerCommandSource source = context.getSource();
      MinecraftServer server = source.getServer();

      String nodeId = StringArgumentType.getString(context, "nodeId");
      boolean ok = NodeManager.claimForPlayer(server, player, nodeId);
      if (!ok) {
        source.sendError(
            Text.literal("Failed to claim node. Ensure you have a hive and node exists."));
        return 0;
      }

      source.sendFeedback(
          () -> Text.literal("Node claimed: " + nodeId).formatted(Formatting.GREEN), false);
      return 1;
    } catch (Exception e) {
      context.getSource().sendError(Text.literal("Error: " + e.getMessage()));
      e.printStackTrace();
      return 0;
    }
  }
}
