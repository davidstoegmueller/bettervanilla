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
  private boolean running;
  private boolean runningOverride;
  private int time;

  private Config config;
  private FileConfiguration fileCfgn;

  public TimerManager(Config config) {
    this.config = config;
    this.fileCfgn = config.getFileCfgrn();

    this.running = fileCfgn.getBoolean("running");
    this.runningOverride = fileCfgn.getBoolean("runningOverride");
    this.time = fileCfgn.getInt("time");

    run();
  }

  public void checkAndSetTimerRunning(int amountPlayers) {
    if (isRunningOverride()) {
      if (amountPlayers > 0) {
        if (!isRunning()) {
          setRunning(true);
        }
      }

      if (amountPlayers == 0) {
        if (isRunning()) {
          setRunning(false);
        }
      }
    }
  }

  public boolean isRunning() {
    return running;
  }

  public boolean isRunningOverride() {
    return runningOverride;
  }

  public void setRunning(boolean running) {
    this.running = running;

    fileCfgn.set("running", running);
    config.save();
  }

  public void setRunningOverride(boolean running) {
    this.runningOverride = running;

    fileCfgn.set("runningOverride", running);
    config.save();
  }

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;

    fileCfgn.set("time", time);
    config.save();
  }

  public void displayTimerActionBar() {
    WaypointsManager waypointsManager = Main.getInstance().getWaypointsManager();
    for (Player p : Bukkit.getOnlinePlayers()) {
      if (!waypointsManager.checkPlayerActiveWaypointNavigation(p)
          && !waypointsManager.checkPlayerActiveToggleLocationNavigation(p)) {

        if (!isRunning()) {
          p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
              new TextComponent(
                  ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + "Paused " + ChatColor.GRAY.toString() + "("
                      + ChatColor.RED.toString() + convertTimeIntoDMS(getTime()) + ChatColor.GRAY.toString() + ")"));
          continue;
        }

        p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
            new TextComponent(ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + convertTimeIntoDMS(getTime())));
      }
    }
  }

  private void run() {
    new BukkitRunnable() {
      @Override
      public void run() {

        displayTimerActionBar();

        if (!isRunning()) {
          return;
        }

        setTime(getTime() + 1);
      }
    }.runTaskTimer(Main.getInstance(), 20, 20);
  }

  private String convertTimeIntoDMS(int time) {
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
