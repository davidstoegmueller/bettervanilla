package com.daveestar.bettervanilla.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.daveestar.bettervanilla.Main;

import net.kyori.adventure.text.Component;

public class HealthDisplayManager {
  private final SettingsManager _settingsManager;

  public HealthDisplayManager() {
    _settingsManager = Main.getInstance().getSettingsManager();
  }

  public void applySettingToAllPlayers() {
    if (_settingsManager.getHealthDisplay()) {
      enable();
    } else {
      disable();
    }
  }

  public void enable() {
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    if (manager == null) {
      return;
    }
    Scoreboard board = manager.getMainScoreboard();
    Objective obj = board.getObjective("bv_health");
    if (obj == null) {
      obj = board.registerNewObjective("bv_health", Criteria.HEALTH, Component.text("Health"));
    }
    obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
  }

  public void disable() {
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    if (manager == null) {
      return;
    }
    Scoreboard board = manager.getMainScoreboard();
    Objective obj = board.getObjective("bv_health");
    if (obj != null) {
      obj.unregister();
    }
  }

  public void onPlayerJoin(Player player) {
    if (!_settingsManager.getHealthDisplay()) {
      return;
    }
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    if (manager == null) {
      return;
    }
    Scoreboard board = manager.getMainScoreboard();
    Objective obj = board.getObjective("bv_health");
    if (obj != null) {
      player.setScoreboard(board);
    }
  }
}

