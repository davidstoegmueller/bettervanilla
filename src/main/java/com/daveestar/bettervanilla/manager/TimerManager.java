package com.daveestar.bettervanilla.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.utils.ActionBar;
import com.daveestar.bettervanilla.utils.Config;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import net.md_5.bungee.api.ChatColor;

public class TimerManager {

  private boolean _running;
  private boolean _runningOverride;
  private int _globalTimer;
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

  public void initManagers() {
    _settingsManager = _plugin.getSettingsManager();
    _navigationManager = _plugin.getNavigationManager();
    _actionBarManager = _plugin.getActionBar();
    _afkManager = _plugin.getAFKManager();

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

    updateRunningState(_plugin.getServer().getOnlinePlayers().size());
  }

  public void onPlayerLeft(Player p) {
    UUID playerId = p.getUniqueId();
    PlayerTimer timer = _playerTimers.remove(playerId);

    if (timer != null) {
      _savePlayerTimer(playerId, timer);
    }

    updateRunningState(_plugin.getServer().getOnlinePlayers().size() - 1);
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

  public int getPlayTime(Player p) {
    return _playerTimers.getOrDefault(p.getUniqueId(), new PlayerTimer(0, 0)).getPlayTime();
  }

  public int getAFKTime(Player p) {
    return _playerTimers.getOrDefault(p.getUniqueId(), new PlayerTimer(0, 0)).getAFKTime();
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
    for (Player p : _plugin.getServer().getOnlinePlayers()) {
      PlayerTimer timer = _playerTimers.get(p.getUniqueId());
      if (timer != null) {
        if (_afkManager.isAFK(p)) {
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

  private void _incrementGlobalTimer() {
    setGlobalTimer(_globalTimer + 1);
  }

  // Action bar and timer display

  private void _displayTimerActionBar() {
    String message = _generateTimerMessage();

    _plugin.getServer().getOnlinePlayers().forEach(p -> {
      if (!_settingsManager.getToggleLocation(p) && !_navigationManager.checkActiveNavigation(p)) {
        _actionBarManager.sendActionBarOnce(p, message);
      }
    });
  }

  private String _generateTimerMessage() {
    String formattedTime = formatTime(_globalTimer);
    return _running
        ? ChatColor.YELLOW + "" + ChatColor.BOLD + formattedTime
        : ChatColor.YELLOW + "" + ChatColor.BOLD + "Paused " + ChatColor.GRAY + "("
            + ChatColor.RED + formattedTime + ChatColor.GRAY + ")";
  }

  // Timer task and formatting

  public String formatTime(int totalSeconds) {
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

  private void _startTimerTask() {
    AsyncScheduler scheduler = _plugin.getServer().getAsyncScheduler();

    scheduler.runAtFixedRate(_plugin, task -> {
      _displayTimerActionBar();

      if (_running) {
        _incrementGlobalTimer();
        _handlePlayerTimers();
      }
    }, 0, 1, TimeUnit.SECONDS);
  }
}
