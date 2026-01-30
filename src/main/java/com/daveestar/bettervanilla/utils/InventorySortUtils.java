package com.daveestar.bettervanilla.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class InventorySortUtils {
  private static final Comparator<ItemStack> ITEM_COMPARATOR = Comparator
      .comparing((ItemStack item) -> item.getType().name())
      .thenComparing(item -> item.hasItemMeta() ? item.getItemMeta().toString() : "")
      .thenComparingInt(ItemStack::getAmount);

  private InventorySortUtils() {
  }

  public static void sortInventory(Inventory inv) {
    List<ItemStack> items = collectStackedItems(Arrays.asList(inv.getContents()));
    items.sort(ITEM_COMPARATOR);
    inv.clear();

    int slot = 0;
    for (ItemStack stack : items) {
      if (slot >= inv.getSize()) {
        break;
      }

      inv.setItem(slot++, stack);
    }
  }

  public static ItemStack[] sortStorageContents(ItemStack[] contents, int startIndex, int endIndexExclusive) {
    if (contents == null || contents.length == 0) {
      return contents;
    }

    ItemStack[] result = Arrays.copyOf(contents, contents.length);
    int start = Math.max(0, startIndex);
    int end = Math.min(endIndexExclusive, result.length);

    if (start >= end) {
      return result;
    }

    List<ItemStack> items = new ArrayList<>();
    for (int i = start; i < end; i++) {
      ItemStack item = result[i];
      if (item == null || item.getType() == Material.AIR) {
        continue;
      }

      items.add(item.clone());
    }

    List<ItemStack> stacked = collectStackedItems(items);
    stacked.sort(ITEM_COMPARATOR);

    for (int i = start; i < end; i++) {
      result[i] = null;
    }

    int slot = start;
    for (ItemStack stack : stacked) {
      if (slot >= end) {
        break;
      }

      result[slot++] = stack;
    }

    return result;
  }

  private static List<ItemStack> collectStackedItems(List<ItemStack> items) {
    List<ItemStack> stacked = new ArrayList<>();

    for (ItemStack item : items) {
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
