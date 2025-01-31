package com.github.ynverxe.littleheads.controller.state;

import com.github.ynverxe.littleheads.controller.LittleHeadController;
import com.github.ynverxe.littleheads.model.entity.LittleHeadState;
import org.jetbrains.annotations.NotNull;

public interface StateFactory {
  @NotNull AbstractStateController createNew(@NotNull LittleHeadController littleHeadController, @NotNull
      LittleHeadState stateModel);
}