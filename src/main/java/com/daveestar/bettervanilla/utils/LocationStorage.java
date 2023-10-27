package com.daveestar.bettervanilla.utils;

import org.bukkit.Location;

public class LocationStorage {
  private Location waypointLoc;
  private String waypointName;

  public LocationStorage(Location waypointLoc, String waypointName) {
    this.waypointLoc = waypointLoc;
    this.waypointName = waypointName;
  }

  public Location getCoordinates() {
    return this.waypointLoc;
  }

  public String getName() {
    return this.waypointName;
  }
}
