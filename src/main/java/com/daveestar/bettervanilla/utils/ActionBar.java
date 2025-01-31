package com.daveestar.bettervanilla.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.daveestar.bettervanilla.Main;

import net.kyori.adventure.text.Component;

public class ActionBar {
  private final Map<Player, BukkitTask> _actionBarTasks = new HashMap<>();

  public void sendActionBarOnce(Player p, String message) {
    p.sendActionBar(Component.text(message));
  }

  public void sendActionBar(Player p, String message) {
    this.removeActionBar(p);

    BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(
        Main.getInstance(),
        () -> this.sendActionBarOnce(p, message),
        0, 40L); // update every 2 seconds to make sure the actionbar doesnt dissappear

    _actionBarTasks.put(p, task);
  }

  public void removeActionBar(Player player) {
    if (_actionBarTasks.containsKey(player)) {
      _actionBarTasks.get(player).cancel();
      _actionBarTasks.remove(player);
    }
  }
}
