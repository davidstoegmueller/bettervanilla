package com.daveestar.bettervanilla.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import com.daveestar.bettervanilla.utils.Config;

public class SettingsManager {
  private Config _config;
  private FileConfiguration _fileConfig;

  public static final List<Material> VEIN_MINER_TOOLS = Arrays.asList(
      Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE,
      Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE,
      Material.NETHERITE_PICKAXE);

  public static final List<Material> VEIN_MINER_BLOCKS = Arrays.asList(
      Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE,
      Material.REDSTONE_ORE, Material.LAPIS_ORE, Material.DIAMOND_ORE,
      Material.EMERALD_ORE, Material.COPPER_ORE, Material.NETHER_QUARTZ_ORE,
      Material.NETHER_GOLD_ORE, Material.DEEPSLATE_COAL_ORE,
      Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_GOLD_ORE,
      Material.DEEPSLATE_REDSTONE_ORE, Material.DEEPSLATE_LAPIS_ORE,
      Material.DEEPSLATE_DIAMOND_ORE, Material.DEEPSLATE_COPPER_ORE);

  public static final List<Material> VEIN_CHOPPER_TOOLS = Arrays.asList(
      Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE,
      Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE);

  public static final List<Material> VEIN_CHOPPER_BLOCKS = Arrays.asList(
      Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG,
      Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG,
      Material.MANGROVE_LOG, Material.CHERRY_LOG);

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

  public boolean getDoubleDoorSync(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".doubledoor", false);
  }

  public void setDoubleDoorSync(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".doubledoor", value);
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
  public boolean getMaintenanceState() {
    return _fileConfig.getBoolean("global.maintenance.value", false);
  }

  public String getMaintenanceMessage() {
    return _fileConfig.getString("global.maintenance.message", "");
  }

  public void setMaintenanceState(boolean value) {
    _fileConfig.set("global.maintenance.value", value);
    _config.save();
  }

  public void setMaintenanceMessage(String message) {
    _fileConfig.set("global.maintenance.message", message);
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

  public boolean getLocatorBarEnabled() {
    return _fileConfig.getBoolean("global.locatorbar", true);
  }

  public void setLocatorBarEnabled(boolean value) {
    _fileConfig.set("global.locatorbar", value);
    _config.save();
  }

  public void applyLocatorBarSetting() {
    boolean enabled = getLocatorBarEnabled();

    for (World world : Bukkit.getWorlds()) {
      world.setGameRule(GameRule.LOCATOR_BAR, enabled);
    }
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
    if (_fileConfig.isList("global.motd")) {
      return String.join("\n", _fileConfig.getStringList("global.motd"));
    }

    return _fileConfig.getString("global.motd",
        "&e&k--- &d&lBetterVanilla &7>>> &b&lSMP &e&k---");
  }

  public String[] getServerMOTDRaw() {
    if (_fileConfig.isList("global.motd")) {
      return _fileConfig.getStringList("global.motd").toArray(new String[0]);
    }

    return new String[] {
        _fileConfig.getString("global.motd",
            "&e&k--- &d&lBetterVanilla &7>>> &b&lSMP &e&k---")
    };
  }

  public void setServerMOTD(String line1, String line2) {
    _fileConfig.set("global.motd", Arrays.asList(line1, line2));
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

  public boolean getVeinMinerEnabled() {
    return _fileConfig.getBoolean("global.veinminer.enabled", false);
  }

  public void setVeinMinerEnabled(boolean value) {
    _fileConfig.set("global.veinminer.enabled", value);
    _config.save();

    if (!value) {
      String[] uuids = getAllPlayersUUIDS();
      for (String uuid : uuids) {
        setPlayerVeinMiner(UUID.fromString(uuid), value);
      }
    }
  }

  public boolean getVeinChopperEnabled() {
    return _fileConfig.getBoolean("global.veinchopper.enabled", false);
  }

  public void setVeinChopperEnabled(boolean value) {
    _fileConfig.set("global.veinchopper.enabled", value);
    _config.save();

    if (!value) {
      String[] uuids = getAllPlayersUUIDS();
      for (String uuid : uuids) {
        setPlayerVeinChopper(UUID.fromString(uuid), value);
      }
    }
  }

  public int getVeinMinerMaxVeinSize() {
    return _fileConfig.getInt("global.veinminer.maxveinsize", 100);
  }

  public boolean getVeinMinerSound() {
    return _fileConfig.getBoolean("global.veinminer.sound", true);
  }

  public void setVeinMinerSound(boolean value) {
    _fileConfig.set("global.veinminer.sound", value);
    _config.save();
  }

  public void setVeinMinerMaxVeinSize(int value) {
    _fileConfig.set("global.veinminer.maxveinsize", value);
    _config.save();
  }

  public int getVeinChopperMaxVeinSize() {
    return _fileConfig.getInt("global.veinchopper.maxveinsize", 100);
  }

  public boolean getVeinChopperSound() {
    return _fileConfig.getBoolean("global.veinchopper.sound", true);
  }

  public void setVeinChopperSound(boolean value) {
    _fileConfig.set("global.veinchopper.sound", value);
    _config.save();
  }

  public void setVeinChopperMaxVeinSize(int value) {
    _fileConfig.set("global.veinchopper.maxveinsize", value);
    _config.save();
  }

  public List<String> getVeinMinerAllowedTools() {
    String path = "global.veinminer.allowedtools";
    if (!_fileConfig.contains(path)) {
      return VEIN_MINER_TOOLS.stream().map(Material::name)
          .collect(Collectors.toCollection(ArrayList::new));
    }

    List<String> list = _fileConfig.getStringList(path);
    return list == null ? new ArrayList<>() : new ArrayList<>(list);
  }

  public void setVeinMinerAllowedTools(List<String> tools) {
    _fileConfig.set("global.veinminer.allowedtools", tools);
    _config.save();
  }

  public List<String> getVeinMinerAllowedBlocks() {
    String path = "global.veinminer.allowedblocks";
    if (!_fileConfig.contains(path)) {
      return VEIN_MINER_BLOCKS.stream().map(Material::name)
          .collect(Collectors.toCollection(ArrayList::new));
    }

    List<String> list = _fileConfig.getStringList(path);
    return list == null ? new ArrayList<>() : new ArrayList<>(list);
  }

  public void setVeinMinerAllowedBlocks(List<String> blocks) {
    _fileConfig.set("global.veinminer.allowedblocks", blocks);
    _config.save();
  }

  public List<String> getVeinChopperAllowedTools() {
    String path = "global.veinchopper.allowedtools";
    if (!_fileConfig.contains(path)) {
      return VEIN_CHOPPER_TOOLS.stream().map(Material::name)
          .collect(Collectors.toCollection(ArrayList::new));
    }

    List<String> list = _fileConfig.getStringList(path);
    return list == null ? new ArrayList<>() : new ArrayList<>(list);
  }

  public void setVeinChopperAllowedTools(List<String> tools) {
    _fileConfig.set("global.veinchopper.allowedtools", tools);
    _config.save();
  }

  public List<String> getVeinChopperAllowedBlocks() {
    String path = "global.veinchopper.allowedblocks";
    if (!_fileConfig.contains(path)) {
      return VEIN_CHOPPER_BLOCKS.stream().map(Material::name)
          .collect(Collectors.toCollection(ArrayList::new));
    }

    List<String> list = _fileConfig.getStringList(path);
    return list == null ? new ArrayList<>() : new ArrayList<>(list);
  }

  public void setVeinChopperAllowedBlocks(List<String> blocks) {
    _fileConfig.set("global.veinchopper.allowedblocks", blocks);
    _config.save();
  }
}
