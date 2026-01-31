package com.daveestar.bettervanilla.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.InventorySortMode;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.InventorySortUtils;

public class ChestSort implements Listener {
  private final Main _plugin;
  private final SettingsManager _settingsManager;

  public ChestSort() {
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

    Inventory topInv = e.getView().getTopInventory();
    if (topInv == null || !_isSortable(topInv)) {
      return;
    }

    Player p = (Player) e.getWhoClicked();

    if (!_settingsManager.getPlayerChestSort(p.getUniqueId())) {
      return;
    }

    e.setCancelled(true);
    InventorySortMode mode = _settingsManager.getPlayerChestSortMode(p.getUniqueId());
    InventorySortUtils.sortInventory(topInv, mode);
  }

  private boolean _isSortable(Inventory inv) {
    InventoryType type = inv.getType();

    if (inv.getHolder() == null) {
      return false;
    }

    return type == InventoryType.CHEST || type == InventoryType.BARREL
        || type == InventoryType.SHULKER_BOX || type == InventoryType.ENDER_CHEST;
  }
}
