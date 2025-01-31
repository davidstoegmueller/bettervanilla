package com.daveestar.bettervanilla.events;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SettingsManager;

import net.md_5.bungee.api.ChatColor;

public class PreventEnd implements Listener {
  @EventHandler
  public void onPlayerPortal(PlayerPortalEvent event) {
    SettingsManager settingsManager = Main.getInstance().getSettingsManager();
    if (settingsManager.getToggleEnd()) {
      return;
    }

    // check if the portal leads to the end
    Location destination = event.getTo();

    if (destination != null && destination.getWorld() != null) {
      World.Environment environment = destination.getWorld().getEnvironment();

      if (environment == World.Environment.THE_END) {
        // cancel the portal event
        event.setCancelled(true);
        event.getPlayer().sendMessage(Main.getPrefix() + ChatColor.RED + "You are not allowed to enter 'The End'");
      }
    }
  }
}
