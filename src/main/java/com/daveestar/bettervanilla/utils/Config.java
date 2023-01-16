package com.daveestar.bettervanilla.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
  private FileConfiguration fileConfiguration;
  private File file;

  public Config(String configName, File path) {
    file = new File(path, configName);

    if (!file.exists()) {
      path.mkdirs();

      try {
        file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    fileConfiguration = new YamlConfiguration();

    try {
      fileConfiguration.load(file);
    } catch (IOException | InvalidConfigurationException e) {
      e.printStackTrace();
    }
  }

  public File getFile() {
    return file;
  }

  public FileConfiguration getFileCfgrn() {
    return fileConfiguration;
  }

  public void save() {
    try {
      fileConfiguration.save(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void reload() {
    try {
      fileConfiguration.load(file);
    } catch (IOException | InvalidConfigurationException e) {
      e.printStackTrace();
    }
  }
}
