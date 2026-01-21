package com.daveestar.bettervanilla.utils;

import java.util.*;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import io.papermc.paper.dialog.Dialog;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.ChatColor;

public class CustomGUI implements Listener {
  private static final int _INVENTORY_ROW_SIZE = 9;
  private final int _POS_SWITCH_PAGE_BUTTON;
  private final int _POS_SEARCH_BUTTON;
  private final int _POS_BACK_BUTTON;
  private final int _pageSize;
  private int _currentPage;
  private int _maxPage;
  private final Inventory _gui;
  private final List<Map.Entry<String, ItemStack>> _allEntryList;
  private List<Map.Entry<String, ItemStack>> _entryList;
  private final Map<String, Integer> _customSlots;
  private final Map<Integer, String> _slotKeyMap;
  private final Map<String, FooterEntry> _footerEntries;
  private final CustomGUI _parentMenu;
  private Map<String, ClickAction> _clickActions;
  private final Set<Option> _options;
  private final boolean _searchEnabled;
  private Consumer<Player> _backAction;
  private PageSwitchListener _pageSwitchListener;
  private String _searchTerm;

  public CustomGUI(Plugin pluginInstance, Player p, String title, Map<String, ItemStack> pageEntries,
      int rows, Map<String, Integer> customSlots, CustomGUI parentMenu, Set<Option> options) {
    int inventorySize = rows * _INVENTORY_ROW_SIZE;
    _currentPage = 1;
    _allEntryList = new ArrayList<>(pageEntries.entrySet());
    _entryList = new ArrayList<>(_allEntryList);
    _customSlots = customSlots != null ? customSlots : new HashMap<>();
    _slotKeyMap = new HashMap<>();
    _footerEntries = new HashMap<>();
    _pageSize = inventorySize - _INVENTORY_ROW_SIZE;
    _maxPage = _calculateMaxPage();
    _parentMenu = parentMenu;
    _options = options != null ? options : EnumSet.noneOf(Option.class);
    _searchEnabled = _options.contains(Option.SEARCH);
    _searchTerm = "";

    _POS_SWITCH_PAGE_BUTTON = inventorySize - 1;
    _POS_SEARCH_BUTTON = inventorySize - 2;
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

  public void setBackAction(Consumer<Player> backAction) {
    _backAction = backAction;
  }

  public void addFooterEntry(String key, ItemStack item, int slot) {
    if (key == null || item == null)
      return;

    _footerEntries.put(key, new FooterEntry(item.clone(), slot));
    _applyFooterEntries();
  }

  public Inventory getInventory() {
    return _gui;
  }

  public int getCurrentPage() {
    return _currentPage;
  }

  public void setEntryItem(String key, ItemStack item) {
    for (Map.Entry<String, ItemStack> entry : _allEntryList) {
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

    if (_options.contains(Option.SEARCH)) {
      _createSearchButton();
    }

    _createPlaceholderButtons();

    if (_parentMenu != null) {
      _createBackButton();
    }
  }

  private void _createSwitchPageButton() {
    _addItemToSlot(_POS_SWITCH_PAGE_BUTTON, Material.BOOK,
        ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Page",
        Arrays.asList(
            ChatColor.YELLOW + "» " + ChatColor.GRAY + "Current Page: " + ChatColor.YELLOW + _currentPage
                + ChatColor.GRAY + " of " + ChatColor.YELLOW + _maxPage,
            "",
            ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Next Page",
            ChatColor.YELLOW + "» " + ChatColor.GRAY + "Right-Click: Previous Page"));
  }

  private void _createBackButton() {
    _addItemToSlot(_POS_BACK_BUTTON, Material.ARROW, ChatColor.YELLOW + "Back", null);
  }

  private void _createSearchButton() {
    String term = _searchTerm != null && !_searchTerm.isEmpty()
        ? ChatColor.YELLOW + _searchTerm
        : ChatColor.RED + "None";

    _addItemToSlot(_POS_SEARCH_BUTTON, Material.NAME_TAG,
        ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Search",
        Arrays.asList(
            ChatColor.YELLOW + "» " + ChatColor.GRAY + "Search for: " + term,
            "",
            ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Search",
            ChatColor.YELLOW + "» " + ChatColor.GRAY + "Right-Click: Reset"));
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
    for (int i = 0; i < currentEntries.size(); i++) {
      Map.Entry<String, ItemStack> entry = currentEntries.get(i);
      String key = entry.getKey();
      int slot = _customSlots.getOrDefault(key, i);

      _gui.setItem(slot, entry.getValue());
      _slotKeyMap.put(slot, key);
    }

    _applyFooterEntries();
  }

  private void _applyFooterEntries() {
    if (_footerEntries.isEmpty())
      return;

    for (Map.Entry<String, FooterEntry> entry : _footerEntries.entrySet()) {
      FooterEntry footerEntry = entry.getValue();
      int slot = footerEntry.slot();

      _gui.setItem(slot, footerEntry.item().clone());
      _slotKeyMap.put(slot, entry.getKey());
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

    boolean pageButtonEnabled = !_options.contains(Option.DISABLE_PAGE_BUTTON);
    boolean hasBackButton = _parentMenu != null;
    boolean isSwitchSlot = pageButtonEnabled && rawSlot == _POS_SWITCH_PAGE_BUTTON;
    boolean isBackSlot = hasBackButton && rawSlot == _POS_BACK_BUTTON;
    boolean isSearchSlot = _searchEnabled && rawSlot == _POS_SEARCH_BUTTON;
    boolean isActionSlot = isSwitchSlot || isBackSlot || isSearchSlot;
    boolean isNavSlot = rawSlot >= topSize - _INVENTORY_ROW_SIZE;
    boolean isItemSlot = _slotKeyMap.containsKey(rawSlot);

    if (allowMove && !isNavSlot && !isActionSlot) {
      // allow default item movement in the editable area
      return;
    }

    e.setCancelled(true);

    if (e.getClick() == ClickType.DOUBLE_CLICK) {
      return;
    }

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

    if (isSearchSlot) {
      _handleSearchClick(p, e.getClick());
    } else if (isSwitchSlot) {
      ClickType click = e.getClick();
      boolean handled = false;

      switch (click) {
        case LEFT:
        case SHIFT_LEFT:
        case WINDOW_BORDER_LEFT:
          _handlePageSwitch(p, true);
          handled = true;
          break;
        case RIGHT:
        case SHIFT_RIGHT:
        case WINDOW_BORDER_RIGHT:
          _handlePageSwitch(p, false);
          handled = true;
          break;
        default:
          break;
      }

      if (handled) {
        p.playSound(p, Sound.UI_BUTTON_CLICK, 0.5F, 1);
      }
    } else if (isBackSlot) {
      p.playSound(p, Sound.UI_BUTTON_CLICK, 0.5F, 1);
      if (_backAction != null) {
        _backAction.accept(p);
      } else if (_parentMenu != null) {
        _parentMenu.open(p);
      }
    } else if (isItemSlot) {
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

  private void _handleSearchClick(Player p, ClickType click) {
    if (!_searchEnabled) {
      return;
    }

    boolean handled = false;
    switch (click) {
      case LEFT:
      case SHIFT_LEFT:
      case WINDOW_BORDER_LEFT:
        _openSearchDialog(p);
        handled = true;
        break;
      case RIGHT:
      case SHIFT_RIGHT:
      case WINDOW_BORDER_RIGHT:
        _applySearchTerm("");
        p.openInventory(_gui);
        handled = true;
        break;
      default:
        break;
    }

    if (handled) {
      p.playSound(p, Sound.UI_BUTTON_CLICK, 0.5F, 1);
    }
  }

  private void _openSearchDialog(Player p) {
    if (!_searchEnabled) {
      return;
    }

    Dialog dialog = CustomDialog.createConfirmationDialog(
        "Search",
        "Enter text to filter items.",
        null,
        List.of(CustomDialog.createTextInput("search",
            ChatColor.YELLOW + "» " + ChatColor.GRAY + "Search",
            _searchTerm)),
        (view, audience) -> {
          Player player = (Player) audience;
          String input = Optional.ofNullable(view.getText("search")).map(String::trim).orElse("");
          _applySearchTerm(input);
          player.openInventory(_gui);
        },
        null);

    p.showDialog(dialog);
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

  private void _applySearchTerm(String term) {
    _searchTerm = term != null ? term.trim() : "";

    if (_searchTerm.isEmpty()) {
      _entryList = new ArrayList<>(_allEntryList);
      _currentPage = 1;
      _maxPage = _calculateMaxPage();
      _updatePage();
      return;
    }

    String normalizedTerm = _normalizeText(_searchTerm);
    _entryList = new ArrayList<>(_allEntryList.stream()
        .filter(entry -> _searchMatcher(entry.getKey(), entry.getValue(), normalizedTerm))
        .toList());

    _currentPage = 1;
    _maxPage = _calculateMaxPage();
    _updatePage();
  }

  private boolean _searchMatcher(String key, ItemStack item, String normalizedTerm) {
    if (normalizedTerm.isEmpty()) {
      return true;
    }

    if (_normalizeText(key).contains(normalizedTerm)) {
      return true;
    }

    if (item == null) {
      return false;
    }

    if (_normalizeText(item.getType().name()).contains(normalizedTerm)) {
      return true;
    }

    ItemMeta meta = item.getItemMeta();
    if (meta == null) {
      return false;
    }

    Component displayName = meta.displayName();
    if (displayName != null && _normalizeComponent(displayName).contains(normalizedTerm)) {
      return true;
    }

    List<Component> lore = meta.lore();
    if (lore == null) {
      return false;
    }

    for (Component line : lore) {
      if (_normalizeComponent(line).contains(normalizedTerm)) {
        return true;
      }
    }

    return false;
  }

  private String _normalizeComponent(Component component) {
    return _normalizeText(PlainTextComponentSerializer.plainText().serialize(component));
  }

  private String _normalizeText(String text) {
    if (text == null) {
      return "";
    }

    String stripped = ChatColor.stripColor(text);
    if (stripped == null) {
      stripped = text;
    }

    return stripped.toLowerCase(Locale.ROOT);
  }

  private int _calculateMaxPage() {
    if (_pageSize <= 0) {
      return 1;
    }

    return Math.max(1, (int) Math.ceil((double) _entryList.size() / _pageSize));
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
    SEARCH,
  }

  private record FooterEntry(ItemStack item, int slot) {
  }
}
