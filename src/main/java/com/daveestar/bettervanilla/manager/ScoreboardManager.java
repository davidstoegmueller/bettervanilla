package com.daveestar.bettervanilla.manager;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.ScoreboardStat;

import net.md_5.bungee.api.ChatColor;

public class ScoreboardManager {
  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final TimerManager _timerManager;
  private final Map<Player, Scoreboard> _boards;

  public ScoreboardManager() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
    _timerManager = _plugin.getTimerManager();
    _boards = new HashMap<>();
  }

  public void initManagers() {
    _startUpdateTask();
  }

  public void onPlayerJoined(Player p) {
    if (_settingsManager.getScoreboardEnabled()) {
      _applyBoard(p);
    }
  }

  public void onPlayerLeft(Player p) {
    _boards.remove(p);
  }

  public void showScoreboardForAll() {
    Bukkit.getOnlinePlayers().forEach(this::_applyBoard);
  }

  public void hideScoreboardForAll() {
    Bukkit.getOnlinePlayers().forEach(p -> p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()));
    _boards.clear();
  }

  private void _applyBoard(Player p) {
    Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
    Objective obj = board.registerNewObjective("bv_stats", Criteria.DUMMY,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "STATS");
    obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    p.setScoreboard(board);
    _boards.put(p, board);
  }

  private void _startUpdateTask() {
    Bukkit.getScheduler().runTaskTimer(_plugin, () -> {
      if (!_settingsManager.getScoreboardEnabled())
        return;

      List<String> statNames = _settingsManager.getScoreboardStats();
      for (Player p : Bukkit.getOnlinePlayers()) {
        Scoreboard board = _boards.computeIfAbsent(p, pl -> {
          _applyBoard(pl);
          return pl.getScoreboard();
        });
        Objective obj = board.getObjective("bv_stats");
        if (obj == null) {
          obj = board.registerNewObjective("bv_stats", Criteria.DUMMY,
              ChatColor.YELLOW + "" + ChatColor.BOLD + "STATS");
          obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        for (String entry : new ArrayList<>(board.getEntries())) {
          board.resetScores(entry);
        }
        int score = statNames.size();
        for (String statName : statNames) {
          String line = _formatStat(p, statName);
          if (line != null) {
            obj.getScore(line).setScore(score--);
          }
        }
      }
    }, 0L, 20L);
  }

  private String _formatStat(Player p, String statName) {
    ScoreboardStat stat;
    try {
      stat = ScoreboardStat.valueOf(statName);
    } catch (IllegalArgumentException ex) {
      return null;
    }

    switch (stat) {
      case PLAYTIME:
        return ChatColor.YELLOW + "Playtime: " + ChatColor.GRAY
            + _timerManager.formatTime(_timerManager.getPlayTime(p));
      case AFKTIME:
        return ChatColor.YELLOW + "AFK: " + ChatColor.GRAY
            + _timerManager.formatTime(_timerManager.getAFKTime(p));
      case INGAMETIME:
        int total = _timerManager.getPlayTime(p) + _timerManager.getAFKTime(p);
        return ChatColor.YELLOW + "Total: " + ChatColor.GRAY + _timerManager.formatTime(total);
      case PLAYERKILLS:
        return ChatColor.YELLOW + "Kills: " + ChatColor.GRAY + p.getStatistic(Statistic.PLAYER_KILLS);
      case MOBKILLS:
        return ChatColor.YELLOW + "Mob Kills: " + ChatColor.GRAY + p.getStatistic(Statistic.MOB_KILLS);
      case DEATHS:
        return ChatColor.YELLOW + "Deaths: " + ChatColor.GRAY + p.getStatistic(Statistic.DEATHS);
      case ONLINE:
        return ChatColor.YELLOW + "Online: " + ChatColor.GRAY + Bukkit.getOnlinePlayers().size();
      case SWIMDISTANCE:
        return ChatColor.YELLOW + "Swim: " + ChatColor.GRAY + _formatDistance(p.getStatistic(Statistic.SWIM_ONE_CM));
      case WALKDISTANCE:
        return ChatColor.YELLOW + "Walk: " + ChatColor.GRAY + _formatDistance(p.getStatistic(Statistic.WALK_ONE_CM));
      case TOTALDISTANCE:
        int dist = p.getStatistic(Statistic.SWIM_ONE_CM) + p.getStatistic(Statistic.WALK_ONE_CM);
        return ChatColor.YELLOW + "Distance: " + ChatColor.GRAY + _formatDistance(dist);
      default:
        return null;
    }
  }

  private String _formatDistance(int cm) {
    int meters = cm / 100;
    return meters + "m";
  }
}
