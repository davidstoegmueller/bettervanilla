package com.daveestar.bettervanilla.commands;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.NavigationType;
import com.daveestar.bettervanilla.gui.WaypointsGUI;
import com.daveestar.bettervanilla.manager.NavigationManager;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.manager.WaypointsManager;
import com.daveestar.bettervanilla.utils.ActionBar;
import com.daveestar.bettervanilla.utils.NavigationData;

import net.md_5.bungee.api.ChatColor;

import java.util.*;
import java.util.stream.Collectors;

public class WaypointsCommand implements TabExecutor {

  private final Main _plugin;
  private final WaypointsManager _waypointsManager;
  private final NavigationManager _navigationManager;
  private final ActionBar _actionBarManager;
  private final SettingsManager _settingsManager;
  private final WaypointsGUI _waypointsGUI;

  public WaypointsCommand() {
    _plugin = Main.getInstance();
    _waypointsManager = _plugin.getWaypointsManager();
    _navigationManager = _plugin.getNavigationManager();
    _actionBarManager = _plugin.getActionBar();
    _settingsManager = _plugin.getSettingsManager();
    _waypointsGUI = new WaypointsGUI();
  }

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (!(cs instanceof Player)) {
      cs.sendMessage(ChatColor.RED + "This command can only be used by players.");
      return true;
    }

    Player p = (Player) cs;
    if (args.length == 0) {
      _waypointsGUI.displayGUI(p);
      return true;
    }

    String subCommand = args[0].toLowerCase();
    switch (subCommand) {
      case "add":
        handleAdd(p, args);
        break;
      case "remove":
        _handleRemove(p, args);
        break;
      case "list":
        _handleList(p);
        break;
      case "nav":
        _handleNavigation(p, args);
        break;
      case "player":
        _handlePlayerNavigation(p, args);
        break;
      case "coords":
        _handleCoordsNavigation(p, args);
        break;
      case "cancel":
        _handleCancel(p);
        break;
      case "help":
        _handleHelp(p);
        break;
      default:
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Unknown waypoints command. Use /waypoints help for help.");
    }
    return true;
  }

  private void handleAdd(Player p, String[] args) {
    if (args.length < 2) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/waypoints add <name>");
      return;
    }

    String waypointName = args[1];
    Location location = p.getLocation().toBlockLocation();
    String world = p.getWorld().getName();

    if (_waypointsManager.checkWaypointExists(world, waypointName)) {
      if (args.length == 3 && args[2].equalsIgnoreCase("confirm")) {
        if (p.hasPermission("bettervanilla.waypoints.overwrite")) {
          _waypointsManager.addWaypoint(world, waypointName, location.getBlockX(), location.getBlockY(),
              location.getBlockZ());
          p.sendMessage(Main.getPrefix() + "The waypoint: " + ChatColor.YELLOW + waypointName + ChatColor.GRAY
              + " was successfully updated!");
          p.sendMessage(Main.getPrefix() + "It is reset to your current location: " + ChatColor.YELLOW + "X: "
              + ChatColor.GRAY + location.getBlockX() + ChatColor.YELLOW
              + " Y: " + ChatColor.GRAY + location.getBlockY() + ChatColor.YELLOW + " Z: " + ChatColor.GRAY
              + location.getBlockZ());
        } else {
          p.sendMessage(Main.getPrefix() + ChatColor.RED
              + "Sorry! You don't have permissions to overwrite existing waypoints.");
        }
      } else {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "A waypoint with name " + ChatColor.YELLOW + waypointName
            + ChatColor.RED + " already exists!");
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Overwrite Usage: "
            + ChatColor.YELLOW + "/waypoints add " + waypointName + " confirm");
      }
    } else {
      _waypointsManager.addWaypoint(world, waypointName, location.getBlockX(), location.getBlockY(),
          location.getBlockZ());
      p.sendMessage(Main.getPrefix() + "The waypoint: " + ChatColor.YELLOW + waypointName + ChatColor.GRAY
          + " was successfully added!");
      p.sendMessage(
          Main.getPrefix() + "It is set to your current location: " + ChatColor.YELLOW + "X: " + ChatColor.GRAY
              + location.getBlockX() + ChatColor.YELLOW + " Y: " + ChatColor.GRAY + location.getBlockY()
              + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + location.getBlockZ());
    }
  }

  private void _handleRemove(Player p, String[] args) {
    if (!p.hasPermission("bettervanilla.waypoints.remove")) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED
          + "Sorry! You don't have permissions to remove existing waypoints.");
      return;
    }

    if (args.length < 2) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: "
          + ChatColor.YELLOW + "/waypoints remove <name>");
      return;
    }

    String waypointName = args[1];
    String world = p.getWorld().getName();

    if (_waypointsManager.checkWaypointExists(world, waypointName)) {
      _waypointsManager.removeWaypoint(world, waypointName);
      p.sendMessage(Main.getPrefix() + "The waypoint " + ChatColor.YELLOW + waypointName + ChatColor.GRAY
          + " was successfully removed!");
    } else {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Could not find a waypoint called " + ChatColor.YELLOW
          + waypointName + ChatColor.RED + ". Please try an existing one!");
    }
  }

  private void _handleList(Player p) {
    String world = p.getWorld().getName();
    List<String> waypoints = _waypointsManager.getWaypoints(world);

    if (waypoints.isEmpty()) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "There are no existing waypoints!");
      return;
    }

    p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "Waypoints » " + world + ":");

    for (String waypoint : waypoints) {
      Map<String, Integer> coords = _waypointsManager.getWaypointByName(world, waypoint);
      Location playerLocation = p.getLocation().toBlockLocation();
      Location waypointLocation = new Location(p.getWorld(), coords.get("x"), coords.get("y"), coords.get("z"));
      long distance = Math.round(playerLocation.distance(waypointLocation));

      p.sendMessage(
          Main.getShortPrefix() + ChatColor.YELLOW + waypoint + ChatColor.GRAY + " is at " + ChatColor.YELLOW
              + "X: " + ChatColor.GRAY + coords.get("x") + ChatColor.YELLOW + " Y: " + ChatColor.GRAY + coords.get("y")
              + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + coords.get("z") + ChatColor.RED + " » " + ChatColor.YELLOW
              + distance + "m");
    }
  }

  private void _handleNavigation(Player p, String[] args) {
    if (args.length < 2) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/waypoints nav <name>");
      return;
    }

    String waypointName = args[1];
    String world = p.getWorld().getName();

    if (!_waypointsManager.checkWaypointExists(world, waypointName)) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Could not find a waypoint called " + ChatColor.YELLOW
          + waypointName + ChatColor.RED + ". Please try an existing one!");
      return;
    }

    Map<String, Integer> coords = _waypointsManager.getWaypointByName(world, waypointName);
    Location destination = new Location(p.getWorld(), coords.get("x"), coords.get("y"), coords.get("z"));

    _settingsManager.setToggleLocation(p.getUniqueId(), false);
    NavigationData navigationData = new NavigationData(waypointName, destination, NavigationType.WAYPOINT,
        Color.YELLOW);
    _navigationManager.startNavigation(p, navigationData);

    p.sendMessage(Main.getPrefix() + ChatColor.GRAY + "Start navigation to " + ChatColor.YELLOW + waypointName
        + ChatColor.GRAY + " at " + ChatColor.YELLOW + "X: " + ChatColor.GRAY + coords.get("x") + ChatColor.YELLOW
        + " Y: " + ChatColor.GRAY + coords.get("y") + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + coords.get("z"));
  }

  private void _handleCoordsNavigation(Player p, String[] args) {
    if (args.length < 4) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: "
          + ChatColor.YELLOW + "/waypoints coords <x> <y> <z>");
      return;
    }

    try {
      int x = Integer.parseInt(args[1]);
      int y = Integer.parseInt(args[2]);
      int z = Integer.parseInt(args[3]);

      Location destination = new Location(p.getWorld(), x, y, z);
      _settingsManager.setToggleLocation(p.getUniqueId(), false);
      NavigationData navigationData = new NavigationData("Coordinates", destination, NavigationType.WAYPOINT,
          Color.YELLOW);
      _navigationManager.startNavigation(p, navigationData);

      p.sendMessage(Main.getPrefix() + ChatColor.GRAY + "Start navigation to coordinates "
          + ChatColor.YELLOW + "X: " + ChatColor.GRAY + x + ChatColor.YELLOW + " Y: " + ChatColor.GRAY + y
          + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + z);
    } catch (NumberFormatException e) {
      p.sendMessage(
          Main.getPrefix() + ChatColor.RED + "Invalid coordinates. Please use numbers for <x>, <y>, <z>.");
    }
  }

  private void _handlePlayerNavigation(Player p, String[] args) {
    if (args.length < 2) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: "
          + ChatColor.YELLOW + "/waypoints player <player>");
      return;
    }

    String targetPlayerName = args[1];
    Player targetPlayer = p.getServer().getPlayer(targetPlayerName);

    if (targetPlayer == null || !targetPlayer.isOnline()) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "The player " + ChatColor.YELLOW + targetPlayerName
          + ChatColor.RED + " is not online or does not exist.");
      return;
    }

    Location targetLocation = targetPlayer.getLocation().toBlockLocation();
    _settingsManager.setToggleLocation(p.getUniqueId(), false);
    NavigationData navigationData = new NavigationData(targetPlayerName, targetLocation, NavigationType.PLAYER,
        Color.YELLOW);
    _navigationManager.startNavigation(p, navigationData);

    p.sendMessage(Main.getPrefix() + ChatColor.GRAY + "Start navigation to player "
        + ChatColor.YELLOW + targetPlayerName + ChatColor.GRAY + " at "
        + ChatColor.YELLOW + "X: " + ChatColor.GRAY + targetLocation.getBlockX()
        + ChatColor.YELLOW + " Y: " + ChatColor.GRAY + targetLocation.getBlockY()
        + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + targetLocation.getBlockZ());
  }

  private void _handleCancel(Player p) {
    if (_navigationManager.checkActiveNavigation(p)) {
      _navigationManager.stopNavigation(p);
      _actionBarManager.sendActionBarOnce(p, ChatColor.RED + "You've canceled active navigation!");
    } else {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "You have no current destination!");
    }
  }

  private void _handleHelp(Player p) {
    p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "WAYPOINTS HELP:");
    p.sendMessage(Main.getShortPrefix() + "/waypoints - Opens the waypoints GUI.");
    p.sendMessage(Main.getShortPrefix() + "/waypoints add <name> - Adds a waypoint at your current location.");
    p.sendMessage(Main.getShortPrefix() + "/waypoints remove <name> - Removes a waypoint by name.");
    p.sendMessage(Main.getShortPrefix() + "/waypoints list - Lists all waypoints in the current world.");
    p.sendMessage(Main.getShortPrefix() + "/waypoints nav <name> - Starts navigation to a specified waypoint.");
    p.sendMessage(Main.getShortPrefix() + "/waypoints player <player> - Navigates to another player's location.");
    p.sendMessage(Main.getShortPrefix() + "/waypoints coords <x> <y> <z> - Navigates to specific coordinates.");
    p.sendMessage(Main.getShortPrefix() + "/waypoints cancel - Cancels the current navigation.");
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    List<String> suggestions = new ArrayList<>();

    if (args.length == 1) {
      suggestions.addAll(Arrays.asList("add", "remove", "list", "nav", "player", "coords", "cancel", "help"));
    } else if (args.length == 2 && (args[0].equalsIgnoreCase("nav") || args[0].equalsIgnoreCase("remove"))) {
      Player p = (Player) sender;
      suggestions.addAll(_waypointsManager.getWaypoints(p.getWorld().getName()));
    } else if (args.length == 2 && args[0].equalsIgnoreCase("player")) {
      Collection<? extends Player> onlinePlayers = _plugin.getServer().getOnlinePlayers();
      suggestions.addAll(onlinePlayers.stream().map(Player::getName).collect(Collectors.toList()));
    }

    return suggestions;
  }
}
