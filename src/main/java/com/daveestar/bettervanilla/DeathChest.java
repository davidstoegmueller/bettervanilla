package com.daveestar.bettervanilla;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DeathChest implements Listener {
  public static HashMap<Block, Inventory> deathChest = new HashMap<Block, Inventory>();

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent e) {
    Player p = (Player) e.getEntity();

    Block blockChest = p.getWorld().getBlockAt(p.getLocation().add(0, 0.5, 0));
    blockChest.setType(Material.CHEST);

    Inventory inv = Bukkit.createInventory(null, 45, "DeathChest from " + p.getName());
    inv.clear();
    inv.setContents(p.getInventory().getContents());

    deathChest.put(blockChest, inv);
    e.getDrops().clear();

    p.sendMessage(
        Main.getPrefix() + "You died. All your items are stored in the death chest on: " + ChatColor.YELLOW
            + "X: " + ChatColor.GRAY
            + blockChest.getLocation().getBlockX() + ChatColor.YELLOW
            + " Y: " + ChatColor.GRAY + blockChest.getLocation().getBlockY() + ChatColor.YELLOW + " Z: "
            + ChatColor.GRAY + blockChest.getLocation().getBlockZ());
    p.sendMessage(Main.getPrefix() + ChatColor.RED + "ATTENTION!" + ChatColor.GRAY
        + " As soon as you close or break the chest all items will be dropped!");

  }

  private Block openedDeathChestBlock;

  @EventHandler
  public void onOpenDeathChest(PlayerInteractEvent e) {
    if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
      if (e.getClickedBlock().getType() == Material.CHEST) {

        Block block = e.getClickedBlock();

        for (Block blocks : deathChest.keySet()) {
          if (blocks.getLocation().equals(block.getLocation())) {
            e.setCancelled(true);
            e.getPlayer().openInventory(deathChest.get(blocks));

            openedDeathChestBlock = blocks;
          }
        }
      }
    }
  }

  @EventHandler
  public void onDeathChestClose(InventoryCloseEvent e) {
    if (e.getView().getTitle().equalsIgnoreCase("DeathChest from " + e.getPlayer().getName())) {
      for (ItemStack item : e.getInventory().getContents()) {
        if (item != null) {
          e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), item);
        }
      }

      deathChest.remove(openedDeathChestBlock);
      openedDeathChestBlock.setType(Material.AIR);
      openedDeathChestBlock = null;
    }
  }

  @EventHandler
  public void onDeathChestBreak(BlockBreakEvent e) {
    if (deathChest.containsKey(e.getBlock())) {
      Inventory inv = deathChest.get(e.getBlock());

      for (ItemStack item : inv.getContents()) {
        if (item != null) {
          e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), item);
        }
      }

      deathChest.remove(e.getBlock());
    }
  }
}
