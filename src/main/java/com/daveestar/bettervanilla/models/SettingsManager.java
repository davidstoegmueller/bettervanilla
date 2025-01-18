package com.daveestar.bettervanilla.models;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.utils.Config;

public class SettingsManager {
  private Config _config;
  private FileConfiguration _fileCfgn;

  public SettingsManager(Config config) {
    this._config = config;
    this._fileCfgn = config.getFileCfgrn();
  }

  // USER SETTINGS
  public boolean getToggleLocation(Player p) {
    return _fileCfgn.getBoolean(p.getUniqueId() + ".togglelocation");
  }

  public void setToggleLocation(Player p, boolean value) {
    _fileCfgn.set(p.getUniqueId() + ".togglelocation", value);
    _config.save();
  }

  public boolean getToggleCompass(Player p) {
    return _fileCfgn.getBoolean(p.getUniqueId() + ".togglecompass");
  }

  public void setToggleCompass(Player p, boolean value) {
    _fileCfgn.set(p.getUniqueId() + ".togglecompass", value);
    _config.save();
  }

  // GLOBAL SETTINGS
  public boolean getMaintenance() {
    return _fileCfgn.getBoolean("global.maintenance");
  }

  public void setMaintenance(boolean value) {
    _fileCfgn.set("global.maintenance", value);
    _config.save();
  }
}
