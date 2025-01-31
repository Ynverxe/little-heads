package com.github.ynverxe.littleheads.service;

import org.bukkit.plugin.java.JavaPlugin;

public interface Service {

  void load(JavaPlugin plugin) throws Exception;

  void reload(JavaPlugin plugin) throws Exception;

  void end(JavaPlugin plugin) throws Exception;

}