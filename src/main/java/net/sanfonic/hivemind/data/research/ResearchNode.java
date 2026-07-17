package net.sanfonic.hivemind.data.research;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NbtCompound;

/** Simple research node definition */
public class ResearchNode {
    private final String id;
    private final String displayName;
    private final Map<String, Integer> cost;
    private final Map<String, Integer> effects; // key -> integer value (percent or flat)

    public ResearchNode(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
        this.cost = new HashMap<>();
        this.effects = new HashMap<>();
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }

    public Map<String,Integer> getCost() { return new HashMap<>(cost); }
    public Map<String,Integer> getEffects() { return new HashMap<>(effects); }

    public ResearchNode addCost(String resource, int amount) { cost.put(resource, amount); return this; }
    public ResearchNode addEffect(String key, int value) { effects.put(key, value); return this; }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("id", id);
        nbt.putString("name", displayName);
        NbtCompound costN = new NbtCompound();
        for (Map.Entry<String,Integer> e : cost.entrySet()) costN.putInt(e.getKey(), e.getValue());
        nbt.put("cost", costN);
        NbtCompound effN = new NbtCompound();
        for (Map.Entry<String,Integer> e : effects.entrySet()) effN.putInt(e.getKey(), e.getValue());
        nbt.put("effects", effN);
        return nbt;
    }

    public static ResearchNode fromNbt(NbtCompound nbt) {
        String id = nbt.getString("id");
        String name = nbt.getString("name");
        ResearchNode node = new ResearchNode(id, name);
        if (nbt.contains("cost")) {
            NbtCompound costN = nbt.getCompound("cost");
            for (String k : costN.getKeys()) node.addCost(k, costN.getInt(k));
        }
        if (nbt.contains("effects")) {
            NbtCompound effN = nbt.getCompound("effects");
            for (String k : effN.getKeys()) node.addEffect(k, effN.getInt(k));
        }
        return node;
    }
}
