package com.daveestar.bettervanilla.events;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.models.NavigationManager;
import com.daveestar.bettervanilla.models.SettingsManager;
import com.daveestar.bettervanilla.utils.ActionBarManager;
import com.daveestar.bettervanilla.utils.NavigationData;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerMove implements Listener {
  @EventHandler
  public void onPlayerMove(PlayerMoveEvent e) {
    Player p = (Player) e.getPlayer();

    _handleNavigationPlayerMove(p);
    _handleLocationPlayerMove(p);
    _handleAFKPlayerMove(p);
  }

  private void _handleAFKPlayerMove(Player p) {
    Main.getInstance().getAFKManager().playerMoved(p);
  }

  private void _handleNavigationPlayerMove(Player p) {
    NavigationManager navigationManager = Main.getInstance().getNavigationManager();

    if (navigationManager.checkActiveNavigation(p)) {
      NavigationData navigationData = navigationManager.getActiveNavigation(p);

      Location targetLocation = navigationData.getLocation();
      Location playerLocation = p.getLocation();

      // check if the target world and player world name is different and abort
      // navigation
      if (targetLocation.getWorld().getName() != playerLocation.getWorld().getName()) {
        navigationManager.stopNavigation(p);
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Your navigation has been canceled due to world change!");

        return;
      }

      // if the player is in range of the target location we abort the navigation
      if (p.getLocation().distance(navigationData.getLocation()) <= 25) {
        navigationManager.stopNavigation(p);
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
            ChatColor.YELLOW + "" + ChatColor.BOLD + navigationData.getName() +
                ChatColor.GRAY
                + " is in a range of 25 blocks!"));

        return;
      }

      navigationManager.updateNavigation(p, navigationData);
    }
  }

  private void _handleLocationPlayerMove(Player p) {
    SettingsManager settingsManager = Main.getInstance().getSettingsManager();
    ActionBarManager actionBarManager = Main.getInstance().getActionBarManager();

    if (settingsManager.getToggleLocation(p)) {
      Biome playerBiome = p.getWorld().getBiome(p.getLocation());

      String locationText = ChatColor.YELLOW + "X: "
          + ChatColor.GRAY
          + p.getLocation().getBlockX() + ChatColor.YELLOW
          + " Y: " + ChatColor.GRAY + p.getLocation().getBlockY() + ChatColor.YELLOW +
          " Z: " + ChatColor.GRAY
          + p.getLocation().getBlockZ() + ChatColor.RED + ChatColor.BOLD + " » "
          + ChatColor.GRAY + playerBiome.getKey();

      actionBarManager.sendActionBar(p, locationText);
    }
  }
}