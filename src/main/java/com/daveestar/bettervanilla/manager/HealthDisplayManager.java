package com.daveestar.bettervanilla.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.Display;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SettingsManager;

import net.kyori.adventure.text.Component;

public class HealthDisplayManager {
  private static final float NORMAL_OFFSET = -0.25f;
  private static final float SNEAK_OFFSET = -0.35f;

  private final Main _plugin;
  private SettingsManager _settingsManager;
  private final Map<UUID, TextDisplay> _displays;
  private final Map<UUID, BukkitTask> _pendingUpdates;

  public HealthDisplayManager() {
    _plugin = Main.getInstance();
    _displays = new HashMap<>();
    _pendingUpdates = new HashMap<>();
  }

  public void initManagers() {
    _settingsManager = _plugin.getSettingsManager();
    if (_settingsManager.getNametagHealth()) {
      Bukkit.getOnlinePlayers().forEach(this::createDisplay);
    }
  }

  public void applyToAll(boolean enabled) {
    if (enabled) {
      Bukkit.getOnlinePlayers().forEach(this::createDisplay);
    } else {
      _displays.values().forEach(TextDisplay::remove);
      _displays.clear();
      _pendingUpdates.values().forEach(BukkitTask::cancel);
      _pendingUpdates.clear();
    }
  }

  public void onPlayerJoined(Player p) {
    if (_settingsManager.getNametagHealth()) {
      createDisplay(p);
    }
  }

  public void onPlayerLeft(Player p) {
    removeDisplay(p);
  }

  public void onHealthChange(Player p) {
    if (!_displays.containsKey(p.getUniqueId())) {
      return;
    }

    BukkitTask task = _pendingUpdates.remove(p.getUniqueId());
    if (task != null) {
      task.cancel();
    }

    task = Bukkit.getScheduler().runTaskLater(_plugin, () -> {
      TextDisplay display = _displays.get(p.getUniqueId());
      if (display != null) {
        display.text(Component.text(formatHealth(p)));
      }
      _pendingUpdates.remove(p.getUniqueId());
    }, 2L);

    _pendingUpdates.put(p.getUniqueId(), task);
  }

  public void onSneak(Player p, boolean sneaking) {
    TextDisplay display = _displays.get(p.getUniqueId());
    if (display == null) {
      return;
    }

    Transformation transformation = display.getTransformation();
    transformation.getTranslation().set(0f, sneaking ? SNEAK_OFFSET : NORMAL_OFFSET, 0f);
    display.setTransformation(transformation);
  }

  public void onGameModeChange(Player p, GameMode newMode) {
    if (newMode == GameMode.SPECTATOR) {
      removeDisplay(p);
    } else {
      if (_settingsManager.getNametagHealth()) {
        createDisplay(p);
      }
    }
  }

  public void onRespawn(Player p) {
    removeDisplay(p);
    if (_settingsManager.getNametagHealth()) {
      Bukkit.getScheduler().runTask(_plugin, () -> createDisplay(p));
    }
  }

  public void onWorldChange(Player p) {
    removeDisplay(p);
    if (_settingsManager.getNametagHealth()) {
      Bukkit.getScheduler().runTask(_plugin, () -> createDisplay(p));
    }
  }

  public void destroy() {
    applyToAll(false);
  }

  private void createDisplay(Player p) {
    if (p.getGameMode() == GameMode.SPECTATOR) {
      return;
    }
    if (_displays.containsKey(p.getUniqueId())) {
      return;
    }

    TextDisplay display = p.getWorld().spawn(p.getLocation(), TextDisplay.class, td -> {
      td.text(Component.text(formatHealth(p)));
      td.setBillboard(Display.Billboard.CENTER);
      td.setViewRange(32f);
      td.setShadowed(false);
      td.setSeeThrough(true);
      td.setBackgroundColor(null);
      Transformation t = td.getTransformation();
      t.getTranslation().set(0f, NORMAL_OFFSET, 0f);
      td.setTransformation(t);
    });
    p.addPassenger(display);
    _displays.put(p.getUniqueId(), display);
  }

  private void removeDisplay(Player p) {
    TextDisplay display = _displays.remove(p.getUniqueId());
    if (display != null) {
      display.remove();
    }
    BukkitTask task = _pendingUpdates.remove(p.getUniqueId());
    if (task != null) {
      task.cancel();
    }
  }

  private String formatHealth(Player p) {
    double hearts = p.getHealth() / 2.0;
    return String.format(java.util.Locale.US, "%.1f ❤", hearts);
  }
}

