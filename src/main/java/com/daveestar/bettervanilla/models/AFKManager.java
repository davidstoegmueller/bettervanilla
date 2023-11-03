package com.daveestar.bettervanilla.models;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.daveestar.bettervanilla.Main;

import net.md_5.bungee.api.ChatColor;

public class AFKManager {
  private HashMap<Player, Long> lastMovement;
  private HashMap<Player, Boolean> afkStates;
  private int MAX_AFK_TIME = 60000 * 10;

  public AFKManager() {
    this.lastMovement = new HashMap<Player, Long>();
    this.afkStates = new HashMap<Player, Boolean>();

    run();
  }

  public void playerJoined(Player p) {
    lastMovement.put(p, System.currentTimeMillis());
  }

  public void playerLeft(Player p) {
    lastMovement.remove(p);
  }

  public void playerMoved(Player p) {

    lastMovement.put(p, System.currentTimeMillis());

    setPlayerAFKState(p);

  }

  public boolean isAFK(Player p) {
    if (lastMovement.containsKey(p)) {
      long timeElapsed = System.currentTimeMillis() - lastMovement.get(p);

      if (timeElapsed >= MAX_AFK_TIME) {
        return true;
      }

    } else {
      lastMovement.put(p, System.currentTimeMillis());
    }

    return false;
  }

  public void checkAllPlayersAFKStatus() {
    for (Map.Entry<Player, Long> entry : lastMovement.entrySet()) {
      setPlayerAFKState(entry.getKey());
    }

    boolean allPlayersAFK = afkStates.values().stream().allMatch(entry -> entry == true);
    TimerManager timer = Main.getInstance().getTimerManager();

    if (Bukkit.getOnlinePlayers().size() > 0) {
      if (allPlayersAFK) {
        if (timer.isRunning()) {
          timer.setRunning(false);
        }
      } else {
        if (!timer.isRunning()) {
          timer.setRunning(true);
        }
      }
    }
  }

  public void setPlayerAFKState(Player p) {
    if (lastMovement.containsKey(p)) {

      boolean nowAFK = isAFK(p);

      if (afkStates.containsKey(p)) {

        boolean wasAFK = afkStates.get(p);

        if (wasAFK && !nowAFK) {
          p.sendMessage(Main.getPrefix() + "You are no longer AFK");
          p.setPlayerListName(ChatColor.YELLOW + "     " + p.getName() + "     ");
          afkStates.put(p, false);

          announceToOthers(p, false);

        } else if (!wasAFK && nowAFK) {
          p.sendMessage(Main.getPrefix() + "You are now AFK!");
          p.setPlayerListName("     " + ChatColor.GRAY + "[" + ChatColor.RED + "AFK" + ChatColor.GRAY + "] "
              + ChatColor.YELLOW + p.getName() + "     ");
          afkStates.put(p, true);

          announceToOthers(p, true);
        }

      } else {
        afkStates.put(p, nowAFK);
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

  private void run() {
    new BukkitRunnable() {
      @Override
      public void run() {
        checkAllPlayersAFKStatus();
      }
    }.runTaskTimer(Main.getInstance(), 0, 20);
  }
}
