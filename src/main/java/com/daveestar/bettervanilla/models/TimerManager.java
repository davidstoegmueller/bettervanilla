package com.daveestar.bettervanilla.models;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.utils.Config;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class TimerManager {
  private boolean _running;
  private boolean _runningOverride;
  private int _time;

  private Config _config;
  private FileConfiguration _fileCfgn;

  public TimerManager(Config config) {
    this._config = config;
    this._fileCfgn = config.getFileCfgrn();

    this._running = _fileCfgn.getBoolean("running");
    this._runningOverride = _fileCfgn.getBoolean("runningOverride");
    this._time = _fileCfgn.getInt("time");

    _run();
  }

  public void checkAndSetTimerRunning(int amountPlayers) {
    if (is_runningOverride()) {
      if (amountPlayers > 0) {
        if (!is_running()) {
          set_running(true);
        }
      }

      if (amountPlayers == 0) {
        if (is_running()) {
          set_running(false);
        }
      }
    }
  }

  public boolean is_running() {
    return _running;
  }

  public boolean is_runningOverride() {
    return _runningOverride;
  }

  public void set_running(boolean running) {
    this._running = running;

    _fileCfgn.set("running", running);
    _config.save();
  }

  public void set_runningOverride(boolean running) {
    this._runningOverride = running;

    _fileCfgn.set("runningOverride", running);
    _config.save();
  }

  public int get_time() {
    return _time;
  }

  public void set_time(int time) {
    this._time = time;

    _fileCfgn.set("time", time);
    _config.save();
  }

  public void displayTimerActionBar() {
    WaypointsManager waypointsManager = Main.getInstance().get_waypointsManager();
    for (Player p : Bukkit.getOnlinePlayers()) {
      if (!waypointsManager.checkPlayerActiveWaypointNavigation(p)
          && !waypointsManager.checkPlayerActiveToggleLocationNavigation(p)) {

        if (!is_running()) {
          p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
              new TextComponent(
                  ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + "Paused " + ChatColor.GRAY.toString() + "("
                      + ChatColor.RED.toString() + _convertTimeIntoDMS(get_time()) + ChatColor.GRAY.toString() + ")"));
          continue;
        }

        p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
            new TextComponent(
                ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + _convertTimeIntoDMS(get_time())));
      }
    }
  }

  private void _run() {
    new BukkitRunnable() {
      @Override
      public void run() {

        displayTimerActionBar();

        if (!is_running()) {
          return;
        }

        set_time(get_time() + 1);
      }
    }.runTaskTimer(Main.getInstance(), 20, 20);
  }

  private String _convertTimeIntoDMS(int time) {
    if (time < 0) {
      return "error: contact dev";
    }

    int days = time / (24 * 60 * 60);
    int remainingSeconds = time % (24 * 60 * 60);
    int hours = remainingSeconds / 3600;
    int minutes = (remainingSeconds % 3600) / 60;
    int remainingSecs = remainingSeconds % 60;

    StringBuilder formattedTime = new StringBuilder();

    if (days > 0) {
      formattedTime.append(days).append("d ");
    }

    if (days > 0 || hours > 0) {
      formattedTime.append(hours).append("h ");
    }

    if (days > 0 || hours > 0 || minutes > 0) {
      formattedTime.append(minutes).append("m ");
    }

    formattedTime.append(remainingSecs).append("s");

    return formattedTime.toString().trim();
  }
}
