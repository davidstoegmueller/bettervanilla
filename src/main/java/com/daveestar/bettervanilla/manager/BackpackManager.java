package com.daveestar.bettervanilla.manager;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.utils.Config;
import com.daveestar.bettervanilla.utils.CustomGUI;
import com.daveestar.bettervanilla.utils.ItemStackUtils;

import net.md_5.bungee.api.ChatColor;

public class BackpackManager implements Listener {
  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final DeathPointsManager _deathPointsManager;
  private final Config _config;
  private final FileConfiguration _fileConfig;

  private final Map<UUID, Map<Integer, ItemStack[]>> _backpacks = new HashMap<>();
  private final Map<UUID, CustomGUI> _openGUIs = new HashMap<>();

  public BackpackManager(Config config) {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
    _deathPointsManager = _plugin.getDeathPointsManager();

    _config = config;
    _fileConfig = config.getFileConfig();

    Bukkit.getPluginManager().registerEvents(this, _plugin);
  }

  public void openBackpack(Player p) {
    if (!_settingsManager.getBackpackEnabled()) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Backpacks are disabled.");
      return;
    }

    if (_deathPointsManager.hasActiveDeathPoints(p)) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED
          + "You have active death points. Claim them before using your backpack again. "
          + ChatColor.YELLOW + "/deathpoints");
      return;
    }

    int pages = _settingsManager.getBackpackPages();
    int rows = _settingsManager.getBackpackRows() + 1;
    int pageSize = _getPageSize();
    Map<Integer, ItemStack[]> backpack = _backpacks.computeIfAbsent(p.getUniqueId(),
        id -> _loadPlayerBackpack(id));

    Map<String, ItemStack> entries = new LinkedHashMap<>();

    for (int page = 1; page <= pages; page++) {
      ItemStack[] items = backpack.get(page);

      for (int i = 0; i < pageSize; i++) {
        String key = page + "_" + i;
        ItemStack item = items[i];

        entries.put(key, item);
      }
    }

    CustomGUI gui = new CustomGUI(_plugin, p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Backpack",
        entries, rows, null, null,
        EnumSet.of(CustomGUI.Option.ALLOW_ITEM_MOVEMENT));

    gui.setClickActions(new HashMap<>());
    gui.setPageSwitchListener((player, newPage, oldPage) -> {
      _saveCurrentPage(p, gui, backpack, oldPage);
    });

    gui.open(p);
    _openGUIs.put(p.getUniqueId(), gui);
  }

  public void closeBackpackOnDeath(Player p) {
    UUID playerId = p.getUniqueId();
    CustomGUI gui = _openGUIs.remove(playerId);
    Map<Integer, ItemStack[]> backpack = _backpacks.remove(playerId);

    if (gui != null && backpack != null) {
      _saveCurrentPage(p, gui, backpack, gui.getCurrentPage());
      p.closeInventory();
    }
  }

  public void setEnabled(boolean value) {
    _settingsManager.setBackpackEnabled(value);
  }

  public void setRows(int rows) {
    _settingsManager.setBackpackRows(rows);
    _backpacks.clear();
  }

  public void setPages(int pages) {
    _settingsManager.setBackpackPages(pages);
    _backpacks.clear();

    if (_fileConfig.isConfigurationSection("players")) {
      for (String player : _fileConfig.getConfigurationSection("players").getKeys(false)) {
        for (int i = pages + 1; _fileConfig.contains("players." + player + ".page" + i); i++) {
          _fileConfig.set("players." + player + ".page" + i, null);
        }
      }
    }

    _config.save();
  }

  public void destroy() {
    for (Map.Entry<UUID, CustomGUI> entry : new HashMap<>(_openGUIs).entrySet()) {
      Player p = _plugin.getServer().getPlayer(entry.getKey());
      Map<Integer, ItemStack[]> backpack = _backpacks.get(entry.getKey());

      if (p != null && backpack != null) {
        CustomGUI gui = entry.getValue();
        _saveCurrentPage(p, gui, backpack, gui.getCurrentPage());
      }
    }

    _openGUIs.clear();
  }

  public void onPlayerLeft(Player p) {
    _openGUIs.remove(p.getUniqueId());
    _backpacks.remove(p.getUniqueId());
  }

  private void _saveCurrentPage(Player p, CustomGUI gui, Map<Integer, ItemStack[]> backpack, int page) {
    int pageSize = _getPageSize();
    ItemStack[] items = new ItemStack[pageSize];
    Inventory inv = gui.getInventory();

    for (int i = 0; i < pageSize; i++) {
      ItemStack item = inv.getItem(i);
      items[i] = item;

      String key = page + "_" + i;
      gui.setEntryItem(key, item);
    }

    backpack.put(page, items);
    _savePage(p.getUniqueId(), page, items);
  }

  private Map<Integer, ItemStack[]> _loadPlayerBackpack(UUID playerId) {
    int pages = _settingsManager.getBackpackPages();
    int pageSize = _getPageSize();
    Map<Integer, ItemStack[]> data = new HashMap<>();

    for (int i = 1; i <= pages; i++) {
      data.put(i, _loadPage(playerId, i, pageSize));
    }

    return data;
  }

  private ItemStack[] _loadPage(UUID playerId, int page, int pageSize) {
    List<?> list = _fileConfig.getList("players." + playerId + ".page" + page);
    ItemStack[] arr = ItemStackUtils.deserializeArray(list);

    if (arr.length < pageSize) {
      ItemStack[] tmp = new ItemStack[pageSize];
      System.arraycopy(arr, 0, tmp, 0, arr.length);
      return tmp;
    }

    return Arrays.copyOf(arr, pageSize);
  }

  private void _savePage(UUID playerId, int page, ItemStack[] items) {
    List<Map<String, Object>> data = ItemStackUtils.serializeArray(items);
    _fileConfig.set("players." + playerId + ".page" + page, data);
    _config.save();
  }

  private int _getPageSize() {
    int rows = _settingsManager.getBackpackRows();
    return (rows + 1) * 9 - 9;
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent e) {
    Player p = (Player) e.getPlayer();
    CustomGUI gui = _openGUIs.remove(p.getUniqueId());
    Map<Integer, ItemStack[]> backpack = _backpacks.remove(p.getUniqueId());

    if (gui != null && e.getInventory().equals(gui.getInventory()) && backpack != null) {
      _saveCurrentPage(p, gui, backpack, gui.getCurrentPage());
    }
  }
}
