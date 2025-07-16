package com.daveestar.bettervanilla.manager;

import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;

import com.daveestar.bettervanilla.utils.Config;

public class SettingsManager {
  private Config _config;
  private FileConfiguration _fileConfig;

  public SettingsManager(Config config) {
    _config = config;
    _fileConfig = config.getFileConfig();
  }

  public String[] getAllPlayersUUIDS() {
    return _fileConfig.getConfigurationSection("players").getKeys(false).toArray(new String[0]);
  }

  // USER SETTINGS
  public boolean getToggleLocation(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".togglelocation", false);
  }

  public void setToggleLocation(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".togglelocation", value);
    _config.save();
  }

  public boolean getToggleCompass(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".togglecompass", false);
  }

  public void setToggleCompass(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".togglecompass", value);
    _config.save();
  }

  public boolean getChestSort(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".chestsort", false);
  }

  public void setChestSort(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".chestsort", value);
    _config.save();
  }

  public boolean getNavigationTrail(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".navigationtrail", false);
  }

  public void setNavigationTrail(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".navigationtrail", value);
    _config.save();
  }

  public boolean getPlayerVeinMiner(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".veinminer", false);
  }

  public void setPlayerVeinMiner(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".veinminer", value);
    _config.save();
  }

  public boolean getPlayerVeinChopper(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".veinchopper", false);
  }

  public void setPlayerVeinChopper(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".veinchopper", value);
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

  public boolean getBackpackEnabled() {
    return _fileConfig.getBoolean("global.backpack.enabled", false);
  }

  public void setBackpackEnabled(boolean value) {
    _fileConfig.set("global.backpack.enabled", value);
    _config.save();
  }

  public int getBackpackRows() {
    return _fileConfig.getInt("global.backpack.rows", 3);
  }

  public void setBackpackRows(int value) {
    _fileConfig.set("global.backpack.rows", value);
    _config.save();
  }

  public int getBackpackPages() {
    return _fileConfig.getInt("global.backpack.pages", 1);
  }

  public void setBackpackPages(int value) {
    _fileConfig.set("global.backpack.pages", value);
    _config.save();
  }

  public boolean getVeinMiner() {
    return _fileConfig.getBoolean("global.veinminer", false);
  }

  public void setVeinMiner(boolean value) {
    _fileConfig.set("global.veinminer", value);
    _config.save();
  }

  public boolean getVeinChopper() {
    return _fileConfig.getBoolean("global.veinchopper", false);
  }

  public void setVeinChopper(boolean value) {
    _fileConfig.set("global.veinchopper", value);
    _config.save();
  }
}
