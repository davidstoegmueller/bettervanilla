package com.daveestar.bettervanilla.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.HealthDisplayManager;

public class HealthDisplayEvents implements Listener {
  private final HealthDisplayManager _manager;

  public HealthDisplayEvents() {
    _manager = Main.getInstance().getHealthDisplayManager();
  }

  @EventHandler
  public void onDamage(EntityDamageEvent e) {
    if (e.getEntity() instanceof Player p) {
      _manager.onHealthChange(p);
    }
  }

  @EventHandler
  public void onRegain(EntityRegainHealthEvent e) {
    if (e.getEntity() instanceof Player p) {
      _manager.onHealthChange(p);
    }
  }

  @EventHandler
  public void onSneak(PlayerToggleSneakEvent e) {
    _manager.onSneak(e.getPlayer(), e.isSneaking());
  }

  @EventHandler
  public void onGameMode(PlayerGameModeChangeEvent e) {
    _manager.onGameModeChange(e.getPlayer(), e.getNewGameMode());
  }

  @EventHandler
  public void onRespawn(PlayerRespawnEvent e) {
    _manager.onRespawn(e.getPlayer());
  }

  @EventHandler
  public void onWorldChange(PlayerChangedWorldEvent e) {
    _manager.onWorldChange(e.getPlayer());
  }
}

