package com.daveestar.bettervanilla.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {
  private FileConfiguration _fileConfiguration;
  private File _file;

  public Config(String configName, File path) {
    this(configName, path, null);
  }

  /**
   * Creates a configuration file, optionally seeding it from a resource bundled
   * with the plugin. Existing files are never overwritten.
   */
  public Config(String configName, File path, JavaPlugin resourcePlugin) {
    _file = new File(path, configName);

    if (!_file.exists()) {
      path.mkdirs();

      try (InputStream resource = resourcePlugin == null ? null : resourcePlugin.getResource(configName)) {
        if (resource != null) {
          Files.copy(resource, _file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } else {
          _file.createNewFile();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    _fileConfiguration = new YamlConfiguration();

    try {
      _fileConfiguration.load(_file);
    } catch (IOException | InvalidConfigurationException e) {
      e.printStackTrace();
    }
  }

  public File getFile() {
    return _file;
  }

  public FileConfiguration getFileConfig() {
    return _fileConfiguration;
  }

  public void save() {
    try {
      _fileConfiguration.save(_file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void reload() {
    try {
      _fileConfiguration.load(_file);
    } catch (IOException | InvalidConfigurationException e) {
      e.printStackTrace();
    }
  }
}
