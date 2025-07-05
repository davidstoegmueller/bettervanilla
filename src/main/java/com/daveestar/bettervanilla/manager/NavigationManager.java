package com.daveestar.bettervanilla.manager;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.utils.ActionBar;
import com.daveestar.bettervanilla.utils.NavigationData;
import com.daveestar.bettervanilla.utils.ParticleBeam;
import com.daveestar.bettervanilla.manager.SettingsManager;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class NavigationManager {
  // stores active navigations for players
  private final Map<Player, NavigationData> _activeNavigations = new HashMap<>();

  // stores active particle beams for players
  private final Map<Player, ParticleBeam> _activeBeams = new HashMap<>();

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

    // store the new navigation data
    _activeNavigations.put(p, navigationData);

    Location targetLocation = navigationData.getLocation().toBlockLocation();
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

      Location playerLocation = p.getLocation().toBlockLocation();
      Location targetLocation = navigationData.getLocation().toBlockLocation();
      String targetName = navigationData.getName();

      // generate the navigation text
      String navigationText = _getNavigationText(targetName, targetLocation, playerLocation);
      _actionBar.sendActionBar(p, navigationText);

      ParticleBeam beam = _activeBeams.get(p);
      beam.updateLocation(targetLocation);

      _displayNavigationTrail(p, targetLocation, navigationData.getColor());
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
    _actionBar.removeActionBar(p);
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

    // calculate the direction to the target location
    String directionText = ChatColor.RED + "" + ChatColor.BOLD + " » " + ChatColor.YELLOW + ChatColor.BOLD
        + "DIRECTION: " + ChatColor.RESET + ChatColor.GRAY + _getDirection(playerLocation, targetLocation);

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

  private void _displayNavigationTrail(Player p, Location targetLocation, Color color) {
    if (!_settingsManager.getNavigationTrail(p))
      return;

    Location start = p.getLocation().clone().add(0, 1, 0);
    double distance = start.distance(targetLocation);
    if (distance == 0)
      return;

    double maxDistance = Math.min(distance, 10);
    org.bukkit.util.Vector direction = targetLocation.toVector().subtract(start.toVector()).normalize();

    for (double d = 0; d <= maxDistance; d += 0.5) {
      Location point = start.clone().add(direction.clone().multiply(d));
      DustOptions options = new DustOptions(color, 1);
      p.spawnParticle(Particle.DUST, point, 1, 0, 0, 0, 0, options, true);
    }
  }
}
