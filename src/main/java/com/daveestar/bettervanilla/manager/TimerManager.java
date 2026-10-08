package com.daveestar.bettervanilla.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.Language;
import com.daveestar.bettervanilla.utils.ActionBar;
import com.daveestar.bettervanilla.utils.Config;
import com.daveestar.bettervanilla.utils.Theme;

import org.bukkit.scheduler.BukkitTask;
import net.md_5.bungee.api.ChatColor;

public class TimerManager {

  private boolean _running;
  private boolean _runningOverride;
  private int _globalTimer;
  private BukkitTask _task;
  private int _secondsSinceSave;
  private long _lastTickNanos;
  private final Map<UUID, PlayerTimer> _playerTimers = new HashMap<>();

  private final Config _config;
  private final FileConfiguration _fileConfig;

  private Main _plugin;
  private SettingsManager _settingsManager;
  private NavigationManager _navigationManager;
  private ActionBar _actionBarManager;
  private AFKManager _afkManager;

  public TimerManager(Config config) {
    _plugin = Main.getInstance();

    _config = config;
    _fileConfig = config.getFileConfig();

    _loadConfiguration();
    _initializePlayerTimers();
  }

  private void _loadConfiguration() {
    _running = _fileConfig.getBoolean("running");
    _runningOverride = _fileConfig.getBoolean("runningOverride");
    _globalTimer = _fileConfig.getInt("globalTimer");
  }

  public void initManagers() {
    _settingsManager = _plugin.getSettingsManager();
    _navigationManager = _plugin.getNavigationManager();
    _actionBarManager = _plugin.getActionBar();
    _afkManager = _plugin.getAFKManager();

    updateRunningState(_plugin.getServer().getOnlinePlayers().size());

    _startTimerTask();
  }

  public void onPlayerJoined(Player p) {
    UUID playerId = p.getUniqueId();
    PlayerTimer timer = _loadPlayerTimer(playerId);
    _playerTimers.put(playerId, timer);

    updateRunningState(_plugin.getServer().getOnlinePlayers().size());
  }

  public void destroy() {
    if (_task != null) {
      _task.cancel();
      _task = null;
    }
    _running = false;
    _fileConfig.set("running", false);
    for (Map.Entry<UUID, PlayerTimer> entry : _playerTimers.entrySet()) {
      _savePlayerTimer(entry.getKey(), entry.getValue());
    }
    _config.save();
    _playerTimers.clear();
  }

  public void onPlayerLeft(Player p) {
    UUID playerId = p.getUniqueId();
    PlayerTimer timer = _playerTimers.remove(playerId);

    if (timer != null) {
      _savePlayerTimer(playerId, timer);
      _config.save();
    }

    updateRunningState(_plugin.getServer().getOnlinePlayers().size() - 1);
  }

  public String formatTime(int totalSeconds) {
    return _formatTime(_plugin.getTranslationManager().getServerLanguage(), totalSeconds);
  }

  public String formatTime(CommandSender viewer, int totalSeconds) {
    return _formatTime(_plugin.getTranslationManager().getLanguage(viewer), totalSeconds);
  }

  public String formatTime(UUID playerId, int totalSeconds) {
    return _formatTime(_plugin.getTranslationManager().getLanguage(playerId), totalSeconds);
  }

  private String _formatTime(Language language, int totalSeconds) {
    int days = totalSeconds / (24 * 3600);
    int hours = (totalSeconds % (24 * 3600)) / 3600;
    int minutes = (totalSeconds % 3600) / 60;
    int seconds = totalSeconds % 60;

    StringBuilder timeBuilder = new StringBuilder();
    if (days > 0)
      timeBuilder.append(_plugin.getTranslationManager().translate(language, "time-duration-days-short",
          "value", days)).append(" ");
    if (hours > 0 || days > 0)
      timeBuilder.append(_plugin.getTranslationManager().translate(language, "time-duration-hours-short",
          "value", hours)).append(" ");
    if (minutes > 0 || hours > 0 || days > 0)
      timeBuilder.append(_plugin.getTranslationManager().translate(language, "time-duration-minutes-short",
          "value", minutes)).append(" ");
    timeBuilder.append(_plugin.getTranslationManager().translate(language, "time-duration-seconds-short",
        "value", seconds));

    return timeBuilder.toString().trim();
  }

  public void resetPlayerTimers() {
    Optional.ofNullable(_fileConfig.getConfigurationSection("playerTimers"))
        .ifPresent(section -> section.getKeys(false).forEach(key -> {
          UUID playerId = UUID.fromString(key);

          PlayerTimer newPlayerTimer = new PlayerTimer(0, 0);
          if (_playerTimers.containsKey(playerId)) {
            _playerTimers.put(playerId, newPlayerTimer);
          }
          _savePlayerTimer(playerId, newPlayerTimer);
        }));
    _config.save();
  }

  public int getPlayTime(Player p) {
    return _playerTimers.getOrDefault(p.getUniqueId(), new PlayerTimer(0, 0)).getPlayTime();
  }

  public int getAFKTime(Player p) {
    return _playerTimers.getOrDefault(p.getUniqueId(), new PlayerTimer(0, 0)).getAFKTime();
  }

  public int getPlayTime(UUID playerId) {
    if (_playerTimers.containsKey(playerId)) {
      return _playerTimers.get(playerId).getPlayTime();
    }

    return _loadPlayerTimer(playerId).getPlayTime();
  }

  public int getAFKTime(UUID playerId) {
    if (_playerTimers.containsKey(playerId)) {
      return _playerTimers.get(playerId).getAFKTime();
    }

    return _loadPlayerTimer(playerId).getAFKTime();
  }

  public void updateRunningState(int playerCount) {
    if (isRunningOverride()) {
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

  public int getGlobalTimer() {
    return _globalTimer;
  }

  public void setRunning(boolean state) {
    if (_running != state) {
      _running = state;
      _fileConfig.set("running", state);
      _config.save();
    }
  }

  public boolean isRunningOverride() {
    return _runningOverride;
  }

  public void setRunningOverride(boolean state) {
    if (_runningOverride != state) {
      _runningOverride = state;
      _fileConfig.set("runningOverride", state);
      _config.save();
    }
  }

  public void setGlobalTimer(int time) {
    if (_globalTimer != time) {
      _globalTimer = time;
      _fileConfig.set("globalTimer", time);
      _config.save();
    }
  }

  private void _incrementGlobalTimer(int seconds) {
    _globalTimer += seconds;
    _fileConfig.set("globalTimer", _globalTimer);
  }

  private void _displayTimerActionBar() {
    if (!_settingsManager.getActionBarTimerEnabled()) {
      return;
    }

    _plugin.getServer().getOnlinePlayers().forEach(p -> {
      if (!_settingsManager.getPlayerActionBarTimer(p.getUniqueId())) {
        return;
      }
      if (!_settingsManager.getPlayerToggleLocation(p.getUniqueId()) && !_navigationManager.checkActiveNavigation(p)) {
        _actionBarManager.sendActionBarOnce(p, _generateTimerMessage(p));
      }
    });
  }

  private String _generateTimerMessage(Player viewer) {
    String formattedTime = formatTime(viewer, _globalTimer);
    return _running
        ? Theme.highlight() + "" + ChatColor.BOLD
            + Main.tr(viewer, "timer-actionbar-running", "time", formattedTime)
        : Theme.highlight() + "" + ChatColor.BOLD + Main.tr(viewer, "timer-actionbar-paused",
            "time", Theme.error() + formattedTime + Theme.highlight() + "" + ChatColor.BOLD);
  }

  private PlayerTimer _loadPlayerTimer(UUID playerId) {
    int playTime = _fileConfig.getInt("playerTimers." + playerId + ".playTime", 0);
    int afkTime = _fileConfig.getInt("playerTimers." + playerId + ".afkTime", 0);
    return new PlayerTimer(playTime, afkTime);
  }

  private void _savePlayerTimer(UUID playerId, PlayerTimer timer) {
    _fileConfig.set("playerTimers." + playerId + ".playTime", timer.getPlayTime());
    _fileConfig.set("playerTimers." + playerId + ".afkTime", timer.getAFKTime());
  }

  private void _initializePlayerTimers() {
    for (Player player : _plugin.getServer().getOnlinePlayers()) {
      UUID playerId = player.getUniqueId();
      _playerTimers.put(playerId, _loadPlayerTimer(playerId));
    }
  }

  private void _handlePlayerTimers(int seconds) {
    for (Player p : _plugin.getServer().getOnlinePlayers()) {
      PlayerTimer timer = _playerTimers.get(p.getUniqueId());

      if (timer != null) {
        if (_afkManager.isAFK(p)) {
          timer.incrementAFKTime(seconds);
        } else {
          timer.incrementPlayTime(seconds);
        }
      }
    }
  }

  private void _startTimerTask() {
    _lastTickNanos = System.nanoTime();
    _task = _plugin.getServer().getScheduler().runTaskTimer(_plugin, () -> {
      // Keep measuring real seconds even when server ticks slow down.
      int seconds = (int) Math.min(Integer.MAX_VALUE, (System.nanoTime() - _lastTickNanos) / 1_000_000_000L);
      if (seconds <= 0) {
        return;
      }
      _lastTickNanos += seconds * 1_000_000_000L;
      _afkManager.checkAllPlayersAFKStatus();

      if (_running) {
        _incrementGlobalTimer(seconds);
        _handlePlayerTimers(seconds);
      }

      _displayTimerActionBar();

      _secondsSinceSave = (int) Math.min(60L, _secondsSinceSave + (long) seconds);
      if (_secondsSinceSave >= 60) {
        for (Map.Entry<UUID, PlayerTimer> entry : _playerTimers.entrySet()) {
          _savePlayerTimer(entry.getKey(), entry.getValue());
        }
        _config.save();
        _secondsSinceSave = 0;
      }
    }, 20L, 20L);
  }
}
