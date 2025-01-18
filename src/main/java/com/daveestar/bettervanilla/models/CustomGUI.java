package com.daveestar.bettervanilla.models;

import java.util.*;
import java.util.function.BiConsumer;

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

  private static final int _INVENTORY_SIZE = 54;
  private static final int _INVENTORY_RESERVED_ROWS = 1;
  private static final int _INVENTORY_ROW_SIZE = 9;
  private static final int _POS_SWITCH_PAGE_BUTTON = 53;

  private final int _pageSize;
  private int _currentPage;
  private final int _maxPage;

  private final Inventory _gui;
  private final Map<String, ItemStack> _pageEntries;
  private final BiConsumer<Player, String> _onItemClick;

  public CustomGUI(Plugin pluginInstance, Player player, String title, Map<String, ItemStack> pageEntries,
      BiConsumer<Player, String> onItemClick) {
    this._currentPage = 1;
    this._pageEntries = pageEntries;
    this._pageSize = _INVENTORY_SIZE - (_INVENTORY_RESERVED_ROWS * _INVENTORY_ROW_SIZE);
    this._maxPage = (int) Math.ceil((double) pageEntries.size() / _pageSize);
    this._onItemClick = onItemClick;

    this._gui = Bukkit.createInventory(null, _INVENTORY_SIZE, title);
    _updatePage();

    Bukkit.getPluginManager().registerEvents(this, pluginInstance);
  }

  public void open(Player player) {
    player.openInventory(_gui);
  }

  private void _clear() {
    _gui.clear();
  }

  private void _createActionButtons() {
    _createSwitchPageButton();
    _createPlaceholderButtons();
  }

  private void _createSwitchPageButton() {
    _addItemToSlot(_POS_SWITCH_PAGE_BUTTON, Material.BOOK,
        ChatColor.YELLOW + "Page " + ChatColor.GRAY + _currentPage + "/" + _maxPage,
        Arrays.asList(
            ChatColor.YELLOW + "» " + ChatColor.GRAY + "Right-Click: Next Page",
            ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Previous Page"));
  }

  private void _createPlaceholderButtons() {
    int startIdx = _INVENTORY_SIZE - (_INVENTORY_RESERVED_ROWS * _INVENTORY_ROW_SIZE);
    int endIdx = startIdx + (_INVENTORY_RESERVED_ROWS * _INVENTORY_ROW_SIZE);

    for (int i = startIdx; i < endIdx; i++) {
      if (_gui.getItem(i) == null) {
        _addItemToSlot(i, Material.YELLOW_STAINED_GLASS_PANE, ChatColor.YELLOW + "Coming Soon", null);
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
      _gui.setItem(i, currentEntries.get(i).getValue());
    }
  }

  @EventHandler
  private void _onInventoryClick(InventoryClickEvent e) {
    if (!e.getInventory().equals(_gui))
      return;

    e.setCancelled(true);
    Player player = (Player) e.getWhoClicked();
    int slot = e.getRawSlot();

    if (slot == _POS_SWITCH_PAGE_BUTTON) {
      _handlePageSwitch(player, e.isRightClick());
    } else if (slot >= 0 && slot < _pageSize) {
      _handleItemClick(player, slot);
    } else {
      player.playSound(player, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
    }
  }

  private void _handlePageSwitch(Player player, boolean isNextPage) {
    if (isNextPage && _currentPage < _maxPage) {
      _currentPage++;
    } else if (!isNextPage && _currentPage > 1) {
      _currentPage--;
    } else {
      player.playSound(player, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
      return;
    }
    _updatePage();
    player.playSound(player, Sound.ITEM_BOOK_PAGE_TURN, 0.5F, 1);
  }

  private void _handleItemClick(Player player, int slot) {
    int entryIndex = (_currentPage - 1) * _pageSize + slot;
    List<Map.Entry<String, ItemStack>> entries = new ArrayList<>(_pageEntries.entrySet());
    if (entryIndex < entries.size()) {
      _onItemClick.accept(player, entries.get(entryIndex).getKey());
    }
  }
}
