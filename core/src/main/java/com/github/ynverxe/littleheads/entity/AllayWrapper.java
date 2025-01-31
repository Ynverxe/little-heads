package com.github.ynverxe.littleheads.entity;

import com.github.ynverxe.littleheads.nms.MovementControllerWrapper;
import org.bukkit.entity.Allay;
import org.jetbrains.annotations.NotNull;

public interface AllayWrapper {

  @NotNull MovementControllerWrapper movementWrapper();

  @NotNull Allay allay();

  boolean isOnGround();

}