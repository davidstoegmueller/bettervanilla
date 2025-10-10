package com.daveestar.bettervanilla.manager;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.daveestar.bettervanilla.Main;

import net.md_5.bungee.api.ChatColor;

public class AFKManager {

  private final Main _plugin;

  private HashMap<Player, Long> _lastMovement;
  private HashMap<Player, Boolean> _afkStates;

  private TimerManager _timerManager;
  private SettingsManager _settingsManager;
  private Team _afkTeam;
  private TabListManager _tabListManager;

  public AFKManager() {
    _plugin = Main.getInstance();

    _lastMovement = new HashMap<Player, Long>();
    _afkStates = new HashMap<Player, Boolean>();
  }

  public void initManagers() {
    _timerManager = _plugin.getTimerManager();
    _settingsManager = _plugin.getSettingsManager();
    _tabListManager = _plugin.getTabListManager();

    // prepare scoreboard team used to disable collisions while AFK
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    if (manager != null) {
      Scoreboard board = manager.getMainScoreboard();
      Team team = board.getTeam("bv_afk");
      if (team == null) {
        team = board.registerNewTeam("bv_afk");
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
      }
      _afkTeam = team;
    }
  }

  public void onPlayerJoined(Player p) {
    _lastMovement.put(p, System.currentTimeMillis());
    _afkStates.put(p, false);

    // ensure normal state on join
    p.setInvulnerable(false);
    p.setCollidable(true);
    if (_afkTeam != null) {
      _afkTeam.removeEntry(p.getName());
    }

    _tabListManager.refreshPlayerListEntry(p);
  }

  public void onPlayerLeft(Player p) {
    _lastMovement.remove(p);
    _afkStates.remove(p);

    // reset any invulnerability or collision changes
    p.setInvulnerable(false);
    p.setCollidable(true);
    if (_afkTeam != null) {
      _afkTeam.removeEntry(p.getName());
    }
  }

  public void onPlayerMoved(Player p) {
    _lastMovement.put(p, System.currentTimeMillis());
    setPlayerAFKState(p);
  }

  public boolean isAFK(Player p) {
    if (_lastMovement.containsKey(p)) {
      long timeElapsed = System.currentTimeMillis() - _lastMovement.get(p);

      if (timeElapsed >= _getAFKTime()) {
        return true;
      }

    } else {
      _lastMovement.put(p, System.currentTimeMillis());
    }

    return false;
  }

  public void checkAllPlayersAFKStatus() {
    for (Map.Entry<Player, Long> entry : _lastMovement.entrySet()) {
      setPlayerAFKState(entry.getKey());
    }

    boolean allPlayersAFK = _afkStates.values().stream().allMatch(entry -> entry == true);
    if (Bukkit.getOnlinePlayers().size() > 0) {
      if (allPlayersAFK) {
        if (_timerManager.isRunning()) {
          _timerManager.setRunning(false);
        }
      } else {
        if (!_timerManager.isRunning() && _timerManager.isRunningOverride()) {
          _timerManager.setRunning(true);
        }
      }
    }

  }

  public void setPlayerAFKState(Player p) {
    if (!_lastMovement.containsKey(p)) {
      return;
    }

    boolean nowAFK = isAFK(p);
    boolean updated = false;

    if (_afkStates.containsKey(p)) {
      boolean wasAFK = _afkStates.get(p);

      if (wasAFK && !nowAFK) {
        p.sendMessage(Main.getPrefix() + "You are no longer AFK");
        _afkStates.put(p, false);
        updated = true;

        if (_settingsManager.getAFKProtection()) {
          p.setInvulnerable(false);
          p.setCollidable(true);
          if (_afkTeam != null) {
            _afkTeam.removeEntry(p.getName());
          }
        }

        announceAFKToOthers(p, false);
      } else if (!wasAFK && nowAFK) {
        p.sendMessage(Main.getPrefix() + "You are now AFK!");
        _afkStates.put(p, true);
        updated = true;

        if (_settingsManager.getAFKProtection()) {
          p.setInvulnerable(true);
          p.setCollidable(false);
          if (_afkTeam != null) {
            _afkTeam.addEntry(p.getName());
          }
        }

        announceAFKToOthers(p, true);
      }
    } else {
      _afkStates.put(p, nowAFK);
      updated = true;
    }

    if (updated && _tabListManager != null) {
      _tabListManager.refreshPlayerListEntry(p);
    }
  }

  public void announceAFKToOthers(Player targetPlayer, boolean isAFK) {
    _plugin.getServer().getOnlinePlayers().stream()
        .forEach(player -> {
          if (!player.equals(targetPlayer)) {
            if (isAFK) {
              player.sendMessage(
                  Main.getPrefix() + ChatColor.YELLOW + targetPlayer.getName() + ChatColor.GRAY + " is now AFK.");
            } else {
              player.sendMessage(
                  Main.getPrefix() + ChatColor.YELLOW + targetPlayer.getName() + ChatColor.GRAY + " is no longer AFK.");
            }
          }
        });
  }

  public void applyProtectionToAFKPlayers(boolean enabled) {
    _afkStates.forEach((player, isAFK) -> {
      if (isAFK) {
        if (enabled) {
          player.setInvulnerable(true);
          player.setCollidable(false);

          if (_afkTeam != null) {
            _afkTeam.addEntry(player.getName());
          }
        } else {
          player.setInvulnerable(false);
          player.setCollidable(true);

          if (_afkTeam != null) {
            _afkTeam.removeEntry(player.getName());
          }
        }
      }
    });
  }

  public boolean isPlayerMarkedAFK(Player p) {
    return _afkStates.getOrDefault(p, false);
  }

  private int _getAFKTime() {
    int afkTimeInMinutes = _settingsManager.getAFKTime();
    return 1000 * 60 * afkTimeInMinutes;
  }

}
