package net.sanfonic.hivemind.data.HiveMindData;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import net.minecraft.nbt.NbtCompound;
import org.junit.jupiter.api.Test;

public class HiveMindLinkManagerTest {

  @Test
  public void testLinkUnlinkAndPersistence() {
    HiveMindLinkManager mgr = new HiveMindLinkManager();
    UUID drone = UUID.randomUUID();
    UUID owner = UUID.randomUUID();

    assertFalse(mgr.isDroneLinked(drone));

    mgr.linkDroneToOwner(drone, owner);
    assertTrue(mgr.isDroneLinked(drone));
    assertEquals(owner, mgr.getDroneOwner(drone));
    assertEquals(1, mgr.getOwnerDrones(owner).size());

    NbtCompound nbt = new NbtCompound();
    mgr.writeNbt(nbt);

    HiveMindLinkManager loaded = HiveMindLinkManager.createFromNbt(nbt);
    assertEquals(owner, loaded.getDroneOwner(drone));

    loaded.unlinkDrone(drone);
    assertFalse(loaded.isDroneLinked(drone));
  }
}
