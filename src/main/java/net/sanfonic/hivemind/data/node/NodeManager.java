package net.sanfonic.hivemind.data.node;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.sanfonic.hivemind.data.HiveMindData.Hive;
import net.sanfonic.hivemind.data.HiveMindData.HiveManager;
import net.sanfonic.hivemind.data.player.PlayerHiveComponent;

/** Registry and claim logic for prototype nodes */
public final class NodeManager {
  private static final Map<String, Node> REGISTRY = new HashMap<>();

  static {
    // Sample node: yields 10 hive_material
    Node sample = new Node("sample_node_1", "hive_material", 10);
    registerNode(sample);
  }

  private NodeManager() {}

  public static void registerNode(Node node) {
    REGISTRY.put(node.getId(), node);
  }

  public static Node getNode(String id) {
    return REGISTRY.get(id);
  }

  public static Map<String, Node> getAllNodes() {
    return Collections.unmodifiableMap(REGISTRY);
  }

  /** Claim a node for an in-memory Hive object (testable) */
  public static boolean claimForHive(Hive hive, String nodeId) {
    if (hive == null || nodeId == null) return false;
    Node node = getNode(nodeId);
    if (node == null) return false;
    // For prototype, simply add resources to hive
    hive.addResource(node.getResourceType(), node.getResourceAmount());
    return true;
  }

  /** Claim a node for a player in a running server; persists to HiveManager */
  public static boolean claimForPlayer(
      MinecraftServer server, ServerPlayerEntity player, String nodeId) {
    if (server == null || player == null || nodeId == null) return false;
    java.util.UUID hiveId = PlayerHiveComponent.getHiveId(player);
    if (hiveId == null) return false; // player has no hive

    HiveManager manager = HiveManager.getInstance(server);
    Hive hive = manager.getHive(hiveId);
    if (hive == null) return false;

    Node node = getNode(nodeId);
    if (node == null) return false;

    manager.addResourcesToHive(hive.getId(), node.getResourceType(), node.getResourceAmount());
    return true;
  }
}
