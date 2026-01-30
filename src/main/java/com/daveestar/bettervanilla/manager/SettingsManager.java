package com.daveestar.bettervanilla.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameRules;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

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
      Material.DEEPSLATE_DIAMOND_ORE, Material.DEEPSLATE_COPPER_ORE, Material.GLOWSTONE);

  public static final List<Material> VEIN_CHOPPER_TOOLS = Arrays.asList(
      Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE,
      Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE);

  public static final List<Material> VEIN_CHOPPER_BLOCKS = Arrays.asList(
      Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG,
      Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG,
      Material.MANGROVE_LOG, Material.CHERRY_LOG, Material.CRIMSON_STEM, Material.WARPED_STEM, Material.PALE_OAK_LOG,
      Material.MANGROVE_ROOTS);

  public SettingsManager(Config config) {
    _config = config;
    _fileConfig = config.getFileConfig();
  }

  public String[] getAllPlayersUUIDS() {
    return _fileConfig.getConfigurationSection("players").getKeys(false).toArray(new String[0]);
  }

  // USER SETTINGS
  public boolean getPlayerToggleLocation(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".togglelocation", false);
  }

  public void setPlayerToggleLocation(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".togglelocation", value);
    _config.save();
  }

  public boolean getPlayerActionBarTimer(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".actionbartimer", true);
  }

  public void setPlayerActionBarTimer(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".actionbartimer", value);
    _config.save();
  }

  public boolean getPlayerToggleCompass(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".togglecompass", false);
  }

  public void setPlayerToggleCompass(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".togglecompass", value);
    _config.save();
  }

  public boolean getPlayerChestSort(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".chestsort", false);
  }

  public void setPlayerChestSort(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".chestsort", value);
    _config.save();
  }

  public boolean getPlayerDoubleDoorSync(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".doubledoor", false);
  }

  public void setPlayerDoubleDoorSync(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".doubledoor", value);
    _config.save();
  }

  public boolean getPlayerItemRestock(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".itemrestock", false);
  }

  public void setPlayerItemRestock(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".itemrestock", value);
    _config.save();
  }

  public boolean getPlayerNavigationTrail(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".navigationtrail", false);
  }

  public void setPlayerNavigationTrail(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".navigationtrail", value);
    _config.save();
  }

  public boolean getPlayerNavigationAutoCancel(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".navigationautocancel", true);
  }

  public void setPlayerNavigationAutoCancel(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".navigationautocancel", value);
    _config.save();
  }

  public int getPlayerNavigationReachedRadius(UUID uuid) {
    return _fileConfig.getInt("players." + uuid + ".navigationreachedradius", 25);
  }

  public void setPlayerNavigationReachedRadius(UUID uuid, int value) {
    int clamped = Math.max(1, Math.min(100, value));
    _fileConfig.set("players." + uuid + ".navigationreachedradius", clamped);
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

  public String getPlayerTagName(UUID uuid) {
    return _fileConfig.getString("players." + uuid + ".tag.name", null);
  }

  public void setPlayerTagName(UUID uuid, String value) {
    _fileConfig.set("players." + uuid + ".tag.name", value);
    _config.save();
  }

  public String getPlayerTagColor(UUID uuid) {
    return _fileConfig.getString("players." + uuid + ".tag.color", "AQUA");
  }

  public void setPlayerTagColor(UUID uuid, String value) {
    _fileConfig.set("players." + uuid + ".tag.color", value);
    _config.save();
  }

  public void clearPlayerTag(UUID uuid) {
    _fileConfig.set("players." + uuid + ".tag.name", null);
    _fileConfig.set("players." + uuid + ".tag.color", null);
    _config.save();
  }

  // GLOBAL SETTINGS
  public boolean getMaintenanceState() {
    return _fileConfig.getBoolean("global.maintenance.enabled", false);
  }

  public boolean getTagsEnabled() {
    return _fileConfig.getBoolean("global.tags.enabled", true);
  }

  public void setTagsEnabled(boolean value) {
    _fileConfig.set("global.tags.enabled", value);
    _config.save();
  }

  public String getMaintenanceMessage() {
    return _fileConfig.getString("global.maintenance.message", "");
  }

  public void setMaintenanceState(boolean value) {
    _fileConfig.set("global.maintenance.enabled", value);
    _config.save();
  }

  public void setMaintenanceMessage(String message) {
    _fileConfig.set("global.maintenance.message", message);
    _config.save();
  }

  public boolean getCreeperBlockDamage() {
    return _fileConfig.getBoolean("global.creeperblockdamage", true);
  }

  public void setCreeperBlockDamage(boolean value) {
    _fileConfig.set("global.creeperblockdamage", value);
    _config.save();
  }

  public boolean getCreeperEntityDamage() {
    return _fileConfig.getBoolean("global.creeperentitydamage", true);
  }

  public void setCreeperEntityDamage(boolean value) {
    _fileConfig.set("global.creeperentitydamage", value);
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

  public boolean getDeathChestEnabled() {
    return _fileConfig.getBoolean("global.deathchest", true);
  }

  public void setDeathChestEnabled(boolean value) {
    _fileConfig.set("global.deathchest", value);
    _config.save();
  }

  public boolean getLocatorBarEnabled() {
    return _fileConfig.getBoolean("global.locatorbar", true);
  }

  public void setLocatorBarEnabled(boolean value) {
    _fileConfig.set("global.locatorbar", value);
    _config.save();
  }

  public boolean getRecipeSyncEnabled() {
    return _fileConfig.getBoolean("global.recipesync", true);
  }

  public void setRecipeSyncEnabled(boolean value) {
    _fileConfig.set("global.recipesync", value);
    _config.save();
  }

  public boolean getHeadsExplorerEnabled() {
    return _fileConfig.getBoolean("global.headsexplorer.enabled", false);
  }

  public void setHeadsExplorerEnabled(boolean value) {
    _fileConfig.set("global.headsexplorer.enabled", value);
    _config.save();
  }

  public String getHeadsExplorerApiKey() {
    return _fileConfig.getString("global.headsexplorer.apikey", "");
  }

  public void setHeadsExplorerApiKey(String value) {
    _fileConfig.set("global.headsexplorer.apikey", value == null ? "" : value);
    _config.save();
  }

  public void applyLocatorBarSetting() {
    boolean enabled = getLocatorBarEnabled();

    for (World world : Bukkit.getWorlds()) {
      world.setGameRule(GameRules.LOCATOR_BAR, enabled);
    }
  }

  public int getPlayersSleepingPercentage() {
    return _fileConfig.getInt("global.playerssleepingpercentage", 100);
  }

  public void setPlayersSleepingPercentage(int value) {
    int clamped = Math.max(0, Math.min(100, value));
    _fileConfig.set("global.playerssleepingpercentage", clamped);
    _config.save();
  }

  public void applyPlayersSleepingPercentageSetting() {
    int percentage = getPlayersSleepingPercentage();

    for (World world : Bukkit.getWorlds()) {
      world.setGameRule(GameRules.PLAYERS_SLEEPING_PERCENTAGE, percentage);
    }
  }

  public boolean getActionBarTimerEnabled() {
    return _fileConfig.getBoolean("global.actionbartimer", true);
  }

  public void setActionBarTimerEnabled(boolean value) {
    _fileConfig.set("global.actionbartimer", value);
    _config.save();

    if (!value) {
      String[] uuids = getAllPlayersUUIDS();
      for (String uuid : uuids) {
        setPlayerActionBarTimer(UUID.fromString(uuid), false);
      }
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
        "&7                  &e&lBetterVanilla\n&7                         &b&lSMP");
  }

  public String[] getServerMOTDRaw() {
    if (_fileConfig.isList("global.motd")) {
      return _fileConfig.getStringList("global.motd").toArray(new String[0]);
    }

    return new String[] {
        _fileConfig.getString("global.motd",
            "[&7                  &e&lBetterVanilla,&7                         &b&lSMP]")
    };
  }

  public void setServerMOTD(String line1, String line2) {
    _fileConfig.set("global.motd", Arrays.asList(line1, line2));
    _config.save();
  }

  public boolean getCropProtection() {
    return _fileConfig.getBoolean("global.cropprotection", false);
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

  public boolean getItemRestockEnabled() {
    return _fileConfig.getBoolean("global.itemrestock", false);
  }

  public void setItemRestockEnabled(boolean value) {
    _fileConfig.set("global.itemrestock", value);
    _config.save();

    if (!value) {
      String[] uuids = getAllPlayersUUIDS();
      for (String uuid : uuids) {
        setPlayerItemRestock(UUID.fromString(uuid), false);
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

  public boolean getCraftingRecipeEnabled(String recipeKey) {
    String path = "global.recipes." + recipeKey + ".enabled";
    return _fileConfig.getBoolean(path, false);
  }

  public void setCraftingRecipeEnabled(String recipeKey, boolean value) {
    _fileConfig.set("global.recipes." + recipeKey + ".enabled", value);
    _config.save();
  }

  public List<ItemStack> getCraftingRecipeMatrix(String recipeKey, List<ItemStack> defaultMatrix) {
    String basePath = "global.recipes." + recipeKey + ".slots";
    List<ItemStack> matrix = new ArrayList<>(Collections.nCopies(9, null));
    boolean hasConfiguredItem = false;

    for (int i = 0; i < matrix.size(); i++) {
      ItemStack configured = _fileConfig.getItemStack(basePath + "." + i);

      if (configured != null && configured.getType() != Material.AIR) {
        matrix.set(i, configured.clone());
        hasConfiguredItem = true;
      }
    }

    if (hasConfiguredItem) {
      return matrix;
    }

    List<ItemStack> fallback = new ArrayList<>(Collections.nCopies(9, null));
    if (defaultMatrix == null) {
      return fallback;
    }

    for (int i = 0; i < Math.min(defaultMatrix.size(), fallback.size()); i++) {
      ItemStack item = defaultMatrix.get(i);
      fallback.set(i, item == null ? null : item.clone());
    }

    return fallback;
  }

  public void setCraftingRecipeMatrix(String recipeKey, List<ItemStack> matrix) {
    String basePath = "global.recipes." + recipeKey + ".slots";

    for (int i = 0; i < 9; i++) {
      ItemStack item = (matrix != null && i < matrix.size()) ? matrix.get(i) : null;
      _fileConfig.set(basePath + "." + i, item == null ? null : item.clone());
    }

    _config.save();
  }
}
