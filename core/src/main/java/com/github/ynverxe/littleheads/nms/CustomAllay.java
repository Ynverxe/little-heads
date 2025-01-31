package com.github.ynverxe.littleheads.nms;

import com.github.ynverxe.littleheads.entity.AllayWrapper;
import com.github.ynverxe.littleheads.util.PositionUtil;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;

import static com.github.ynverxe.littleheads.entity.PositionManager.MIN_DISTANCE_FROM_GROUND;

public class CustomAllay extends Allay implements AllayWrapper {

  public CustomAllay(Level world, double x, double y, double z) {
    super(EntityType.ALLAY, world);
    this.moveControl = new net.minecraft.world.entity.ai.control.FlyingMoveControl(this, Integer.MAX_VALUE, false);
    teleportTo(x, y, z);
    world.addFreshEntity(this);
    //goalSelector.addGoal(0, new MoveTowardsRestrictionGoal(this, 1));
  }

  @Override
  protected Brain<?> makeBrain(Dynamic<?> dynamic) {
    return new Brain<>(List.of(), List.of(), ImmutableList.of(), () -> null);
  }

  @Override
  protected PathNavigation createNavigation(Level world) {
    if (true) return super.createNavigation(world);

    FlyingPathNavigation navigationflying = new FlyingPathNavigation(this, world) {
      @Override
      protected PathFinder createPathFinder(int range) {
        this.nodeEvaluator = new CustomNodeEvaluator();
        return new PathFinder(this.nodeEvaluator, range);
      }
    };

    navigationflying.setCanOpenDoors(false);
    navigationflying.setCanFloat(true);
    navigationflying.setCanPassDoors(true);
    navigationflying.setRequiredPathLength(100F);
    navigationflying.setMaxVisitedNodesMultiplier(5);
    return navigationflying;
  }

  @Override
  public void playAmbientSound() {
    // do nothing
  }

  @Override
  public SoundEvent getDeathSound() {
    return null;
  }

  @Override
  public SoundEvent getDeathSound0() {
    return null;
  }

  @Override
  public void setNoGravity(boolean noGravity) {
    super.setNoGravity(noGravity);
  }

  public static @NotNull CustomAllay spawn(@NotNull World world, double x, double y, double z) {
    Level level = ((CraftWorld) world).getHandle();

    return new CustomAllay(level, x, y, z);
  }

  @Override
  public @NotNull MovementControllerWrapper movementWrapper() {
    return new MovementControllerWrapper() {
      @Override
      public void setUpwardSpeed(double upwardSpeed) {
        try {
          SPEED_FIELD.set(CustomAllay.this, (float) Math.abs(upwardSpeed));
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
        travel(new Vec3(0, upwardSpeed, 0));
      }

      @Override
      public void setWantedPos(double x, double y, double z, double speed) {
        moveControl.setWantedPosition(x, y, z, speed);
      }

      @Override
      public Vector getWantedPos() {
        return new Vector(moveControl.getWantedX(), moveControl.getWantedY(), moveControl.getWantedZ());
      }

      @Override
      public void changeUpwardSpeed(boolean below, double speed) {
        float i;
        if (onGround()) {
          i = (float)(speed * getAttributeValue(Attributes.MOVEMENT_SPEED));
        } else {
          i = (float)(speed * getAttributeValue(Attributes.FLYING_SPEED));
        }

        setUpwardSpeed(below ? -i : i);
      }
    };
  }

  private static final Field SPEED_FIELD;

  static {
    try {
      SPEED_FIELD = LivingEntity.class.getDeclaredField("speed");
      SPEED_FIELD.setAccessible(true);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public org.bukkit.entity.@NotNull Allay allay() {
    return (org.bukkit.entity.Allay) getBukkitEntity();
  }

  @Override
  public boolean isOnGround() {
    return false;
  }

  public class FlyingMoveControl extends MoveControl {
    private final int maxTurn;
    private final boolean hoversInPlace;

    public FlyingMoveControl(Mob entity, int maxPitchChange, boolean noGravity) {
      super(entity);
      this.maxTurn = maxPitchChange;
      this.hoversInPlace = noGravity;
    }

    @Override
    public void tick() {
      if (this.operation == MoveControl.Operation.MOVE_TO) {
        this.operation = MoveControl.Operation.WAIT;
        this.mob.setNoGravity(true);
        double d = this.wantedX - this.mob.getX();
        double e = this.wantedY - this.mob.getY();
        double f = this.wantedZ - this.mob.getZ();

        double g = d * d + e * e + f * f;
        if (g < 2.5000003E-7F) {
          this.mob.setYya(0.0F);
          this.mob.setZza(0.0F);
          return;
        }

        float h = (float) (Mth.atan2(f, d) * 180.0F / (float) Math.PI) - 90.0F;
        this.mob.setYRot(this.rotlerp(this.mob.getYRot(), h, 90.0F));
        float i;
        if (this.mob.onGround()) {
          i = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
        } else {
          i = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.FLYING_SPEED));
        }

        this.mob.setSpeed(i);
        double k = Math.sqrt(d * d + f * f);

        // Factor de suavización
        float smoothingFactor = 0.1f; // Ajusta este valor para controlar la velocidad de suavizado (puedes probar diferentes valores entre 0.1 y 0.5)

        if (Math.abs(e) > 1.0E-5F || Math.abs(k) > 1.0E-5F) {
          float l = (float) (-(Mth.atan2(e, k) * 180.0F / (float) Math.PI));
          this.mob.setXRot(this.rotlerp(this.mob.getXRot(), l, (float) this.maxTurn));

          float interpolatedYya = Mth.lerp(smoothingFactor, this.mob.yya, e > 0.0 ? i : -i);
          setYya(interpolatedYya);
        }
      } else {
        if (!this.hoversInPlace) {
          this.mob.setNoGravity(false);
        }

        this.mob.setYya(0.0F);
        this.mob.setZza(0.0F);
      }
    }
  }

  @Override
  public void move(MoverType type, Vec3 movement) {
    double distance = PositionUtil.getDistanceToGround(getBukkitEntity(), 10);

    if (distance != Double.MAX_VALUE) {
      /**
       * double y = movement.y;
       *       if (distance >= MIN_DISTANCE_FROM_GROUND && distance + y < MIN_DISTANCE_FROM_GROUND) {
       *         movement = movement.with(Direction.Axis.Y, MIN_DISTANCE_FROM_GROUND - distance);
       *       }
       */
    }

    super.move(type, movement);
  }
}