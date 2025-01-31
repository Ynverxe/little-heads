package com.github.ynverxe.littleheads.listener.entity;

import com.github.ynverxe.littleheads.controller.LittleHeadControllerService;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;

public class EntityDamageListener implements Listener {

  private final LittleHeadControllerService controllerService;

  public EntityDamageListener(LittleHeadControllerService controllerService) {
    this.controllerService = controllerService;
  }

  @EventHandler
  public void onInteractWithEntityController(EntityDamageEvent event) {
    Entity clicked = event.getEntity();
    UUID uuid = clicked.getUniqueId();
    controllerService.controllerByEntityId(uuid)
        .ifPresent(controller -> event.setCancelled(true));
  }
}