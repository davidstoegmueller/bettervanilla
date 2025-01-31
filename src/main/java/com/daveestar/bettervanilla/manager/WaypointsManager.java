package com.daveestar.bettervanilla.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.daveestar.bettervanilla.utils.Config;

public class WaypointsManager {
  private Config _config;
  private FileConfiguration _fileCfgn;

  public WaypointsManager(Config config) {
    this._config = config;
    this._fileCfgn = config.getFileCfgrn();
  }

  public List<String> getWaypoints(String worldName) {
    ConfigurationSection waypointsCfgnSection = _fileCfgn.getConfigurationSection(worldName);
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
    ConfigurationSection waypoint = _fileCfgn.getConfigurationSection(worldName + "." + waypointName);

    HashMap<String, Integer> coordinates = new HashMap<String, Integer>();

    coordinates.put("x", waypoint.getInt("x", 0));
    coordinates.put("y", waypoint.getInt("y", 0));
    coordinates.put("z", waypoint.getInt("z", 0));

    return coordinates;
  }

  public Boolean checkWaypointExists(String worldName, String waypointName) {
    return _fileCfgn.contains(worldName + "." + waypointName);
  }

  public void addWaypoint(String worldName, String waypointName, Integer x, Integer y, Integer z) {
    _fileCfgn.set(worldName + "." + waypointName + ".x", x);
    _fileCfgn.set(worldName + "." + waypointName + ".y", y);
    _fileCfgn.set(worldName + "." + waypointName + ".z", z);

    _config.save();
  }

  public void removeWaypoint(String worldName, String waypointName) {
    _fileCfgn.set(worldName + "." + waypointName, null);

    _config.save();
  }

  public void renameWaypoint(String worldName, String oldName, String newName) {
    ConfigurationSection waypoint = _fileCfgn.getConfigurationSection(worldName + "." + oldName);
    if (waypoint != null) {
      _fileCfgn.set(worldName + "." + newName + ".x", waypoint.getInt("x"));
      _fileCfgn.set(worldName + "." + newName + ".y", waypoint.getInt("y"));
      _fileCfgn.set(worldName + "." + newName + ".z", waypoint.getInt("z"));
      _fileCfgn.set(worldName + "." + oldName, null);
      _config.save();
    }
  }
}
