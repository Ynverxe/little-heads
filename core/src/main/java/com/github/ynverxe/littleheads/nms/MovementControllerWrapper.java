package com.github.ynverxe.littleheads.nms;

import org.bukkit.util.Vector;

import java.util.function.Consumer;

public interface MovementControllerWrapper {
  void setUpwardSpeed(double upwardSpeed);

  void setWantedPos(double x, double y, double z, double speed);

  Vector getWantedPos();

  void changeUpwardSpeed(boolean below, double speed);

  default void modifyWantedPos(Consumer<Vector> vectorConsumer, double speed) {
    Vector vector = getWantedPos();
    vectorConsumer.accept(vector);
    setWantedPos(vector.getX(), vector.getY(), vector.getZ(), speed);
  }
}