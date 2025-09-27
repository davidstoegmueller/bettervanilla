package com.daveestar.bettervanilla.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import com.daveestar.bettervanilla.enums.WaypointVisibility;
import com.daveestar.bettervanilla.utils.Config;

public class WaypointsManager {
  private final Config _config;
  private final FileConfiguration _fileConfig;

  public WaypointsManager(Config config) {
    _config = config;
    _fileConfig = config.getFileConfig();
  }

  public List<String> getWaypoints(String worldName) {
    ConfigurationSection waypointsConfigSection = _fileConfig.getConfigurationSection(worldName);
    Set<String> allWaypointNames = waypointsConfigSection != null ? waypointsConfigSection.getKeys(false) : null;

    if (allWaypointNames == null) {
      return new ArrayList<>();
    }

    List<String> sortedWaypointNames = new ArrayList<>(allWaypointNames);
    Collections.sort(sortedWaypointNames);

    return sortedWaypointNames;
  }

  public HashMap<String, Integer> getWaypointCoordinates(String worldName, String waypointName) {
    String basePath = worldName + "." + waypointName;
    ConfigurationSection waypoint = _fileConfig.getConfigurationSection(basePath);

    HashMap<String, Integer> coordinates = new HashMap<>();

    coordinates.put("x", waypoint != null ? waypoint.getInt("x", 0) : 0);
    coordinates.put("y", waypoint != null ? waypoint.getInt("y", 0) : 0);
    coordinates.put("z", waypoint != null ? waypoint.getInt("z", 0) : 0);

    return coordinates;
  }

  public String getWaypointOwnerName(String worldName, String waypointName) {
    String path = worldName + "." + waypointName + ".owner";

    if (_fileConfig.contains(path)) {
      String ownerString = _fileConfig.getString(path);

      if (ownerString != null && !ownerString.isEmpty()) {
        try {
          UUID ownerUUID = UUID.fromString(ownerString);
          OfflinePlayer p = Bukkit.getOfflinePlayer(ownerUUID);

          return p.getName();
        } catch (IllegalArgumentException ex) {
          return "unknown";
        }
      }
    }

    return "unknown";
  }

  public Optional<UUID> getWaypointOwnerId(String worldName, String waypointName) {
    String path = worldName + "." + waypointName + ".owner";

    if (_fileConfig.contains(path)) {
      String ownerString = _fileConfig.getString(path);

      if (ownerString != null && !ownerString.isEmpty()) {
        try {
          return Optional.of(UUID.fromString(ownerString));
        } catch (IllegalArgumentException ex) {
          return Optional.empty();
        }
      }
    }

    return Optional.empty();
  }

  public WaypointVisibility getWaypointVisibility(String worldName, String waypointName) {
    String path = worldName + "." + waypointName + ".visibility";

    if (_fileConfig.contains(path)) {
      String visibilityString = _fileConfig.getString(path);

      if (visibilityString != null && !visibilityString.isEmpty()) {
        return WaypointVisibility.fromString(visibilityString).orElse(WaypointVisibility.PUBLIC);
      }
    }

    return WaypointVisibility.PUBLIC;
  }

  public ItemStack getWaypointIcon(String worldName, String waypointName) {
    String path = worldName + "." + waypointName + ".icon";

    if (_fileConfig.contains(path)) {
      Object raw = _fileConfig.get(path);
      Map<String, Object> map = null;

      if (raw instanceof Map) {
        @SuppressWarnings("unchecked")
        Map<String, Object> temp = (Map<String, Object>) raw;
        map = temp;
      } else if (raw instanceof ConfigurationSection) {
        map = ((ConfigurationSection) raw).getValues(false);
      }

      if (map != null) {
        return ItemStack.deserialize(map);
      }
    }

    return new ItemStack(Material.PAPER);
  }

  public void addWaypoint(String worldName, String waypointName, UUID playerId, WaypointVisibility visibility,
      int x, int y, int z) {
    String basePath = worldName + "." + waypointName;

    _fileConfig.set(basePath + ".x", x);
    _fileConfig.set(basePath + ".y", y);
    _fileConfig.set(basePath + ".z", z);
    _fileConfig.set(basePath + ".owner", playerId.toString());
    _fileConfig.set(basePath + ".visibility", visibility.name());

    _config.save();
  }

  public void removeWaypoint(String worldName, String waypointName) {
    _fileConfig.set(worldName + "." + waypointName, null);

    _config.save();
  }

  public void renameWaypoint(String worldName, String oldName, String newName) {
    String oldPath = worldName + "." + oldName;
    String newPath = worldName + "." + newName;
    ConfigurationSection waypoint = _fileConfig.getConfigurationSection(oldPath);
    if (waypoint != null) {
      _fileConfig.set(newPath + ".x", waypoint.getInt("x"));
      _fileConfig.set(newPath + ".y", waypoint.getInt("y"));
      _fileConfig.set(newPath + ".z", waypoint.getInt("z"));

      if (_fileConfig.contains(oldPath + ".icon")) {
        Object iconData = _fileConfig.get(oldPath + ".icon");
        _fileConfig.set(newPath + ".icon", iconData);
      }

      if (_fileConfig.contains(oldPath + ".owner")) {
        _fileConfig.set(newPath + ".owner", _fileConfig.getString(oldPath + ".owner"));
      }

      if (_fileConfig.contains(oldPath + ".visibility")) {
        _fileConfig.set(newPath + ".visibility", _fileConfig.getString(oldPath + ".visibility"));
      }

      _fileConfig.set(oldPath, null);
      _config.save();
    }
  }

  public void setWaypointVisibility(String worldName, String waypointName, WaypointVisibility visibility) {
    _fileConfig.set(worldName + "." + waypointName + ".visibility", visibility.name());
    _config.save();
  }

  public void setWaypointIcon(String worldName, String waypointName, ItemStack iconItem) {
    Map<String, Object> serializedIcon = iconItem.serialize();
    _fileConfig.set(worldName + "." + waypointName + ".icon", serializedIcon);
    _config.save();
  }

  public Boolean checkWaypointExists(String worldName, String waypointName) {
    return _fileConfig.contains(worldName + "." + waypointName);
  }
}
