package com.github.ynverxe.littleheads.entity.ai;

import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

public abstract class GoalSelector {

  private WeakReference<EntityAIGroup> holder = new WeakReference<>(null);

  public abstract boolean shouldStart(@NotNull Mob entity);

  public abstract void start(@NotNull Mob entity);

  public abstract boolean shouldEnd(@NotNull Mob entity);

  public abstract void end(@NotNull Mob entity);

  public abstract void tick(@NotNull Mob entity);

  void attach(EntityAIGroup holder) {
    this.holder = new WeakReference<>(holder);
  }

  public @Nullable EntityAIGroup holder() {
    return this.holder.get();
  }
}
