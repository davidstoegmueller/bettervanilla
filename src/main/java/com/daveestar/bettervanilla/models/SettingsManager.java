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
    return _fileCfgn.getBoolean("global.maintenance.value", false);
  }

  public String getMaintenanceMessage() {
    return _fileCfgn.getString("global.maintenance.message", "");
  }

  public void setMaintenance(boolean value, String message) {
    _fileCfgn.set("global.maintenance.value", value);

    if (message != null) {
      _fileCfgn.set("global.maintenance.message", message);
    }

    _config.save();
  }

  public boolean getToggleCreeperDamage() {
    return _fileCfgn.getBoolean("global.creeperdamage", true);
  }

  public void setToggleCreeperDamage(boolean value) {
    _fileCfgn.set("global.creeperdamage", value);
    _config.save();
  }

  public boolean getToggleEnd() {
    return _fileCfgn.getBoolean("global.toggleend", false);
  }

  public void setToggleEnd(boolean value) {
    _fileCfgn.set("global.toggleend", value);
    _config.save();
  }

  public boolean getSleepingRain() {
    return _fileCfgn.getBoolean("global.sleepingrain", false);
  }

  public void setSleepingRain(boolean value) {
    _fileCfgn.set("global.sleepingrain", value);
    _config.save();
  }

  public int getAFKTime() {
    return _fileCfgn.getInt("global.afktime", 10);
  }

  public void setAFKTime(int value) {
    _fileCfgn.set("global.afktime", value);
    _config.save();
  }
}
