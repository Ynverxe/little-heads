package com.github.ynverxe.littleheads.controller.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.github.ynverxe.littleheads.controller.state.idling.IdlingState;
import org.jetbrains.annotations.NotNull;

public final class StateTypeRegistry {

  public static final StateTypeRegistry REGISTRY = new StateTypeRegistry();

  private StateTypeRegistry() {
    registerType("idling", IdlingState::new);
  }

  private final @NotNull Map<String, StateFactory> factoryMap = new HashMap<>();

  public void registerType(@NotNull String typeName, @NotNull StateFactory factory) {
    this.factoryMap.put(typeName.toLowerCase(), Objects.requireNonNull(factory, "factory"));
  }

  public @NotNull Optional<StateFactory> factory(@NotNull String typeName) {
    return Optional.ofNullable(this.factoryMap.get(typeName.toLowerCase()));
  }
}