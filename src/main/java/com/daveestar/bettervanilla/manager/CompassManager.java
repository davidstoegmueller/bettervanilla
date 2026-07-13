package com.daveestar.bettervanilla.manager;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.utils.Theme;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import net.md_5.bungee.api.ChatColor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CompassManager {
  private static final int _SCALE_LENGTH = 80;
  private static final int _ARROW_POSITION = _SCALE_LENGTH / 2;
  private static final String[] _DIRECTION_TRANSLATION_KEYS = {
      "compass-direction-north-short",
      "compass-direction-northeast-short",
      "compass-direction-east-short",
      "compass-direction-southeast-short",
      "compass-direction-south-short",
      "compass-direction-southwest-short",
      "compass-direction-west-short",
      "compass-direction-northwest-short"
  };
  // update interval in milliseconds (approx one server tick)
  private static final int _UPDATE_INTERVAL = 50;
  private static final char _FILL_CHARACTER = '·'; // enhanced visual fill character
  private static final String _ARROW_CHARACTER = "▲"; // enhanced arrow character without color
  private static final int _FILL_CHAR_AMOUNT = 20; // number of fill characters between directions

  private final Map<Player, BossBar> _activeCompass = new ConcurrentHashMap<>();

  private final Main _plugin;
  private SettingsManager _settingsManager;

  public CompassManager() {
    _plugin = Main.getInstance();

    // fetch settings manager before accessing player preferences
    _settingsManager = _plugin.getSettingsManager();

    _plugin.getServer().getOnlinePlayers().forEach(p -> {
      if (_settingsManager.getPlayerToggleCompass(p.getUniqueId())) {
        addPlayerToCompass(p);
      }
    });

    _startCompassUpdateTask();
  }

  public void initManagers() {
    _settingsManager = _plugin.getSettingsManager();
  }

  public void onPlayerJoined(Player p) {
    if (_settingsManager.getPlayerToggleCompass(p.getUniqueId())) {
      _plugin.getServer().getScheduler().runTask(_plugin, () -> addPlayerToCompass(p));
    }
  }

  public void onPlayerLeft(Player p) {
    BossBar bar = _activeCompass.remove(p);
    if (bar != null) {
      bar.removePlayer(p);
    }
  }

  public void destroy() {
    _activeCompass.values().forEach(BossBar::removeAll);
    _activeCompass.clear();
  }

  public Boolean checkPlayerActiveCompass(Player p) {
    return _activeCompass.containsKey(p);
  }

  public void addPlayerToCompass(Player p) {
    _activeCompass.computeIfAbsent(p, pl -> {
      BossBar compassBossBar = Bukkit.createBossBar("", BarColor.YELLOW, BarStyle.SOLID);
      compassBossBar.addPlayer(p);

      _settingsManager.setPlayerToggleCompass(p.getUniqueId(), true);

      return compassBossBar;
    });
  }

  public void removePlayerFromCompass(Player p) {
    BossBar compassBossBar = _activeCompass.remove(p);

    _settingsManager.setPlayerToggleCompass(p.getUniqueId(), false);

    if (compassBossBar != null) {
      compassBossBar.removePlayer(p);
    }
  }

  private void _startCompassUpdateTask() {
    AsyncScheduler scheduler = _plugin.getServer().getAsyncScheduler();

    scheduler.runAtFixedRate(_plugin, task -> {
      _activeCompass.forEach((pl, compassBossBar) -> _updateCompassDirection(pl, compassBossBar));
    }, 0, _UPDATE_INTERVAL, TimeUnit.MILLISECONDS);
  }

  private void _updateCompassDirection(Player p, BossBar compassBossBar) {
    float yaw = p.getLocation().toBlockLocation().getYaw();
    yaw = (yaw + 180) % 360; // adjust to align North and South correctly // normalize yaw to the 0-360 range

    String compassScale = _getDynamicCompassScale(p, yaw);
    compassBossBar.setTitle(compassScale);
  }

  private String _getDynamicCompassScale(Player viewer, double yaw) {
    String[] directionNames = new String[_DIRECTION_TRANSLATION_KEYS.length];
    StringBuilder templateBuilder = new StringBuilder();
    for (int directionIndex = 0; directionIndex < _DIRECTION_TRANSLATION_KEYS.length; directionIndex++) {
      String directionName = Main.tr(viewer, _DIRECTION_TRANSLATION_KEYS[directionIndex]);
      directionNames[directionIndex] = directionName;
      templateBuilder.append(directionName);
      for (int i = 0; i < _FILL_CHAR_AMOUNT; i++) {
        templateBuilder.append(_FILL_CHARACTER);
      }
    }

    String fullScaleTemplate = templateBuilder.toString();
    int fullScaleLength = fullScaleTemplate.length();
    String completeCompass = fullScaleTemplate + fullScaleTemplate;

    // calculate the starting point based on the yaw, centering the arrow position
    int startIndex = (int) Math.round((yaw / 360) * fullScaleLength) - _ARROW_POSITION;
    if (startIndex < 0) {
      startIndex += fullScaleLength;
    }

    // extract the visible portion of the compass
    String visibleCompass = completeCompass.substring(startIndex, startIndex + _SCALE_LENGTH);

    // insert the arrow at the center
    StringBuilder finalCompass = new StringBuilder(visibleCompass);
    finalCompass.replace(_ARROW_POSITION, _ARROW_POSITION + 1,
        Theme.textSymbol() + _ARROW_CHARACTER + ChatColor.RESET);

    // build the final string with colors applied
    StringBuilder coloredCompass = new StringBuilder();
    int currentIndex = 0;
    while (currentIndex < finalCompass.length()) {
      boolean matched = false;
      for (String directionName : directionNames) {
        if (finalCompass.indexOf(directionName, currentIndex) == currentIndex) {
          coloredCompass.append(Theme.highlight()).append(directionName).append(ChatColor.RESET);
          currentIndex += directionName.length();
          matched = true;
          break;
        }
      }
      if (!matched) {
        coloredCompass.append(finalCompass.charAt(currentIndex));
        currentIndex++;
      }
    }

    return coloredCompass.toString();
  }
}
