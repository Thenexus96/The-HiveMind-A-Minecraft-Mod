package net.sanfonic.hivemind.network;

import net.minecraft.util.Identifier;
import net.sanfonic.hivemind.Hivemind;

public final class DebugNetworkConstants {
  public static final Identifier DEBUG_SPAWN_PACKET =
      new Identifier(Hivemind.MOD_ID, "debug_spawn");
  public static final Identifier DEBUG_KILL_NEARBY_PACKET =
      new Identifier(Hivemind.MOD_ID, "debug_kill_nearby");
  public static final Identifier DEBUG_TELEPORT_PACKET =
      new Identifier(Hivemind.MOD_ID, "debug_teleport");

  private DebugNetworkConstants() {}
}
