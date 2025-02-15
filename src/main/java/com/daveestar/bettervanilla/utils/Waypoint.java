package com.daveestar.bettervanilla.utils;

import org.bukkit.Location;

public class Waypoint {
  private Location _location;
  private String _name;

  public Waypoint(String name, Location location) {
    _location = location;
    _name = name;
  }

  public String getName() {
    return _name;
  }

  public Location getLocation() {
    return _location;
  }
}
