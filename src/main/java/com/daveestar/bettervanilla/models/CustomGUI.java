package com.daveestar.bettervanilla.models;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  private static int _INVENTORY_SIZE = 54;
  private static int _INVENTORY_RESERVED_ROWS = 1;
  private static int _INVENTORY_ROW_SIZE = 9;
  private static int _POS_SWITCH_PAGE_BUTTON = 53;

  private int _pageSize;
  private int _currentPage;
  private int _maxPage;

  private Inventory _gui;
  private Map<String, ItemStack> _pageEntries;
  private BiConsumer<Player, String> _onItemClick;

  public CustomGUI(Plugin pluginInstance, Player p, String title, HashMap<String, ItemStack> pageEntries,
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
    ItemStack switchPageButton = new ItemStack(Material.BOOK);
    switchPageButton.setAmount(_currentPage);

    ItemMeta switchPageButtonMeta = switchPageButton.getItemMeta();
    switchPageButtonMeta.setDisplayName(ChatColor.YELLOW + "Page " + ChatColor.GRAY + _currentPage + "/" + _maxPage);

    List<String> switchPageButtonLore = Arrays.asList(
        ChatColor.YELLOW + "» " + ChatColor.GRAY + "Right-Click: Next Page",
        ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Previous Page");
    switchPageButtonMeta.setLore(switchPageButtonLore);

    switchPageButton.setItemMeta(switchPageButtonMeta);

    _gui.setItem(_POS_SWITCH_PAGE_BUTTON, switchPageButton);
  }

  private void _createPlaceholderButtons() {
    int startIdx = _INVENTORY_SIZE - (_INVENTORY_RESERVED_ROWS * _INVENTORY_ROW_SIZE);
    int endIdx = startIdx + (_INVENTORY_RESERVED_ROWS * _INVENTORY_ROW_SIZE);

    for (int i = startIdx; i < endIdx; i++) {
      ItemStack itemInSlot = _gui.getItem(i);

      // only set placeholder items for "unused slots"
      if (itemInSlot == null) {
        ItemStack placeholderButton = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);

        ItemMeta placeholderButtonMeta = placeholderButton.getItemMeta();
        placeholderButtonMeta.setDisplayName(ChatColor.YELLOW + "coming soon");

        placeholderButton.setItemMeta(placeholderButtonMeta);

        _gui.setItem(i, placeholderButton);
      }
    }
  }

  private void _updatePage() {
    _clear();
    _createActionButtons();

    int startIdx = (_currentPage - 1) * _pageSize;
    int endIdx = Math.min(startIdx + _pageSize, _pageEntries.size());
    int slotIndex = 0;

    for (Map.Entry<String, ItemStack> entry : _pageEntries.entrySet()) {
      if (slotIndex >= startIdx && slotIndex < endIdx) {
        _gui.setItem(slotIndex - startIdx, entry.getValue());
      }
      slotIndex++;
    }
  }

  @EventHandler
  private void _onInventoryClick(InventoryClickEvent e) {
    if (e.getInventory().equals(_gui)) {
      // prevent item movement by default
      e.setCancelled(true);

      Player p = (Player) e.getWhoClicked();
      int slot = e.getRawSlot();

      if (slot == _POS_SWITCH_PAGE_BUTTON) {
        if (e.isRightClick()) {
          if (_currentPage < _maxPage) {
            _currentPage++;
            _updatePage();
            p.playSound(p, Sound.ITEM_BOOK_PAGE_TURN, 0.5F, 1);
          } else {
            p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          }
        }

        if (e.isLeftClick()) {
          if (_currentPage > 1) {
            _currentPage--;
            _updatePage();
            p.playSound(p, Sound.ITEM_BOOK_PAGE_TURN, 0.5F, 1);
          } else {
            p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          }
        }
      } else if (slot >= 0 && slot < _pageSize) {
        if (e.isLeftClick()) {
          int entryIndex = (_currentPage - 1) * _pageSize + slot;
          int currentIndex = 0;

          for (Map.Entry<String, ItemStack> entry : _pageEntries.entrySet()) {
            if (currentIndex == entryIndex) {
              String key = entry.getKey();
              _onItemClick.accept(p, key);
              break;
            }
            currentIndex++;
          }
        }
      } else {
        p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
      }
    }
  }
}
