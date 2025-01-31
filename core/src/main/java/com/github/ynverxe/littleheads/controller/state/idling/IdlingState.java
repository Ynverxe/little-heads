package com.github.ynverxe.littleheads.controller.state.idling;

import com.github.ynverxe.littleheads.controller.LittleHeadController;
import com.github.ynverxe.littleheads.controller.state.AbstractStateController;
import com.github.ynverxe.littleheads.entity.PositionManager;
import com.github.ynverxe.littleheads.entity.animation.AdjustHeightAnimation;
import com.github.ynverxe.littleheads.entity.animation.AnimationManager;
import com.github.ynverxe.littleheads.model.entity.LittleHeadState;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class IdlingState extends AbstractStateController {

  public IdlingState(@NotNull LittleHeadController headController,
      @NotNull LittleHeadState stateModel) {
    super(headController, stateModel);
    aiGroup.getGoalSelectors().add(new FollowOwnerGoal(1.7, 3, 10, headController));
  }

  @Override
  public void run() {
    super.run();

    AnimationManager animationManager = headController.animations();
    boolean hasPath = headController.pathfinder().hasPath();

    if (hasPath) return;

    PositionManager positionManager = headController.positionManager();

    AdjustHeightAnimation.Direction adjDirection = positionManager.neededAdjustmentDirection();
    if (adjDirection != null && !positionManager.isYBeingAdjusted()) {
      animationManager.setCurrentAnimation(AdjustHeightAnimation.ID, null, null);
    } else if (!positionManager.isYBeingAdjusted()) {
      animationManager.setCurrentAnimation("floating_animation", null, null);
    }

    String currentAnimation = animationManager.currentAnimationId();
    headController.armorStand().customName(Component.text(Objects.toString(currentAnimation, "no animation")));
    headController.armorStand().setCustomNameVisible(true);
    //System.out.println(headController.positionManager().isOnGround());
    //System.out.println(animationManager.currentAnimation());
  }
}