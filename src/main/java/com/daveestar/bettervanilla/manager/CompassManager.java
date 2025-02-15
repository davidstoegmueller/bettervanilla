package com.daveestar.bettervanilla.manager;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CompassManager {
  private static final int _SCALE_LENGTH = 80;
  private static final int _ARROW_POSITION = _SCALE_LENGTH / 2;
  private static final ChatColor _DIRECTION_COLOR = ChatColor.YELLOW; // static color for all directions
  private static final DirectionInfo[] _DIRECTIONS = {
      new DirectionInfo("N"), // N
      new DirectionInfo("NE"), // NE
      new DirectionInfo("E"), // E
      new DirectionInfo("SE"), // SE
      new DirectionInfo("S"), // S
      new DirectionInfo("SW"), // SW
      new DirectionInfo("W"), // W
      new DirectionInfo("NW") // NW
  }; // directions with text only
  private static final int _UPDATE_INTERVAL = 1; // tick interval for compass updates
  private static final char _FILL_CHARACTER = '·'; // enhanced visual fill character
  private static final String _ARROW_CHARACTER = "▲"; // enhanced arrow character without color
  private static final int _FILL_CHAR_AMOUNT = 20; // number of fill characters between directions

  private final Map<Player, BossBar> _activeCompass = new HashMap<>();

  private final Main _plugin;
  private SettingsManager _settingsManager;

  public CompassManager() {
    _plugin = Main.getInstance();

    _plugin.getServer().getOnlinePlayers().forEach(p -> {
      if (_settingsManager.getToggleCompass(p)) {
        addPlayerToCompass(p);
      }
    });

    _startCompassUpdateTask();
  }

  public void initManagers() {
    _settingsManager = _plugin.getSettingsManager();
  }

  public void destroy() {
    _activeCompass.values().forEach(BossBar::removeAll);
    _activeCompass.clear();
  }

  public Boolean checkPlayerActiveCompass(Player p) {
    return _activeCompass.containsKey(p);
  }

  public void addPlayerToCompass(Player player) {
    _activeCompass.computeIfAbsent(player, p -> {
      BossBar compassBossBar = Bukkit.createBossBar("", BarColor.YELLOW, BarStyle.SOLID);
      compassBossBar.addPlayer(player);

      _settingsManager.setToggleCompass(player, true);

      return compassBossBar;
    });
  }

  public void removePlayerFromCompass(Player p) {
    BossBar compassBossBar = _activeCompass.remove(p);

    _settingsManager.setToggleCompass(p, false);

    if (compassBossBar != null) {
      compassBossBar.removePlayer(p);
    }
  }

  private void _startCompassUpdateTask() {
    AsyncScheduler scheduler = _plugin.getServer().getAsyncScheduler();

    scheduler.runAtFixedRate(_plugin, task -> {
      _activeCompass.forEach((player, compassBossBar) -> _updateCompassDirection(player, compassBossBar));
    }, 0, _UPDATE_INTERVAL, TimeUnit.MILLISECONDS);
  }

  private void _updateCompassDirection(Player p, BossBar compassBossBar) {
    float yaw = p.getLocation().toBlockLocation().getYaw();
    yaw = (yaw + 180) % 360; // adjust to align North and South correctly // normalize yaw to the 0-360 range

    String compassScale = _getDynamicCompassScale(yaw);
    compassBossBar.setTitle(compassScale);
  }

  private String _getDynamicCompassScale(double yaw) {
    StringBuilder fullScale = new StringBuilder();
    for (DirectionInfo direction : _DIRECTIONS) {
      fullScale.append(direction._name);
      for (int i = 0; i < _FILL_CHAR_AMOUNT; i++) {
        fullScale.append(_FILL_CHARACTER);
      }
    }

    // create a full rotation of the compass and wrap around if needed
    String completeCompass = fullScale.toString() + fullScale.toString();

    // calculate the starting point based on the yaw, centering the arrow position
    int startIndex = (int) Math.round((yaw / 360) * fullScale.length()) - _ARROW_POSITION;
    if (startIndex < 0) {
      startIndex += fullScale.length();
    }

    // extract the visible portion of the compass
    String visibleCompass = completeCompass.substring(startIndex, startIndex + _SCALE_LENGTH);

    // insert the arrow at the center
    StringBuilder finalCompass = new StringBuilder(visibleCompass);
    finalCompass.replace(_ARROW_POSITION, _ARROW_POSITION + 1, ChatColor.RED + _ARROW_CHARACTER + ChatColor.RESET);

    // build the final string with colors applied
    StringBuilder coloredCompass = new StringBuilder();
    int currentIndex = 0;
    while (currentIndex < finalCompass.length()) {
      boolean matched = false;
      for (DirectionInfo direction : _DIRECTIONS) {
        if (finalCompass.indexOf(direction._name, currentIndex) == currentIndex) {
          // apply static color to the direction name
          coloredCompass.append(_DIRECTION_COLOR).append(direction._name).append(ChatColor.RESET);
          currentIndex += direction._name.length();
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

  private static class DirectionInfo {
    public String _name;

    DirectionInfo(String name) {
      _name = name;
    }
  }
}
