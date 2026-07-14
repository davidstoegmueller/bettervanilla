package com.daveestar.bettervanilla.events;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.DeathPointsManager;
import com.daveestar.bettervanilla.manager.DeathPointsManager.DeathPointReference;
import com.daveestar.bettervanilla.manager.BackpackManager;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.Theme;

import net.kyori.adventure.text.Component;

public class DeathChest implements Listener {

  private static final int DEATH_CHEST_SEARCH_RADIUS = 16;
  private static final int[][] SEARCH_DIRECTIONS = {
      { 0, 1, 0 },
      { 1, 0, 0 },
      { -1, 0, 0 },
      { 0, 0, 1 },
      { 0, 0, -1 },
      { 0, -1, 0 }
  };

  private final HashMap<Player, Location> openedDeathChests = new HashMap<>();
  private final HashMap<Inventory, String> deathChestInventories = new HashMap<>();

  private final Main _plugin;
  private final DeathPointsManager _deathPointsManager;
  private final SettingsManager _settingsManager;
  private final BackpackManager _backpackManager;

  public DeathChest() {
    _plugin = Main.getInstance();
    _deathPointsManager = _plugin.getDeathPointsManager();
    _settingsManager = _plugin.getSettingsManager();
    _backpackManager = _plugin.getBackpackManager();
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent e) {
    Player p = (Player) e.getEntity();

    _backpackManager.closeBackpackOnDeath(p);

    Location blockLoc = p.getLocation().toBlockLocation();
    Boolean isEnd = blockLoc.getWorld().getEnvironment() == Environment.THE_END;
    Boolean fellIntoVoid = isEnd && blockLoc.getY() < 1;

    Location deathChestLocation = blockLoc.clone().add(0, 0.5, 0);
    if (fellIntoVoid) {
      deathChestLocation.setY(100.5);
    }

    boolean deathChestEnabled = _settingsManager.getDeathChestEnabled();
    boolean deathChestSpawned = false;

    if (deathChestEnabled) {
      Location validatedLocation = _findNearestDeathChestLocation(deathChestLocation);
      if (validatedLocation != null) {
        deathChestLocation = validatedLocation;
        deathChestSpawned = true;
      }
    }

    _deathPointsManager.addDeathPoint(p, deathChestLocation, deathChestSpawned);

    if (deathChestSpawned) {
      e.getDrops().clear();
    }

    int chestX = deathChestLocation.getBlockX();
    int chestY = deathChestLocation.getBlockY();
    int chestZ = deathChestLocation.getBlockZ();

    p.sendMessage(Main.getPrefix() + Main.tr(p, "event-death-location",
        "x", chestX, "y", chestY, "z", chestZ));

    if (deathChestSpawned) {
      p.sendMessage(Main.getPrefix() + Main.tr(p, "event-death-chest-items-stored"));
      p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "event-death-chest-drop-warning"));
    } else if (deathChestEnabled) {
      p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "event-death-chest-no-safe-location"));
    } else {
      p.sendMessage(Main.getPrefix() + Main.tr(p, "event-death-items-dropped"));
    }

    if (fellIntoVoid) {
      if (deathChestSpawned) {
        p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "event-death-void-chest-relocated",
            "y", chestY));
      } else if (!deathChestEnabled) {
        p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "event-death-void-chest-disabled"));
      } else {
        p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "event-death-void-items-lost"));
      }
    }

    p.sendMessage(Main.getPrefix() + Main.tr(p, "event-deathpoints-list-hint",
        "command", Theme.highlight() + "/deathpoints" + Theme.primary()));
  }

  private Location _findNearestDeathChestLocation(Location origin) {
    PriorityQueue<SearchOffset> candidates = new PriorityQueue<>();
    Set<SearchOffset> visited = new HashSet<>();
    SearchOffset start = new SearchOffset(0, 0, 0);

    candidates.add(start);
    visited.add(start);

    while (!candidates.isEmpty()) {
      SearchOffset offset = candidates.poll();
      Location candidate = origin.clone().add(offset.x(), offset.y(), offset.z());

      if (_isValidDeathChestLocation(candidate)) {
        return candidate;
      }

      for (int[] direction : SEARCH_DIRECTIONS) {
        SearchOffset next = new SearchOffset(
            offset.x() + direction[0],
            offset.y() + direction[1],
            offset.z() + direction[2]);

        if (next.isWithin(DEATH_CHEST_SEARCH_RADIUS) && visited.add(next)) {
          candidates.add(next);
        }
      }
    }

    return null;
  }

  private boolean _isValidDeathChestLocation(Location location) {
    int blockY = location.getBlockY();
    if (blockY < location.getWorld().getMinHeight() || blockY >= location.getWorld().getMaxHeight()) {
      return false;
    }

    Material material = location.getBlock().getType();
    boolean replaceable = material.isAir() || location.getBlock().isLiquid();
    return replaceable
        && _deathPointsManager.getDeathPointAtLocation(location.toBlockLocation()) == null;
  }

  private record SearchOffset(int x, int y, int z) implements Comparable<SearchOffset> {
    private int distanceSquared() {
      return x * x + y * y + z * z;
    }

    private boolean isWithin(int radius) {
      return Math.abs(x) <= radius && Math.abs(y) <= radius && Math.abs(z) <= radius;
    }

    @Override
    public int compareTo(SearchOffset other) {
      int distanceComparison = Integer.compare(distanceSquared(), other.distanceSquared());
      if (distanceComparison != 0) {
        return distanceComparison;
      }

      int yComparison = Integer.compare(other.y, y);
      if (yComparison != 0) {
        return yComparison;
      }

      int xComparison = Integer.compare(x, other.x);
      return xComparison != 0 ? xComparison : Integer.compare(z, other.z);
    }
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
          if (playerName == null) {
            playerName = Main.tr(p, "event-death-chest-owner-unknown");
          }
          ItemStack[] items = _deathPointsManager.getDeathPointItems(ref.ownerUUID, ref.pointUUID);
          Inventory inv = Bukkit.createInventory(null, 45,
              Component.text(Theme.titlePrefix() + Main.tr(p, "event-death-chest-inventory-title",
                  "player", playerName)));

          inv.setContents(items);
          p.openInventory(inv);

          openedDeathChests.put(p, clickedLoc);
          deathChestInventories.put(inv, ref.ownerUUID);
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

    p.sendMessage(Main.getPrefix() + Main.tr(p, "event-death-chest-claimed"));

  }

  @EventHandler
  public void onDeathChestClick(InventoryClickEvent e) {
    Inventory topInv = e.getView().getTopInventory();

    if (!deathChestInventories.containsKey(topInv))
      return;

    String ownerUUID = deathChestInventories.get(topInv);
    Player p = (Player) e.getWhoClicked();

    if (!p.getUniqueId().toString().equals(ownerUUID)) {
      e.setCancelled(true);
      return;
    }
  }

  @EventHandler
  public void onDeathChestClose(InventoryCloseEvent e) {
    Player p = (Player) e.getPlayer();

    if (openedDeathChests.containsKey(p)) {
      Location chestLoc = openedDeathChests.get(p);
      DeathPointReference ref = _deathPointsManager.getDeathPointAtLocation(chestLoc);

      if (ref != null) {
        if (p.getUniqueId().toString().equals(ref.ownerUUID)) {
          Location playerLoc = p.getLocation().toBlockLocation();
          removeAndDropDeathChestItems(p, playerLoc, ref.ownerUUID, ref.pointUUID, e.getInventory().getContents());
        }
      }

      openedDeathChests.remove(p);
    }

    deathChestInventories.remove(e.getInventory());
  }

  @EventHandler
  public void onDeathChestBreak(BlockBreakEvent e) {
    if (e.getBlock().getType() == Material.CHEST) {
      Player p = e.getPlayer();

      Location breakLoc = e.getBlock().getLocation().toBlockLocation();
      DeathPointReference ref = _deathPointsManager.getDeathPointAtLocation(breakLoc);

      if (ref != null) {
        if (p.getUniqueId().toString().equals(ref.ownerUUID)) {
          Location playerLoc = p.getLocation().toBlockLocation();
          ItemStack[] items = _deathPointsManager.getDeathPointItems(ref.ownerUUID, ref.pointUUID);

          removeAndDropDeathChestItems(p, playerLoc, ref.ownerUUID, ref.pointUUID, items);
        } else {
          e.setCancelled(true);
          p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "event-death-chest-break-denied"));
        }
      }
    }
  }

  @EventHandler
  public void onEntityExplode(EntityExplodeEvent e) {
    e.blockList().removeIf(block -> _deathPointsManager.isDeathPointBlock(block));

  }

  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
    _protectDeathChestArmorStand(e);
  }

  @EventHandler
  public void onDeathChestExplosionDamage(EntityDamageEvent e) {
    if (e.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
        && e.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
      return;
    }

    _protectDeathChestArmorStand(e);
  }

  @EventHandler
  public void onBlockExplode(BlockExplodeEvent e) {
    e.blockList().removeIf(block -> _deathPointsManager.isDeathPointBlock(block));
  }

  private boolean _protectDeathChestArmorStand(EntityDamageEvent e) {
    if (!_isDeathChestArmorStand(e.getEntity())) {
      return false;
    }

    e.setCancelled(true);
    return true;
  }

  private boolean _isDeathChestArmorStand(Entity entity) {
    if (!(entity instanceof ArmorStand stand) || !stand.isMarker()) {
      return false;
    }

    Location loc = stand.getLocation().toBlockLocation();

    if (loc.getWorld() == null) {
      return false;
    }

    return _deathPointsManager.isDeathPointBlock(loc.getBlock());
  }
}
