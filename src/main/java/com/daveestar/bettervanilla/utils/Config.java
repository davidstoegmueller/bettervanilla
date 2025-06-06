package com.daveestar.bettervanilla.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
  private FileConfiguration _fileConfiguration;
  private File _file;

  public Config(String configName, File path) {
    _file = new File(path, configName);

    if (!_file.exists()) {
      path.mkdirs();

      try {
        _file.createNewFile();
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
