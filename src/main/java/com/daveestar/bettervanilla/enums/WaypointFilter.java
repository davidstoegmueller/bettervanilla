package com.daveestar.bettervanilla.enums;

import java.util.function.Supplier;

import com.daveestar.bettervanilla.utils.Theme;

import net.md_5.bungee.api.ChatColor;

public enum WaypointFilter {
  ALL("All", Theme::primary) {
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
  PUBLIC("Public", Theme::highlight) {
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
  PRIVATE("Private", Theme::error) {
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
  private final Supplier<ChatColor> _color;

  WaypointFilter(String displayName, Supplier<ChatColor> color) {
    _displayName = displayName;
    _color = color;
  }

  public String getColoredName() {
    return _color.get() + _displayName + Theme.highlight();
  }

  public abstract WaypointFilter next();

  public abstract boolean matches(WaypointVisibility visibility);

  public abstract WaypointFilter previous();
}
