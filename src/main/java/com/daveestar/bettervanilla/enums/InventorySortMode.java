package com.daveestar.bettervanilla.enums;

import org.bukkit.command.CommandSender;

import com.daveestar.bettervanilla.Main;

public enum InventorySortMode {
  ALPHABETICAL_ASC("enum-inventory-sort-mode-alphabetical-ascending"),
  ALPHABETICAL_DESC("enum-inventory-sort-mode-alphabetical-descending"),
  STACK_DESC("enum-inventory-sort-mode-stack-descending"),
  STACK_ASC("enum-inventory-sort-mode-stack-ascending");

  private final String _translationKey;

  InventorySortMode(String translationKey) {
    _translationKey = translationKey;
  }

  public String getLabel() {
    return getLabel(null);
  }

  public String getLabel(CommandSender viewer) {
    return Main.tr(viewer, _translationKey);
  }

  public InventorySortMode next() {
    InventorySortMode[] values = values();
    int nextIndex = (ordinal() + 1) % values.length;
    return values[nextIndex];
  }

  public static InventorySortMode fromString(String value) {
    if (value == null || value.isBlank()) {
      return ALPHABETICAL_ASC;
    }

    for (InventorySortMode mode : values()) {
      if (mode.name().equalsIgnoreCase(value)) {
        return mode;
      }
    }

    return ALPHABETICAL_ASC;
  }
}
