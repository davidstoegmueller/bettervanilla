package com.daveestar.bettervanilla.events;

import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.NavigationType;
import com.daveestar.bettervanilla.manager.AFKManager;
import com.daveestar.bettervanilla.manager.NavigationManager;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.ActionBar;
import com.daveestar.bettervanilla.utils.NavigationData;
import com.daveestar.bettervanilla.utils.Theme;

import net.md_5.bungee.api.ChatColor;

public class PlayerMove implements Listener {

  private final Main _plugin;
  private final AFKManager _afkManager;
  private final NavigationManager _navigationManager;
  private final ActionBar _actionBar;
  private final SettingsManager _settingsManager;

  public PlayerMove() {
    _plugin = Main.getInstance();
    _afkManager = _plugin.getAFKManager();
    _navigationManager = _plugin.getNavigationManager();
    _actionBar = _plugin.getActionBar();
    _settingsManager = _plugin.getSettingsManager();
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent e) {
    Player p = (Player) e.getPlayer();

    _handleNavigationPlayerMove(p);
    _handleLocationPlayerMove(p);
    _handleAFKPlayerMove(p);
  }

  private void _handleAFKPlayerMove(Player p) {
    _afkManager.onPlayerMoved(p);
  }

  private void _handleNavigationPlayerMove(Player p) {

    // check if any player is navigating to the current player
    for (Player navigatingPlayer : p.getServer().getOnlinePlayers()) {
      if (_navigationManager.checkActiveNavigation(navigatingPlayer)) {
        NavigationData navigationData = _navigationManager.getActiveNavigation(navigatingPlayer);

        // check if the navigation targets the moving player
        if (navigationData.getType() == NavigationType.PLAYER && navigationData.getName().equals(p.getName())) {
          Location newTargetLocation = p.getLocation().toBlockLocation();

          // update navigation data for the navigating player
          navigationData.setLocation(newTargetLocation);

          _navigationManager.updateNavigation(navigatingPlayer, navigationData);
        }
      }
    }

    // handle the player’s own navigation logic
    if (_navigationManager.checkActiveNavigation(p)) {
      NavigationData navigationData = _navigationManager.getActiveNavigation(p);

      Location targetLocation = navigationData.getLocation().toBlockLocation();
      Location playerLocation = p.getLocation().toBlockLocation();

      // handle world change and cancel navigation if different worlds
      if (!targetLocation.getWorld().equals(playerLocation.getWorld())) {
        _navigationManager.stopNavigation(p);
        p.sendMessage(Main.getPrefix() + Theme.error() + "Your navigation has been canceled due to world change!");
        return;
      }

      // handle proximity to target location (player-configurable)
      if (_settingsManager.getPlayerNavigationAutoCancel(p.getUniqueId())) {
        int radius = _settingsManager.getPlayerNavigationReachedRadius(p.getUniqueId());

        if (playerLocation.distance(targetLocation) <= radius) {
          _navigationManager.stopNavigation(p);

          p.sendMessage(Main.getPrefix() + "You've reached your destination. You're within " + Theme.highlight()
              + radius + Theme.primary() + " blocks of " + Theme.highlight()
              + navigationData.getName() + Theme.primary() + ".");
          return;
        }
      }

      // handle dynamic updates for player-based navigation
      if (navigationData.getType() == NavigationType.PLAYER) {
        String targetPlayerName = navigationData.getName();
        Player targetPlayer = p.getServer().getPlayer(targetPlayerName);

        if (targetPlayer == null || !targetPlayer.isOnline()) {
          _navigationManager.stopNavigation(p);
          p.sendMessage(Main.getPrefix() + Theme.error() + "Navigation canceled as the target player is offline!");
          return;
        }

        // update the target location to the current position of the target player
        targetLocation = targetPlayer.getLocation().toBlockLocation();
        navigationData.setLocation(targetLocation);
      }

      // update navigation data
      _navigationManager.updateNavigation(p, navigationData);
    }
  }

  private void _handleLocationPlayerMove(Player p) {
    if (_settingsManager.getPlayerToggleLocation(p.getUniqueId())) {
      Biome playerBiome = p.getWorld().getBiome(p.getLocation().toBlockLocation());

      String locationText = Theme.highlight() + "X: "
          + Theme.primary()
          + p.getLocation().toBlockLocation().getBlockX() + Theme.highlight()
          + " Y: " + Theme.primary() + p.getLocation().toBlockLocation().getBlockY() + Theme.highlight() +
          " Z: " + Theme.primary()
          + p.getLocation().toBlockLocation().getBlockZ() + Theme.textSymbol() + ChatColor.BOLD + " » "
          + Theme.primary() + playerBiome.getKey();

      _actionBar.sendActionBar(p, locationText);
    }
  }
}
