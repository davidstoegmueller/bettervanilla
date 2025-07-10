package com.daveestar.bettervanilla.manager;

import java.util.*;

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
import com.daveestar.bettervanilla.utils.ItemStackUtils;

public class BackpackManager implements Listener {
  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final Config _config;
  private final FileConfiguration _fileConfig;

  private boolean _enabled;
  private int _rows;
  private int _pages;

  private final Map<UUID, CustomGUI> _openBackpacks = new HashMap<>();

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
  }

  public int getPages() {
    return _pages;
  }

  public void setPages(int pages) {
    _pages = pages;
    _settingsManager.setBackpackPages(pages);
  }

  private ItemStack[] _loadPage(UUID playerId, int page) {
    List<Map<?, ?>> list = _fileConfig.getMapList("players." + playerId + ".page" + page);
    ItemStack[] arr = ItemStackUtils.deserializeArray(list);
    int size = _getPageSize();
    if (arr.length < size) {
      ItemStack[] tmp = new ItemStack[size];
      System.arraycopy(arr, 0, tmp, 0, arr.length);
      return tmp;
    }
    return Arrays.copyOf(arr, size);
  }

  private void _savePage(UUID playerId, int page, ItemStack[] items) {
    List<Map<String, Object>> data = ItemStackUtils.serializeArray(items);
    _fileConfig.set("players." + playerId + ".page" + page, data);
    _config.save();
  }

  public void openBackpack(Player p) {
    if (!_enabled) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Backpacks are disabled.");
      return;
    }

    int pageSize = _getPageSize();

    Map<String, ItemStack> entries = new LinkedHashMap<>();
    Map<String, Integer> customSlots = new HashMap<>();

    for (int page = 1; page <= _pages; page++) {
      ItemStack[] items = _loadPage(p.getUniqueId(), page);
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
      _saveCurrentPage(pl, gui, oldPage);
    });

    gui.open(p);
    _openBackpacks.put(p.getUniqueId(), gui);
  }

  private void _saveCurrentPage(Player p, CustomGUI gui, int page) {
    int pageSize = _getPageSize();
    ItemStack[] items = new ItemStack[pageSize];
    Inventory inv = gui.getInventory();
    for (Map.Entry<Integer, String> entry : gui.getSlotKeyMap().entrySet()) {
      String key = entry.getValue();
      String[] parts = key.split("_");
      if (parts.length != 2)
        continue;
      int slotIndex = Integer.parseInt(parts[1]);
      items[slotIndex] = inv.getItem(entry.getKey());
    }
    _savePage(p.getUniqueId(), page, items);
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent e) {
    Player p = (Player) e.getPlayer();
    CustomGUI gui = _openBackpacks.remove(p.getUniqueId());
    if (gui != null && e.getInventory().equals(gui.getInventory())) {
      _saveCurrentPage(p, gui, gui.getCurrentPage());
    }
  }

  public void saveAllOpenBackpacks() {
    for (Map.Entry<UUID, CustomGUI> entry : new HashMap<>(_openBackpacks).entrySet()) {
      Player p = _plugin.getServer().getPlayer(entry.getKey());
      if (p != null) {
        CustomGUI gui = entry.getValue();
        _saveCurrentPage(p, gui, gui.getCurrentPage());
      }
    }
    _openBackpacks.clear();
  }
}
