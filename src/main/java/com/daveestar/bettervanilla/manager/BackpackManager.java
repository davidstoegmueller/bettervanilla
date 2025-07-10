package com.daveestar.bettervanilla.manager;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.Config;
import com.daveestar.bettervanilla.utils.CustomGUI;

public class BackpackManager implements Listener {
  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final Config _config;
  private final FileConfiguration _fileConfig;

  private boolean _enabled;
  private int _rows;
  private int _pages;

  private final Map<UUID, Backpack> _backpacks = new HashMap<>();
  private final Map<UUID, CustomGUI> _openGUIs = new HashMap<>();

  public BackpackManager(Config config) {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
    _config = config;
    _fileConfig = config.getFileConfig();

    _loadSettings();

    Bukkit.getPluginManager().registerEvents(this, _plugin);
  }

  private void _loadSettings() {
    _enabled = _settingsManager.getBackpackEnabled();
    _rows = _settingsManager.getBackpackRows();
    _pages = _settingsManager.getBackpackPages();
  }

  private int _getPageSize() {
    return _rows * 9 - 9;
  }

  public boolean isEnabled() {
    return _enabled;
  }

  public void setEnabled(boolean value) {
    _enabled = value;
    _settingsManager.setBackpackEnabled(value);
  }

  public int getRows() {
    return _rows;
  }

  public void setRows(int rows) {
    _rows = rows;
    _settingsManager.setBackpackRows(rows);
    _backpacks.clear();
  }

  public int getPages() {
    return _pages;
  }

  public void setPages(int pages) {
    _pages = pages;
    _settingsManager.setBackpackPages(pages);
    _backpacks.clear();
  }


  public void openBackpack(Player p) {
    if (!_enabled) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Backpacks are disabled.");
      return;
    }

    int pageSize = _getPageSize();
    Backpack backpack = _backpacks.computeIfAbsent(p.getUniqueId(),
        id -> new Backpack(id, _pages, pageSize, _fileConfig));

    Map<String, ItemStack> entries = new LinkedHashMap<>();
    Map<String, Integer> customSlots = new HashMap<>();

    for (int page = 1; page <= backpack.getTotalPages(); page++) {
      ItemStack[] items = backpack.getPage(page);
      for (int i = 0; i < pageSize; i++) {
        String key = page + "_" + i;
        ItemStack item = items[i];
        entries.put(key, item);
        customSlots.put(key, i);
      }
    }

    CustomGUI gui = new CustomGUI(_plugin, p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Backpack",
        entries, _rows, customSlots, null,
        EnumSet.of(CustomGUI.Option.ALLOW_ITEM_MOVEMENT));

    gui.setClickActions(new HashMap<>());
    gui.setPageSwitchListener((pl, newPage, oldPage) -> {
      _saveCurrentPage(pl, gui, backpack, oldPage);
    });

    gui.open(p);
    _openGUIs.put(p.getUniqueId(), gui);
  }

  private void _saveCurrentPage(Player p, CustomGUI gui, Backpack backpack, int page) {
    int pageSize = _getPageSize();
    ItemStack[] items = new ItemStack[pageSize];
    Inventory inv = gui.getInventory();
    for (int i = 0; i < pageSize; i++) {
      ItemStack it = inv.getItem(i);
      items[i] = it;
      String key = page + "_" + i;
      gui.setEntryItem(key, it);
    }
    backpack.setPage(page, items);
    backpack.savePage(_fileConfig, page);
    _config.save();
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent e) {
    Player p = (Player) e.getPlayer();
    CustomGUI gui = _openGUIs.remove(p.getUniqueId());
    Backpack backpack = _backpacks.get(p.getUniqueId());
    if (gui != null && e.getInventory().equals(gui.getInventory()) && backpack != null) {
      _saveCurrentPage(p, gui, backpack, gui.getCurrentPage());
    }
  }

  public void saveAllOpenBackpacks() {
    for (Map.Entry<UUID, CustomGUI> entry : new HashMap<>(_openGUIs).entrySet()) {
      Player p = _plugin.getServer().getPlayer(entry.getKey());
      Backpack backpack = _backpacks.get(entry.getKey());
      if (p != null && backpack != null) {
        CustomGUI gui = entry.getValue();
        _saveCurrentPage(p, gui, backpack, gui.getCurrentPage());
      }
    }
    _openGUIs.clear();
  }
}
