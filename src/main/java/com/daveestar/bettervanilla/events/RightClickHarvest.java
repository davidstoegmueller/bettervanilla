package com.daveestar.bettervanilla.events;

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class RightClickHarvest implements Listener {

  @EventHandler
  public void onCropRightClick(PlayerInteractEvent e) {
    if (e.getHand() != EquipmentSlot.HAND) {
      return;
    }

    if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    Block block = e.getClickedBlock();
    if (block == null) {
      return;
    }

    if (!(block.getBlockData() instanceof Ageable)) {
      return;
    }

    Ageable ageable = (Ageable) block.getBlockData();
    if (ageable.getAge() < ageable.getMaximumAge()) {
      return;
    }

    Material seed = switch (block.getType()) {
      case WHEAT -> Material.WHEAT_SEEDS;
      case CARROTS -> Material.CARROT;
      case POTATOES -> Material.POTATO;
      case BEETROOTS -> Material.BEETROOT_SEEDS;
      case NETHER_WART -> Material.NETHER_WART;
      case COCOA -> Material.COCOA_BEANS;
      default -> null;
    };

    if (seed == null) {
      return;
    }

    e.setCancelled(true);
    Player p = e.getPlayer();

    Collection<ItemStack> drops = block.getDrops(p.getInventory().getItemInMainHand(), p);
    boolean seedConsumed = false;

    for (ItemStack drop : drops) {
      if (!seedConsumed && drop.getType() == seed) {
        if (drop.getAmount() > 1) {
          drop.setAmount(drop.getAmount() - 1);
        } else {
          continue;
        }

        seedConsumed = true;
      }

      block.getWorld().dropItemNaturally(block.getLocation(), drop);
    }

    if (!seedConsumed) {
      ItemStack seedItem = new ItemStack(seed, 1);

      if (p.getInventory().containsAtLeast(seedItem, 1)) {
        p.getInventory().removeItem(seedItem);
        seedConsumed = true;
      } else {
        block.breakNaturally(p.getInventory().getItemInMainHand());
        return;
      }
    }

    ageable.setAge(0);
    block.setBlockData(ageable);
  }
}
