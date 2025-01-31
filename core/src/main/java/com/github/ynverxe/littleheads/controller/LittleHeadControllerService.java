package com.github.ynverxe.littleheads.controller;

import com.github.ynverxe.littleheads.LittleHeadsPlugin;
import com.github.ynverxe.littleheads.model.entity.LittleHeadModel;
import com.github.ynverxe.littleheads.service.Service;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LittleHeadControllerService implements Service {

  private final @NotNull Set<UUID> defectiveController = new HashSet<>();
  private final @NotNull Map<UUID, LittleHeadController> runningControllers = new ConcurrentHashMap<>();
  private final @NotNull Map<UUID, LittleHeadController> controllersById = new ConcurrentHashMap<>();
  private @Nullable BukkitTask updaterTask;

  @Override
  public void load(JavaPlugin plugin) throws Exception {
    this.runningControllers.clear();

    this.updaterTask = Bukkit.getScheduler().runTaskTimer(LittleHeadsPlugin.plugin(), this::updateAll, 0L, 1L);
  }

  @Override
  public void reload(JavaPlugin plugin) throws Exception {
    this.updaterTask.cancel();

    load(plugin);
  }

  @Override
  public void end(JavaPlugin plugin) throws Exception {

  }

  public @NotNull LittleHeadController spawnHead(@NotNull Player owner, @NotNull LittleHeadModel model) {
    LittleHeadController controller = new LittleHeadController(this, model, owner);
    controller.init();
    register(controller);
    return controller;
  }

  private void register(LittleHeadController controller) {
    this.controllersById.put(controller.armorStand().getUniqueId(), controller);
    this.runningControllers.put(controller.owner().getUniqueId(), controller);
  }

  private void unregister(LittleHeadController controller) {
    this.controllersById.remove(controller.armorStand().getUniqueId());
    this.runningControllers.remove(controller.owner().getUniqueId());
  }

  public void unregisterController(@NotNull LittleHeadController controller) {
    try {
      controller.finish();
    } finally {
      this.runningControllers.remove(controller.owner().getUniqueId());
    }
  }

  public void onPlayerQuit(@NotNull Player player) {
    LittleHeadController controller = this.runningControllers.get(player.getUniqueId());
    if (controller != null) {
      unregisterController(controller);
    }
  }

  private void updateAll() {
    for (Entry<UUID, LittleHeadController> entry : this.runningControllers.entrySet()) {
      UUID owner = entry.getKey();
      LittleHeadController controller = entry.getValue();

      try {
        controller.run();
      } catch (Exception e) {
        this.defectiveController.add(owner);
        unregister(controller);
        throw new RuntimeException("Controller for owner '" + owner + "' caused an error while ticking", e);
      }
    }
  }

  public @NotNull Optional<LittleHeadController> controllerByEntityId(@NotNull UUID uuid) {
    return Optional.ofNullable(this.controllersById.get(uuid));
  }
}