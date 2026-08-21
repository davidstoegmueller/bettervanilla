package com.daveestar.bettervanilla.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
   * with the plugin. Missing values from the bundled resource are added to an
   * existing file without replacing user-defined values.
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

    boolean loaded = false;
    try {
      _fileConfiguration.load(_file);
      loaded = true;
    } catch (IOException | InvalidConfigurationException e) {
      e.printStackTrace();
    }

    if (loaded && resourcePlugin != null) {
      _mergeMissingResourceValues(configName, resourcePlugin);
    }
  }

  private void _mergeMissingResourceValues(String resourceName, JavaPlugin resourcePlugin) {
    try (InputStream resource = resourcePlugin.getResource(resourceName)) {
      if (resource == null)
        return;

      YamlConfiguration defaults = new YamlConfiguration();
      defaults.load(new InputStreamReader(resource, StandardCharsets.UTF_8));

      int addedValues = 0;
      for (String key : defaults.getKeys(true)) {
        if (defaults.isConfigurationSection(key) || _fileConfiguration.contains(key))
          continue;

        _fileConfiguration.set(key, defaults.get(key));
        addedValues++;
      }

      if (addedValues > 0) {
        save();
        resourcePlugin.getLogger().info(
            "Added " + addedValues + " new default value(s) to " + _file.getName());
      }
    } catch (IOException | InvalidConfigurationException e) {
      resourcePlugin.getLogger().warning(
          "Could not merge bundled defaults into " + _file.getName() + ": " + e.getMessage());
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
