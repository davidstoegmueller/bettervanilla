package com.daveestar.bettervanilla.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.VanishManager;

public class VanishEvents implements Listener {
  private final VanishManager _vanishManager;

  public VanishEvents() {
    _vanishManager = Main.getInstance().getVanishManager();
  }

  @EventHandler(ignoreCancelled = true)
  public void onDamage(EntityDamageEvent e) {
    if (e.getEntity() instanceof Player p) {
      if (_vanishManager.isVanished(p)) {
        e.setCancelled(true);
      }
    }
  }
}
