package com.daveestar.bettervanilla.events;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.EntityType;
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
import com.daveestar.bettervanilla.manager.DeathPointsManager;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.manager.DeathPointsManager.DeathPointReference;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class DeathChest implements Listener {

  private final HashMap<Player, Location> openedDeathChests = new HashMap<>();

  private final Main _plugin;
  private final DeathPointsManager _deathPointsManager;
  private final SettingsManager _settingsManager;

  public DeathChest() {
    _plugin = Main.getInstance();
    _deathPointsManager = _plugin.getDeathPointsManager();
    _settingsManager = _plugin.getSettingsManager();
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent e) {
    Player p = (Player) e.getEntity();

    Location blockLoc = p.getLocation().toBlockLocation();
    Boolean isEnd = blockLoc.getWorld().getEnvironment() == Environment.THE_END;
    Boolean fellIntoVoid = isEnd && blockLoc.getY() < 1;

    Location deathChestLocation = blockLoc.clone().add(0, 0.5, 0);
    if (fellIntoVoid) {
      deathChestLocation.setY(100.5);
    }

    _deathPointsManager.addDeathPoint(p, deathChestLocation);
    e.getDrops().clear();

    int chestX = deathChestLocation.getBlockX();
    int chestY = deathChestLocation.getBlockY();
    int chestZ = deathChestLocation.getBlockZ();

    p.sendMessage(Main.getPrefix() + "You died. All your items are stored in the death chest on: "
        + ChatColor.YELLOW + "X: " + ChatColor.GRAY + chestX
        + ChatColor.YELLOW + " Y: " + ChatColor.GRAY + chestY
        + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + chestZ);
    p.sendMessage(Main.getPrefix() + ChatColor.RED + "ATTENTION!" + ChatColor.GRAY
        + " As soon as you close or break the chest all items will be dropped!");

    if (fellIntoVoid) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Hint:" + ChatColor.GRAY
          + " You fell into the void! Your deathchest will spawn at "
          + ChatColor.YELLOW + "Y: " + ChatColor.GRAY + "100");
    }

    p.sendMessage(Main.getPrefix() + "If you want to list your deathpoints please use: "
        + ChatColor.YELLOW + "/deathpoints");
  }

  @EventHandler
  public void onOpenDeathChest(PlayerInteractEvent e) {
    if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null) {
      if (e.getClickedBlock().getType() == Material.CHEST) {
        Player p = e.getPlayer();

        Location clickedLoc = e.getClickedBlock().getLocation().toBlockLocation();
        DeathPointReference ref = _deathPointsManager.getDeathPointAtLocation(clickedLoc);

        if (ref != null) {
          e.setCancelled(true);

          String playerName = Bukkit.getOfflinePlayer(UUID.fromString(ref.ownerUUID)).getName();
          ItemStack[] items = _deathPointsManager.getDeathPointItems(ref.ownerUUID, ref.pointUUID);
          Inventory inv = Bukkit.createInventory(null, 45,
              Component.text(ChatColor.YELLOW + "" + ChatColor.BOLD + "» Death Chest: " + playerName));

          inv.setContents(items);
          p.openInventory(inv);

          openedDeathChests.put(p, clickedLoc);
        }
      }
    }
  }

  private void removeAndDropDeathChestItems(Player p, Location dropLocation, String ownerUUID, String pointUUID,
      ItemStack[] items) {
    for (ItemStack item : items) {
      if (item != null) {
        p.getWorld().dropItem(dropLocation, item);
      }
    }

    _deathPointsManager.removeDeathPoint(ownerUUID, pointUUID);

    String playerUUID = p.getUniqueId().toString();
    if (playerUUID.equals(ownerUUID)) {
      p.sendMessage(Main.getPrefix() + "You've claimed your deathchest.");
    } else {
      Player ownerPlayer = (Player) Bukkit.getOfflinePlayer(UUID.fromString(ownerUUID));
      p.sendMessage(Main.getPrefix() + "You've claimed the deathchest of " + ChatColor.YELLOW
          + ownerPlayer.getName());

      if (ownerPlayer.isOnline()) {
        ownerPlayer.sendMessage(Main.getPrefix() + "Your deathchest has been claimed by " + ChatColor.YELLOW
            + p.getName());
      }
    }
  }

  @EventHandler
  public void onDeathChestClose(InventoryCloseEvent e) {
    Player p = (Player) e.getPlayer();

    if (openedDeathChests.containsKey(p)) {
      Location chestLoc = openedDeathChests.get(p);
      DeathPointReference ref = _deathPointsManager.getDeathPointAtLocation(chestLoc);

      if (ref != null) {
        Location playerLoc = p.getLocation().toBlockLocation();
        removeAndDropDeathChestItems(p, playerLoc, ref.ownerUUID, ref.pointUUID, e.getInventory().getContents());
      }
    }
  }

  @EventHandler
  public void onDeathChestBreak(BlockBreakEvent e) {
    if (e.getBlock().getType() == Material.CHEST) {
      Player p = e.getPlayer();

      Location breakLoc = e.getBlock().getLocation().toBlockLocation();
      DeathPointReference ref = _deathPointsManager.getDeathPointAtLocation(breakLoc);

      if (ref != null) {
        Location playerLoc = p.getLocation().toBlockLocation();
        ItemStack[] items = _deathPointsManager.getDeathPointItems(ref.ownerUUID, ref.pointUUID);

        removeAndDropDeathChestItems(p, playerLoc, ref.ownerUUID, ref.pointUUID, items);
      }
    }
  }

  @EventHandler
  public void onEntityExplode(EntityExplodeEvent e) {
    e.blockList().removeIf(block -> _deathPointsManager.isDeathPointBlock(block));

    if (!_settingsManager.getToggleCreeperDamage()) {
      if (e.getEntity() != null && e.getEntity().getType() == EntityType.CREEPER) {
        e.blockList().clear();
      }
    }
  }

  @EventHandler
  public void onBlockExplode(BlockExplodeEvent e) {
    e.blockList().removeIf(block -> _deathPointsManager.isDeathPointBlock(block));
  }
}
