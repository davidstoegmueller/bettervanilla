package com.daveestar.bettervanilla.events;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SettingsManager;

public class EndermanProtection implements Listener {
  private final SettingsManager _settingsManager;

  public EndermanProtection() {
    _settingsManager = Main.getInstance().getSettingsManager();
  }

  @EventHandler
  public void onEndermanChangeBlock(EntityChangeBlockEvent e) {
    if (e.getEntityType() == EntityType.ENDERMAN && !_settingsManager.getEndermanBlockSteal()) {
      e.setCancelled(true);
    }
  }
}
