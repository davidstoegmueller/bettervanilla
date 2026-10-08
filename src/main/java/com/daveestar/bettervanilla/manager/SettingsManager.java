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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.daveestar.bettervanilla.enums.InventorySortMode;
import com.daveestar.bettervanilla.enums.Language;
import com.daveestar.bettervanilla.utils.Config;
import com.daveestar.bettervanilla.utils.Theme;

public class SettingsManager {
  private Config _config;
  private FileConfiguration _fileConfig;

  public static final List<Material> VEIN_MINER_TOOLS = Arrays.asList(
      Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.COPPER_PICKAXE, Material.IRON_PICKAXE,
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
      Material.WOODEN_AXE, Material.STONE_AXE, Material.COPPER_AXE, Material.IRON_AXE,
      Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE);

  public static final List<Material> VEIN_CHOPPER_BLOCKS = Arrays.asList(
      Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG,
      Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG,
      Material.MANGROVE_LOG, Material.CHERRY_LOG, Material.CRIMSON_STEM, Material.WARPED_STEM, Material.PALE_OAK_LOG,
      Material.MANGROVE_ROOTS, Material.STRIPPED_OAK_LOG, Material.STRIPPED_BIRCH_LOG, Material.STRIPPED_ACACIA_LOG,
      Material.STRIPPED_CHERRY_LOG, Material.STRIPPED_JUNGLE_LOG, Material.STRIPPED_SPRUCE_LOG,
      Material.STRIPPED_DARK_OAK_LOG, Material.STRIPPED_PALE_OAK_LOG, Material.STRIPPED_MANGROVE_LOG,
      Material.POPLAR_LOG, Material.STRIPPED_POPLAR_LOG);

  public SettingsManager(Config config) {
    _config = config;
    _fileConfig = config.getFileConfig();
  }

  public synchronized String[] getAllPlayersUUIDS() {
    ConfigurationSection players = _fileConfig.getConfigurationSection("players");
    return players == null ? new String[0] : players.getKeys(false).toArray(new String[0]);
  }

  // USER SETTINGS
  public synchronized String getPlayerLanguage(UUID uuid) {
    return Language.fromCode(_fileConfig.getString("players." + uuid + ".language", getServerLanguage())).getCode();
  }

  public synchronized void setPlayerLanguage(UUID uuid, String value) {
    _fileConfig.set("players." + uuid + ".language", Language.fromCode(value).getCode());
    _config.save();
  }

  public synchronized boolean getPlayerToggleLocation(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".togglelocation", false);
  }

  public synchronized void setPlayerToggleLocation(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".togglelocation", value);
    _config.save();
  }

  public synchronized boolean getPlayerActionBarTimer(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".actionbartimer", true);
  }

  public synchronized void setPlayerActionBarTimer(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".actionbartimer", value);
    _config.save();
  }

  public synchronized boolean getPlayerToggleCompass(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".togglecompass", false);
  }

  public synchronized void setPlayerToggleCompass(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".togglecompass", value);
    _config.save();
  }

  public synchronized boolean getPlayerChestSort(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".chestsort", false);
  }

  public synchronized void setPlayerChestSort(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".chestsort", value);
    _config.save();
  }

  public synchronized InventorySortMode getPlayerChestSortMode(UUID uuid) {
    String mode = _fileConfig.getString("players." + uuid + ".chestsortmode", "ALPHABETICAL_ASC");
    return InventorySortMode.fromString(mode);
  }

  public synchronized void setPlayerChestSortMode(UUID uuid, InventorySortMode mode) {
    String value = mode == null ? InventorySortMode.ALPHABETICAL_ASC.name() : mode.name();
    _fileConfig.set("players." + uuid + ".chestsortmode", value);
    _config.save();
  }

  public synchronized boolean getPlayerBackpackSort(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".backpacksort", false);
  }

  public synchronized void setPlayerBackpackSort(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".backpacksort", value);
    _config.save();
  }

  public synchronized InventorySortMode getPlayerBackpackSortMode(UUID uuid) {
    String mode = _fileConfig.getString("players." + uuid + ".backpacksortmode", "ALPHABETICAL_ASC");
    return InventorySortMode.fromString(mode);
  }

  public synchronized void setPlayerBackpackSortMode(UUID uuid, InventorySortMode mode) {
    String value = mode == null ? InventorySortMode.ALPHABETICAL_ASC.name() : mode.name();
    _fileConfig.set("players." + uuid + ".backpacksortmode", value);
    _config.save();
  }

  public synchronized boolean getPlayerInventorySort(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".inventorysort", false);
  }

  public synchronized void setPlayerInventorySort(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".inventorysort", value);
    _config.save();
  }

  public synchronized InventorySortMode getPlayerInventorySortMode(UUID uuid) {
    String mode = _fileConfig.getString("players." + uuid + ".inventorysortmode", "ALPHABETICAL_ASC");
    return InventorySortMode.fromString(mode);
  }

  public synchronized void setPlayerInventorySortMode(UUID uuid, InventorySortMode mode) {
    String value = mode == null ? InventorySortMode.ALPHABETICAL_ASC.name() : mode.name();
    _fileConfig.set("players." + uuid + ".inventorysortmode", value);
    _config.save();
  }

  public synchronized boolean getPlayerInventorySortIncludeHotbar(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".inventorysortincludehotbar", false);
  }

  public synchronized void setPlayerInventorySortIncludeHotbar(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".inventorysortincludehotbar", value);
    _config.save();
  }

  public synchronized boolean getPlayerDoubleDoorSync(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".doubledoor", false);
  }

  public synchronized void setPlayerDoubleDoorSync(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".doubledoor", value);
    _config.save();
  }

  public synchronized boolean getPlayerItemRestock(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".itemrestock", false);
  }

  public synchronized void setPlayerItemRestock(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".itemrestock", value);
    _config.save();
  }

  public synchronized boolean getPlayerNavigationTrail(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".navigationtrail", false);
  }

  public synchronized void setPlayerNavigationTrail(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".navigationtrail", value);
    _config.save();
  }

  public synchronized boolean getPlayerNavigationAutoCancel(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".navigationautocancel", true);
  }

  public synchronized void setPlayerNavigationAutoCancel(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".navigationautocancel", value);
    _config.save();
  }

  public synchronized int getPlayerNavigationReachedRadius(UUID uuid) {
    return Math.max(1, Math.min(200, _fileConfig.getInt("players." + uuid + ".navigationreachedradius", 25)));
  }

  public synchronized void setPlayerNavigationReachedRadius(UUID uuid, int value) {
    int clamped = Math.max(1, Math.min(200, value));
    _fileConfig.set("players." + uuid + ".navigationreachedradius", clamped);
    _config.save();
  }

  public synchronized boolean getPlayerVeinMiner(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".veinminer", false);
  }

  public synchronized void setPlayerVeinMiner(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".veinminer", value);
    _config.save();
  }

  public synchronized boolean getPlayerVeinChopper(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".veinchopper", false);
  }

  public synchronized boolean getPlayerRightClickCropHarvest(UUID uuid) {
    return _fileConfig.getBoolean("players." + uuid + ".rightclickcropharvest", false);
  }

  public synchronized void setPlayerRightClickCropHarvest(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".rightclickcropharvest", value);
    _config.save();
  }

  public synchronized void setPlayerVeinChopper(UUID uuid, boolean value) {
    _fileConfig.set("players." + uuid + ".veinchopper", value);
    _config.save();
  }

  public synchronized String getPlayerTagName(UUID uuid) {
    return _fileConfig.getString("players." + uuid + ".tag.name", null);
  }

  public synchronized void setPlayerTagName(UUID uuid, String value) {
    _fileConfig.set("players." + uuid + ".tag.name", value);
    _config.save();
  }

  public synchronized String getPlayerTagColor(UUID uuid) {
    return _fileConfig.getString("players." + uuid + ".tag.color", "AQUA");
  }

  public synchronized void setPlayerTagColor(UUID uuid, String value) {
    _fileConfig.set("players." + uuid + ".tag.color", value);
    _config.save();
  }

  public synchronized void clearPlayerTag(UUID uuid) {
    _fileConfig.set("players." + uuid + ".tag.name", null);
    _fileConfig.set("players." + uuid + ".tag.color", null);
    _config.save();
  }

  // GLOBAL SETTINGS
  public synchronized String getServerLanguage() {
    return Language.fromCode(_fileConfig.getString("global.language", Language.EN.getCode())).getCode();
  }

  public synchronized void setServerLanguage(String value) {
    _fileConfig.set("global.language", Language.fromCode(value).getCode());
    _config.save();
  }

  public synchronized int getPlaytimeGUIRows() {
    return Math.max(2, Math.min(6, _fileConfig.getInt("global.guirows.playtime", 3)));
  }

  public synchronized void setPlaytimeGUIRows(int value) {
    _fileConfig.set("global.guirows.playtime", Math.max(2, Math.min(6, value)));
    _config.save();
  }

  public synchronized int getWaypointsGUIRows() {
    return Math.max(2, Math.min(6, _fileConfig.getInt("global.guirows.waypoints", 3)));
  }

  public synchronized void setWaypointsGUIRows(int value) {
    _fileConfig.set("global.guirows.waypoints", Math.max(2, Math.min(6, value)));
    _config.save();
  }

  public synchronized String getPrimaryFontColor() {
    return _fileConfig.getString("global.theme.primaryFontColor", Theme.DEFAULT_PRIMARY_FONT_COLOR);
  }

  public synchronized void setPrimaryFontColor(String value) {
    _setThemeValue("primaryFontColor", value);
  }

  public synchronized String getHighlightFontColor() {
    return _fileConfig.getString("global.theme.highlightFontColor", Theme.DEFAULT_HIGHLIGHT_FONT_COLOR);
  }

  public synchronized void setHighlightFontColor(String value) {
    _setThemeValue("highlightFontColor", value);
  }

  public synchronized String getErrorFontColor() {
    return _fileConfig.getString("global.theme.errorFontColor", Theme.DEFAULT_ERROR_FONT_COLOR);
  }

  public synchronized void setErrorFontColor(String value) {
    _setThemeValue("errorFontColor", value);
  }

  public synchronized String getTitleSymbolColor() {
    return _fileConfig.getString("global.theme.titleSymbolColor", Theme.DEFAULT_TITLE_SYMBOL_COLOR);
  }

  public synchronized void setTitleSymbolColor(String value) {
    _setThemeValue("titleSymbolColor", value);
  }

  public synchronized String getTextSymbolColor() {
    return _fileConfig.getString("global.theme.textSymbolColor", Theme.DEFAULT_TEXT_SYMBOL_COLOR);
  }

  public synchronized void setTextSymbolColor(String value) {
    _setThemeValue("textSymbolColor", value);
  }

  public synchronized String getGlassPaneColor() {
    return _fileConfig.getString("global.theme.glassPaneColor", Theme.DEFAULT_GLASS_PANE_COLOR);
  }

  public synchronized void setGlassPaneColor(String value) {
    _setThemeValue("glassPaneColor", value);
  }

  public synchronized String getThemeName() {
    return _fileConfig.getString("global.theme.name", Theme.DEFAULT_NAME);
  }

  public synchronized void setThemeName(String value) {
    _setThemeValue("name", value);
  }

  public synchronized void resetTheme() {
    _fileConfig.set("global.theme", null);
    _config.save();
  }

  private void _setThemeValue(String key, String value) {
    _fileConfig.set("global.theme." + key, value);
    _config.save();
  }

  public synchronized boolean getMaintenanceState() {
    return _fileConfig.getBoolean("global.maintenance.enabled", false);
  }

  public synchronized boolean getTagsEnabled() {
    return _fileConfig.getBoolean("global.tags.enabled", true);
  }

  public synchronized void setTagsEnabled(boolean value) {
    _fileConfig.set("global.tags.enabled", value);
    _config.save();
  }

  public synchronized String getMaintenanceMessage() {
    return _fileConfig.getString("global.maintenance.message", "");
  }

  public synchronized void setMaintenanceState(boolean value) {
    _fileConfig.set("global.maintenance.enabled", value);
    _config.save();
  }

  public synchronized void setMaintenanceMessage(String message) {
    _fileConfig.set("global.maintenance.message", message);
    _config.save();
  }

  public synchronized boolean getCreeperBlockDamage() {
    return _fileConfig.getBoolean("global.creeperblockdamage", true);
  }

  public synchronized void setCreeperBlockDamage(boolean value) {
    _fileConfig.set("global.creeperblockdamage", value);
    _config.save();
  }

  public synchronized boolean getCreeperEntityDamage() {
    return _fileConfig.getBoolean("global.creeperentitydamage", true);
  }

  public synchronized void setCreeperEntityDamage(boolean value) {
    _fileConfig.set("global.creeperentitydamage", value);
    _config.save();
  }

  public synchronized boolean getEndermanBlockSteal() {
    return _fileConfig.getBoolean("global.endermanblocksteal", true);
  }

  public synchronized void setEndermanBlockSteal(boolean value) {
    _fileConfig.set("global.endermanblocksteal", value);
    _config.save();
  }

  public synchronized boolean getEnableEnd() {
    return _fileConfig.getBoolean("global.enableend", false);
  }

  public synchronized void setEnableEnd(boolean value) {
    _fileConfig.set("global.enableend", value);
    _config.save();
  }

  public synchronized boolean getEnableNether() {
    return _fileConfig.getBoolean("global.enablenether", false);
  }

  public synchronized void setEnableNether(boolean value) {
    _fileConfig.set("global.enablenether", value);
    _config.save();
  }

  public synchronized boolean getSleepingRain() {
    return _fileConfig.getBoolean("global.sleepingrain", false);
  }

  public synchronized void setSleepingRain(boolean value) {
    _fileConfig.set("global.sleepingrain", value);
    _config.save();
  }

  public synchronized boolean getDeathChestEnabled() {
    return _fileConfig.getBoolean("global.deathchest", true);
  }

  public synchronized void setDeathChestEnabled(boolean value) {
    _fileConfig.set("global.deathchest", value);
    _config.save();
  }

  public synchronized boolean getLocatorBarEnabled() {
    return _fileConfig.getBoolean("global.locatorbar", true);
  }

  public synchronized void setLocatorBarEnabled(boolean value) {
    _fileConfig.set("global.locatorbar", value);
    _config.save();
  }

  public synchronized boolean getRecipeSyncEnabled() {
    return _fileConfig.getBoolean("global.recipesync", true);
  }

  public synchronized void setRecipeSyncEnabled(boolean value) {
    _fileConfig.set("global.recipesync", value);
    _config.save();
  }

  public synchronized boolean getHeadsExplorerEnabled() {
    return _fileConfig.getBoolean("global.headsexplorer.enabled", false);
  }

  public synchronized void setHeadsExplorerEnabled(boolean value) {
    _fileConfig.set("global.headsexplorer.enabled", value);
    _config.save();
  }

  public synchronized String getHeadsExplorerApiKey() {
    return _fileConfig.getString("global.headsexplorer.apikey", "");
  }

  public synchronized void setHeadsExplorerApiKey(String value) {
    _fileConfig.set("global.headsexplorer.apikey", value == null ? "" : value);
    _config.save();
  }

  public synchronized void applyLocatorBarSetting() {
    boolean enabled = getLocatorBarEnabled();

    for (World world : Bukkit.getWorlds()) {
      world.setGameRule(GameRules.LOCATOR_BAR, enabled);
    }
  }

  public synchronized int getPlayersSleepingPercentage() {
    return Math.max(0, Math.min(100, _fileConfig.getInt("global.playerssleepingpercentage", 100)));
  }

  public synchronized void setPlayersSleepingPercentage(int value) {
    int clamped = Math.max(0, Math.min(100, value));
    _fileConfig.set("global.playerssleepingpercentage", clamped);
    _config.save();
  }

  public synchronized void applyPlayersSleepingPercentageSetting() {
    int percentage = getPlayersSleepingPercentage();

    for (World world : Bukkit.getWorlds()) {
      world.setGameRule(GameRules.PLAYERS_SLEEPING_PERCENTAGE, percentage);
    }
  }

  public synchronized boolean getActionBarTimerEnabled() {
    return _fileConfig.getBoolean("global.actionbartimer", true);
  }

  public synchronized void setActionBarTimerEnabled(boolean value) {
    _fileConfig.set("global.actionbartimer", value);
    _config.save();

    if (!value) {
      String[] uuids = getAllPlayersUUIDS();
      for (String uuid : uuids) {
        setPlayerActionBarTimer(UUID.fromString(uuid), false);
      }
    }
  }

  public synchronized boolean getAFKProtection() {
    return _fileConfig.getBoolean("global.afkprotection", false);
  }

  public synchronized void setAFKProtection(boolean value) {
    _fileConfig.set("global.afkprotection", value);
    _config.save();
  }

  public synchronized int getAFKTime() {
    return _fileConfig.getInt("global.afktime", 10);
  }

  public synchronized void setAFKTime(int value) {
    _fileConfig.set("global.afktime", value);
    _config.save();
  }

  public synchronized String getServerMOTD() {
    return String.join("\n", _getServerMOTDLines());
  }

  public synchronized String[] getServerMOTDRaw() {
    return _getServerMOTDLines().toArray(new String[0]);
  }

  private List<String> _getServerMOTDLines() {
    List<String> lines;

    if (_fileConfig.isList("global.motd")) {
      lines = _fileConfig.getStringList("global.motd");
    } else {
      String motd = _fileConfig.getString("global.motd");
      lines = motd == null
          ? _getDefaultServerMOTDLines()
          : Arrays.asList(motd.split("\\R", 2));
    }

    return lines;
  }

  private List<String> _getDefaultServerMOTDLines() {
    String primary = Theme.asAmpersandCode(Theme.primary());
    String highlight = Theme.asAmpersandCode(Theme.highlight());
    return Arrays.asList(
        primary + "                  " + highlight + "&l" + Theme.name(),
        primary + "                         " + highlight + "&lSMP");
  }

  public synchronized void setServerMOTD(String line1, String line2) {
    _fileConfig.set("global.motd", Arrays.asList(line1, line2));
    _config.save();
  }

  public synchronized boolean getCropProtection() {
    return _fileConfig.getBoolean("global.cropprotection", false);
  }

  public synchronized void setCropProtection(boolean value) {
    _fileConfig.set("global.cropprotection", value);
    _config.save();
  }

  public synchronized boolean getRightClickCropHarvest() {
    return _fileConfig.getBoolean("global.rightclickcropharvest", false);
  }

  public synchronized void setRightClickCropHarvest(boolean value) {
    _fileConfig.set("global.rightclickcropharvest", value);
    _config.save();

    if (!value) {
      String[] uuids = getAllPlayersUUIDS();
      for (String uuid : uuids) {
        setPlayerRightClickCropHarvest(UUID.fromString(uuid), false);
      }
    }
  }

  public synchronized boolean getVillagerTradeCyclingEnabled() {
    return _fileConfig.getBoolean("global.villagertradecycling", false);
  }

  public synchronized void setVillagerTradeCyclingEnabled(boolean value) {
    _fileConfig.set("global.villagertradecycling", value);
    _config.save();
  }

  public synchronized boolean getBackpackEnabled() {
    return _fileConfig.getBoolean("global.backpack.enabled", false);
  }

  public synchronized void setBackpackEnabled(boolean value) {
    _fileConfig.set("global.backpack.enabled", value);
    _config.save();
  }

  public synchronized int getBackpackRows() {
    return Math.max(1, Math.min(5, _fileConfig.getInt("global.backpack.rows", 3)));
  }

  public synchronized void setBackpackRows(int value) {
    _fileConfig.set("global.backpack.rows", Math.max(1, Math.min(5, value)));
    _config.save();
  }

  public synchronized int getBackpackPages() {
    return Math.max(1, _fileConfig.getInt("global.backpack.pages", 1));
  }

  public synchronized void setBackpackPages(int value) {
    _fileConfig.set("global.backpack.pages", Math.max(1, value));
    _config.save();
  }

  public synchronized boolean getVeinMinerEnabled() {
    return _fileConfig.getBoolean("global.veinminer.enabled", false);
  }

  public synchronized void setVeinMinerEnabled(boolean value) {
    _fileConfig.set("global.veinminer.enabled", value);
    _config.save();

    if (!value) {
      String[] uuids = getAllPlayersUUIDS();
      for (String uuid : uuids) {
        setPlayerVeinMiner(UUID.fromString(uuid), value);
      }
    }
  }

  public synchronized boolean getVeinChopperEnabled() {
    return _fileConfig.getBoolean("global.veinchopper.enabled", false);
  }

  public synchronized void setVeinChopperEnabled(boolean value) {
    _fileConfig.set("global.veinchopper.enabled", value);
    _config.save();

    if (!value) {
      String[] uuids = getAllPlayersUUIDS();
      for (String uuid : uuids) {
        setPlayerVeinChopper(UUID.fromString(uuid), value);
      }
    }
  }

  public synchronized boolean getItemRestockEnabled() {
    return _fileConfig.getBoolean("global.itemrestock", false);
  }

  public synchronized void setItemRestockEnabled(boolean value) {
    _fileConfig.set("global.itemrestock", value);
    _config.save();

    if (!value) {
      String[] uuids = getAllPlayersUUIDS();
      for (String uuid : uuids) {
        setPlayerItemRestock(UUID.fromString(uuid), false);
      }
    }
  }

  public synchronized int getVeinMinerMaxVeinSize() {
    return _fileConfig.getInt("global.veinminer.maxveinsize", 100);
  }

  public synchronized boolean getVeinMinerSound() {
    return _fileConfig.getBoolean("global.veinminer.sound", true);
  }

  public synchronized void setVeinMinerSound(boolean value) {
    _fileConfig.set("global.veinminer.sound", value);
    _config.save();
  }

  public synchronized void setVeinMinerMaxVeinSize(int value) {
    _fileConfig.set("global.veinminer.maxveinsize", value);
    _config.save();
  }

  public synchronized int getVeinChopperMaxVeinSize() {
    return _fileConfig.getInt("global.veinchopper.maxveinsize", 100);
  }

  public synchronized boolean getVeinChopperSound() {
    return _fileConfig.getBoolean("global.veinchopper.sound", true);
  }

  public synchronized void setVeinChopperSound(boolean value) {
    _fileConfig.set("global.veinchopper.sound", value);
    _config.save();
  }

  public synchronized void setVeinChopperMaxVeinSize(int value) {
    _fileConfig.set("global.veinchopper.maxveinsize", value);
    _config.save();
  }

  public synchronized List<String> getVeinMinerAllowedTools() {
    String path = "global.veinminer.allowedtools";
    if (!_fileConfig.contains(path)) {
      return VEIN_MINER_TOOLS.stream().map(Material::name)
          .collect(Collectors.toCollection(ArrayList::new));
    }

    List<String> list = _fileConfig.getStringList(path);
    return list == null ? new ArrayList<>() : new ArrayList<>(list);
  }

  public synchronized void setVeinMinerAllowedTools(List<String> tools) {
    _fileConfig.set("global.veinminer.allowedtools", tools);
    _config.save();
  }

  public synchronized List<String> getVeinMinerAllowedBlocks() {
    String path = "global.veinminer.allowedblocks";
    if (!_fileConfig.contains(path)) {
      return VEIN_MINER_BLOCKS.stream().map(Material::name)
          .collect(Collectors.toCollection(ArrayList::new));
    }

    List<String> list = _fileConfig.getStringList(path);
    return list == null ? new ArrayList<>() : new ArrayList<>(list);
  }

  public synchronized void setVeinMinerAllowedBlocks(List<String> blocks) {
    _fileConfig.set("global.veinminer.allowedblocks", blocks);
    _config.save();
  }

  public synchronized List<String> getVeinChopperAllowedTools() {
    String path = "global.veinchopper.allowedtools";
    if (!_fileConfig.contains(path)) {
      return VEIN_CHOPPER_TOOLS.stream().map(Material::name)
          .collect(Collectors.toCollection(ArrayList::new));
    }

    List<String> list = _fileConfig.getStringList(path);
    return list == null ? new ArrayList<>() : new ArrayList<>(list);
  }

  public synchronized void setVeinChopperAllowedTools(List<String> tools) {
    _fileConfig.set("global.veinchopper.allowedtools", tools);
    _config.save();
  }

  public synchronized List<String> getVeinChopperAllowedBlocks() {
    String path = "global.veinchopper.allowedblocks";
    if (!_fileConfig.contains(path)) {
      return VEIN_CHOPPER_BLOCKS.stream().map(Material::name)
          .collect(Collectors.toCollection(ArrayList::new));
    }

    List<String> list = _fileConfig.getStringList(path);
    return list == null ? new ArrayList<>() : new ArrayList<>(list);
  }

  public synchronized void setVeinChopperAllowedBlocks(List<String> blocks) {
    _fileConfig.set("global.veinchopper.allowedblocks", blocks);
    _config.save();
  }

  public synchronized boolean getCraftingRecipeEnabled(String recipeKey) {
    String path = "global.recipes." + recipeKey + ".enabled";
    return _fileConfig.getBoolean(path, false);
  }

  public synchronized void setCraftingRecipeEnabled(String recipeKey, boolean value) {
    _fileConfig.set("global.recipes." + recipeKey + ".enabled", value);
    _config.save();
  }

  public synchronized List<ItemStack> getCraftingRecipeMatrix(String recipeKey, List<ItemStack> defaultMatrix) {
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

  public synchronized void setCraftingRecipeMatrix(String recipeKey, List<ItemStack> matrix) {
    String basePath = "global.recipes." + recipeKey + ".slots";

    for (int i = 0; i < 9; i++) {
      ItemStack item = (matrix != null && i < matrix.size()) ? matrix.get(i) : null;
      _fileConfig.set(basePath + "." + i, item == null ? null : item.clone());
    }

    _config.save();
  }
}
