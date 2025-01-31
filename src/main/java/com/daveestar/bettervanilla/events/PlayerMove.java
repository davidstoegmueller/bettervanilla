package com.daveestar.bettervanilla.events;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.NavigationType;
import com.daveestar.bettervanilla.manager.NavigationManager;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.ActionBar;
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
    Main.getInstance().getAFKManager().onPlayerMoved(p);
  }

  private void _handleNavigationPlayerMove(Player p) {
    NavigationManager navigationManager = Main.getInstance().getNavigationManager();

    // check if any player is navigating to the current player
    for (Player navigatingPlayer : p.getServer().getOnlinePlayers()) {
      if (navigationManager.checkActiveNavigation(navigatingPlayer)) {
        NavigationData navigationData = navigationManager.getActiveNavigation(navigatingPlayer);

        // check if the navigation targets the moving player
        if (navigationData.getType() == NavigationType.PLAYER && navigationData.getName().equals(p.getName())) {
          Location newTargetLocation = p.getLocation();

          // update navigation data for the navigating player
          navigationData.setLocation(newTargetLocation);

          navigationManager.updateNavigation(navigatingPlayer, navigationData);
        }
      }
    }

    // handle the player’s own navigation logic
    if (navigationManager.checkActiveNavigation(p)) {
      NavigationData navigationData = navigationManager.getActiveNavigation(p);

      Location targetLocation = navigationData.getLocation();
      Location playerLocation = p.getLocation();

      // handle world change and cancel navigation if different worlds
      if (!targetLocation.getWorld().getName().equals(playerLocation.getWorld().getName())) {
        navigationManager.stopNavigation(p);
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Your navigation has been canceled due to world change!");
        return;
      }

      // handle proximity to target location (25-block range)
      if (playerLocation.distance(targetLocation) <= 25) {
        navigationManager.stopNavigation(p);
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
            ChatColor.YELLOW + "" + ChatColor.BOLD + navigationData.getName() +
                ChatColor.GRAY + " is within 25 blocks!"));
        return;
      }

      // handle dynamic updates for player-based navigation
      if (navigationData.getType() == NavigationType.PLAYER) {
        String targetPlayerName = navigationData.getName();
        Player targetPlayer = p.getServer().getPlayer(targetPlayerName);

        if (targetPlayer == null || !targetPlayer.isOnline()) {
          navigationManager.stopNavigation(p);
          p.sendMessage(Main.getPrefix() + ChatColor.RED + "Navigation canceled as the target player is offline!");
          return;
        }

        // update the target location to the current position of the target player
        targetLocation = targetPlayer.getLocation();
        navigationData.setLocation(targetLocation);
      }

      // update navigation data
      navigationManager.updateNavigation(p, navigationData);
    }
  }

  private void _handleLocationPlayerMove(Player p) {
    SettingsManager settingsManager = Main.getInstance().getSettingsManager();
    ActionBar actionBarManager = Main.getInstance().getActionBarManager();

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