package com.daveestar.bettervanilla.manager;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.utils.ActionBar;
import com.daveestar.bettervanilla.utils.NavigationData;
import com.daveestar.bettervanilla.utils.ParticleNavigation;
import com.daveestar.bettervanilla.utils.Theme;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class NavigationManager {
  // stores active navigations for players
  private final Map<Player, NavigationData> _activeNavigations = new HashMap<>();

  // stores active particle navigations for players
  private final Map<Player, ParticleNavigation> _activeParticleNavigations = new HashMap<>();

  private final Main _plugin;
  private ActionBar _actionBar;
  private SettingsManager _settingsManager;

  public NavigationManager() {
    _plugin = Main.getInstance();
  }

  public void initManagers() {
    _actionBar = _plugin.getActionBar();
    _settingsManager = _plugin.getSettingsManager();
  }

  public boolean checkActiveNavigation(Player p) {
    // check if the player has an active navigation
    return _activeNavigations.containsKey(p);
  }

  public void startNavigation(Player p, NavigationData navigationData) {
    // stop any existing navigation for the player
    stopNavigation(p);

    Location playerLocation = p.getLocation().toBlockLocation();
    Location targetLocation = navigationData.getLocation().toBlockLocation();

    if (!targetLocation.getWorld().equals(playerLocation.getWorld())) {
      p.sendMessage(
          Main.getPrefix() + Theme.error() + "Cannot start navigation because the target is in a different world!");
      return;
    }

    // store the new navigation data
    _activeNavigations.put(p, navigationData);

    Color particleColor = navigationData.getColor();

    // create and display the particle navigation
    ParticleNavigation particleNavigation = new ParticleNavigation(p, targetLocation, particleColor);
    particleNavigation.displayBeam();

    // if the player has the trail setting enabled, display the trail
    if (_settingsManager.getPlayerNavigationTrail(p.getUniqueId())) {
      particleNavigation.displayTrail();
    }

    _activeParticleNavigations.put(p, particleNavigation);

    // update the action bar with navigation details
    updateNavigation(p, navigationData);

  }

  public void updateNavigation(Player p, NavigationData navigationData) {
    // update the navigation action bar text if a navigation is active
    if (checkActiveNavigation(p)) {
      // update the navigation data
      _activeNavigations.put(p, navigationData);

      Location playerLocation = p.getLocation().toBlockLocation();
      Location targetLocation = navigationData.getLocation().toBlockLocation();

      if (!targetLocation.getWorld().equals(playerLocation.getWorld())) {
        this.stopNavigation(p);
        p.sendMessage(Main.getPrefix() + Theme.error() + "Your navigation has been canceled due to world change!");
        return;
      }

      String targetName = navigationData.getName();

      // generate the navigation text
      String navigationText = _getNavigationText(targetName, targetLocation, playerLocation);
      _actionBar.sendActionBar(p, navigationText);

      ParticleNavigation particleNavigation = _activeParticleNavigations.get(p);
      particleNavigation.update(targetLocation, true, _settingsManager.getPlayerNavigationTrail(p.getUniqueId()));
    }
  }

  public void stopNavigation(Player p) {
    // remove the player's active navigation if it exists
    if (_activeNavigations.containsKey(p)) {
      _activeNavigations.remove(p);
    }

    // remove the particle navigation if it exists
    if (_activeParticleNavigations.containsKey(p)) {
      _activeParticleNavigations.get(p).removeBeam();
      _activeParticleNavigations.get(p).removeTrail();
      _activeParticleNavigations.remove(p);
    }

    // clear the action bar for the player
    _actionBar.removeActionBar(p);
  }

  public NavigationData getActiveNavigation(Player p) {
    // retrieve the active navigation data for the player
    return _activeNavigations.get(p);
  }

  private String _getNavigationText(String name, Location targetLocation, Location playerLocation) {
    // format the target location details
    String targetLocationText = Theme.highlight() + "" + ChatColor.BOLD + name.toUpperCase() + ": "
        + ChatColor.RESET
        + Theme.highlight()
        + "X: " + Theme.primary()
        + targetLocation.getBlockX() + Theme.highlight()
        + " Y: " + Theme.primary() + targetLocation.getBlockY() + Theme.highlight() + " Z: " + Theme.primary()
        + targetLocation.getBlockZ();

    // format the player's current location details
    String playerLocationText = Theme.textSymbol() + "" + ChatColor.BOLD + " » " + Theme.highlight()
        + ChatColor.BOLD
        + "CURRENT: " + ChatColor.RESET + Theme.highlight() + "X: "
        + Theme.primary()
        + playerLocation.getBlockX() + Theme.highlight()
        + " Y: " + Theme.primary() + playerLocation.getBlockY() + Theme.highlight() + " Z: " + Theme.primary()
        + playerLocation.getBlockZ();

    // calculate and format the distance to the target location
    String distanceText = Theme.textSymbol() + "" + ChatColor.BOLD + " » " + Theme.highlight() + ChatColor.BOLD
        + "DISTANCE: "
        + ChatColor.RESET + Theme.primary() + Math.round(playerLocation.distance(targetLocation));

    // calculate the direction to the target location
    String directionText = Theme.textSymbol() + "" + ChatColor.BOLD + " » " + Theme.highlight() + ChatColor.BOLD
        + "DIRECTION: " + ChatColor.RESET + Theme.primary() + _getDirection(playerLocation, targetLocation);

    // combine all parts into the final navigation string
    return targetLocationText + playerLocationText + distanceText + directionText;
  }

  private String _getDirection(Location playerLocation, Location targetLocation) {
    double dx = targetLocation.getX() - playerLocation.getX();
    double dz = targetLocation.getZ() - playerLocation.getZ();
    double angle = Math.toDegrees(Math.atan2(dz, dx)) - playerLocation.getYaw();

    if (angle < 0) {
      angle += 360;
    }

    if (angle >= 337.5 || angle < 22.5) {
      return "⬅"; // West
    } else if (angle >= 22.5 && angle < 67.5) {
      return "⬉"; // North-West
    } else if (angle >= 67.5 && angle < 112.5) {
      return "⬆"; // North
    } else if (angle >= 112.5 && angle < 157.5) {
      return "⬈"; // North-East
    } else if (angle >= 157.5 && angle < 202.5) {
      return "➡"; // East
    } else if (angle >= 202.5 && angle < 247.5) {
      return "⬊"; // South-East
    } else if (angle >= 247.5 && angle < 292.5) {
      return "⬇"; // South
    } else {
      return "⬋"; // South-West
    }
  }
}
