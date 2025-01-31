package com.github.ynverxe.littleheads;

import com.github.ynverxe.littleheads.command.CommandService;
import com.github.ynverxe.littleheads.controller.LittleHeadControllerService;
import com.github.ynverxe.littleheads.listener.PlayerInteractAtEntityListener;
import com.github.ynverxe.littleheads.listener.PlayerQuitListener;
import com.github.ynverxe.littleheads.listener.entity.EntityDamageListener;
import com.github.ynverxe.littleheads.model.entity.LittleHeadModelService;
import java.io.IOException;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class LittleHeadsPlugin extends JavaPlugin {

  private final LittleHeadControllerService littleHeadControllerService = new LittleHeadControllerService();
  private final LittleHeadModelService littleHeadModelService = new LittleHeadModelService(this);
  private final CommandService commandService = new CommandService();

  public LittleHeadsPlugin() throws IOException {
  }

  @Override
  public void onEnable() {
    try {
      this.littleHeadModelService.load(this);
      this.littleHeadControllerService.load(this);
      this.commandService.load(this);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    registerListeners();
  }

  private void registerListeners() {
    PluginManager manager = Bukkit.getPluginManager();
    manager.registerEvents(new PlayerQuitListener(), this);
    manager.registerEvents(new EntityDamageListener(littleHeadControllerService), this);
    manager.registerEvents(new PlayerInteractAtEntityListener(littleHeadControllerService), this);
  }

  public LittleHeadControllerService littleHeadControllerService() {
    return littleHeadControllerService;
  }

  public LittleHeadModelService littleHeadModelService() {
    return littleHeadModelService;
  }

  public static @NotNull LittleHeadsPlugin plugin() {
    return JavaPlugin.getPlugin(LittleHeadsPlugin.class);
  }

  public static @NotNull Logger logger() {
    return plugin().getLogger();
  }
}