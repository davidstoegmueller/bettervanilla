package com.daveestar.bettervanilla.commands;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.NavigationType;
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.gui.WaypointsGUI;
import com.daveestar.bettervanilla.manager.NavigationManager;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.NavigationData;
import com.daveestar.bettervanilla.utils.Theme;

import net.md_5.bungee.api.ChatColor;

import java.util.*;
import java.util.stream.Collectors;

public class WaypointsCommand implements TabExecutor {
  private final Main _plugin;
  private final NavigationManager _navigationManager;
  private final SettingsManager _settingsManager;
  private final WaypointsGUI _waypointsGUI;

  public WaypointsCommand() {
    _plugin = Main.getInstance();
    _navigationManager = _plugin.getNavigationManager();
    _settingsManager = _plugin.getSettingsManager();
    _waypointsGUI = new WaypointsGUI();
  }

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (!(cs instanceof Player)) {
      cs.sendMessage(Main.getNoPlayerMessage());
      return true;
    }

    Player p = (Player) cs;

    if (!p.hasPermission(Permissions.WAYPOINTS.getName())) {
      p.sendMessage(Main.getNoPermissionMessage(Permissions.WAYPOINTS));
      return true;
    }

    if (args.length == 0) {
      _waypointsGUI.displayWaypointsGUI(p);
      return true;
    }

    String subCommand = args[0].toLowerCase();
    switch (subCommand) {
      case "player":
        _handlePlayerNavigation(p, args);
        break;
      case "coords":
        _handleCoordsNavigation(p, args);
        break;
      case "help":
        _handleHelp(p);
        break;
      default:
        p.sendMessage(Main.getPrefix() + Theme.error() + "Unknown waypoints command. Use /waypoints help for help.");
    }

    return true;
  }

  private void _handleCoordsNavigation(Player p, String[] args) {
    if (args.length < 4) {
      p.sendMessage(Main.getPrefix() + Theme.error() + "Usage: " + Theme.highlight() + "/waypoints coords <x> <y> <z>");
      return;
    }

    try {
      int x = Integer.parseInt(args[1]);
      int y = Integer.parseInt(args[2]);
      int z = Integer.parseInt(args[3]);

      Location destination = new Location(p.getWorld(), x, y, z);
      _settingsManager.setPlayerToggleLocation(p.getUniqueId(), false);

      NavigationData navigationData = new NavigationData("Coordinates", destination, NavigationType.WAYPOINT,
          Color.YELLOW);
      _navigationManager.startNavigation(p, navigationData);

      p.sendMessage(Main.getPrefix() + Theme.primary() + "Start navigation to coordinates "
          + Theme.highlight() + "X: " + Theme.primary() + x + Theme.highlight() + " Y: " + Theme.primary() + y
          + Theme.highlight() + " Z: " + Theme.primary() + z);
    } catch (NumberFormatException e) {
      p.sendMessage(
          Main.getPrefix() + Theme.error() + "Invalid coordinates. Please use numbers for <x>, <y>, <z>.");
    }
  }

  private void _handlePlayerNavigation(Player p, String[] args) {
    if (args.length < 2) {
      p.sendMessage(Main.getPrefix() + Theme.error() + "Usage: " + Theme.highlight() + "/waypoints player <player>");
      return;
    }

    String targetPlayerName = args[1];
    Player targetPlayer = p.getServer().getPlayer(targetPlayerName);

    if (targetPlayer == null || !targetPlayer.isOnline()) {
      p.sendMessage(Main.getPrefix() + Theme.error() + "The player " + Theme.highlight() + targetPlayerName
          + Theme.error() + " is not online or does not exist.");
      return;
    }

    Location targetLocation = targetPlayer.getLocation().toBlockLocation();
    _settingsManager.setPlayerToggleLocation(p.getUniqueId(), false);

    NavigationData navigationData = new NavigationData(targetPlayerName, targetLocation, NavigationType.PLAYER,
        Color.YELLOW);
    _navigationManager.startNavigation(p, navigationData);

    p.sendMessage(Main.getPrefix() + Theme.primary() + "Start navigation to player "
        + Theme.highlight() + targetPlayerName + Theme.primary() + " at "
        + Theme.highlight() + "X: " + Theme.primary() + targetLocation.getBlockX()
        + Theme.highlight() + " Y: " + Theme.primary() + targetLocation.getBlockY()
        + Theme.highlight() + " Z: " + Theme.primary() + targetLocation.getBlockZ());
  }

  private void _handleHelp(Player p) {
    p.sendMessage(Main.getPrefix() + Theme.highlight() + ChatColor.BOLD + "WAYPOINTS HELP:");
    p.sendMessage(Main.getShortPrefix() + "/waypoints - Opens the waypoints GUI.");
    p.sendMessage(Main.getShortPrefix() + "/waypoints player <player> - Navigates to another player's location.");
    p.sendMessage(Main.getShortPrefix() + "/waypoints coords <x> <y> <z> - Navigates to specific coordinates.");
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    List<String> suggestions = new ArrayList<>();

    if (args.length == 1) {
      suggestions.addAll(Arrays.asList("player", "coords", "help"));
    } else if (args.length == 2 && args[0].equalsIgnoreCase("player")) {
      Collection<? extends Player> onlinePlayers = _plugin.getServer().getOnlinePlayers();
      suggestions.addAll(onlinePlayers.stream().map(Player::getName).collect(Collectors.toList()));
    }

    return suggestions;
  }
}
