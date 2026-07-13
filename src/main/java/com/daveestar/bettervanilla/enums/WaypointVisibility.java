package com.daveestar.bettervanilla.enums;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import com.daveestar.bettervanilla.Main;

public enum WaypointVisibility {
  PUBLIC("enum-waypoint-visibility-public"),
  PRIVATE("enum-waypoint-visibility-private");

  public static final EnumSet<WaypointVisibility> ALL = EnumSet.allOf(WaypointVisibility.class);

  private final String translationKey;

  WaypointVisibility(String translationKey) {
    this.translationKey = translationKey;
  }

  public String getName() {
    return name();
  }

  public String getDisplayName() {
    return getDisplayName(null);
  }

  public String getDisplayName(CommandSender viewer) {
    return Main.tr(viewer, translationKey);
  }

  private static final WaypointVisibility[] VALUES = values();

  public WaypointVisibility next() {
    return VALUES[(ordinal() + 1) % VALUES.length];
  }

  public WaypointVisibility previous() {
    return VALUES[(ordinal() - 1 + VALUES.length) % VALUES.length];
  }

  public static Map<String, String> toMap() {
    return toMap(null);
  }

  public static Map<String, String> toMap(CommandSender viewer) {
    return ALL.stream().collect(Collectors.toMap(WaypointVisibility::getName,
        visibility -> visibility.getDisplayName(viewer)));
  }

  public static Optional<WaypointVisibility> fromString(String value) {
    if (value == null || value.isEmpty()) {
      return Optional.empty();
    }

    try {
      return Optional.of(WaypointVisibility.valueOf(value.toUpperCase(Locale.ROOT)));
    } catch (IllegalArgumentException ex) {
      return Optional.empty();
    }
  }
}
