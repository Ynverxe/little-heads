package com.github.ynverxe.littleheads.model.entity;

import com.github.ynverxe.littleheads.exception.InvalidLittleHeadModel;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Processor;
import org.spongepowered.configurate.objectmapping.meta.Processor.Factory;

@ConfigSerializable
public class LittleHeadModel {

  private @NotNull String name = "";
  private @NotNull LittleHeadState defaultState = new LittleHeadState();
  private @NotNull List<LittleHeadState> states = new ArrayList<>();

  public @NotNull String name() {
    return name;
  }

  public LittleHeadModel name(@NotNull String name) {
    this.name = name;
    return this;
  }

  public @NotNull List<LittleHeadState> states() {
    return states;
  }

  public @NotNull LittleHeadState defaultState() {
    return defaultState;
  }

  public void checkIsValid() {
    if (name.isEmpty()) {
      throw new InvalidLittleHeadModel("Empty name", name);
    }

    defaultState.checkIsValid();

    for (LittleHeadState state : states) {
      try {
        state.checkIsValid();
      } catch (Exception e) {
        throw new InvalidLittleHeadModel("Invalid little head state", name, e);
      }
    }
  }
}