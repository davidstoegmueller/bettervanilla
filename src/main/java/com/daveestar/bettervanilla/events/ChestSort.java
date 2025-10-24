package com.daveestar.bettervanilla.events;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SettingsManager;

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
    _sortInventory(topInv);
  }

  private boolean _isSortable(Inventory inv) {
    InventoryType type = inv.getType();

    if (inv.getHolder() == null) {
      return false;
    }

    return type == InventoryType.CHEST || type == InventoryType.BARREL
        || type == InventoryType.SHULKER_BOX || type == InventoryType.ENDER_CHEST;
  }

  private void _sortInventory(Inventory inv) {
    List<ItemStack> items = _collectStackedItems(inv);
    Comparator<ItemStack> comparator = Comparator
        .comparing((ItemStack item) -> item.getType().name())
        .thenComparing(item -> item.hasItemMeta() ? item.getItemMeta().toString() : "")
        .thenComparingInt(ItemStack::getAmount);

    items.sort(comparator);
    inv.clear();

    int slot = 0;
    for (ItemStack stack : items) {
      if (slot >= inv.getSize()) {
        break;
      }

      inv.setItem(slot++, stack);
    }
  }

  private List<ItemStack> _collectStackedItems(Inventory inv) {
    List<ItemStack> stacked = new ArrayList<>();

    for (ItemStack item : inv.getContents()) {
      if (item == null || item.getType() == Material.AIR) {
        continue;
      }

      ItemStack source = item.clone();

      if (source.getMaxStackSize() <= 1) {
        int copies = Math.max(1, source.getAmount());
        source.setAmount(1);

        for (int i = 0; i < copies; i++) {
          stacked.add(source.clone());
        }

        continue;
      }

      int amountToDistribute = source.getAmount();
      int maxStackSize = source.getMaxStackSize();

      for (ItemStack existing : stacked) {
        if (!existing.isSimilar(source)) {
          continue;
        }

        int space = maxStackSize - existing.getAmount();
        if (space <= 0) {
          continue;
        }

        int transfer = Math.min(space, amountToDistribute);
        existing.setAmount(existing.getAmount() + transfer);
        amountToDistribute -= transfer;

        if (amountToDistribute == 0) {
          break;
        }
      }

      while (amountToDistribute > 0) {
        ItemStack newStack = source.clone();
        int stackSize = Math.min(maxStackSize, amountToDistribute);
        newStack.setAmount(stackSize);
        stacked.add(newStack);
        amountToDistribute -= stackSize;
      }
    }

    return stacked;
  }
}
