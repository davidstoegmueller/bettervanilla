package com.daveestar.bettervanilla.manager;

import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.utils.Config;

import net.md_5.bungee.api.ChatColor;

public class TagManager {
  private final static String KEY_TAGS = "tags";
  private final Config _config;

  public TagManager(Config config) {
    this._config = config;
  }

  public void setTag(Player p, String tag) {
    _config.getFileConfig().set(KEY_TAGS + "." + p.getUniqueId(), tag);
    _config.save();
  }

  public void removeTag(Player p) {
    _config.getFileConfig().set(KEY_TAGS + "." + p.getUniqueId(), null);
    _config.save();
  }

  public String getTag(Player p) {
    return _config.getFileConfig().getString(KEY_TAGS + "." + p.getUniqueId());
  }

  public String getFormattedTag(Player p) {
    String tag = getTag(p);

    if (tag == null)
      return "";

    return ChatColor.GRAY + " [" + ChatColor.YELLOW + tag + ChatColor.GRAY + "]";
  }
}
