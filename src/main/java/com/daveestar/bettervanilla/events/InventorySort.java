package com.daveestar.bettervanilla.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.InventorySortMode;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.InventorySortUtils;

public class InventorySort implements Listener {
  private final Main _plugin;
  private final SettingsManager _settingsManager;

  public InventorySort() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    if (!e.isRightClick()) {
      return;
    }

    if (e.getRawSlot() != -999) {
      return;
    }

    if (e.getView() == null || e.getView().getTopInventory() == null) {
      return;
    }

    if (e.getView().getTopInventory().getType() != InventoryType.CRAFTING) {
      return;
    }

    Player p = (Player) e.getWhoClicked();
    if (!_settingsManager.getPlayerInventorySort(p.getUniqueId())) {
      return;
    }

    e.setCancelled(true);

    boolean includeHotbar = _settingsManager.getPlayerInventorySortIncludeHotbar(p.getUniqueId());
    int startIndex = includeHotbar ? 0 : 9;
    int endIndex = 36;

    PlayerInventory inv = p.getInventory();
    ItemStack[] storage = inv.getStorageContents();
    InventorySortMode mode = _settingsManager.getPlayerInventorySortMode(p.getUniqueId());
    ItemStack[] sorted = InventorySortUtils.sortStorageContents(storage, startIndex, endIndex, mode);
    inv.setStorageContents(sorted);
  }
}
