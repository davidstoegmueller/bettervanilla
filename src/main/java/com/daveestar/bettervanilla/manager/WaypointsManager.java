package com.daveestar.bettervanilla.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.daveestar.bettervanilla.utils.Config;

public class WaypointsManager {
  private Config _config;
  private FileConfiguration _fileConfig;

  public WaypointsManager(Config config) {
    _config = config;
    _fileConfig = config.getFileConfig();
  }

  public List<String> getWaypoints(String worldName) {
    ConfigurationSection waypointsCfgnSection = _fileConfig.getConfigurationSection(worldName);
    Set<String> allWaypointNames = null;

    if (waypointsCfgnSection != null) {
      allWaypointNames = waypointsCfgnSection.getKeys(false);
    }

    if (allWaypointNames == null) {
      return new ArrayList<>(); // return empty list if no waypoints are found
    }

    // convert to list and sort alphabetically
    List<String> sortedWaypointNames = new ArrayList<>(allWaypointNames);
    Collections.sort(sortedWaypointNames);

    return sortedWaypointNames;
  }

  public HashMap<String, Integer> getWaypointByName(String worldName, String waypointName) {
    ConfigurationSection waypoint = _fileConfig.getConfigurationSection(worldName + "." + waypointName);

    HashMap<String, Integer> coordinates = new HashMap<>();

    if (waypoint != null) {
      coordinates.put("x", waypoint.getInt("x", 0));
      coordinates.put("y", waypoint.getInt("y", 0));
      coordinates.put("z", waypoint.getInt("z", 0));
    } else {
      coordinates.put("x", 0);
      coordinates.put("y", 0);
      coordinates.put("z", 0);
    }

    return coordinates;
  }

  public Boolean checkWaypointExists(String worldName, String waypointName) {
    return _fileConfig.contains(worldName + "." + waypointName);
  }

  public void addWaypoint(String worldName, String waypointName, Integer x, Integer y, Integer z) {
    _fileConfig.set(worldName + "." + waypointName + ".x", x);
    _fileConfig.set(worldName + "." + waypointName + ".y", y);
    _fileConfig.set(worldName + "." + waypointName + ".z", z);

    _config.save();
  }

  public void removeWaypoint(String worldName, String waypointName) {
    _fileConfig.set(worldName + "." + waypointName, null);

    _config.save();
  }

  public void renameWaypoint(String worldName, String oldName, String newName) {
    ConfigurationSection waypoint = _fileConfig.getConfigurationSection(worldName + "." + oldName);
    if (waypoint != null) {
      _fileConfig.set(worldName + "." + newName + ".x", waypoint.getInt("x"));
      _fileConfig.set(worldName + "." + newName + ".y", waypoint.getInt("y"));
      _fileConfig.set(worldName + "." + newName + ".z", waypoint.getInt("z"));

      if (_fileConfig.contains(worldName + "." + oldName + ".icon")) {
        Object iconData = _fileConfig.get(worldName + "." + oldName + ".icon");
        _fileConfig.set(worldName + "." + newName + ".icon", iconData);
      }

      _fileConfig.set(worldName + "." + oldName, null);
      _config.save();
    }
  }

  public void setWaypointIcon(String worldName, String waypointName, ItemStack iconItem) {
    Map<String, Object> serializedIcon = iconItem.serialize();
    _fileConfig.set(worldName + "." + waypointName + ".icon", serializedIcon);
    _config.save();
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
}
