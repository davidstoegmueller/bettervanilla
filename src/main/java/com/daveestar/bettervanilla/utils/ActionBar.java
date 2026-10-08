package com.daveestar.bettervanilla.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;

import org.bukkit.scheduler.BukkitTask;
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

  private final Map<Player, BukkitTask> _actionBarTasks = new HashMap<>();
  private final Map<Player, String> _actionBarMessages = new HashMap<>();
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

    _updateActionBar(p, message);
  }

  public void startOverride(Player p, String message) {
    startOverride(p, message, Priority.HIGH);
  }

  public void startOverride(Player p, String message, Priority priority) {
    if (!_canOverride(p, priority)) {
      return;
    }

    _overridePriorities.put(p.getUniqueId(), priority);
    _updateActionBar(p, message);
  }

  public void destroy() {
    _actionBarTasks.values().forEach(BukkitTask::cancel);
    _actionBarTasks.clear();
    _actionBarMessages.clear();
    _overridePriorities.clear();
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

  private void _updateActionBar(Player p, String message) {
    String previous = _actionBarMessages.put(p, message);
    if (!message.equals(previous)) {
      _sendActionBarNow(p, message);
    }

    if (!_actionBarTasks.containsKey(p)) {
      BukkitTask task = Main.getInstance().getServer().getScheduler().runTaskTimer(Main.getInstance(), () -> {
        if (!p.isOnline()) {
          clearOverride(p);
          return;
        }
        _sendActionBarNow(p, _actionBarMessages.get(p));
      }, 40L, 40L);
      _actionBarTasks.put(p, task);
    }
  }

  private void _removeActionBarInternal(Player p) {
    BukkitTask task = _actionBarTasks.remove(p);
    if (task != null) {
      task.cancel();
    }
    _actionBarMessages.remove(p);
  }

  private boolean _isOverridden(Player p) {
    return _overridePriorities.containsKey(p.getUniqueId());
  }

  private boolean _canOverride(Player p, Priority priority) {
    Priority current = _overridePriorities.get(p.getUniqueId());
    return current == null || priority.getLevel() >= current.getLevel();
  }
}
