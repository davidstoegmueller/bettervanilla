package com.daveestar.bettervanilla.enums;

public enum InventorySortMode {
  ALPHABETICAL_ASC("Name (A → Z)"),
  ALPHABETICAL_DESC("Name (Z → A)"),
  STACK_DESC("Stack (High → Low)"),
  STACK_ASC("Stack (Low → High)");

  private final String _label;

  InventorySortMode(String label) {
    _label = label;
  }

  public String getLabel() {
    return _label;
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
