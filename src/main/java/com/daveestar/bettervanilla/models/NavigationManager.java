package com.daveestar.bettervanilla.models;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.utils.NavigationData;
import com.daveestar.bettervanilla.utils.ParticleBeam;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class NavigationManager {
  // stores active navigations for players
  private final Map<Player, NavigationData> _activeNavigations = new HashMap<>();

  // stores active particle beams for players
  private final Map<Player, ParticleBeam> _activeBeams = new HashMap<>();

  public NavigationManager() {
  }

  public boolean checkActiveNavigation(Player player) {
    // check if the player has an active navigation
    return _activeNavigations.containsKey(player);
  }

  public void startNavigation(Player p, NavigationData navigationData) {
    // stop any existing navigation for the player
    stopNavigation(p);

    // store the new navigation data
    _activeNavigations.put(p, navigationData);

    Location targetLocation = navigationData.getLocation();
    Color beamColor = navigationData.getColor();

    // create and display the particle beam
    ParticleBeam beam = new ParticleBeam(p, targetLocation, beamColor);
    beam.displayBeam();
    _activeBeams.put(p, beam);

    // update the action bar with navigation details
    updateNavigation(p, navigationData);

  }

  public void updateNavigation(Player p, NavigationData navigationData) {
    // update the navigation action bar text if a navigation is active
    if (checkActiveNavigation(p)) {
      // update the navigation data
      _activeNavigations.put(p, navigationData);

      Location playerLocation = p.getLocation();
      Location targetLocation = navigationData.getLocation();
      String targetName = navigationData.getName();

      // generate the navigation text
      String navigationText = _getNavigationText(targetName, targetLocation, playerLocation);
      Main.getInstance().getActionBarManager().sendActionBar(p, navigationText);

      ParticleBeam beam = _activeBeams.get(p);
      beam.updateLocation(targetLocation);
    }
  }

  public void stopNavigation(Player p) {
    // remove the player's active navigation if it exists
    if (_activeNavigations.containsKey(p)) {
      _activeNavigations.remove(p);
    }

    // remove the particle beam if it exists
    if (_activeBeams.containsKey(p)) {
      _activeBeams.get(p).removeBeam();
      _activeBeams.remove(p);
    }

    // clear the action bar for the player
    Main.getInstance().getActionBarManager().removeActionBar(p);
  }

  public NavigationData getActiveNavigation(Player p) {
    // retrieve the active navigation data for the player
    return _activeNavigations.get(p);
  }

  private String _getNavigationText(String name, Location targetLocation, Location playerLocation) {
    // format the target location details
    String targetLocationText = ChatColor.YELLOW + "" + ChatColor.BOLD + name.toUpperCase() + ": "
        + ChatColor.RESET
        + ChatColor.YELLOW
        + "X: " + ChatColor.GRAY
        + targetLocation.getBlockX() + ChatColor.YELLOW
        + " Y: " + ChatColor.GRAY + targetLocation.getBlockY() + ChatColor.YELLOW + " Z: " + ChatColor.GRAY
        + targetLocation.getBlockZ();

    // format the player's current location details
    String playerLocationText = ChatColor.RED + "" + ChatColor.BOLD + " » " + ChatColor.YELLOW
        + ChatColor.BOLD
        + "CURRENT: " + ChatColor.RESET + ChatColor.YELLOW + "X: "
        + ChatColor.GRAY
        + playerLocation.getBlockX() + ChatColor.YELLOW
        + " Y: " + ChatColor.GRAY + playerLocation.getBlockY() + ChatColor.YELLOW + " Z: " + ChatColor.GRAY
        + playerLocation.getBlockZ();

    // calculate and format the distance to the target location
    String distanceText = ChatColor.RED + "" + ChatColor.BOLD + " » " + ChatColor.YELLOW + ChatColor.BOLD
        + "DISTANCE: "
        + ChatColor.RESET + ChatColor.GRAY + Math.round(playerLocation.distance(targetLocation));

    // combine all parts into the final navigation string
    return targetLocationText + playerLocationText + distanceText;
  }
}
