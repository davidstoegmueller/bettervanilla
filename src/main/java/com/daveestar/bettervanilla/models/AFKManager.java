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
  private int _MAX_AFK_TIME = 1000 * 10; // max afk time 10 minutes

  public AFKManager() {
    this._lastMovement = new HashMap<Player, Long>();
    this._afkStates = new HashMap<Player, Boolean>();

    _run();
  }

  public void playerJoined(Player p) {
    _lastMovement.put(p, System.currentTimeMillis());
    _afkStates.put(p, false);
  }

  public void playerLeft(Player p) {
    _lastMovement.remove(p);
    _afkStates.remove(p);
  }

  public void playerMoved(Player p) {

    _lastMovement.put(p, System.currentTimeMillis());

    setPlayerAFKState(p);

  }

  public boolean isAFK(Player p) {
    if (_lastMovement.containsKey(p)) {
      long timeElapsed = System.currentTimeMillis() - _lastMovement.get(p);

      if (timeElapsed >= _MAX_AFK_TIME) {
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
    TimerManager timer = Main.getInstance().get_timerManager();

    if (Bukkit.getOnlinePlayers().size() > 0) {
      if (allPlayersAFK) {
        if (timer.is_running()) {
          timer.set_running(false);
        }
      } else {
        if (!timer.is_running() && timer.is_runningOverride()) {
          timer.set_running(true);
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
          p.setPlayerListName(ChatColor.YELLOW + "     " + p.getName() + "     ");
          _afkStates.put(p, false);

          announceToOthers(p, false);

        } else if (!wasAFK && nowAFK) {
          p.sendMessage(Main.getPrefix() + "You are now AFK!");
          p.setPlayerListName("     " + ChatColor.GRAY + "[" + ChatColor.RED + "AFK" + ChatColor.GRAY + "] "
              + ChatColor.YELLOW + p.getName() + "     ");
          _afkStates.put(p, true);

          announceToOthers(p, true);
        }

      } else {
        _afkStates.put(p, nowAFK);
      }
    }
  }

  public void announceToOthers(Player p, boolean isAFK) {
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

  private void _run() {
    new BukkitRunnable() {
      @Override
      public void run() {
        checkAllPlayersAFKStatus();
      }
    }.runTaskTimer(Main.getInstance(), 0, 20);
  }
}
