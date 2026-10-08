package com.daveestar.bettervanilla.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {
  private final FileConfiguration _fileConfiguration;
  private final File _file;

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
      try (InputStream resource = resourcePlugin == null ? null : resourcePlugin.getResource(configName)) {
        Files.createDirectories(path.toPath());
        if (resource != null) {
          Files.copy(resource, _file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } else {
          _file.createNewFile();
        }
      } catch (IOException e) {
        throw new IllegalStateException("Could not create configuration " + _file, e);
      }
    }

    _fileConfiguration = new YamlConfiguration();

    try {
      _fileConfiguration.load(_file);
    } catch (IOException | InvalidConfigurationException e) {
      throw new IllegalStateException("Could not load configuration " + _file
          + ". Fix the file before enabling the plugin; existing data was preserved.", e);
    }

    if (resourcePlugin != null) {
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

  public synchronized void save() {
    Path temporaryFile = null;
    try {
      Path target = _file.toPath().toAbsolutePath();
      temporaryFile = Files.createTempFile(target.getParent(), _file.getName() + "---", ".tmp");
      _fileConfiguration.save(temporaryFile.toFile());
      try {
        Files.move(temporaryFile, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
      } catch (AtomicMoveNotSupportedException e) {
        Files.move(temporaryFile, target, StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (IOException e) {
      throw new IllegalStateException("Could not save configuration " + _file, e);
    } finally {
      if (temporaryFile != null) {
        try {
          Files.deleteIfExists(temporaryFile);
        } catch (IOException ignored) {
          // The original configuration remains intact if replacing it failed.
        }
      }
    }
  }

  public synchronized void reload() {
    try {
      String contents = Files.readString(_file.toPath(), StandardCharsets.UTF_8);
      // Validate first: loadFromString clears the live configuration before parsing.
      new YamlConfiguration().loadFromString(contents);
      _fileConfiguration.loadFromString(contents);
    } catch (IOException | InvalidConfigurationException e) {
      throw new IllegalStateException("Could not reload configuration " + _file
          + "; the previous configuration is still active.", e);
    }
  }
}
