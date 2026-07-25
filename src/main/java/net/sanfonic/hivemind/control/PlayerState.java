package net.sanfonic.hivemind.control;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.util.math.Vec3d;

public class PlayerState {
  private final Vec3d position;
  private final float xRot;
  private final float yRot;
  private final PlayerAbilities abilities;

  public PlayerState(Vec3d position, float xRot, float yRot, PlayerAbilities abilities) {
    this.position = position;
    this.xRot = xRot;
    this.yRot = yRot;
    this.abilities = copyAbilities(abilities);
  }

  public Vec3d getPosition() {
    return position;
  }

  public float getxRot() {
    return xRot;
  }

  public float getyRot() {
    return yRot;
  }

  public PlayerAbilities getAbilities() {
    return copyAbilities(this.abilities);
  }

  private static PlayerAbilities copyAbilities(PlayerAbilities source) {
    // Create a new PlayerAbilites and copy values manually
    PlayerAbilities copiedAbilites = new PlayerAbilities();
    copiedAbilites.allowFlying = source.allowFlying;
    copiedAbilites.flying = source.flying;
    copiedAbilites.invulnerable = source.invulnerable;
    copiedAbilites.creativeMode = source.creativeMode;
    copiedAbilites.allowModifyWorld = source.allowModifyWorld;
    copiedAbilites.setFlySpeed(source.getFlySpeed());
    copiedAbilites.setWalkSpeed(source.getWalkSpeed());
    return copiedAbilites;
  }
}
