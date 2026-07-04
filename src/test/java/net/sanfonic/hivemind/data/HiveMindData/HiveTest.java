package net.sanfonic.hivemind.data.HiveMindData;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import net.minecraft.nbt.NbtCompound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Hive and HiveManager Tests")
class HiveTest {

  private UUID ownerUuid;
  private UUID memberUuid1;
  private UUID memberUuid2;
  private Hive hive;

  @BeforeEach
  void setUp() {
    ownerUuid = UUID.randomUUID();
    memberUuid1 = UUID.randomUUID();
    memberUuid2 = UUID.randomUUID();
    hive = new Hive(UUID.randomUUID(), ownerUuid);
  }

  @Test
  @DisplayName("Hive creation includes owner as member")
  void testHiveCreation() {
    assertTrue(hive.isMember(ownerUuid), "Owner should be a member of newly created hive");
    assertEquals(1, hive.getMembers().size(), "New hive should have exactly one member");
  }

  @Test
  @DisplayName("Can add members to hive")
  void testAddMembers() {
    assertTrue(hive.addMember(memberUuid1), "Should successfully add new member");
    assertTrue(hive.isMember(memberUuid1), "Member should be present in hive");
    assertEquals(2, hive.getMembers().size(), "Hive should have two members");

    // Adding same member twice should return false
    assertFalse(hive.addMember(memberUuid1), "Adding duplicate member should return false");
    assertEquals(2, hive.getMembers().size(), "Hive should still have two members");
  }

  @Test
  @DisplayName("Cannot remove hive owner")
  void testCannotRemoveOwner() {
    assertFalse(hive.removeMember(ownerUuid), "Should not be able to remove owner from hive");
    assertTrue(hive.isMember(ownerUuid), "Owner should still be a member");
  }

  @Test
  @DisplayName("Can remove regular members")
  void testRemoveMembers() {
    hive.addMember(memberUuid1);
    assertTrue(hive.isMember(memberUuid1), "Member should be added");

    assertTrue(hive.removeMember(memberUuid1), "Should successfully remove member");
    assertFalse(hive.isMember(memberUuid1), "Member should no longer be present");
    assertEquals(1, hive.getMembers().size(), "Hive should have only owner as member");
  }

  @Test
  @DisplayName("Can manage resources")
  void testResourceManagement() {
    assertEquals(0, hive.getResourceValue("wood"), "New hive should have no resources");

    hive.addResource("wood", 10);
    assertEquals(10, hive.getResourceValue("wood"), "Should have 10 wood after adding");

    hive.addResource("wood", 5);
    assertEquals(15, hive.getResourceValue("wood"), "Should have 15 wood after adding again");

    hive.setResourceValue("wood", 5);
    assertEquals(5, hive.getResourceValue("wood"), "Should be able to set resource value");

    hive.setResourceValue("wood", 0);
    assertEquals(0, hive.getResourceValue("wood"), "Should remove resource when set to 0");
  }

  @Test
  @DisplayName("Can serialize and deserialize hive to NBT")
  void testNbtSerialization() {
    hive.addMember(memberUuid1);
    hive.addMember(memberUuid2);
    hive.addResource("wood", 100);
    hive.addResource("stone", 50);

    NbtCompound nbt = hive.toNbt();
    Hive loadedHive = Hive.fromNbt(nbt);

    assertEquals(hive.getId(), loadedHive.getId(), "IDs should match");
    assertEquals(hive.getOwnerUuid(), loadedHive.getOwnerUuid(), "Owner UUIDs should match");
    assertEquals(
        hive.getMembers().size(), loadedHive.getMembers().size(), "Member counts should match");
    assertTrue(loadedHive.isMember(memberUuid1), "Loaded hive should have member 1");
    assertTrue(loadedHive.isMember(memberUuid2), "Loaded hive should have member 2");
    assertEquals(100, loadedHive.getResourceValue("wood"), "Wood resources should match");
    assertEquals(50, loadedHive.getResourceValue("stone"), "Stone resources should match");
  }
}
