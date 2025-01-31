package com.github.ynverxe.littleheads.configurate;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.objectmapping.ObjectMapper.Factory;
import org.spongepowered.configurate.util.NamingSchemes;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public final class ConfigurateConstants {

  private static final Factory objectMapperFactory = ObjectMapper.factoryBuilder()
      .defaultNamingScheme(NamingSchemes.LOWER_CASE_DASHED)
      .build();

  private ConfigurateConstants() {
  }

  public static @NotNull YamlConfigurationLoader.Builder newLoader() {
    return YamlConfigurationLoader.builder()
        .indent(2)
        .defaultOptions(configurationOptions ->
            configurationOptions.serializers(builder -> builder.registerAnnotatedObjects(objectMapperFactory)));
  }
}