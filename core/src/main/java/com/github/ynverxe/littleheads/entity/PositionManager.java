package com.github.ynverxe.littleheads.entity;

import com.github.ynverxe.littleheads.controller.LittleHeadController;
import com.github.ynverxe.littleheads.entity.animation.AdjustHeightAnimation;
import com.github.ynverxe.littleheads.util.PositionUtil;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class PositionManager {

  public static final double MIN_DISTANCE_FROM_GROUND = 1.5D;

  private final LittleHeadController controller;
  private boolean isNearTheGround; // Custom bounding box is 1.5 blocks height, so if it collides downwards means that entity is 1.5 blocks aprox from the ground
  private double lastCalcDistanceFromGroundUnsafe; //
  private @Nullable AdjustHeightAnimation.Direction neededAdjustmentDirection;

  public PositionManager(LittleHeadController controller) {
    this.controller = controller;
  }

  public void tick() {
    this.neededAdjustmentDirection = calculateNeededYAdjust();
    this.lastCalcDistanceFromGroundUnsafe = PositionUtil.getDistanceToGround(controller.entity(), 5);
    Entity entity = this.controller.entity();

    BoundingBox temp = newBB(0.35F, -1.6F, entity.getX(), entity.getY(), entity.getZ());
    this.isNearTheGround = entity.getWorld().hasCollisionsIn(temp);

    drawBoundingBox(temp, entity.getWorld());
  }

  public double lastCalcDistanceFromGroundUnsafe() {
    return lastCalcDistanceFromGroundUnsafe;
  }

  public boolean isYBeingAdjusted() {
    return AdjustHeightAnimation.ID.equalsIgnoreCase(controller.animations().currentAnimationId());
  }

  public void setNeededAdjustmentDirection(@Nullable AdjustHeightAnimation.Direction neededAdjustmentDirection) {
    this.neededAdjustmentDirection = neededAdjustmentDirection;
  }

  public @Nullable AdjustHeightAnimation.Direction neededAdjustmentDirection() {
    return neededAdjustmentDirection;
  }

  public boolean isNearTheGround() {
    return this.isNearTheGround;
  }

  private BoundingBox newBB(float width, float height, double x, double y, double z) {
    float f = width / 2.0F;
    return BoundingBox.of(new Vector(x - (double)f, y, z - (double)f), new Vector(x + (double)f, y + (double) height, z + (double)f));
  }

  public static void drawBoundingBox(BoundingBox boundingBox, World world) {
    // Obtener las esquinas de la BoundingBox
    Vector min = boundingBox.getMin(); // Esquina inferior izquierda
    Vector max = boundingBox.getMax(); // Esquina superior derecha

    world.spawnParticle(Particle.DUST, min.toLocation(world), 1, new Particle.DustOptions(Color.RED, 1));
    world.spawnParticle(Particle.DUST, max.toLocation(world), 1, new Particle.DustOptions(Color.RED, 1));
  }

  private @Nullable AdjustHeightAnimation.Direction calculateNeededYAdjust() {
    double distanceToGround = PositionUtil.getDistanceToGround(controller.entity(), 5);

    if (isNearTheGround()) {
      return distanceToGround < PositionManager.MIN_DISTANCE_FROM_GROUND ? AdjustHeightAnimation.Direction.UPWARD : null;
    }

    if (distanceToGround == Double.MAX_VALUE) {
      return AdjustHeightAnimation.Direction.DOWNWARD;
    }

    double failRange = distanceToGround - PositionManager.MIN_DISTANCE_FROM_GROUND;
    return failRange > 2 ? AdjustHeightAnimation.Direction.DOWNWARD : null;
  }
}