package com.github.ynverxe.littleheads.listener;

import com.github.ynverxe.littleheads.LittleHeadsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    LittleHeadsPlugin.plugin().littleHeadControllerService()
        .onPlayerQuit(event.getPlayer());
  }
}