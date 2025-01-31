package com.github.ynverxe.littleheads.controller.state.idling;

import com.github.ynverxe.littleheads.controller.LittleHeadController;
import com.github.ynverxe.littleheads.entity.ai.GoalSelector;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

public class FollowOwnerGoal extends GoalSelector {

  private final double speed;
  private final int maxDistanceToOwner; // squared
  private final int minDistanceFromOwner; // squared
  private final LittleHeadController controller;

  public FollowOwnerGoal(double speed, int maxDistanceToOwner, int minDistanceFromOwner, LittleHeadController controller) {
    this.speed = speed;
    this.maxDistanceToOwner = maxDistanceToOwner * maxDistanceToOwner;
    this.minDistanceFromOwner = minDistanceFromOwner * minDistanceFromOwner;
    this.controller = controller;
  }

  @Override
  public boolean shouldStart(@NotNull Mob entity) {
    double distance = controller.owner().getLocation().distanceSquared(entity.getLocation());

    return distance > minDistanceFromOwner;
  }

  @Override
  public void start(@NotNull Mob entity) {
  }

  @Override
  public boolean shouldEnd(@NotNull Mob entity) {
    double distance = controller.owner().getLocation().distanceSquared(entity.getLocation());
    return distance <= maxDistanceToOwner;
  }

  @Override
  public void end(@NotNull Mob entity) {
    entity.getPathfinder().stopPathfinding();
  }

  @Override
  public void tick(@NotNull Mob entity) {
    entity.getPathfinder().moveTo(controller.owner(), speed);
  }
}