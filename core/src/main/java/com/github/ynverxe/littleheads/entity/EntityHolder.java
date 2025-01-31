package com.github.ynverxe.littleheads.entity;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface EntityHolder<T extends Entity> {
  @NotNull T entity();
}