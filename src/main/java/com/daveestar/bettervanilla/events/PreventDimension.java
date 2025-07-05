package com.daveestar.bettervanilla.events;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SettingsManager;

import net.md_5.bungee.api.ChatColor;

public class PreventDimension implements Listener {

  private final Main _plugin;
  private final SettingsManager _settingsManager;

  public PreventDimension() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
  }

  @EventHandler
  public void onPlayerPortal(PlayerPortalEvent event) {
    // get the destination location of the portal
    Location destination = event.getTo();

    if (destination != null && destination.getWorld() != null) {
      World.Environment destinationEnvironment = destination.getWorld().getEnvironment();

      if (destinationEnvironment == World.Environment.THE_END && !_settingsManager.getEnableEnd()) {
        // cancel the portal event
        event.setCancelled(true);
        event.getPlayer().sendMessage(Main.getPrefix() + ChatColor.RED + "You are not allowed to enter 'The End' yet!");
      }

      if (destinationEnvironment == World.Environment.NETHER && !_settingsManager.getEnableNether()) {
        // cancel the portal event
        event.setCancelled(true);
        event.getPlayer()
            .sendMessage(Main.getPrefix() + ChatColor.RED + "You are not allowed to enter 'The Nether' yet!");
      }
    }
  }
}
