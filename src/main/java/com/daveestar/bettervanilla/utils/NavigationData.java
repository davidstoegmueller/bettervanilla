package com.daveestar.bettervanilla.utils;

import org.bukkit.Color;
import org.bukkit.Location;

import com.daveestar.bettervanilla.enums.NavigationType;

public class NavigationData {
  private final String name;
  private Location location;
  private final NavigationType type;
  private final Color color;

  public NavigationData(String name, Location location, NavigationType type, Color color) {
    this.name = name;
    this.location = location;
    this.type = type;
    this.color = color;
  }

  public String getName() {
    return name;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location newLocation) {
    location = newLocation;
  }

  public NavigationType getType() {
    return type;
  }

  public Color getColor() {
    return color;
  }
}
