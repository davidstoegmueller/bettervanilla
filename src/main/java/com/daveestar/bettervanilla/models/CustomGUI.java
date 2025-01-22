package com.daveestar.bettervanilla.models;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import net.md_5.bungee.api.ChatColor;

public class CustomGUI implements Listener {

  private static final int _INVENTORY_ROW_SIZE = 9;
  private final int _POS_SWITCH_PAGE_BUTTON;
  private final int _POS_BACK_BUTTON;

  private final int _pageSize;
  private int _currentPage;
  private final int _maxPage;

  private final Inventory _gui;
  private final Map<String, ItemStack> _pageEntries;
  private final Map<String, Integer> _customSlots;
  private final CustomGUI _parentMenu;
  private Map<String, ClickAction> _clickActions;
  private final Set<Option> _options;

  public CustomGUI(Plugin pluginInstance, Player player, String title, Map<String, ItemStack> pageEntries,
      int rows, Map<String, Integer> customSlots, CustomGUI parentMenu, Set<Option> options) {
    int inventorySize = rows * _INVENTORY_ROW_SIZE;
    this._currentPage = 1;
    this._pageEntries = pageEntries;
    this._customSlots = customSlots != null ? customSlots : new HashMap<>();
    this._pageSize = inventorySize - _INVENTORY_ROW_SIZE;
    this._maxPage = (int) Math.ceil((double) pageEntries.size() / _pageSize);
    this._parentMenu = parentMenu;
    this._options = options != null ? options : EnumSet.noneOf(Option.class);

    this._POS_SWITCH_PAGE_BUTTON = inventorySize - 1;
    this._POS_BACK_BUTTON = inventorySize - _INVENTORY_ROW_SIZE;

    this._gui = Bukkit.createInventory(null, inventorySize, title);
    _updatePage();

    Bukkit.getPluginManager().registerEvents(this, pluginInstance);
  }

  public void open(Player p) {
    p.openInventory(_gui);
  }

  public void setClickActions(Map<String, ClickAction> clickActions) {
    this._clickActions = clickActions;
  }

  private void _clear() {
    _gui.clear();
  }

  private void _createActionButtons() {
    if (!_options.contains(Option.DISABLE_PAGE_BUTTON)) {
      _createSwitchPageButton();
    }
    _createPlaceholderButtons();
    if (_parentMenu != null) {
      _createBackButton();
    }
  }

  private void _createSwitchPageButton() {
    _addItemToSlot(_POS_SWITCH_PAGE_BUTTON, Material.BOOK,
        ChatColor.YELLOW + "Page " + ChatColor.GRAY + _currentPage + "/" + _maxPage,
        Arrays.asList(
            ChatColor.YELLOW + "» " + ChatColor.GRAY + "Right-Click: Next Page",
            ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Previous Page"));
  }

  private void _createBackButton() {
    _addItemToSlot(_POS_BACK_BUTTON, Material.ARROW, ChatColor.YELLOW + "Back", null);
  }

  private void _createPlaceholderButtons() {
    int startIdx = _gui.getSize() - _INVENTORY_ROW_SIZE;
    int endIdx = _gui.getSize();

    for (int i = startIdx; i < endIdx; i++) {
      if (_gui.getItem(i) == null) {
        _addItemToSlot(i, Material.YELLOW_STAINED_GLASS_PANE, ChatColor.YELLOW + "*", null);
      }
    }
  }

  private void _addItemToSlot(int slot, Material material, String displayName, List<String> lore) {
    ItemStack item = new ItemStack(material);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.setDisplayName(displayName);
      if (lore != null)
        meta.setLore(lore);
      item.setItemMeta(meta);
    }
    _gui.setItem(slot, item);
  }

  private List<Map.Entry<String, ItemStack>> _getPageEntries() {
    int startIdx = (_currentPage - 1) * _pageSize;
    int endIdx = Math.min(startIdx + _pageSize, _pageEntries.size());
    return new ArrayList<>(_pageEntries.entrySet()).subList(startIdx, endIdx);
  }

  private void _updatePage() {
    _clear();
    _createActionButtons();

    List<Map.Entry<String, ItemStack>> currentEntries = _getPageEntries();
    for (int i = 0; i < currentEntries.size(); i++) {
      String key = currentEntries.get(i).getKey();
      int slot = _customSlots.containsKey(key) ? _customSlots.get(key) : i;
      _gui.setItem(slot, currentEntries.get(i).getValue());
    }
  }

  @EventHandler
  private void _onInventoryClick(InventoryClickEvent e) {
    if (!e.getInventory().equals(_gui))
      return;

    e.setCancelled(true);
    Player p = (Player) e.getWhoClicked();
    int slot = e.getRawSlot();

    if (slot == _POS_SWITCH_PAGE_BUTTON) {
      _handlePageSwitch(p, e.isRightClick());
    } else if (slot == _POS_BACK_BUTTON && _parentMenu != null) {
      _parentMenu.open(p);
    } else if (_customSlots.containsValue(slot)) {
      String key = _customSlots.entrySet().stream()
          .filter(entry -> entry.getValue().equals(slot))
          .map(Map.Entry::getKey)
          .findFirst()
          .orElse(null);
      if (key != null) {
        _handleItemClick(p, key, e.isShiftClick(), e.isRightClick());
      }
    } else if (slot >= 0 && slot < _pageSize) {
      _handleItemClick(p, slot, e.isShiftClick(), e.isRightClick());
    } else {
      p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
    }
  }

  private void _handlePageSwitch(Player p, boolean isNextPage) {
    if (isNextPage && _currentPage < _maxPage) {
      _currentPage++;
    } else if (!isNextPage && _currentPage > 1) {
      _currentPage--;
    } else {
      p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
      return;
    }
    _updatePage();
    p.playSound(p, Sound.ITEM_BOOK_PAGE_TURN, 0.5F, 1);
  }

  private void _handleItemClick(Player player, Object keyOrSlot, boolean isShiftClick, boolean isRightClick) {
    String key = null;
    if (keyOrSlot instanceof Integer) {
      int slot = (int) keyOrSlot;
      int entryIndex = (_currentPage - 1) * _pageSize + slot;
      List<Map.Entry<String, ItemStack>> entries = new ArrayList<>(_pageEntries.entrySet());
      if (entryIndex < entries.size()) {
        key = entries.get(entryIndex).getKey();
      }
    } else if (keyOrSlot instanceof String) {
      key = (String) keyOrSlot;
    }

    if (key != null) {
      ClickAction action = _clickActions.get(key);
      if (action != null) {
        if (isShiftClick && isRightClick) {
          action.onShiftRightClick(player);
        } else if (isShiftClick) {
          action.onShiftLeftClick(player);
        } else if (isRightClick) {
          action.onRightClick(player);
        } else {
          action.onLeftClick(player);
        }
      }
    }
  }

  public interface ClickAction {
    void onLeftClick(Player player);

    void onRightClick(Player player);

    void onShiftLeftClick(Player player);

    void onShiftRightClick(Player player);
  }

  public enum Option {
    DISABLE_PAGE_BUTTON,
  }
}