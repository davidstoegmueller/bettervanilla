package com.daveestar.bettervanilla.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.daveestar.bettervanilla.Main;

/**
 * Handles spawning and managing invisible armor stands that allow players to
 * sit.
 */
public class SittingManager implements Listener {
  private final Main _plugin;
  private final Map<UUID, ArmorStand> _sittingPlayers = new HashMap<>();

  public SittingManager(JavaPlugin plugin) {
    _plugin = Main.getInstance();
    Bukkit.getPluginManager().registerEvents(this, _plugin);
  }

  public boolean isSitting(Player player) {
    return player != null && _sittingPlayers.containsKey(player.getUniqueId());
  }

  public void sitPlayer(Player player, Location location) {
    if (player == null || location == null) {
      return;
    }

    World world = location.getWorld();
    if (world == null) {
      return;
    }

    // ensure the player is not already sitting on an armor stand
    unsitPlayer(player);

    Location seatLocation = location.clone();
    ArmorStand armorStand = world.spawn(seatLocation, ArmorStand.class);
    armorStand.setVisible(false);
    armorStand.setGravity(false);
    armorStand.setMarker(true);
    armorStand.setBasePlate(false);
    armorStand.setSmall(true);
    armorStand.setInvulnerable(true);
    armorStand.setSilent(true);
    armorStand.setPersistent(false);
    armorStand.setRotation(seatLocation.getYaw(), seatLocation.getPitch());

    armorStand.addPassenger(player);
    _sittingPlayers.put(player.getUniqueId(), armorStand);
  }

  public void unsitPlayer(Player player) {
    if (player == null) {
      return;
    }

    ArmorStand armorStand = _sittingPlayers.remove(player.getUniqueId());
    if (armorStand == null) {
      return;
    }

    if (player.isInsideVehicle()) {
      player.leaveVehicle();
    }

    armorStand.remove();
  }

  public void destroy() {
    Set<UUID> trackedPlayers = new HashSet<>(_sittingPlayers.keySet());
    for (UUID uuid : trackedPlayers) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null) {
        unsitPlayer(player);
        continue;
      }

      ArmorStand armorStand = _sittingPlayers.remove(uuid);
      if (armorStand != null) {
        for (Entity passenger : armorStand.getPassengers()) {
          passenger.leaveVehicle();
        }
        armorStand.remove();
      }
    }
  }

  @EventHandler
  public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
    if (!event.isSneaking()) {
      return;
    }

    unsitPlayer(event.getPlayer());
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    unsitPlayer(event.getPlayer());
  }

  @EventHandler
  public void onPlayerKick(PlayerKickEvent event) {
    unsitPlayer(event.getPlayer());
  }
}
