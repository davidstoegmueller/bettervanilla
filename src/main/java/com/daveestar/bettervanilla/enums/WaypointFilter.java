package com.daveestar.bettervanilla.enums;

import java.util.function.Supplier;

import org.bukkit.command.CommandSender;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.utils.Theme;

import net.md_5.bungee.api.ChatColor;

public enum WaypointFilter {
  ALL("enum-waypoint-filter-all", Theme::primary) {
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
  PUBLIC("enum-waypoint-filter-public", Theme::highlight) {
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
  PRIVATE("enum-waypoint-filter-private", Theme::error) {
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

  private final String _translationKey;
  private final Supplier<ChatColor> _color;

  WaypointFilter(String translationKey, Supplier<ChatColor> color) {
    _translationKey = translationKey;
    _color = color;
  }

  public String getColoredName() {
    return getColoredName(null);
  }

  public String getColoredName(CommandSender viewer) {
    return _color.get() + Main.tr(viewer, _translationKey) + Theme.highlight();
  }

  public abstract WaypointFilter next();

  public abstract boolean matches(WaypointVisibility visibility);

  public abstract WaypointFilter previous();
}
