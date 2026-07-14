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
      p.sendMessage(Main.getNoPermissionMessage(p, Permissions.WAYPOINTS));
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
        p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "command-waypoints-error-unknown"));
    }

    return true;
  }

  private void _handleCoordsNavigation(Player p, String[] args) {
    if (args.length < 4) {
      p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "command-waypoints-coordinates-usage"));
      return;
    }

    try {
      int x = Integer.parseInt(args[1]);
      int y = Integer.parseInt(args[2]);
      int z = Integer.parseInt(args[3]);

      Location destination = new Location(p.getWorld(), x, y, z);
      _settingsManager.setPlayerToggleLocation(p.getUniqueId(), false);

      NavigationData navigationData = new NavigationData(
          Main.tr(p, "navigation-coordinates-name"), destination, NavigationType.WAYPOINT, Color.YELLOW);
      _navigationManager.startNavigation(p, navigationData);

      p.sendMessage(Main.getPrefix() + Main.tr(p, "command-waypoints-coordinates-navigation-started",
          "x", x, "y", y, "z", z));
    } catch (NumberFormatException e) {
      p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "command-waypoints-error-invalid-coordinates"));
    }
  }

  private void _handlePlayerNavigation(Player p, String[] args) {
    if (args.length < 2) {
      p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "command-waypoints-player-usage"));
      return;
    }

    String targetPlayerName = args[1];
    Player targetPlayer = p.getServer().getPlayer(targetPlayerName);

    if (targetPlayer == null || !targetPlayer.isOnline()) {
      p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "command-waypoints-error-player-unavailable",
          "player", Theme.highlight() + targetPlayerName + Theme.error()));
      return;
    }

    Location targetLocation = targetPlayer.getLocation().toBlockLocation();
    _settingsManager.setPlayerToggleLocation(p.getUniqueId(), false);

    NavigationData navigationData = new NavigationData(targetPlayerName, targetLocation, NavigationType.PLAYER,
        Color.YELLOW);
    _navigationManager.startNavigation(p, navigationData);

    p.sendMessage(Main.getPrefix() + Main.tr(p, "command-waypoints-player-navigation-started",
        "player", Theme.highlight() + targetPlayerName + Theme.primary(),
        "x", targetLocation.getBlockX(),
        "y", targetLocation.getBlockY(),
        "z", targetLocation.getBlockZ()));
  }

  private void _handleHelp(Player p) {
    p.sendMessage(Main.getPrefix() + Theme.highlight() + ChatColor.BOLD + Main.tr(p, "command-waypoints-help-title"));
    p.sendMessage(Main.getShortPrefix() + Main.tr(p, "command-waypoints-help-open"));
    p.sendMessage(Main.getShortPrefix() + Main.tr(p, "command-waypoints-help-player"));
    p.sendMessage(Main.getShortPrefix() + Main.tr(p, "command-waypoints-help-coordinates"));
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
