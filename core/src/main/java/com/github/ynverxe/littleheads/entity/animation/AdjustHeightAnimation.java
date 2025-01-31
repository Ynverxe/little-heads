package com.github.ynverxe.littleheads.entity.animation;

import com.github.ynverxe.littleheads.controller.LittleHeadController;
import com.github.ynverxe.littleheads.entity.PositionManager;
import com.github.ynverxe.littleheads.util.PositionUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

public class AdjustHeightAnimation extends AbstractAnimation {

  public static final String ID = "adjust_y_animation";
  public static final double MIN_DISTANCE_FROM_GROUND = 1.5;

  private final double delta = -0.10;
  private final LittleHeadController controller;
  private final Direction direction;

  public AdjustHeightAnimation(LittleHeadController controller) {
    this.controller = controller;
    this.direction = controller.positionManager().neededAdjustmentDirection();
  }

  @Override
  public void tick() {
    if (direction == null) return;

    double delta = this.delta;
    Location location = controller.entity().getLocation();
    double distance = PositionUtil.getDistanceToGround(controller.entity(), 5);

    if (distance == Double.MAX_VALUE) {
      distance = 5; // normalize distance to avoid infinity values
    }

    double distanceToMinDistance = Math.abs(distance - MIN_DISTANCE_FROM_GROUND);

    double newDelta = getYDelta(distanceToMinDistance, delta, 2, location.getWorld());
    newDelta = direction.factorFixer.apply(newDelta);
    controller.entity().teleport(location.clone().add(0, newDelta, 0));
  }

  @Override
  public boolean isTerminated() {
    return this.direction == null || this.direction.stopCriteria.test(this.controller);
  }

  private double getYDelta(double distanceToEdge, double delta, double frictionFactor, World world) {
    double smoothFriction = Math.pow(distanceToEdge / world.getMaxHeight(), frictionFactor);
    return delta * (1 - smoothFriction);
  }

  public enum Direction {
    DOWNWARD(Function.identity(), controller -> controller.positionManager().isNearTheGround()),
    UPWARD(Math::abs, controller -> controller.positionManager().lastCalcDistanceFromGroundUnsafe() >= PositionManager.MIN_DISTANCE_FROM_GROUND);

    private final Function<Double, Double> factorFixer;
    private final Predicate<LittleHeadController> stopCriteria;

    Direction(Function<Double, Double> factorFixer, Predicate<LittleHeadController> stopCriteria) {
      this.factorFixer = factorFixer;
      this.stopCriteria = stopCriteria;
    }
  }
}