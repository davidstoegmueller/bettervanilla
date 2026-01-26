package com.daveestar.bettervanilla.manager;

import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

import net.md_5.bungee.api.ChatColor;

public class TagManager {
  private static final ChatColor DEFAULT_TAG_COLOR = ChatColor.AQUA;
  private static final Map<String, ChatColor> TAG_COLORS = new LinkedHashMap<>();
  static {
    TAG_COLORS.put("BLACK", ChatColor.BLACK);
    TAG_COLORS.put("DARK_BLUE", ChatColor.DARK_BLUE);
    TAG_COLORS.put("DARK_GREEN", ChatColor.DARK_GREEN);
    TAG_COLORS.put("DARK_AQUA", ChatColor.DARK_AQUA);
    TAG_COLORS.put("DARK_RED", ChatColor.DARK_RED);
    TAG_COLORS.put("DARK_PURPLE", ChatColor.DARK_PURPLE);
    TAG_COLORS.put("GOLD", ChatColor.GOLD);
    TAG_COLORS.put("GRAY", ChatColor.GRAY);
    TAG_COLORS.put("DARK_GRAY", ChatColor.DARK_GRAY);
    TAG_COLORS.put("BLUE", ChatColor.BLUE);
    TAG_COLORS.put("GREEN", ChatColor.GREEN);
    TAG_COLORS.put("AQUA", ChatColor.AQUA);
    TAG_COLORS.put("RED", ChatColor.RED);
    TAG_COLORS.put("LIGHT_PURPLE", ChatColor.LIGHT_PURPLE);
    TAG_COLORS.put("YELLOW", ChatColor.YELLOW);
    TAG_COLORS.put("WHITE", ChatColor.WHITE);
  }

  private final SettingsManager _settingsManager;

  public TagManager(SettingsManager settingsManager) {
    this._settingsManager = settingsManager;
  }

  public void setTag(Player p, String tag) {
    if (p == null) {
      return;
    }

    ChatColor existing = getTagColor(p);
    setTag(p, tag, existing != null ? existing : DEFAULT_TAG_COLOR);
  }

  public void setTag(Player p, String tag, ChatColor color) {
    if (p == null) {
      return;
    }

    _settingsManager.setPlayerTagName(p.getUniqueId(), tag);
    _settingsManager.setPlayerTagColor(p.getUniqueId(), _getColorKey(color));
  }

  public void removeTag(Player p) {
    if (p == null) {
      return;
    }

    _settingsManager.clearPlayerTag(p.getUniqueId());
  }

  public String getTag(Player p) {
    if (p == null) {
      return null;
    }

    return _settingsManager.getPlayerTagName(p.getUniqueId());
  }

  public ChatColor getTagColor(Player p) {
    if (p == null) {
      return ChatColor.AQUA;
    }

    String colorName = _settingsManager.getPlayerTagColor(p.getUniqueId());
    return _resolveColor(colorName);
  }

  public String getFormattedTag(Player p) {
    if (!_settingsManager.getTagsEnabled()) {
      return "";
    }

    String tag = getTag(p);

    if (tag == null)
      return "";

    ChatColor color = getTagColor(p);
    return ChatColor.GRAY + " [" + color + tag + ChatColor.GRAY + "]";
  }

  private ChatColor _resolveColor(String colorName) {
    if (colorName == null) {
      return DEFAULT_TAG_COLOR;
    }

    for (Map.Entry<String, ChatColor> entry : TAG_COLORS.entrySet()) {
      if (entry.getKey().equalsIgnoreCase(colorName)) {
        return entry.getValue();
      }
    }

    return DEFAULT_TAG_COLOR;
  }

  private String _getColorKey(ChatColor color) {
    if (color == null) {
      return DEFAULT_TAG_COLOR == null ? null : "AQUA";
    }

    for (Map.Entry<String, ChatColor> entry : TAG_COLORS.entrySet()) {
      if (entry.getValue().equals(color)) {
        return entry.getKey();
      }
    }

    return "AQUA";
  }
}
