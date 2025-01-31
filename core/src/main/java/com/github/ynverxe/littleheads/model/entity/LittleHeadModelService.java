package com.github.ynverxe.littleheads.model.entity;

import com.github.ynverxe.configuratehelper.handler.FastConfiguration;
import com.github.ynverxe.configuratehelper.handler.content.ContentChannel;
import com.github.ynverxe.littleheads.configurate.ConfigurateConstants;
import com.github.ynverxe.littleheads.service.Service;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;

import static com.github.ynverxe.littleheads.LittleHeadsPlugin.logger;
import static java.nio.file.Files.*;

public class LittleHeadModelService implements Service {

  private final @NotNull Path modelsDirPath;
  private final @NotNull Map<String, LittleHeadModel> modelsByFileName = new LinkedHashMap<>();

  public LittleHeadModelService(@NotNull Plugin plugin) throws IOException {
    this.modelsDirPath = plugin.getDataFolder().toPath().resolve("heads");
  }

  @Override
  public void load(JavaPlugin plugin) throws Exception {
    this.modelsByFileName.clear();

    if (!exists(this.modelsDirPath)) {
      createDirectories(this.modelsDirPath);
    }

    loadDefaultTemplate();

    try (Stream<Path> paths = list(this.modelsDirPath).filter(path -> path.endsWith(".yml"))){
      Iterator<Path> pathIterator = paths.iterator();

      while (pathIterator.hasNext()) {
        Path path = pathIterator.next();

        this.loadModel(path, null);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void reload(JavaPlugin plugin) throws Exception {
    load(plugin);
  }

  @Override
  public void end(JavaPlugin plugin) throws Exception {

  }

  public @NotNull Optional<LittleHeadModel> model(@NotNull String name) {
    return Optional.ofNullable(this.modelsByFileName.get(name.toLowerCase()));
  }

  private void loadDefaultTemplate() throws SerializationException {
    loadModel(this.modelsDirPath.resolve("default.yml"), ContentChannel.resourceByLoader("heads/default.yml", getClass().getClassLoader()));
  }

  private void loadModel(Path path, ContentChannel fallback) throws SerializationException {
    FastConfiguration configuration = new FastConfiguration(
        ContentChannel.path(path),
        fallback,
        ConfigurateConstants.newLoader()
            .nodeStyle(NodeStyle.BLOCK)
    );

    LittleHeadModel littleHeadModel = configuration.node().get(LittleHeadModel.class);

    if (littleHeadModel != null) {
      logger().info(littleHeadModel.name() + " loaded!");

      String fileName = path.getFileName().toString();
      fileName = fileName.substring(0, fileName.indexOf('.'));

      littleHeadModel.checkIsValid();

      this.modelsByFileName.put(fileName.toLowerCase(), littleHeadModel);
    } else {
      logger().info("Unable to load model file '" + path + "'");
    }
  }
}