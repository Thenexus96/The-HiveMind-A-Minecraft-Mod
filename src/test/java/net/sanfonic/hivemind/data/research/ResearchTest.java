package net.sanfonic.hivemind.data.research;

import static org.junit.jupiter.api.Assertions.*;

import net.minecraft.nbt.NbtCompound;
import net.sanfonic.hivemind.data.HiveMindData.Hive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Research Manager Tests")
public class ResearchTest {

    @Test
    public void testUnlockNodeWritesToHive() {
        Hive hive = new Hive(java.util.UUID.randomUUID(), java.util.UUID.randomUUID());
        boolean ok = ResearchManager.unlockForHive(hive, "soldier_damage_1");
        assertTrue(ok, "Unlock should succeed for known node");
        assertTrue(hive.hasResearchNode("soldier_damage_1"));
        NbtCompound state = hive.getResearchState();
        assertTrue(state.contains("soldier_damage_1"));
        NbtCompound node = state.getCompound("soldier_damage_1");
        assertEquals(5, node.getInt("soldier_damage_percent"));
    }
}
