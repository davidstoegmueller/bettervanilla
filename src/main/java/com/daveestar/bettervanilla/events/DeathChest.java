package com.daveestar.bettervanilla.events;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.utils.Config;

public class DeathChest implements Listener {
  public static HashMap<Block, Inventory> deathChest = new HashMap<Block, Inventory>();

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent e) {
    Player p = (Player) e.getEntity();

    Config lastDeaths = new Config("lastDeaths.yml", Main.getInstance().getDataFolder());
    FileConfiguration cfgn = lastDeaths.getFileCfgrn();

    cfgn.set(p.getName() + ".x", p.getLocation().getBlockX());
    cfgn.set(p.getName() + ".y", p.getLocation().getBlockY());
    cfgn.set(p.getName() + ".z", p.getLocation().getBlockZ());
    cfgn.set(p.getName() + ".world", p.getLocation().getWorld().getName());
    lastDeaths.save();

    Location loc = p.getLocation();
    Boolean isEnd = loc.getWorld().getEnvironment() == Environment.THE_END;
    Boolean fellIntoVoid = isEnd && loc.getY() < 1;

    // define the death chests y coordinate
    // if the player is in the end and fell into the void
    // we set the y location to 100.5
    // this makes sure that the deathchest is always accessible by the player
    Location deathChestLocation = loc.add(0, 0.5, 0);
    if (fellIntoVoid) {
      deathChestLocation.setY(100.5);
    }

    Block blockChest = p.getWorld().getBlockAt(deathChestLocation);
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

    // display a hint message to the player if he fell into the void that the
    // chest will spawn above him at y = 100
    if (fellIntoVoid) {
      p.sendMessage(
          Main.getPrefix() + ChatColor.RED + "Hint:" + ChatColor.GRAY
              + " You fell into the void! Your deathchest will spawn at " + ChatColor.YELLOW + "Y: "
              + ChatColor.GRAY + "100");
    }

    p.sendMessage(Main.getPrefix() + "If you want to navigate to you latest deathpoint please use: " + ChatColor.YELLOW
        + "/lastdeath");
  }

  private Block _openedDeathChestBlock;

  @EventHandler
  public void onOpenDeathChest(PlayerInteractEvent e) {
    if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
      if (e.getClickedBlock().getType() == Material.CHEST) {

        Block block = e.getClickedBlock();

        for (Block blocks : deathChest.keySet()) {
          if (blocks.getLocation().equals(block.getLocation())) {
            e.setCancelled(true);
            e.getPlayer().openInventory(deathChest.get(blocks));

            _openedDeathChestBlock = blocks;
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

      deathChest.remove(_openedDeathChestBlock);
      _openedDeathChestBlock.setType(Material.AIR);
      _openedDeathChestBlock = null;

      Config lastDeaths = new Config("lastDeaths.yml", Main.getInstance().getDataFolder());
      FileConfiguration cfgn = lastDeaths.getFileCfgrn();
      cfgn.set(e.getPlayer().getName(), null);
      lastDeaths.save();
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

      Config lastDeaths = new Config("lastDeaths.yml", Main.getInstance().getDataFolder());
      FileConfiguration cfgn = lastDeaths.getFileCfgrn();
      cfgn.set(e.getPlayer().getName(), null);
      lastDeaths.save();
    }
  }

  @EventHandler
  public void onEntityExplode(EntityExplodeEvent e) {
    e.blockList().removeIf(block -> deathChest.containsKey(block));
  }

  @EventHandler
  public void onBlockExplode(BlockExplodeEvent e) {
    e.blockList().removeIf(block -> deathChest.containsKey(block));
  }
}
