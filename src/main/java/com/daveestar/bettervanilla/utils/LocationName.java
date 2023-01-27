package com.daveestar.bettervanilla.utils;

import org.bukkit.Location;

public class LocationName {
  private Location waypointLoc;
  private String waypointName;

  public LocationName(Location waypointLoc, String waypointName) {
    this.waypointLoc = waypointLoc;
    this.waypointName = waypointName;
  }

  public Location getLoc() {
    return this.waypointLoc;
  }

  public String getName() {
    return this.waypointName;
  }
}
