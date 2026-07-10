package com.daveestar.bettervanilla.manager;

import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.utils.Theme;

import net.md_5.bungee.api.ChatColor;

public class TagManager {
  private final SettingsManager _settingsManager;

  public TagManager(SettingsManager settingsManager) {
    this._settingsManager = settingsManager;
  }

  public void setTag(Player p, String tag) {
    if (p == null) {
      return;
    }

    setTag(p, tag, _settingsManager.getPlayerTagColor(p.getUniqueId()));
  }

  public void setTag(Player p, String tag, String colorKey) {
    if (p == null) {
      return;
    }

    _settingsManager.setPlayerTagName(p.getUniqueId(), tag);
    _settingsManager.setPlayerTagColor(p.getUniqueId(), colorKey);
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

    return Theme.minecraftColors().get(_settingsManager.getPlayerTagColor(p.getUniqueId()));
  }

  public String getFormattedTag(Player p) {
    if (!_settingsManager.getTagsEnabled()) {
      return "";
    }

    String tag = getTag(p);

    if (tag == null)
      return "";

    ChatColor color = getTagColor(p);
    return Theme.primary() + " [" + color + tag + Theme.primary() + "]";
  }
}
