package com.github.ynverxe.littleheads.model.entity;

import java.util.Objects;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class LittleHeadState {

  private @NotNull String type = "";
  private @NotNull String name = "";
  private @NotNull String texture = "";
  private @MonotonicNonNull ConfigurationNode options;

  public @NotNull String name() {
    return name;
  }

  public @NotNull String texture() {
    return Objects.requireNonNull(texture, "texture");
  }

  public @NotNull String type() {
    return type;
  }

  public LittleHeadState texture(@NonNull String texture) {
    this.texture = texture;
    return this;
  }

  public @NotNull ConfigurationNode node() {
    return options;
  }

  public void checkIsValid() {
    if (name.isEmpty()) {
      throw new IllegalArgumentException("Empty name");
    }

    if (type.isBlank()) {
      throw new IllegalArgumentException("No type provided");
    }

    if (texture.isEmpty()) {
      throw new IllegalArgumentException("No texture provided");
    }
  }
}