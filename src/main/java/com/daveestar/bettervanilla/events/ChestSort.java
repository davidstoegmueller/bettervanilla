package com.daveestar.bettervanilla.events;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.daveestar.bettervanilla.Main;

public class ChestSort implements Listener {
  private final Main _plugin;

  public ChestSort() {
    _plugin = Main.getInstance();
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    // Only handle shift right click outside the inventory
    if (!e.isShiftClick() || !e.isRightClick()) {
      return;
    }

    if (e.getRawSlot() != -999) {
      return;
    }

    Inventory topInv = e.getView().getTopInventory();
    if (topInv == null || topInv.getType() != InventoryType.CHEST) {
      return;
    }

    e.setCancelled(true);
    _sortInventory(topInv);
  }

  private void _sortInventory(Inventory inv) {
    Map<Material, Integer> itemCount = new HashMap<>();
    for (ItemStack item : inv.getContents()) {
      if (item == null || item.getType() == Material.AIR) {
        continue;
      }
      itemCount.merge(item.getType(), item.getAmount(), Integer::sum);
    }

    List<Material> materials = new ArrayList<>(itemCount.keySet());
    materials.sort(Comparator.comparing(Enum::name));

    inv.clear();
    int slot = 0;
    for (Material mat : materials) {
      int amount = itemCount.get(mat);
      int max = mat.getMaxStackSize();

      while (amount > 0 && slot < inv.getSize()) {
        int toSet = Math.min(max, amount);
        inv.setItem(slot++, new ItemStack(mat, toSet));
        amount -= toSet;
      }
    }
  }
}
