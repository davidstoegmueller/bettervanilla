package com.daveestar.bettervanilla.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;

public class ActionBar {
  private final Map<Player, ScheduledTask> _actionBarTasks = new HashMap<>();

  public void sendActionBarOnce(Player p, String message) {
    p.sendActionBar(Component.text(message));
  }

  public void sendActionBar(Player p, String message) {
    removeActionBar(p);

    AsyncScheduler scheduler = Main.getInstance().getServer().getAsyncScheduler();
    ScheduledTask schduledTask = scheduler.runAtFixedRate(Main.getInstance(), task -> {
      sendActionBarOnce(p, message);
    }, 0, 2, TimeUnit.SECONDS);

    _actionBarTasks.put(p, schduledTask);
  }

  public void removeActionBar(Player p) {
    if (_actionBarTasks.containsKey(p)) {
      _actionBarTasks.get(p).cancel();
      _actionBarTasks.remove(p);
    }
  }
}
