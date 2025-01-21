package com.daveestar.bettervanilla.models;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.daveestar.bettervanilla.Main;

import net.md_5.bungee.api.ChatColor;

public class AFKManager {
  private HashMap<Player, Long> _lastMovement;
  private HashMap<Player, Boolean> _afkStates;

  private final SettingsManager _settingsManager;

  public AFKManager() {
    this._settingsManager = Main.getInstance().getSettingsManager();

    this._lastMovement = new HashMap<Player, Long>();
    this._afkStates = new HashMap<Player, Boolean>();

    _startAFKTask();
  }

  public void onPlayerJoined(Player p) {
    _lastMovement.put(p, System.currentTimeMillis());
    _afkStates.put(p, false);
  }

  public void onPlayerLeft(Player p) {
    _lastMovement.remove(p);
    _afkStates.remove(p);
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
    TimerManager timer = Main.getInstance().getTimerManager();

    if (Bukkit.getOnlinePlayers().size() > 0) {
      if (allPlayersAFK) {
        if (timer.isRunning()) {
          timer.setRunning(false);
        }
      } else {
        if (!timer.isRunning() && timer.isRunningOverride()) {
          timer.setRunning(true);
        }
      }
    }

  }

  public void setPlayerAFKState(Player p) {
    if (_lastMovement.containsKey(p)) {
      boolean nowAFK = isAFK(p);

      if (_afkStates.containsKey(p)) {
        boolean wasAFK = _afkStates.get(p);

        if (wasAFK && !nowAFK) {
          p.sendMessage(Main.getPrefix() + "You are no longer AFK");
          p.setPlayerListName(ChatColor.RED + " » " + ChatColor.YELLOW + p.getName());
          _afkStates.put(p, false);

          announceAFKToOthers(p, false);
        } else if (!wasAFK && nowAFK) {
          p.sendMessage(Main.getPrefix() + "You are now AFK!");
          p.setPlayerListName(ChatColor.GRAY + "[" + ChatColor.RED + "AFK" + ChatColor.GRAY + "] "
              + ChatColor.YELLOW + p.getName());
          _afkStates.put(p, true);

          announceAFKToOthers(p, true);
        }
      } else {
        _afkStates.put(p, nowAFK);
      }
    }
  }

  public void announceAFKToOthers(Player p, boolean isAFK) {
    Bukkit.getServer().getOnlinePlayers().stream()
        .forEach(player -> {
          if (!player.equals(p)) {
            if (isAFK) {
              player.sendMessage(Main.getPrefix() + ChatColor.YELLOW + p.getName() + ChatColor.GRAY + " is now AFK.");
            } else {
              player.sendMessage(
                  Main.getPrefix() + ChatColor.YELLOW + p.getName() + ChatColor.GRAY + " is no longer AFK.");
            }
          }
        });
  }

  private int _getAFKTime() {
    int afkTimeInMinutes = _settingsManager.getAFKTime();
    return 1000 * 60 * afkTimeInMinutes;
  }

  private void _startAFKTask() {
    new BukkitRunnable() {
      @Override
      public void run() {
        checkAllPlayersAFKStatus();
      }
    }.runTaskTimer(Main.getInstance(), 0, 20);
  }
}
