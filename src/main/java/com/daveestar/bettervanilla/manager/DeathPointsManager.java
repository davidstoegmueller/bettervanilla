package com.daveestar.bettervanilla.manager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.utils.Config;
import com.daveestar.bettervanilla.utils.ItemStackUtils;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.block.Block;

public class DeathPointsManager {
  private final Config _config;
  private final FileConfiguration _fileConfig;

  public DeathPointsManager(Config config) {
    _config = config;
    _fileConfig = config.getFileConfig();
  }

  public void addDeathPoint(Player p, Location loc) {
    String playerName = p.getName();
    String playerUUID = p.getUniqueId().toString();
    String pointUUID = UUID.randomUUID().toString();

    String deathPointPath = playerUUID + ".deathpoints." + pointUUID;

    String world = loc.getWorld().getName();
    int x = loc.getBlockX();
    int y = loc.getBlockY();
    int z = loc.getBlockZ();

    _fileConfig.set(deathPointPath + ".world", world);
    _fileConfig.set(deathPointPath + ".x", x);
    _fileConfig.set(deathPointPath + ".y", y);
    _fileConfig.set(deathPointPath + ".z", z);

    long timestamp = System.currentTimeMillis();
    _fileConfig.set(deathPointPath + ".timestamp", timestamp);

    List<Map<String, Object>> serializedItemStacks = ItemStackUtils.serializeArray(p.getInventory().getContents());

    _fileConfig.set(deathPointPath + ".inventory", serializedItemStacks);
    _config.save();

    _createDeathChest(loc);
    _createDeathHologram(playerName, loc);
  }

  public void removeDeathPoint(String ownerUUID, String pointUUID) {
    Location loc = getDeathPointLocation(ownerUUID, pointUUID);
    if (loc != null) {
      _removeDeathChest(loc);
      _removeDeathHologram(loc);
    }

    String deathPointPath = ownerUUID + ".deathpoints." + pointUUID;
    _fileConfig.set(deathPointPath, null);
    _config.save();
  }

  public String[] getDeathPointUUIDs(Player p) {
    String playerUUID = p.getUniqueId().toString();
    String deathPointsPath = playerUUID + ".deathpoints";
    ConfigurationSection deathPointsSection = _fileConfig.getConfigurationSection(deathPointsPath);

    if (deathPointsSection == null) {
      return new String[0];
    }

    List<String> uuids = new ArrayList<>(deathPointsSection.getKeys(false));
    String prefix = deathPointsPath + ".";

    uuids.sort((a, b) -> {
      long t1 = _fileConfig.getLong(prefix + a + ".timestamp", 0);
      long t2 = _fileConfig.getLong(prefix + b + ".timestamp", 0);
      return Long.compare(t2, t1);
    });

    return uuids.toArray(new String[0]);
  }

  public Location getDeathPointLocation(String ownerUUID, String pointUUID) {
    String deathPointPath = ownerUUID + ".deathpoints." + pointUUID;
    return _readLocation(deathPointPath);
  }

  public String getDeathPointDateTime(Player p, String pointUUID) {
    String playerUUID = p.getUniqueId().toString();
    String deathPointPath = playerUUID + ".deathpoints." + pointUUID;

    long timestamp = _fileConfig.getLong(deathPointPath + ".timestamp", 0);
    SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy » HH:mm:ss");
    return timestamp != 0 ? sdf.format(new Date(timestamp)) : "-";
  }

  public ItemStack[] getDeathPointItems(Player p, String pointUUID) {
    return getDeathPointItems(p.getUniqueId().toString(), pointUUID);
  }

  public ItemStack[] getDeathPointItems(String ownerUUID, String pointUUID) {
    String deathPointPath = ownerUUID + ".deathpoints." + pointUUID;

    List<Map<?, ?>> inventoryList = _fileConfig.getMapList(deathPointPath + ".inventory");
    return ItemStackUtils.deserializeArray(inventoryList);
  }

  public DeathPointReference getDeathPointAtLocation(Location loc) {
    for (String playerUUID : _fileConfig.getKeys(false)) {
      ConfigurationSection deathpointsSection = _fileConfig.getConfigurationSection(playerUUID + ".deathpoints");

      if (deathpointsSection != null) {
        for (String pointUUID : deathpointsSection.getKeys(false)) {
          String path = playerUUID + ".deathpoints." + pointUUID;
          Location pointLoc = _readLocation(path);
          if (loc.getWorld().getName().equals(pointLoc.getWorld().getName())
              && loc.getBlockX() == pointLoc.getBlockX()
              && loc.getBlockY() == pointLoc.getBlockY()
              && loc.getBlockZ() == pointLoc.getBlockZ()) {
            return new DeathPointReference(playerUUID, pointUUID);
          }
        }
      }
    }
    return null;
  }

  public boolean isDeathPointBlock(Block block) {
    return getDeathPointAtLocation(block.getLocation().toBlockLocation()) != null;
  }

  // ------------------
  // HELPER ADD METHODS
  // ------------------

  private void _createDeathChest(Location loc) {
    loc.getBlock().setType(Material.CHEST, false);
  }

  private void _createDeathHologram(String playerName, Location loc) {
    ArmorStand hologram = loc.getWorld().spawn(loc.clone().add(0.5, 0.5, 0.5), ArmorStand.class);
    hologram.setInvisible(true);
    hologram.setGravity(false);
    hologram.setMarker(true);
    hologram.customName(Component.text(ChatColor.YELLOW + "" + ChatColor.BOLD + "» Death Chest: " + playerName));
    hologram.setCustomNameVisible(true);
  }

  // -----------------------
  // HELPER REMOVAL METHODS
  // -----------------------

  private void _removeDeathChest(Location loc) {
    if (loc.getBlock() != null) {
      loc.getBlock().setType(Material.AIR, false);
    }
  }

  private void _removeDeathHologram(Location loc) {
    loc.getWorld().getEntitiesByClass(ArmorStand.class).forEach(stand -> {
      if (stand.isMarker() && stand.getLocation().distance(loc.clone().add(0.5, 0.5, 0.5)) < 1.0) {
        stand.remove();
      }
    });
  }

  // ----------------------
  // GENERAL HELPER METHODS
  // ----------------------

  private Location _readLocation(String path) {
    String world = _fileConfig.getString(path + ".world", "-");
    int x = _fileConfig.getInt(path + ".x", 0);
    int y = _fileConfig.getInt(path + ".y", 0);
    int z = _fileConfig.getInt(path + ".z", 0);
    return new Location(Main.getInstance().getServer().getWorld(world), x, y, z);
  }

  public static class DeathPointReference {
    public final String ownerUUID;
    public final String pointUUID;

    public DeathPointReference(String ownerUUID, String pointUUID) {
      this.ownerUUID = ownerUUID;
      this.pointUUID = pointUUID;
    }
  }
}
