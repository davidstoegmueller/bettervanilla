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

  private static final int INVENTORY_SIZE = 54;
  private static final int INVENTORY_RESERVED_ROWS = 1;
  private static final int INVENTORY_ROW_SIZE = 9;
  private static final int POS_SWITCH_PAGE_BUTTON = 53;

  private final int pageSize;
  private int currentPage;
  private final int maxPage;

  private final Inventory gui;
  private final Map<String, ItemStack> pageEntries;
  private final BiConsumer<Player, String> onItemClick;

  public CustomGUI(Plugin pluginInstance, Player player, String title, Map<String, ItemStack> pageEntries,
      BiConsumer<Player, String> onItemClick) {
    this.currentPage = 1;
    this.pageEntries = pageEntries;
    this.pageSize = INVENTORY_SIZE - (INVENTORY_RESERVED_ROWS * INVENTORY_ROW_SIZE);
    this.maxPage = (int) Math.ceil((double) pageEntries.size() / pageSize);
    this.onItemClick = onItemClick;

    this.gui = Bukkit.createInventory(null, INVENTORY_SIZE, title);
    updatePage();

    Bukkit.getPluginManager().registerEvents(this, pluginInstance);
  }

  public void open(Player player) {
    player.openInventory(gui);
  }

  private void clear() {
    gui.clear();
  }

  private void createActionButtons() {
    createSwitchPageButton();
    createPlaceholderButtons();
  }

  private void createSwitchPageButton() {
    addItemToSlot(POS_SWITCH_PAGE_BUTTON, Material.BOOK,
        ChatColor.YELLOW + "Page " + ChatColor.GRAY + currentPage + "/" + maxPage,
        Arrays.asList(
            ChatColor.YELLOW + "» " + ChatColor.GRAY + "Right-Click: Next Page",
            ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Previous Page"));
  }

  private void createPlaceholderButtons() {
    int startIdx = INVENTORY_SIZE - (INVENTORY_RESERVED_ROWS * INVENTORY_ROW_SIZE);
    int endIdx = startIdx + (INVENTORY_RESERVED_ROWS * INVENTORY_ROW_SIZE);

    for (int i = startIdx; i < endIdx; i++) {
      if (gui.getItem(i) == null) {
        addItemToSlot(i, Material.YELLOW_STAINED_GLASS_PANE, ChatColor.YELLOW + "Coming Soon", null);
      }
    }
  }

  private void addItemToSlot(int slot, Material material, String displayName, List<String> lore) {
    ItemStack item = new ItemStack(material);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.setDisplayName(displayName);
      if (lore != null)
        meta.setLore(lore);
      item.setItemMeta(meta);
    }
    gui.setItem(slot, item);
  }

  private List<Map.Entry<String, ItemStack>> getPageEntries() {
    int startIdx = (currentPage - 1) * pageSize;
    int endIdx = Math.min(startIdx + pageSize, pageEntries.size());
    return new ArrayList<>(pageEntries.entrySet()).subList(startIdx, endIdx);
  }

  private void updatePage() {
    clear();
    createActionButtons();

    List<Map.Entry<String, ItemStack>> currentEntries = getPageEntries();
    for (int i = 0; i < currentEntries.size(); i++) {
      gui.setItem(i, currentEntries.get(i).getValue());
    }
  }

  @EventHandler
  private void onInventoryClick(InventoryClickEvent e) {
    if (!e.getInventory().equals(gui))
      return;

    e.setCancelled(true);
    Player player = (Player) e.getWhoClicked();
    int slot = e.getRawSlot();

    if (slot == POS_SWITCH_PAGE_BUTTON) {
      handlePageSwitch(player, e.isRightClick());
    } else if (slot >= 0 && slot < pageSize) {
      handleItemClick(player, slot);
    } else {
      player.playSound(player, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
    }
  }

  private void handlePageSwitch(Player player, boolean isNextPage) {
    if (isNextPage && currentPage < maxPage) {
      currentPage++;
    } else if (!isNextPage && currentPage > 1) {
      currentPage--;
    } else {
      player.playSound(player, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
      return;
    }
    updatePage();
    player.playSound(player, Sound.ITEM_BOOK_PAGE_TURN, 0.5F, 1);
  }

  private void handleItemClick(Player player, int slot) {
    int entryIndex = (currentPage - 1) * pageSize + slot;
    List<Map.Entry<String, ItemStack>> entries = new ArrayList<>(pageEntries.entrySet());
    if (entryIndex < entries.size()) {
      onItemClick.accept(player, entries.get(entryIndex).getKey());
    }
  }
}
