package com.github.ynverxe.littleheads.entity.animation;

import com.github.ynverxe.littleheads.controller.LittleHeadController;

public class FloatingAnimation extends AbstractAnimation {

  private final LittleHeadController controller;
  private double progress;
  private final float maxNegative;
  private final float maxPositive;
  private final float delta;
  private boolean down;

  public FloatingAnimation(LittleHeadController controller, float range, float durationInTicks) {
    this.controller = controller;
    this.delta = range / durationInTicks;
    this.maxNegative = 0;
    this.maxPositive = range;
  }

  @Override
  public void tick() {
    double delta = fixedDelta();

    if (progress + delta > maxPositive) {
      this.down = true;
    } else if (progress + delta < maxNegative){
      this.down = false;
    }

    delta = getYDelta(progress, delta, maxNegative, maxPositive, 2.5);

    this.progress += delta;

    System.out.println(progress);
    System.out.println(delta);
    this.controller.entity().teleport(this.controller.entity().getLocation().add(0, delta, 0));
  }

  private float fixedDelta() {
    return down ? -delta : delta;
  }

  private double getYDelta(double progress, double delta, double maxNeg, double maxPos, double frictionFactor) {
    double center = (maxNeg + maxPos) / 2.0;
    double halfRange = (maxPos - maxNeg) / 2.0;
    double distanceToEdge = progress == maxNeg || progress == maxPos ? 0 : Math.abs(progress - center) / halfRange;

    double smoothFriction = Math.pow(distanceToEdge, frictionFactor);
    return delta * (1 - smoothFriction);
  }
}