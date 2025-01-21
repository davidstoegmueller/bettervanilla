package com.daveestar.bettervanilla.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.utils.ActionBarManager;
import com.daveestar.bettervanilla.utils.Config;

import net.md_5.bungee.api.ChatColor;

public class TimerManager {

  private boolean _running;
  private boolean _runningOverride;
  private int _globalTimer;
  private final Map<UUID, PlayerTimer> _playerTimers = new HashMap<>();

  private final Config _config;
  private final FileConfiguration _fileConfig;
  private final ActionBarManager _actionBarManager;
  private final AFKManager _afkManager;

  public TimerManager(Config config) {
    this._config = config;
    this._fileConfig = config.getFileCfgrn();
    this._actionBarManager = Main.getInstance().getActionBarManager();
    this._afkManager = Main.getInstance().getAFKManager();

    _loadConfiguration();
    _initializePlayerTimers();
    _startTimerTask();
  }

  private void _loadConfiguration() {
    _running = _fileConfig.getBoolean("running");
    _runningOverride = _fileConfig.getBoolean("runningOverride");
    _globalTimer = _fileConfig.getInt("globalTimer");
  }

  // Player timer handling

  public void onPlayerJoined(Player p) {
    UUID playerId = p.getUniqueId();
    PlayerTimer timer = _loadPlayerTimer(playerId);
    _playerTimers.put(playerId, timer);

    updateRunningState(Bukkit.getOnlinePlayers().size());
  }

  public void onPlayerLeft(Player p) {
    UUID playerId = p.getUniqueId();
    PlayerTimer timer = _playerTimers.remove(playerId);

    if (timer != null) {
      _savePlayerTimer(playerId, timer);
    }

    updateRunningState(Bukkit.getOnlinePlayers().size() - 1);
  }

  public void resetPlayerTimers() {
    Optional.ofNullable(_fileConfig.getConfigurationSection("playerTimers"))
        .ifPresent(section -> section.getKeys(false).forEach(key -> {
          UUID playerId = UUID.fromString(key);

          PlayerTimer newPlayerTimer = new PlayerTimer(0, 0);
          _playerTimers.put(playerId, newPlayerTimer);
          _savePlayerTimer(playerId, newPlayerTimer);
        }));
  }

  private PlayerTimer _loadPlayerTimer(UUID playerId) {
    int playTime = _fileConfig.getInt("playerTimers." + playerId + ".playTime", 0);
    int afkTime = _fileConfig.getInt("playerTimers." + playerId + ".afkTime", 0);
    return new PlayerTimer(playTime, afkTime);
  }

  private void _savePlayerTimer(UUID playerId, PlayerTimer timer) {
    _fileConfig.set("playerTimers." + playerId + ".playTime", timer.getPlayTime());
    _fileConfig.set("playerTimers." + playerId + ".afkTime", timer.getAFKTime());
    _config.save();
  }

  private void _initializePlayerTimers() {
    Optional.ofNullable(_fileConfig.getConfigurationSection("playerTimers"))
        .ifPresent(section -> section.getKeys(false).forEach(key -> {
          UUID playerId = UUID.fromString(key);
          _playerTimers.put(playerId, _loadPlayerTimer(playerId));
        }));
  }

  private void _handlePlayerTimers() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      PlayerTimer timer = _playerTimers.get(player.getUniqueId());
      if (timer != null) {
        if (_afkManager.isAFK(player)) {
          timer.incrementAFKTime();
        } else {
          timer.incrementPlayTime();
        }
      }
    }
  }

  // Timer state management

  public void updateRunningState(int playerCount) {
    if (isRunningOverride()) {
      // Automatically set running state based on player count
      boolean shouldRun = playerCount > 0;

      if (isRunning() != shouldRun) {
        setRunning(shouldRun);
      }

      return;
    }
  }

  public boolean isRunning() {
    return _running;
  }

  public void setRunning(boolean state) {
    if (this._running != state) {
      this._running = state;
      _fileConfig.set("running", state);
      _config.save();
    }
  }

  public boolean isRunningOverride() {
    return _runningOverride;
  }

  public void setRunningOverride(boolean state) {
    if (this._runningOverride != state) {
      this._runningOverride = state;
      _fileConfig.set("runningOverride", state);
      _config.save();
    }
  }

  public void setGlobalTimer(int time) {
    if (this._globalTimer != time) {
      this._globalTimer = time;
      _fileConfig.set("globalTimer", time);
      _config.save();
    }
  }

  private void _incrementGlobalTimer() {
    setGlobalTimer(_globalTimer + 1);
  }

  // Action bar and timer display

  private void _displayTimerActionBar() {
    String message = _generateTimerMessage();
    Bukkit.getOnlinePlayers().forEach(player -> _actionBarManager.sendActionBarOnce(player, message));
  }

  private String _generateTimerMessage() {
    String formattedTime = _formatTime(_globalTimer);
    return _running
        ? ChatColor.YELLOW + "" + ChatColor.BOLD + formattedTime
        : ChatColor.YELLOW + "" + ChatColor.BOLD + "Paused " + ChatColor.GRAY + "("
            + ChatColor.RED + formattedTime + ChatColor.GRAY + ")";
  }

  // Timer task and formatting

  private void _startTimerTask() {
    new BukkitRunnable() {
      @Override
      public void run() {
        _displayTimerActionBar();

        if (_running) {
          _incrementGlobalTimer();
          _handlePlayerTimers();
        }
      }
    }.runTaskTimer(Main.getInstance(), 20L, 20L);
  }

  private String _formatTime(int totalSeconds) {
    int days = totalSeconds / (24 * 3600);
    int hours = (totalSeconds % (24 * 3600)) / 3600;
    int minutes = (totalSeconds % 3600) / 60;
    int seconds = totalSeconds % 60;

    StringBuilder timeBuilder = new StringBuilder();
    if (days > 0)
      timeBuilder.append(days).append("d ");
    if (hours > 0 || days > 0)
      timeBuilder.append(hours).append("h ");
    if (minutes > 0 || hours > 0 || days > 0)
      timeBuilder.append(minutes).append("m ");
    timeBuilder.append(seconds).append("s");

    return timeBuilder.toString().trim();
  }
}
