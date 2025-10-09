package com.daveestar.bettervanilla.enums;

import net.md_5.bungee.api.ChatColor;

public enum WaypointFilter {
  ALL("All", ChatColor.GRAY) {
    @Override
    public WaypointFilter next() {
      return PUBLIC;
    }

    @Override
    public WaypointFilter previous() {
      return PRIVATE;
    }

    @Override
    public boolean matches(WaypointVisibility visibility) {
      return true;
    }
  },
  PUBLIC("Public", ChatColor.GREEN) {
    @Override
    public WaypointFilter next() {
      return PRIVATE;
    }

    @Override
    public WaypointFilter previous() {
      return ALL;
    }

    @Override
    public boolean matches(WaypointVisibility visibility) {
      return visibility == WaypointVisibility.PUBLIC;
    }
  },
  PRIVATE("Private", ChatColor.RED) {
    @Override
    public WaypointFilter next() {
      return ALL;
    }

    @Override
    public WaypointFilter previous() {
      return PUBLIC;
    }

    @Override
    public boolean matches(WaypointVisibility visibility) {
      return visibility == WaypointVisibility.PRIVATE;
    }
  };

  private final String _displayName;
  private final ChatColor _color;

  WaypointFilter(String displayName, ChatColor color) {
    _displayName = displayName;
    _color = color;
  }

  public String getColoredName() {
    return _color + _displayName + ChatColor.YELLOW;
  }

  public abstract WaypointFilter next();

  public abstract boolean matches(WaypointVisibility visibility);

  public abstract WaypointFilter previous();
}
