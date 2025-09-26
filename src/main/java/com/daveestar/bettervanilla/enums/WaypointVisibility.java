package com.daveestar.bettervanilla.enums;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public enum WaypointVisibility {
  PUBLIC("Public"),
  PRIVATE("Private");

  public static final EnumSet<WaypointVisibility> ALL = EnumSet.allOf(WaypointVisibility.class);

  private final String displayName;

  WaypointVisibility(String displayName) {
    this.displayName = displayName;
  }

  public String getName() {
    return name();
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> toMap() {
    return ALL.stream().collect(Collectors.toMap(WaypointVisibility::getName, WaypointVisibility::getDisplayName));
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
