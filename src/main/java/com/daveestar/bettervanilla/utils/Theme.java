package com.daveestar.bettervanilla.utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Material;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SettingsManager;

import net.md_5.bungee.api.ChatColor;

/**
 * Resolves the currently configured visual theme.
 *
 * Theme values are intentionally read on every call so changes made through the
 * admin GUI take effect immediately.
 */
public final class Theme {
  public static final String DEFAULT_PRIMARY_FONT_COLOR = "GRAY";
  public static final String DEFAULT_HIGHLIGHT_FONT_COLOR = "YELLOW";
  public static final String DEFAULT_ERROR_FONT_COLOR = "RED";
  public static final String DEFAULT_TITLE_SYMBOL_COLOR = "RED";
  public static final String DEFAULT_TEXT_SYMBOL_COLOR = "YELLOW";
  public static final String DEFAULT_GLASS_PANE_COLOR = "YELLOW";
  public static final String DEFAULT_NAME = "BetterVanilla";

  private static final Map<String, ChatColor> MINECRAFT_COLORS;
  static {
    Map<String, ChatColor> colors = new LinkedHashMap<>();
    colors.put("BLACK", ChatColor.BLACK);
    colors.put("DARK_BLUE", ChatColor.DARK_BLUE);
    colors.put("DARK_GREEN", ChatColor.DARK_GREEN);
    colors.put("DARK_AQUA", ChatColor.DARK_AQUA);
    colors.put("DARK_RED", ChatColor.DARK_RED);
    colors.put("DARK_PURPLE", ChatColor.DARK_PURPLE);
    colors.put("GOLD", ChatColor.GOLD);
    colors.put("GRAY", ChatColor.GRAY);
    colors.put("DARK_GRAY", ChatColor.DARK_GRAY);
    colors.put("BLUE", ChatColor.BLUE);
    colors.put("GREEN", ChatColor.GREEN);
    colors.put("AQUA", ChatColor.AQUA);
    colors.put("RED", ChatColor.RED);
    colors.put("LIGHT_PURPLE", ChatColor.LIGHT_PURPLE);
    colors.put("YELLOW", ChatColor.YELLOW);
    colors.put("WHITE", ChatColor.WHITE);
    MINECRAFT_COLORS = Collections.unmodifiableMap(colors);
  }

  private Theme() {
  }

  public static ChatColor primary() {
    SettingsManager settings = _settings();
    String value = settings == null ? DEFAULT_PRIMARY_FONT_COLOR : settings.getPrimaryFontColor();
    return MINECRAFT_COLORS.get(value);
  }

  public static ChatColor highlight() {
    SettingsManager settings = _settings();
    String value = settings == null ? DEFAULT_HIGHLIGHT_FONT_COLOR : settings.getHighlightFontColor();
    return MINECRAFT_COLORS.get(value);
  }

  public static ChatColor error() {
    SettingsManager settings = _settings();
    String value = settings == null ? DEFAULT_ERROR_FONT_COLOR : settings.getErrorFontColor();
    return MINECRAFT_COLORS.get(value);
  }

  public static ChatColor titleSymbol() {
    SettingsManager settings = _settings();
    String value = settings == null ? DEFAULT_TITLE_SYMBOL_COLOR : settings.getTitleSymbolColor();
    return MINECRAFT_COLORS.get(value);
  }

  public static ChatColor textSymbol() {
    SettingsManager settings = _settings();
    String value = settings == null ? DEFAULT_TEXT_SYMBOL_COLOR : settings.getTextSymbolColor();
    return MINECRAFT_COLORS.get(value);
  }

  public static ChatColor glassPaneColor() {
    SettingsManager settings = _settings();
    String value = settings == null ? DEFAULT_GLASS_PANE_COLOR : settings.getGlassPaneColor();
    return MINECRAFT_COLORS.get(value);
  }

  public static Material glassPaneMaterial() {
    SettingsManager settings = _settings();
    String color = settings == null ? DEFAULT_GLASS_PANE_COLOR : settings.getGlassPaneColor();
    return switch (color) {
      case "BLACK" -> Material.BLACK_STAINED_GLASS_PANE;
      case "DARK_BLUE", "BLUE" -> Material.BLUE_STAINED_GLASS_PANE;
      case "DARK_GREEN" -> Material.GREEN_STAINED_GLASS_PANE;
      case "DARK_AQUA" -> Material.CYAN_STAINED_GLASS_PANE;
      case "DARK_RED", "RED" -> Material.RED_STAINED_GLASS_PANE;
      case "DARK_PURPLE" -> Material.PURPLE_STAINED_GLASS_PANE;
      case "GOLD" -> Material.ORANGE_STAINED_GLASS_PANE;
      case "GRAY" -> Material.LIGHT_GRAY_STAINED_GLASS_PANE;
      case "DARK_GRAY" -> Material.GRAY_STAINED_GLASS_PANE;
      case "GREEN" -> Material.LIME_STAINED_GLASS_PANE;
      case "AQUA" -> Material.LIGHT_BLUE_STAINED_GLASS_PANE;
      case "LIGHT_PURPLE" -> Material.MAGENTA_STAINED_GLASS_PANE;
      case "WHITE" -> Material.WHITE_STAINED_GLASS_PANE;
      default -> Material.YELLOW_STAINED_GLASS_PANE;
    };
  }

  public static Map<String, ChatColor> minecraftColors() {
    return MINECRAFT_COLORS;
  }

  public static String name() {
    SettingsManager settings = _settings();
    String value = settings == null ? DEFAULT_NAME : settings.getThemeName();
    if (value == null || value.isBlank()) {
      return DEFAULT_NAME;
    }

    String sanitized = ChatColor.stripColor(value)
        .replaceAll("(?i)&[0-9A-FK-ORX]", "")
        .replace('\r', ' ')
        .replace('\n', ' ')
        .trim();
    return sanitized.isEmpty() ? DEFAULT_NAME : sanitized;
  }

  public static String titlePrefix() {
    return titleSymbol() + "" + ChatColor.BOLD + "» " + highlight();
  }

  public static String textPrefix() {
    return textSymbol() + "» " + primary();
  }

  /**
   * Converts a configured theme color to the legacy ampersand form used by the
   * server MOTD serializer.
   */
  public static String asAmpersandCode(ChatColor color) {
    return color.toString().replace(ChatColor.COLOR_CHAR, '&');
  }

  private static SettingsManager _settings() {
    Main plugin = Main.getInstance();
    return plugin == null ? null : plugin.getSettingsManager();
  }
}
