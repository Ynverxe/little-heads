package com.github.ynverxe.littleheads.entity.animation;

import com.github.ynverxe.littleheads.controller.LittleHeadController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class AnimationManager {

  private final Map<String, AbstractAnimation.Provider> animationProviders = new HashMap<>();
  private final @NotNull LittleHeadController controller;
  private @Nullable AbstractAnimation currentAnimation;
  private @Nullable String currentAnimationId;

  public AnimationManager(@NotNull LittleHeadController controller) {
    this.controller = controller;
    addAnimation("floating_animation", headController -> new FloatingAnimation(controller, 1F, 20));
    addAnimation(AdjustHeightAnimation.ID, AdjustHeightAnimation::new);
  }

  public void tick() {
    if (controller.pathfinder().hasPath()) {
      setCurrentAnimation(null, null, null);
      return;
    }

    if (currentAnimation != null) {
      if (currentAnimation.isTerminated()) {
        this.currentAnimationId = null;
        this.currentAnimation = null;
      } else {
        this.currentAnimation.tick();
      }
    }
  }

  public <T extends AbstractAnimation> @Nullable T setCurrentAnimation(@Nullable String name, @Nullable Class<T> animationClass, @Nullable Consumer<T> configurator) {
    if (name != null && !this.animationProviders.containsKey(name.toLowerCase(Locale.ROOT)))
      throw new IllegalArgumentException("Unknown animation '" + name + "'");

    if (Objects.equals(this.currentAnimationId, name)) return animationClass != null ? animationClass.cast(this.currentAnimation) : null;

    try {
      if (this.currentAnimation != null) {
        this.currentAnimation.onForcedStop();
      }
    } finally {
      this.currentAnimationId = name;
      if (name != null) {
        this.currentAnimation = this.animationProviders.get(name).create(controller);
      }

      if (this.currentAnimation != null) {
        if (configurator != null) configurator.accept((T) this.currentAnimation);
      }
    }

    return animationClass != null ? animationClass.cast(this.currentAnimation) : null;
  }

  public @Nullable AbstractAnimation currentAnimation() {
    return currentAnimation;
  }

  public @Nullable <T extends AbstractAnimation> T currentAnimation(@NotNull Class<T> expectedType) {
    return expectedType.isInstance(this.currentAnimation) ? expectedType.cast(this.currentAnimation) : null;
  }

  public @Nullable String currentAnimationId() {
    return currentAnimationId;
  }

  public AnimationManager addAnimation(String name, AbstractAnimation.Provider animationProvider) {
    this.animationProviders.put(name.toLowerCase(Locale.ROOT), animationProvider);
    return this;
  }
}