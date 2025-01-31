package com.github.ynverxe.littleheads.entity.ai;

import com.github.ynverxe.littleheads.entity.EntityHolder;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Taken from Minestom hehe ty!
 */
public class EntityAIGroup {

  private final EntityHolder<Mob> entityHolder;

  public EntityAIGroup(EntityHolder<Mob> entityHolder) {
    this.entityHolder = entityHolder;
  }

  private GoalSelector currentGoalSelector;
  private final List<GoalSelector> goalSelectors = new GoalSelectorsArrayList();

  /**
   * Gets the goal selectors of this group.
   *
   * @return a modifiable list containing this group goal selectors
   */
  @NotNull
  public List<GoalSelector> getGoalSelectors() {
    return this.goalSelectors;
  }

  /**
   * Gets the current goal selector of this group.
   *
   * @return the current goal selector of this group, null if not any
   */
  @Nullable
  public GoalSelector getCurrentGoalSelector() {
    return this.currentGoalSelector;
  }

  /**
   * Changes the current goal selector of this group.
   * <p>
   * Mostly unsafe since the current goal selector should normally
   * be chosen during the group tick method.
   *
   * @param goalSelector the new goal selector of this group, null to disable it
   */
  public void setCurrentGoalSelector(@Nullable GoalSelector goalSelector) {
    if (goalSelector != null && goalSelector.holder() != this) {
      throw new IllegalStateException("Tried to set goal selector attached to another AI group!");
    }

    this.currentGoalSelector = goalSelector;
  }

  public void tick() {
    GoalSelector currentGoalSelector = getCurrentGoalSelector();

    Mob entity = entityHolder.entity();
    if (currentGoalSelector != null && currentGoalSelector.shouldEnd(entity)) {
      currentGoalSelector.end(entity);
      currentGoalSelector = null;
      setCurrentGoalSelector(null);
    }

    for (GoalSelector selector : getGoalSelectors()) {
      if (selector == currentGoalSelector) {
        break;
      }
      if (selector.shouldStart(entity)) {
        if (currentGoalSelector != null) {
          currentGoalSelector.end(entity);
        }
        currentGoalSelector = selector;
        setCurrentGoalSelector(currentGoalSelector);
        currentGoalSelector.start(entity);
        break;
      }
    }

    if (currentGoalSelector != null) {
      currentGoalSelector.tick(entity);
    }
  }

  /**
   * The purpose of this list is to guarantee that every {@link GoalSelector} added to that group
   * has a reference to it for some internal interactions. We don't provide developers with
   * methods like `addGoalSelector` or `removeGoalSelector`: instead we provide them with direct
   * access to list of goal selectors, so that they could use operations such as `clear`, `set`, `removeIf`, etc.
   */
  private class GoalSelectorsArrayList extends ArrayList<GoalSelector> {

    private GoalSelectorsArrayList() {
    }

    @Override
    public GoalSelector set(int index, GoalSelector element) {
      element.attach(EntityAIGroup.this);
      return super.set(index, element);
    }

    @Override
    public boolean add(GoalSelector element) {
      element.attach(EntityAIGroup.this);
      return super.add(element);
    }

    @Override
    public void add(int index, GoalSelector element) {
      element.attach(EntityAIGroup.this);
      super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends GoalSelector> c) {
      c.forEach(goalSelector -> goalSelector.attach(EntityAIGroup.this));
      return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends GoalSelector> c) {
      c.forEach(goalSelector -> goalSelector.attach(EntityAIGroup.this));
      return super.addAll(index, c);
    }

    @Override
    public void replaceAll(UnaryOperator<GoalSelector> operator) {
      super.replaceAll(goalSelector -> {
        goalSelector = operator.apply(goalSelector);
        goalSelector.attach(EntityAIGroup.this);
        return goalSelector;
      });
    }
  }
}