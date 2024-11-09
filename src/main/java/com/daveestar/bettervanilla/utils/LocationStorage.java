package com.daveestar.bettervanilla.utils;

import org.bukkit.Location;

public class LocationStorage {
  private Location _waypointLoc;
  private String _waypointName;

  public LocationStorage(Location waypointLoc, String waypointName) {
    this._waypointLoc = waypointLoc;
    this._waypointName = waypointName;
  }

  public Location getCoordinates() {
    return this._waypointLoc;
  }

  public String getName() {
    return this._waypointName;
  }
}
