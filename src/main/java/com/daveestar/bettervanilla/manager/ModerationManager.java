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

  public synchronized void banPlayer(OfflinePlayer p, String reason) {
    _handleBanPlayer(p, reason, -1);
  }

  public synchronized void tempBanPlayer(OfflinePlayer p, String reason, long durationMillis) {
    _handleBanPlayer(p, reason, _expiry(durationMillis));
  }

  public synchronized void unbanPlayer(OfflinePlayer p) {
    _fileConfig.set("bans." + p.getUniqueId(), null);
    _config.save();
  }

  public synchronized boolean isBanned(OfflinePlayer p) {
    return isBanned(p.getUniqueId());
  }

  public synchronized boolean isBanned(UUID playerId) {
    String path = "bans." + playerId;
    if (!_fileConfig.contains(path))
      return false;

    long expires = _fileConfig.getLong(path + ".expires", -1);
    if (expires != -1 && System.currentTimeMillis() > expires) {
      _fileConfig.set(path, null);
      _config.save();
      return false;
    }

    return true;
  }

  public synchronized String getBanReason(OfflinePlayer p) {
    return getBanReason(p.getUniqueId());
  }

  public synchronized String getBanReason(UUID playerId) {
    return _fileConfig.getString("bans." + playerId + ".reason", "");
  }

  public synchronized long getBanExpiry(OfflinePlayer p) {
    return getBanExpiry(p.getUniqueId());
  }

  public synchronized long getBanExpiry(UUID playerId) {
    return _fileConfig.getLong("bans." + playerId + ".expires", -1);
  }

  public synchronized List<String> getBannedPlayerNames() {
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

  public synchronized void mutePlayer(OfflinePlayer p, String reason) {
    _handleMutePlayer(p, reason, -1);
  }

  public synchronized void tempMutePlayer(OfflinePlayer p, String reason, long durationMillis) {
    _handleMutePlayer(p, reason, _expiry(durationMillis));
  }

  public synchronized void unmutePlayer(OfflinePlayer p) {
    _fileConfig.set("mutes." + p.getUniqueId(), null);
    _config.save();
  }

  public synchronized boolean isMuted(OfflinePlayer p) {
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

  public synchronized String getMuteReason(OfflinePlayer p) {
    return _fileConfig.getString("mutes." + p.getUniqueId() + ".reason", "");
  }

  public synchronized long getMuteExpiry(OfflinePlayer p) {
    return _fileConfig.getLong("mutes." + p.getUniqueId() + ".expires", -1);
  }

  public synchronized List<String> getMutedPlayerNames() {
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

  private long _expiry(long durationMillis) {
    if (durationMillis <= 0 || durationMillis > Integer.MAX_VALUE * 1000L) {
      throw new IllegalArgumentException("Moderation duration must be positive and fit in integer seconds.");
    }
    return Math.addExact(System.currentTimeMillis(), durationMillis);
  }

  private void _handleMutePlayer(OfflinePlayer p, String reason, long expires) {
    String path = "mutes." + p.getUniqueId();
    _fileConfig.set(path + ".reason", reason);
    _fileConfig.set(path + ".expires", expires);
    _config.save();
  }
}
