package com.daveestar.bettervanilla.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;

public class ActionBar {
  public enum Priority {
    LOW(0),
    NORMAL(1),
    HIGH(2),
    CRITICAL(3);

    private final int _level;

    Priority(int level) {
      _level = level;
    }

    public int getLevel() {
      return _level;
    }
  }

  private final Map<Player, ScheduledTask> _actionBarTasks = new HashMap<>();
  private final Map<UUID, Priority> _overridePriorities = new HashMap<>();

  public void sendActionBarOnce(Player p, String message) {
    if (_isOverridden(p)) {
      return;
    }

    _sendActionBarNow(p, message);
  }

  public void sendActionBar(Player p, String message) {
    if (_isOverridden(p)) {
      return;
    }

    removeActionBar(p);

    AsyncScheduler scheduler = Main.getInstance().getServer().getAsyncScheduler();
    ScheduledTask schduledTask = scheduler.runAtFixedRate(Main.getInstance(), task -> {
      _sendActionBarNow(p, message);
    }, 0, 2, TimeUnit.SECONDS);

    _actionBarTasks.put(p, schduledTask);
  }

  public void startOverride(Player p, String message) {
    startOverride(p, message, Priority.HIGH);
  }

  public void startOverride(Player p, String message, Priority priority) {
    if (!_canOverride(p, priority)) {
      return;
    }

    _overridePriorities.put(p.getUniqueId(), priority);
    _removeActionBarInternal(p);

    AsyncScheduler scheduler = Main.getInstance().getServer().getAsyncScheduler();
    ScheduledTask schduledTask = scheduler.runAtFixedRate(Main.getInstance(), task -> {
      _sendActionBarNow(p, message);
    }, 0, 2, TimeUnit.SECONDS);

    _actionBarTasks.put(p, schduledTask);
  }

  public void clearOverride(Player p) {
    _overridePriorities.remove(p.getUniqueId());
    _removeActionBarInternal(p);
  }

  public void removeActionBar(Player p) {
    if (_isOverridden(p)) {
      return;
    }

    _removeActionBarInternal(p);
  }

  private void _sendActionBarNow(Player p, String message) {
    p.sendActionBar(Component.text(message));
  }

  private void _removeActionBarInternal(Player p) {
    if (_actionBarTasks.containsKey(p)) {
      _actionBarTasks.get(p).cancel();
      _actionBarTasks.remove(p);
    }
  }

  private boolean _isOverridden(Player p) {
    return _overridePriorities.containsKey(p.getUniqueId());
  }

  private boolean _canOverride(Player p, Priority priority) {
    Priority current = _overridePriorities.get(p.getUniqueId());
    return current == null || priority.getLevel() >= current.getLevel();
  }
}
