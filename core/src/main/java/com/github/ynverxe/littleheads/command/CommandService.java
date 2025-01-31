package com.github.ynverxe.littleheads.command;

import com.github.ynverxe.littleheads.LittleHeadsPlugin;
import com.github.ynverxe.littleheads.service.Service;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.execution.ExecutionCoordinator;

public class CommandService implements Service {

  private BukkitCommandManager<CommandSender> commandManager;
  private AnnotationParser<CommandSender> annotationParser;
  private LittleHeadsPlugin plugin;

  @Override
  public void load(JavaPlugin plugin) throws Exception {
    this.plugin = (LittleHeadsPlugin) plugin;

    this.commandManager = new BukkitCommandManager<>(plugin, ExecutionCoordinator.simpleCoordinator(), SenderMapper.identity()) {};
    this.annotationParser = new AnnotationParser<>(commandManager, CommandSender.class);
    this.annotationParser.parse(new TestCommand(this.plugin.littleHeadModelService(), this.plugin.littleHeadControllerService()));
  }

  @Override
  public void reload(JavaPlugin plugin) throws Exception {

  }

  @Override
  public void end(JavaPlugin plugin) throws Exception {

  }
}