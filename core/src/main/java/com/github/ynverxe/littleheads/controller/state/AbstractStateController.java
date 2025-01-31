package com.github.ynverxe.littleheads.controller.state;

import com.github.ynverxe.littleheads.LittleHeadsPlugin;
import com.github.ynverxe.littleheads.controller.LittleHeadController;
import com.github.ynverxe.littleheads.entity.ai.EntityAIGroup;
import com.github.ynverxe.littleheads.model.entity.LittleHeadState;
import com.github.ynverxe.littleheads.util.SkullSetter;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractStateController implements Runnable, Listener {

  protected final @NotNull EntityAIGroup aiGroup;
  protected final @NotNull LittleHeadController headController;
  protected final @Nullable LittleHeadState stateModel;

  public AbstractStateController(@NotNull LittleHeadController headController,
      @Nullable LittleHeadState stateModel) {
    this.headController = Objects.requireNonNull(headController);
    this.stateModel = stateModel;
    this.aiGroup = new EntityAIGroup(headController);
  }

  public void init() {
    Bukkit.getPluginManager().registerEvents(this, LittleHeadsPlugin.plugin());

    if (this.stateModel != null) {
      ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
      ItemMeta meta = itemStack.getItemMeta();
      if (meta instanceof SkullMeta skullMeta) {
        SkullSetter.setSkin(this.stateModel.texture(), skullMeta);
        itemStack.setItemMeta(meta);
        this.headController.armorStand().getEquipment().setHelmet(itemStack);
      }
    }
  }

  @Override
  public void run() {
    aiGroup.tick();
  }

  public void finish() {
    HandlerList.unregisterAll(this);
  }

  protected @NotNull Optional<LittleHeadState> stateModel() {
    return Optional.ofNullable(stateModel);
  }
}