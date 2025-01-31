package com.github.ynverxe.littleheads.controller;

import com.destroystokyo.paper.entity.Pathfinder;
import com.github.ynverxe.littleheads.LittleHeadsPlugin;
import com.github.ynverxe.littleheads.controller.state.AbstractStateController;
import com.github.ynverxe.littleheads.controller.state.StateFactory;
import com.github.ynverxe.littleheads.controller.state.StateTypeRegistry;
import com.github.ynverxe.littleheads.entity.AllayWrapper;
import com.github.ynverxe.littleheads.entity.EntityHolder;
import com.github.ynverxe.littleheads.entity.PositionManager;
import com.github.ynverxe.littleheads.entity.animation.AnimationManager;
import com.github.ynverxe.littleheads.model.entity.LittleHeadModel;
import com.github.ynverxe.littleheads.model.entity.LittleHeadState;
import com.github.ynverxe.littleheads.nms.CustomAllay;
import com.github.ynverxe.littleheads.nms.MovementControllerWrapper;

import java.util.Objects;

import com.github.ynverxe.littleheads.util.PositionUtil;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class LittleHeadController implements Runnable, Listener, EntityHolder<Mob> {

  private final @NotNull ArmorStand armorStand;

  private final @NotNull AnimationManager animationManager;
  private final @NotNull PositionManager positionManager;
  private final @NotNull LittleHeadControllerService service;
  private final @NotNull LittleHeadModel model;
  private final @NotNull AllayWrapper allayWrapper;
  private final @NotNull Player owner;
  private @NotNull Runnable afterTicking = () -> {};

  private volatile @NotNull AbstractStateController currentState;

  private final boolean debugPathfinding = true;

  public LittleHeadController(
      @NotNull LittleHeadControllerService service,
      @NotNull LittleHeadModel model,
      @NotNull Player owner
  ) {
    this.model = Objects.requireNonNull(model);
    this.owner = Objects.requireNonNull(owner);
    this.service = Objects.requireNonNull(service);
    this.armorStand = spawnEntity();
    this.allayWrapper = spawnInvisibleMob(armorStand.getLocation());
    this.currentState = newState(model.defaultState());
    this.currentState.init();

    this.animationManager = new AnimationManager(this);
    this.positionManager = new PositionManager(this);
  }

  public void init() {
    Bukkit.getPluginManager().registerEvents(this, LittleHeadsPlugin.plugin());
  }

  public void finish() {
    HandlerList.unregisterAll(this);
  }

  @Override
  public void run() {
    if (!armorStand.isValid()) {
      this.service.unregisterController(this);
      return;
    }

    try {
      this.currentState.run();
      this.animationManager.tick();
      this.debugPathfinding();
    } finally {
      this.moveArmorStandToMainEntity();
    }

    this.afterTicking.run();
    this.positionManager.tick();
  }

  public @NotNull PositionManager positionManager() {
    return positionManager;
  }

  public @NotNull Pathfinder pathfinder() {
    return this.allayWrapper.allay().getPathfinder();
  }

  public @NotNull MovementControllerWrapper movement() {
    return allayWrapper.movementWrapper();
  }

  public @NotNull AnimationManager animations() {
    return animationManager;
  }

  public void currentState(
      @NotNull AbstractStateController currentState) {
    this.currentState.finish(); // finish old
    this.currentState = currentState;
    this.currentState.init(); // init new
  }

  public @NotNull Player owner() {
    return owner;
  }

  private @NotNull ArmorStand spawnEntity() {
    Vector ownerFacingDirection = owner.getEyeLocation().getDirection();

    Location inFrontOfPlayer = owner.getEyeLocation()
        .clone()
        .add(ownerFacingDirection.normalize().multiply(2D));

    ArmorStand armorStand = (ArmorStand) inFrontOfPlayer.getWorld().spawnEntity(inFrontOfPlayer, EntityType.ARMOR_STAND);
    armorStand.setArms(false);
    armorStand.setBasePlate(false);
    armorStand.setVisible(false);
    return armorStand;
  }

  private static AllayWrapper spawnInvisibleMob(Location at) {
    CustomAllay mob = CustomAllay.spawn(at.getWorld(), at.getX(), at.getY(), at.getZ());
    mob.allay().setPersistent(false);
    //mob.allay().setInvisible(true);
    return mob;
  }

  public @NotNull ArmorStand armorStand() {
    return armorStand;
  }

  @Override
  public @NotNull Mob entity() {
    return this.allayWrapper.allay();
  }

  public @NotNull AllayWrapper getAllayWrapper() {
    return allayWrapper;
  }

  private AbstractStateController newState(@NotNull LittleHeadState model) {
    StateFactory factory = StateTypeRegistry.REGISTRY.factory(model.type())
        .orElseThrow(() -> new IllegalArgumentException("Cannot create state for type: " + model.type()));

    return factory.createNew(this, model);
  }

  private void debugPathfinding() {
    if (!debugPathfinding) return;

    Pathfinder.PathResult result = pathfinder().getCurrentPath();
    if (result != null) {
      for (Location point : result.getPoints()) {
        World world = point.getWorld();
        world.spawnParticle(Particle.DUST, point, 1, new Particle.DustOptions(Color.RED, 1));
      }
    }
  }

  private void moveArmorStandToMainEntity() {
    Location fixedLoc = this.entity().getLocation()
        .clone()
        .subtract(new Vector(0, 1.4, 0));

    this.armorStand.teleport(fixedLoc);
    PositionUtil.setArmorStandRotationInDegrees(this.armorStand, this.entity().getPitch(), this.entity().getYaw());
  }
}