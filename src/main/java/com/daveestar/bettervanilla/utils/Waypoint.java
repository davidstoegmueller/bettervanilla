package com.daveestar.bettervanilla.utils;

import org.bukkit.Location;

public class Waypoint {
  private Location _location;
  private String _name;

  public Waypoint(String name, Location location) {
    this._location = location;
    this._name = name;
  }

  public String getName() {
    return this._name;
  }

  public Location getLocation() {
    return this._location;
  }
}
