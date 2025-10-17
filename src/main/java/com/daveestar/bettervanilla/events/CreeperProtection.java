package com.daveestar.bettervanilla.events;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SettingsManager;

public class CreeperProtection implements Listener {
  private final SettingsManager _settingsManager;

  public CreeperProtection() {
    _settingsManager = Main.getInstance().getSettingsManager();
  }

  @EventHandler
  public void onCreeperExplode(EntityExplodeEvent e) {
    if (e.getEntity() == null || e.getEntity().getType() != EntityType.CREEPER) {
      return;
    }

    if (!_settingsManager.getCreeperBlockDamage()) {
      e.blockList().clear();
    }
  }

  @EventHandler
  public void onCreeperDamage(EntityDamageByEntityEvent e) {
    if (e.getDamager().getType() != EntityType.CREEPER) {
      return;
    }

    if (_settingsManager.getCreeperEntityDamage()) {
      return;
    }

    if (e.getEntity() instanceof Player) {
      return;
    }

    e.setCancelled(true);
  }
}
