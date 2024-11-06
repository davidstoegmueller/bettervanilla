package com.daveestar.bettervanilla.models;

import java.util.HashMap;
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
  private Config config;
  private FileConfiguration fileCfgn;

  private HashMap<Player, LocationStorage> activeWaypointNavigations;
  private HashMap<Player, Location> activeToggleLocationNavigations;

  private BukkitScheduler navigationScheduler;
  private HashMap<Player, BukkitTask> navigationTasks;

  private HashMap<Player, ParticleBeam> waypointBeams;

  public WaypointsManager(Config config) {
    this.config = config;
    this.fileCfgn = config.getFileCfgrn();

    this.activeWaypointNavigations = new HashMap<Player, LocationStorage>();
    this.activeToggleLocationNavigations = new HashMap<Player, Location>();
    this.navigationScheduler = Bukkit.getScheduler();
    this.navigationTasks = new HashMap<Player, BukkitTask>();
    this.waypointBeams = new HashMap<Player, ParticleBeam>();
  }

  // waypoints list helper
  public Set<String> getAllWaypoints(String wordlName) {
    Set<String> allWaypoints = fileCfgn.getConfigurationSection(wordlName).getKeys(false);

    return allWaypoints;
  }

  public HashMap<String, Integer> getSpecificWaypoint(String worldName, String waypointName) {
    ConfigurationSection waypoint = fileCfgn.getConfigurationSection(worldName + "." + waypointName);

    HashMap<String, Integer> coordinates = new HashMap<String, Integer>();

    coordinates.put("x", waypoint.getInt("x", 0));
    coordinates.put("y", waypoint.getInt("y", 0));
    coordinates.put("z", waypoint.getInt("z", 0));

    return coordinates;
  }

  public Boolean checkWaypointExists(String worldName, String waypointName) {
    return fileCfgn.contains(worldName + "." + waypointName);
  }

  public void setWaypoint(String worldName, String waypointName, Integer x, Integer y, Integer z) {
    fileCfgn.set(worldName + "." + waypointName + ".x", x);
    fileCfgn.set(worldName + "." + waypointName + ".y", y);
    fileCfgn.set(worldName + "." + waypointName + ".z", z);

    config.save();
  }

  public void removeWaypoint(String worldName, String waypointName) {
    fileCfgn.set(worldName + "." + waypointName, null);

    config.save();
  }

  // waypoint navigation helper
  public Boolean checkPlayerActiveWaypointNavigation(Player p) {
    return activeWaypointNavigations.containsKey(p);
  }

  public void removePlayerActiveWaypointNavigation(Player p) {
    activeWaypointNavigations.remove(p);
    cancelTask(p);

    // hide the particle beam and remove reference
    ParticleBeam beam = waypointBeams.get(p);
    beam.removeBeam();
    waypointBeams.remove(p);
  }

  public void addPlayerActiveWaypointNavigation(Player p, Location location, String locationName, Color waypointColor) {
    activeWaypointNavigations.put(p, new LocationStorage(location, locationName));

    // display the particle beam
    ParticleBeam beam = new ParticleBeam(p, location, waypointColor);
    beam.displayBeam();

    waypointBeams.put(p, beam);
  }

  public LocationStorage getPlayerActiveWaypointNavigation(Player p) {
    return activeWaypointNavigations.get(p);
  }

  // togglelocation navigation helper
  public Boolean checkPlayerActiveToggleLocationNavigation(Player p) {
    return activeToggleLocationNavigations.containsKey(p);
  }

  public void removePlayerActiveToggleLocationNavigation(Player p) {
    activeToggleLocationNavigations.remove(p);
    cancelTask(p);
  }

  public void addPlayerActiveToggleLocationNavigation(Player p, Location location) {
    activeToggleLocationNavigations.put(p, location);
  }

  public Location getPlayerActiveToggleLocationNavigation(Player p) {
    return activeToggleLocationNavigations.get(p);
  }

  // display action bar helper
  public void displayActionBar(Player p, String displayText) {
    cancelTask(p);

    BukkitTask task = navigationScheduler.runTaskTimerAsynchronously(Main.getInstance(), new Runnable() {
      public void run() {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(displayText));

      }
    }, 0, 3 * 10);

    navigationTasks.put(p, task);
  }

  // scheduler task helper
  public void cancelTask(Player p) {
    if (navigationTasks.containsKey(p)) {
      navigationTasks.get(p).cancel();
      navigationTasks.remove(p);
    }
  }
}
