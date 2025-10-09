package com.daveestar.bettervanilla.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.daveestar.bettervanilla.utils.Config;

public class ModerationManager {
  private final Config _config;
  private final FileConfiguration _fileConfig;

  public ModerationManager(Config config) {
    _config = config;
    _fileConfig = config.getFileConfig();

    if (!_fileConfig.contains("bans")) {
      _fileConfig.createSection("bans");
    }
    if (!_fileConfig.contains("mutes")) {
      _fileConfig.createSection("mutes");
    }

    _config.save();
  }

  public void banPlayer(OfflinePlayer p, String reason) {
    _handleBanPlayer(p, reason, -1);
  }

  public void tempBanPlayer(OfflinePlayer p, String reason, long durationMillis) {
    _handleBanPlayer(p, reason, System.currentTimeMillis() + durationMillis);
  }

  public void unbanPlayer(OfflinePlayer p) {
    _fileConfig.set("bans." + p.getUniqueId(), null);
    _config.save();
  }

  public boolean isBanned(OfflinePlayer p) {
    String path = "bans." + p.getUniqueId();
    if (!_fileConfig.contains(path))
      return false;

    long expires = _fileConfig.getLong(path + ".expires", -1);
    if (expires != -1 && System.currentTimeMillis() > expires) {
      unbanPlayer(p);
      return false;
    }

    return true;
  }

  public String getBanReason(OfflinePlayer p) {
    return _fileConfig.getString("bans." + p.getUniqueId() + ".reason", "");
  }

  public long getBanExpiry(OfflinePlayer p) {
    return _fileConfig.getLong("bans." + p.getUniqueId() + ".expires", -1);
  }

  public List<String> getBannedPlayerNames() {
    ConfigurationSection section = _fileConfig.getConfigurationSection("bans");
    if (section == null)
      return Collections.emptyList();

    List<String> names = new ArrayList<>();
    for (String key : section.getKeys(false)) {
      OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(key));

      if (p.getName() != null)
        names.add(p.getName());
    }

    return names;
  }

  public void mutePlayer(OfflinePlayer p, String reason) {
    _handleMutePlayer(p, reason, -1);
  }

  public void tempMutePlayer(OfflinePlayer p, String reason, long durationMillis) {
    _handleMutePlayer(p, reason, System.currentTimeMillis() + durationMillis);
  }

  public void unmutePlayer(OfflinePlayer p) {
    _fileConfig.set("mutes." + p.getUniqueId(), null);
    _config.save();
  }

  public boolean isMuted(OfflinePlayer p) {
    String path = "mutes." + p.getUniqueId();
    if (!_fileConfig.contains(path))
      return false;

    long expires = _fileConfig.getLong(path + ".expires", -1);
    if (expires != -1 && System.currentTimeMillis() > expires) {
      unmutePlayer(p);
      return false;
    }

    return true;
  }

  public String getMuteReason(OfflinePlayer p) {
    return _fileConfig.getString("mutes." + p.getUniqueId() + ".reason", "");
  }

  public long getMuteExpiry(OfflinePlayer p) {
    return _fileConfig.getLong("mutes." + p.getUniqueId() + ".expires", -1);
  }

  public List<String> getMutedPlayerNames() {
    ConfigurationSection section = _fileConfig.getConfigurationSection("mutes");
    if (section == null)
      return Collections.emptyList();

    List<String> names = new ArrayList<>();
    for (String key : section.getKeys(false)) {
      OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(key));

      if (p.getName() != null)
        names.add(p.getName());
    }

    return names;
  }

  private void _handleBanPlayer(OfflinePlayer p, String reason, long expires) {
    String path = "bans." + p.getUniqueId();
    _fileConfig.set(path + ".reason", reason);
    _fileConfig.set(path + ".expires", expires);
    _config.save();
  }

  private void _handleMutePlayer(OfflinePlayer p, String reason, long expires) {
    String path = "mutes." + p.getUniqueId();
    _fileConfig.set(path + ".reason", reason);
    _fileConfig.set(path + ".expires", expires);
    _config.save();
  }
}
