package com.github.ynverxe.littleheads.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Field;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;

public final class SkullSetter {

  private static final PlayerProfile PLAYER_PROFILE_BASE = Bukkit.getOfflinePlayer("/")
      .getPlayerProfile();

  static {
    PLAYER_PROFILE_BASE.clearProperties();
  }

  private SkullSetter() {}

  public static void setSkin(@NotNull String texture, @NotNull SkullMeta meta) {
    PlayerProfile profile = Bukkit.createProfile(null, null);
    System.out.println(texture);
    profile.setProperty(new ProfileProperty("textures", texture));
    meta.setPlayerProfile(profile);
  }
}