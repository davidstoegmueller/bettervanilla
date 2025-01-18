package com.daveestar.bettervanilla.utils;

import org.bukkit.Color;
import org.bukkit.Location;

import com.daveestar.bettervanilla.enums.NavigationType;

public class NavigationData {
  private final String _name;
  private Location _location;
  private final NavigationType _type;
  private final Color _color;

  public NavigationData(String name, Location location, NavigationType type, Color color) {
    this._name = name;
    this._location = location;
    this._type = type;
    this._color = color;
  }

  public String getName() {
    return _name;
  }

  public Location getLocation() {
    return _location;
  }

  public void setLocation(Location newLocation) {
    _location = newLocation;
  }

  public NavigationType getType() {
    return _type;
  }

  public Color getColor() {
    return _color;
  }
}
