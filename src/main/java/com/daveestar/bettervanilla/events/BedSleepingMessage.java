package com.daveestar.bettervanilla.events;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.ActionBar;

import net.md_5.bungee.api.ChatColor;

public class BedSleepingMessage implements Listener {

  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final ActionBar _actionBar;
  private final Map<World, Set<UUID>> _sleepingPlayers = new HashMap<>();

  public BedSleepingMessage() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
    _actionBar = _plugin.getActionBar();
  }

  @EventHandler
  public void onPlayerBedEnter(PlayerBedEnterEvent e) {
    if (e.isCancelled()) {
      return;
    }

    Player p = e.getPlayer();

    Bukkit.getScheduler().runTask(_plugin, () -> {
      if (!p.isSleeping()) {
        return;
      }

      _getWorldSleepers(p.getWorld()).add(p.getUniqueId());
      _updateSleepingActionBar(p.getWorld());
    });
  }

  @EventHandler
  public void onPlayerBedLeave(PlayerBedLeaveEvent e) {
    Player p = e.getPlayer();

    _clearPlayer(p);
    Bukkit.getScheduler().runTask(_plugin, () -> _updateSleepingActionBar(p.getWorld()));
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent e) {
    _clearPlayer(e.getPlayer());
  }

  @EventHandler
  public void onPlayerKick(PlayerKickEvent e) {
    _clearPlayer(e.getPlayer());
  }

  private void _clearPlayer(Player p) {
    _removeSleepingPlayer(p);
    _actionBar.clearOverride(p);
  }

  private void _updateSleepingActionBar(World world) {
    int totalPlayers = (int) world.getPlayers().stream().filter(this::_countsForSleep).count();
    int sleepingPlayers = _cleanupAndCountSleepers(world);
    int percentage = _settingsManager.getPlayersSleepingPercentage();
    int requiredPlayers = _calculateRequiredPlayers(totalPlayers, percentage);

    String message = ChatColor.GRAY + "Sleeping: " + ChatColor.YELLOW + sleepingPlayers + ChatColor.GRAY + " of "
        + ChatColor.YELLOW + requiredPlayers + ChatColor.GRAY + " players required";

    for (UUID playerId : _getWorldSleepers(world)) {
      Player player = Bukkit.getPlayer(playerId);
      if (player != null && player.isSleeping()) {
        _actionBar.startOverride(player, message, ActionBar.Priority.HIGH);
      }
    }
  }

  private boolean _countsForSleep(Player p) {
    return p.getGameMode() != GameMode.SPECTATOR;
  }

  private int _calculateRequiredPlayers(int totalPlayers, int percentage) {
    if (totalPlayers <= 0) {
      return 0;
    }

    return (int) Math.ceil(totalPlayers * (percentage / 100.0));
  }

  private Set<UUID> _getWorldSleepers(World world) {
    return _sleepingPlayers.computeIfAbsent(world, key -> new HashSet<>());
  }

  private void _removeSleepingPlayer(Player p) {
    Set<UUID> sleepers = _sleepingPlayers.get(p.getWorld());
    if (sleepers != null) {
      sleepers.remove(p.getUniqueId());
      if (sleepers.isEmpty()) {
        _sleepingPlayers.remove(p.getWorld());
      }
    }
  }

  private int _cleanupAndCountSleepers(World world) {
    Set<UUID> sleepers = _getWorldSleepers(world);

    sleepers.removeIf(playerId -> {
      Player player = Bukkit.getPlayer(playerId);
      return player == null || player.getWorld() != world || !player.isSleeping();
    });

    return sleepers.size();
  }
}
