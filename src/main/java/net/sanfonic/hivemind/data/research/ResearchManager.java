package net.sanfonic.hivemind.data.research;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.sanfonic.hivemind.data.HiveMindData.Hive;

/** Registry and simple unlock logic for research nodes */
public final class ResearchManager {
    private static final Map<String, ResearchNode> REGISTRY = new HashMap<>();

    static {
        // Sample node: +5% soldier damage
        ResearchNode soldierDamage = new ResearchNode("soldier_damage_1", "+5% Soldier Damage")
                .addCost("research_points", 1)
                .addEffect("soldier_damage_percent", 5);
        registerNode(soldierDamage);
    }

    private ResearchManager() {}

    public static void registerNode(ResearchNode node) {
        REGISTRY.put(node.getId(), node);
    }

    public static ResearchNode getNode(String id) {
        return REGISTRY.get(id);
    }

    public static Map<String, ResearchNode> getAllNodes() {
        return Collections.unmodifiableMap(REGISTRY);
    }

    /**
     * Unlocks a node for the given hive. Writes node effect into hive.researchState
     */
    public static boolean unlockForHive(Hive hive, String nodeId) {
        if (hive == null || nodeId == null) return false;
        if (hive.hasResearchNode(nodeId)) return false; // already unlocked
        ResearchNode node = getNode(nodeId);
        if (node == null) return false;

        // Persist effect into hive's research state as NBT
        NbtCompound effectNbt = new NbtCompound();
        for (Map.Entry<String,Integer> e : node.getEffects().entrySet()) {
            effectNbt.putInt(e.getKey(), e.getValue());
        }
        hive.setResearchState(nodeId, effectNbt);
        return true;
    }
}
