package com.daveestar.bettervanilla.utils;

import org.bukkit.Color;
import org.bukkit.Location;

public class NavigationData {
  private final String name;
  private final Location location;
  private final Color color;

  public NavigationData(String name, Location location, Color color) {
    this.name = name;
    this.location = location;
    this.color = color;
  }

  public String getName() {
    return name;
  }

  public Location getLocation() {
    return location;
  }

  public Color getColor() {
    return color;
  }
}
