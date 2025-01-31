package com.github.ynverxe.littleheads.entity.animation;

import com.github.ynverxe.littleheads.controller.LittleHeadController;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractAnimation {

  public abstract void tick();

  public boolean isTerminated() {
    return false;
  }

  public void onForcedStop() {}

  public interface Provider {
    AbstractAnimation create(@NotNull LittleHeadController controller);
  }
}