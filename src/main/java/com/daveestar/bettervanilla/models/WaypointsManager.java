package com.daveestar.bettervanilla.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.utils.Config;
import com.daveestar.bettervanilla.utils.LocationStorage;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class WaypointsManager {
  private Config _config;
  private FileConfiguration _fileCfgn;

  private HashMap<Player, LocationStorage> _activeWaypointNavigations;
  private HashMap<Player, Location> _activeToggleLocationNavigations;

  private BukkitScheduler _navigationScheduler;
  private HashMap<Player, BukkitTask> _navigationTasks;

  private HashMap<Player, ParticleBeam> _waypointBeams;

  public WaypointsManager(Config config) {
    this._config = config;
    this._fileCfgn = config.getFileCfgrn();

    this._activeWaypointNavigations = new HashMap<Player, LocationStorage>();
    this._activeToggleLocationNavigations = new HashMap<Player, Location>();
    this._navigationScheduler = Bukkit.getScheduler();
    this._navigationTasks = new HashMap<Player, BukkitTask>();
    this._waypointBeams = new HashMap<Player, ParticleBeam>();
  }

  // waypoints list helper
  public List<String> getAllWaypointNames(String worldName) {
    ConfigurationSection waypointsCfgnSection = _fileCfgn.getConfigurationSection(worldName);
    Set<String> allWaypointNames = null;

    if (waypointsCfgnSection != null) {
      allWaypointNames = waypointsCfgnSection.getKeys(false);
    }

    if (allWaypointNames == null) {
      return new ArrayList<>(); // return empty list if no waypoints are found
    }

    // Convert to list and sort alphabetically
    List<String> sortedWaypointNames = new ArrayList<>(allWaypointNames);
    Collections.sort(sortedWaypointNames);

    return sortedWaypointNames;
  }

  public HashMap<String, Integer> getSpecificWaypoint(String worldName, String waypointName) {
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

  public void setWaypoint(String worldName, String waypointName, Integer x, Integer y, Integer z) {
    _fileCfgn.set(worldName + "." + waypointName + ".x", x);
    _fileCfgn.set(worldName + "." + waypointName + ".y", y);
    _fileCfgn.set(worldName + "." + waypointName + ".z", z);

    _config.save();
  }

  public void removeWaypoint(String worldName, String waypointName) {
    _fileCfgn.set(worldName + "." + waypointName, null);

    _config.save();
  }

  // waypoint navigation helper
  public Boolean checkPlayerActiveWaypointNavigation(Player p) {
    return _activeWaypointNavigations.containsKey(p);
  }

  public void removePlayerActiveWaypointNavigation(Player p) {
    _activeWaypointNavigations.remove(p);
    cancelTask(p);

    // hide the particle beam and remove reference
    if (_waypointBeams.containsKey(p)) {
      ParticleBeam beam = _waypointBeams.get(p);
      beam.removeBeam();
      _waypointBeams.remove(p);
    }
  }

  public void addPlayerActiveWaypointNavigation(Player p, Location location, String locationName, Color waypointColor) {
    _activeWaypointNavigations.put(p, new LocationStorage(location, locationName));

    // display the particle beam
    ParticleBeam beam = new ParticleBeam(p, location, waypointColor);
    beam.displayBeam();

    _waypointBeams.put(p, beam);
  }

  public LocationStorage getPlayerActiveWaypointNavigation(Player p) {
    return _activeWaypointNavigations.get(p);
  }

  // togglelocation navigation helper
  public Boolean checkPlayerActiveToggleLocationNavigation(Player p) {
    return _activeToggleLocationNavigations.containsKey(p);
  }

  public void removePlayerActiveToggleLocationNavigation(Player p) {
    _activeToggleLocationNavigations.remove(p);
    cancelTask(p);
  }

  public void addPlayerActiveToggleLocationNavigation(Player p, Location location) {
    _activeToggleLocationNavigations.put(p, location);
  }

  public Location getPlayerActiveToggleLocationNavigation(Player p) {
    return _activeToggleLocationNavigations.get(p);
  }

  // display action bar helper
  public void displayActionBar(Player p, String displayText) {
    cancelTask(p);

    BukkitTask task = _navigationScheduler.runTaskTimerAsynchronously(Main.getInstance(), new Runnable() {
      public void run() {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(displayText));

      }
    }, 0, 3 * 10);

    _navigationTasks.put(p, task);
  }

  // scheduler task helper
  public void cancelTask(Player p) {
    if (_navigationTasks.containsKey(p)) {
      _navigationTasks.get(p).cancel();
      _navigationTasks.remove(p);
    }
  }
}
