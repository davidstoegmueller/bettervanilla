package com.daveestar.bettervanilla.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.utils.Config;

public class SettingsManager {
  private Config _config;
  private FileConfiguration _fileConfig;

  public SettingsManager(Config config) {
    _config = config;
    _fileConfig = config.getFileConfig();
  }

  // USER SETTINGS
  public boolean getToggleLocation(Player p) {
    return _fileConfig.getBoolean(p.getUniqueId() + ".togglelocation", false);
  }

  public void setToggleLocation(Player p, boolean value) {
    _fileConfig.set(p.getUniqueId() + ".togglelocation", value);
    _config.save();
  }

  public boolean getToggleCompass(Player p) {
    return _fileConfig.getBoolean(p.getUniqueId() + ".togglecompass", false);
  }

  public void setToggleCompass(Player p, boolean value) {
    _fileConfig.set(p.getUniqueId() + ".togglecompass", value);
    _config.save();
  }

  public boolean getChestSort(Player p) {
    return _fileConfig.getBoolean(p.getUniqueId() + ".chestsort", false);
  }

  public void setChestSort(Player p, boolean value) {
    _fileConfig.set(p.getUniqueId() + ".chestsort", value);
    _config.save();
  }

  public boolean getNavigationTrail(Player p) {
    return _fileConfig.getBoolean(p.getUniqueId() + ".navigationtrail", false);
  }

  public void setNavigationTrail(Player p, boolean value) {
    _fileConfig.set(p.getUniqueId() + ".navigationtrail", value);
    _config.save();
  }

  // GLOBAL SETTINGS
  public boolean getMaintenance() {
    return _fileConfig.getBoolean("global.maintenance.value", false);
  }

  public String getMaintenanceMessage() {
    return _fileConfig.getString("global.maintenance.message", "");
  }

  public void setMaintenance(boolean value, String message) {
    _fileConfig.set("global.maintenance.value", value);

    if (message != null) {
      _fileConfig.set("global.maintenance.message", message);
    }

    _config.save();
  }

  public boolean getToggleCreeperDamage() {
    return _fileConfig.getBoolean("global.creeperdamage", true);
  }

  public void setToggleCreeperDamage(boolean value) {
    _fileConfig.set("global.creeperdamage", value);
    _config.save();
  }

  public boolean getEnableEnd() {
    return _fileConfig.getBoolean("global.enableend", false);
  }

  public void setEnableEnd(boolean value) {
    _fileConfig.set("global.enableend", value);
    _config.save();
  }

  public boolean getEnableNether() {
    return _fileConfig.getBoolean("global.enablenether", false);
  }

  public void setEnableNether(boolean value) {
    _fileConfig.set("global.enablenether", value);
    _config.save();
  }

  public boolean getSleepingRain() {
    return _fileConfig.getBoolean("global.sleepingrain", false);
  }

  public void setSleepingRain(boolean value) {
    _fileConfig.set("global.sleepingrain", value);
    _config.save();
  }

  public boolean getAFKProtection() {
    return _fileConfig.getBoolean("global.afkprotection", false);
  }

  public void setAFKProtection(boolean value) {
    _fileConfig.set("global.afkprotection", value);
    _config.save();
  }

  public int getAFKTime() {
    return _fileConfig.getInt("global.afktime", 10);
  }

  public void setAFKTime(int value) {
    _fileConfig.set("global.afktime", value);
    _config.save();
  }

  public String getServerMOTD() {
    return _fileConfig.getString("global.motd", "&e&k--- &d&lBetterVanilla &7>>> &b&lSMP &e&k---");
  }

  public void setServerMOTD(String value) {
    _fileConfig.set("global.motd", value);
    _config.save();
  }

  public boolean getCropProtection() {
    return _fileConfig.getBoolean("global.cropprotection", true);
  }

  public void setCropProtection(boolean value) {
    _fileConfig.set("global.cropprotection", value);
    _config.save();
  }

  public boolean getRightClickCropHarvest() {
    return _fileConfig.getBoolean("global.rightclickcropharvest", false);
  }

  public void setRightClickCropHarvest(boolean value) {
    _fileConfig.set("global.rightclickcropharvest", value);
    _config.save();
  }
}
