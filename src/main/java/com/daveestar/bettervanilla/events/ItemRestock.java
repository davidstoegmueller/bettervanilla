package com.daveestar.bettervanilla.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SettingsManager;

public class ItemRestock implements Listener {
  private final Main plugin;
  private final SettingsManager settings;

  public ItemRestock() {
    plugin = Main.getInstance();
    settings = plugin.getSettingsManager();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockPlace(BlockPlaceEvent event) {
    if (event.getHand() != EquipmentSlot.HAND) {
      return;
    }

    ItemStack usedItem = event.getItemInHand();
    if (usedItem == null || usedItem.getType() == Material.AIR) {
      return;
    }

    _scheduleRestock(event.getPlayer(), usedItem.getType());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onItemBreak(PlayerItemBreakEvent event) {
    ItemStack broken = event.getBrokenItem();
    if (broken == null || broken.getType() == Material.AIR) {
      return;
    }

    _scheduleRestock(event.getPlayer(), broken.getType());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onItemConsume(PlayerItemConsumeEvent event) {
    ItemStack consumed = event.getItem();
    if (consumed == null || consumed.getType() == Material.AIR) {
      return;
    }

    _scheduleRestock(event.getPlayer(), consumed.getType());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onInteract(PlayerInteractEvent event) {
    if (event.getHand() != EquipmentSlot.HAND) {
      return;
    }

    ItemStack item = event.getItem();
    if (item == null || item.getType() == Material.AIR) {
      return;
    }

    _scheduleRestock(event.getPlayer(), item.getType());
  }

  private void _scheduleRestock(Player player, Material material) {
    if (material == Material.AIR || !_shouldRestock(player)) {
      return;
    }

    Bukkit.getScheduler().runTask(plugin, () -> _restock(player, material));
  }

  private void _restock(Player player, Material material) {
    PlayerInventory inventory = player.getInventory();
    int hotbarSlot = inventory.getHeldItemSlot();

    if (hotbarSlot < 0 || hotbarSlot > 8) {
      return;
    }

    ItemStack current = inventory.getItem(hotbarSlot);
    if (current != null && current.getType() != Material.AIR) {
      return;
    }

    int sourceSlot = _findMatchingInventorySlot(inventory, material, hotbarSlot);
    if (sourceSlot == -1) {
      return;
    }

    ItemStack sourceStack = inventory.getItem(sourceSlot);
    if (sourceStack == null || sourceStack.getType() != material) {
      return;
    }

    inventory.setItem(hotbarSlot, sourceStack.clone());
    inventory.setItem(sourceSlot, null);
    player.updateInventory();
  }

  private int _findMatchingInventorySlot(PlayerInventory inventory, Material material, int hotbarSlot) {
    // search main inventory (slots 9-35) first
    for (int slot = 9; slot < 36; slot++) {
      ItemStack stack = inventory.getItem(slot);
      if (stack != null && stack.getType() == material) {
        return slot;
      }
    }

    // fall back to other hotbar slots (0-8)
    for (int slot = 0; slot < 9; slot++) {
      if (slot == hotbarSlot) {
        continue;
      }

      ItemStack stack = inventory.getItem(slot);
      if (stack != null && stack.getType() == material) {
        return slot;
      }
    }

    return -1;
  }

  private boolean _shouldRestock(Player player) {
    return settings.getItemRestockEnabled() && settings.getPlayerItemRestock(player.getUniqueId());
  }
}
