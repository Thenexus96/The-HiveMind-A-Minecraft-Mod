package net.sanfonic.hivemind.data.node;

import static org.junit.jupiter.api.Assertions.*;

import net.sanfonic.hivemind.data.HiveMindData.Hive;
import org.junit.jupiter.api.Test;

public class NodeTest {

    @Test
    public void testClaimForHiveAddsResources() {
        Hive hive = new Hive(java.util.UUID.randomUUID(), java.util.UUID.randomUUID());
        Node node = NodeManager.getNode("sample_node_1");
        assertNotNull(node);
        boolean ok = NodeManager.claimForHive(hive, "sample_node_1");
        assertTrue(ok);
        assertEquals(node.getResourceAmount(), hive.getResourceValue(node.getResourceType()));
    }
}
