package com.github.ynverxe.littleheads.command;

import com.github.ynverxe.littleheads.controller.LittleHeadController;
import com.github.ynverxe.littleheads.controller.LittleHeadControllerService;
import com.github.ynverxe.littleheads.model.entity.LittleHeadModel;
import com.github.ynverxe.littleheads.model.entity.LittleHeadModelService;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;

public final class TestCommand {

  private final LittleHeadModelService modelService;
  private final LittleHeadControllerService controllerService;

  public TestCommand(LittleHeadModelService modelService,
      LittleHeadControllerService controllerService) {
    this.modelService = modelService;
    this.controllerService = controllerService;
  }

  @Command("spawn-head")
  public void spawnHead(Player sender) {
    LittleHeadModel model = modelService.model("default").orElseThrow();

    LittleHeadController littleHeadController = controllerService.spawnHead(sender, model);
  }
}