package com.github.ynverxe.littleheads.util;

import net.minecraft.util.Mth;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class PositionUtil {

  private PositionUtil() {
  }

  public static void lookAtEntity(@NotNull Entity entity, @NotNull Entity at, boolean isArmorStand, boolean fromEyes) {
    lookAt(entity, at.getLocation().toVector(), isArmorStand, fromEyes);
  }

  public static void lookAt(@NotNull Entity entity, @NotNull Vector at, boolean isArmorStand, boolean fromEyes) {
    Vector npcLoc = fromEyes && entity instanceof LivingEntity ? ((LivingEntity) entity).getEyeLocation().toVector() : entity.getLocation().toVector();

    Vector direction = at.subtract(npcLoc);

    direction.normalize();

    double yaw = Math.atan2(direction.getZ(), direction.getX());
    float degrees = (float) Math.toDegrees(yaw) - 90;
    if (degrees < -180) degrees += 360;
    if (degrees > 180) degrees -= 360;

    double squaredX = NumberConversions.square(direction.getX());
    double squaredY = NumberConversions.square(direction.getY());
    double squaredZ = NumberConversions.square(direction.getZ());

    double r = Math.sqrt(squaredX + squaredY + squaredZ);

    double pitch = Math.asin(direction.getY() / r);

    entity.setRotation(degrees, 0);
    if (isArmorStand && entity instanceof ArmorStand armorStand) {
      armorStand.setHeadPose(new EulerAngle(-pitch, 0, 0));
    } else {
      entity.setRotation(degrees, (float) Math.toDegrees(-pitch));
    }
  }

  public static void setArmorStandRotationInDegrees(@NotNull ArmorStand armorStand, float pitch, float yaw) {
    armorStand.setRotation(yaw, pitch);
    armorStand.setHeadPose(new EulerAngle(Math.toRadians(pitch), 0, 0));
  }

  public static double getDistanceToGround(Entity entity, double distance) {
    Vector start = entity.getLocation().toVector();

    RayTraceResult result = entity.getWorld().rayTraceBlocks(entity.getLocation(), new Vector(0, -1, 0), distance);

    if (result == null || result.getHitBlock() == null) return Double.MAX_VALUE;

    Vector hitPos = result.getHitPosition();
    return start.getY() - hitPos.getY();
  }
}