package com.github.ynverxe.littleheads.listener;

import com.github.ynverxe.littleheads.controller.LittleHeadControllerService;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.UUID;

public class PlayerInteractAtEntityListener implements Listener {

  private final LittleHeadControllerService controllerService;

  public PlayerInteractAtEntityListener(LittleHeadControllerService controllerService) {
    this.controllerService = controllerService;
  }

  @EventHandler
  public void onInteractWithEntityController(PlayerInteractAtEntityEvent event) {
    Entity clicked = event.getRightClicked();
    UUID uuid = clicked.getUniqueId();
    controllerService.controllerByEntityId(uuid)
        .ifPresent(controller -> event.setCancelled(true));
  }
}
