package com.daveestar.bettervanilla.utils;

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

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class CustomGUI implements Listener {

  private static final int _INVENTORY_ROW_SIZE = 9;
  private final int _POS_SWITCH_PAGE_BUTTON;
  private final int _POS_BACK_BUTTON;

  private final int _pageSize;
  private int _currentPage;
  private final int _maxPage;
  private final int _defaultSlotOffset;

  private final Inventory _gui;
  private final List<Map.Entry<String, ItemStack>> _entryList;
  private final Map<String, Integer> _customSlots;
  private final Map<Integer, String> _slotKeyMap;
  private final CustomGUI _parentMenu;
  private Map<String, ClickAction> _clickActions;
  private final Set<Option> _options;
  private PageSwitchListener _pageSwitchListener;

  public CustomGUI(Plugin pluginInstance, Player p, String title, Map<String, ItemStack> pageEntries,
      int rows, Map<String, Integer> customSlots, CustomGUI parentMenu, Set<Option> options) {
    this(pluginInstance, p, title, pageEntries, rows, customSlots, parentMenu, options, 0);
  }

  public CustomGUI(Plugin pluginInstance, Player p, String title, Map<String, ItemStack> pageEntries,
      int rows, Map<String, Integer> customSlots, CustomGUI parentMenu, Set<Option> options,
      int defaultSlotOffset) {
    int inventorySize = rows * _INVENTORY_ROW_SIZE;
    _currentPage = 1;
    _entryList = new ArrayList<>(pageEntries.entrySet());
    _customSlots = customSlots != null ? customSlots : new HashMap<>();
    _slotKeyMap = new HashMap<>();
    _defaultSlotOffset = Math.max(0, defaultSlotOffset);
    _pageSize = inventorySize - _INVENTORY_ROW_SIZE - _defaultSlotOffset;
    if (_pageSize <= 0)
      _pageSize = 1;
    _maxPage = (int) Math.ceil((double) _entryList.size() / _pageSize);
    _parentMenu = parentMenu;
    _options = options != null ? options : EnumSet.noneOf(Option.class);

    _POS_SWITCH_PAGE_BUTTON = inventorySize - 1;
    _POS_BACK_BUTTON = inventorySize - _INVENTORY_ROW_SIZE;

    _gui = Bukkit.createInventory(null, inventorySize, Component.text(title));
    _updatePage();

    Bukkit.getPluginManager().registerEvents(this, pluginInstance);
  }

  public void open(Player p) {
    p.playSound(p, Sound.UI_TOAST_IN, 0.7F, 1);
    p.openInventory(_gui);
  }

  public void setClickActions(Map<String, ClickAction> clickActions) {
    _clickActions = clickActions;
  }

  public Inventory getInventory() {
    return _gui;
  }

  public int getCurrentPage() {
    return _currentPage;
  }

  public void setEntryItem(String key, ItemStack item) {
    for (Map.Entry<String, ItemStack> entry : _entryList) {
      if (entry.getKey().equals(key)) {
        entry.setValue(item);
        break;
      }
    }
  }

  public void setPageSwitchListener(PageSwitchListener listener) {
    _pageSwitchListener = listener;
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
            ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Next Page",
            ChatColor.YELLOW + "» " + ChatColor.GRAY + "Right-Click: Previous Page"));
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
      meta.displayName(Component.text(displayName));

      if (lore != null)
        meta.lore(lore.stream().map(Component::text).toList());
      item.setItemMeta(meta);
    }

    _gui.setItem(slot, item);
  }

  private List<Map.Entry<String, ItemStack>> _getPageEntries() {
    int startIdx = (_currentPage - 1) * _pageSize;
    int endIdx = Math.min(startIdx + _pageSize, _entryList.size());
    return _entryList.subList(startIdx, endIdx);
  }

  private void _updatePage() {
    _clear();
    _createActionButtons();

    _slotKeyMap.clear();

    List<Map.Entry<String, ItemStack>> currentEntries = _getPageEntries();
    int defaultSlotIndex = _defaultSlotOffset;
    for (Map.Entry<String, ItemStack> entry : currentEntries) {
      String key = entry.getKey();
      int slot = _customSlots.getOrDefault(key, defaultSlotIndex);

      _gui.setItem(slot, entry.getValue());
      _slotKeyMap.put(slot, key);

      if (!_customSlots.containsKey(key)) {
        defaultSlotIndex++;
      }
    }
  }

  @EventHandler
  private void _onInventoryClick(InventoryClickEvent e) {
    if (!e.getInventory().equals(_gui))
      return;

    boolean allowMove = _options.contains(Option.ALLOW_ITEM_MOVEMENT);
    Player p = (Player) e.getWhoClicked();
    int rawSlot = e.getRawSlot();
    int topSize = _gui.getSize();

    if (rawSlot >= topSize) {
      if (!allowMove) {
        e.setCancelled(true);
      }

      // player inventory interaction
      return;
    }

    boolean isNavSlot = rawSlot >= topSize - _INVENTORY_ROW_SIZE;
    boolean isActionSlot = rawSlot == _POS_SWITCH_PAGE_BUTTON
        || (rawSlot == _POS_BACK_BUTTON && _parentMenu != null);
    boolean isItemSlot = _slotKeyMap.containsKey(rawSlot);

    if (allowMove && !isNavSlot && !isActionSlot) {
      // allow default item movement in the editable area
      return;
    }

    e.setCancelled(true);

    if (allowMove && isItemSlot && e.getCursor().getType() == Material.AIR
        && !isNavSlot && !isActionSlot) {
      ItemStack item = _gui.getItem(rawSlot);

      if (item != null) {
        if (e.isShiftClick()) {
          Map<Integer, ItemStack> left = p.getInventory().addItem(item.clone());

          if (left.isEmpty()) {
            _gui.setItem(rawSlot, null);
            setEntryItem(_slotKeyMap.get(rawSlot), null);
          } else {
            p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          }
        } else {
          p.setItemOnCursor(item.clone());
          _gui.setItem(rawSlot, null);
          setEntryItem(_slotKeyMap.get(rawSlot), null);
        }

        return;
      }
    }

    if (!isActionSlot && !isItemSlot)
      return;

    if (rawSlot == _POS_SWITCH_PAGE_BUTTON) {
      _handlePageSwitch(p, e.isLeftClick());
      p.playSound(p, Sound.UI_BUTTON_CLICK, 0.5F, 1);
    } else if (rawSlot == _POS_BACK_BUTTON && _parentMenu != null) {
      p.playSound(p, Sound.UI_BUTTON_CLICK, 0.5F, 1);
      _parentMenu.open(p);
    } else if (_slotKeyMap.containsKey(rawSlot)) {
      _handleItemClick(p, _slotKeyMap.get(rawSlot), e.isShiftClick(), e.isRightClick());
    } else {
      p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
    }
  }

  private void _handlePageSwitch(Player p, boolean isNextPage) {
    int oldPage = _currentPage;
    int newPage = _currentPage;

    if (isNextPage && _currentPage < _maxPage) {
      newPage = _currentPage + 1;
    } else if (!isNextPage && _currentPage > 1) {
      newPage = _currentPage - 1;
    } else {
      p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
      return;
    }

    if (_pageSwitchListener != null) {
      _pageSwitchListener.onPageSwitch(p, newPage, oldPage);
    }

    _currentPage = newPage;
    _updatePage();
    p.playSound(p, Sound.ITEM_BOOK_PAGE_TURN, 0.5F, 1);
  }

  private void _handleItemClick(Player p, String key, boolean isShiftClick, boolean isRightClick) {
    if (key == null)
      return;

    ClickAction action = _clickActions.get(key);
    if (action != null) {
      if (isShiftClick && isRightClick) {
        action.onShiftRightClick(p);
      } else if (isShiftClick) {
        action.onShiftLeftClick(p);
      } else if (isRightClick) {
        action.onRightClick(p);
      } else {
        action.onLeftClick(p);
      }

      p.playSound(p, Sound.UI_BUTTON_CLICK, 0.5F, 1);
    }
  }

  public interface ClickAction {
    default void onLeftClick(Player p) {
      // Default implementation (no action)
    }

    default void onRightClick(Player p) {
      // Default implementation (no action)
    }

    default void onShiftLeftClick(Player p) {
      // Default implementation (no action)
    }

    default void onShiftRightClick(Player p) {
      // Default implementation (no action)
    }
  }

  public interface PageSwitchListener {
    void onPageSwitch(Player p, int newPage, int oldPage);
  }

  public enum Option {
    DISABLE_PAGE_BUTTON,
    ALLOW_ITEM_MOVEMENT,
  }
}