package com.daveestar.bettervanilla.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.models.WaypointsManager;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class WaypointsCommand implements TabExecutor {
  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {

    if (c.getName().equalsIgnoreCase("waypoints") && cs instanceof Player) {
      Player p = (Player) cs;
      String worldName = p.getWorld().getName();

      WaypointsManager waypointsManager = Main.getInstance().getWaypointsManager();

      if (args.length == 0) {
        // no agruments -> list all waypoints
        p.sendMessage(
            Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "All waypoints in " + worldName + ":");
        p.sendMessage("");

        // get all waypoints
        Set<String> allWaypoints = waypointsManager.getAllWaypoints(worldName);

        // loop through all waypoints and send them to the player
        if (allWaypoints != null && allWaypoints.size() > 0) {
          for (String waypointName : allWaypoints) {
            HashMap<String, Integer> waypointCoordinates = waypointsManager.getSpecificWaypoint(worldName,
                waypointName);

            Location waypointLocation = new Location(Bukkit.getWorld(worldName), waypointCoordinates.get("x"),
                waypointCoordinates.get("y"), waypointCoordinates.get("z"));

            p.sendMessage(
                Main.getPrefix() + ChatColor.YELLOW + waypointName + ChatColor.GRAY + " is at " + ChatColor.YELLOW
                    + "X: " + ChatColor.GRAY
                    + waypointCoordinates.get("x") + ChatColor.YELLOW
                    + " Y: " + ChatColor.GRAY + waypointCoordinates.get("y") + ChatColor.YELLOW + " Z: "
                    + ChatColor.GRAY + waypointCoordinates.get("z")
                    + ChatColor.RED + " | " + ChatColor.YELLOW
                    + Math.round(p.getLocation().distance(waypointLocation)) + "m");
          }
        } else {
          p.sendMessage(Main.getPrefix() + ChatColor.RED + "There are no existing waypoints!");
        }
      }

      if (args.length > 0) {
        if (args[0].equalsIgnoreCase("add")) {
          if ((args.length == 2) || (args.length == 3 && args[2].equalsIgnoreCase("confirm"))) {
            String waypointName = args[1];
            Location playerLocation = p.getLocation();

            int pLocX = playerLocation.getBlockX();
            int pLocY = playerLocation.getBlockY();
            int pLocZ = playerLocation.getBlockZ();

            if (!waypointsManager.checkWaypointExists(worldName, waypointName)) {
              // if the waypoint doesnt already exist in the waypoints yml
              waypointsManager.setWaypoint(worldName, waypointName, pLocX, pLocY, pLocZ);

              // send the player the success message
              p.sendMessage(Main.getPrefix() + "The waypoint: " + ChatColor.YELLOW + waypointName + ChatColor.GRAY
                  + " was successfully added!");
              p.sendMessage(
                  Main.getPrefix() + "It is set to your current location: " + ChatColor.YELLOW + "X: " + ChatColor.GRAY
                      + pLocX + ChatColor.YELLOW
                      + " Y: " + ChatColor.GRAY + pLocY + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + pLocZ);
            } else {
              // if it already exists give a hint how to overwrite the pos
              p.sendMessage(Main.getPrefix() + ChatColor.RED + "A waypoint with name " + ChatColor.YELLOW + waypointName
                  + ChatColor.RED + " already exists!");
              p.sendMessage(Main.getPrefix() + ChatColor.RED + "If you want to overwrite this waypoint please use: "
                  + ChatColor.YELLOW + " /waypoints add " + waypointName + " confirm");

              if (args.length == 3 && args[2].equalsIgnoreCase("confirm")) {
                if (p.hasPermission("bettervanilla.waypoints.overwrite")) {
                  waypointsManager.setWaypoint(worldName, waypointName, pLocX, pLocY, pLocZ);

                  // send the player the success message
                  p.sendMessage(Main.getPrefix() + "The waypoint: " + ChatColor.YELLOW + waypointName + ChatColor.GRAY
                      + " was successfully updated!");
                  p.sendMessage(
                      Main.getPrefix() + "It is reset to your current location: " + ChatColor.YELLOW + "X: "
                          + ChatColor.GRAY
                          + pLocX + ChatColor.YELLOW
                          + " Y: " + ChatColor.GRAY + pLocY + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + pLocZ);
                } else {
                  p.sendMessage(Main.getPrefix() + ChatColor.RED
                      + "Sorry! You don't have permissions to overwrite existing waypoints. Please ask to gain "
                      + ChatColor.YELLOW + "bettervanilla.waypoints.overwrite");
                }
              }
            }
          } else {
            p.sendMessage(Main.getPrefix() + ChatColor.RED + "To add an waypoint please use: "
                + ChatColor.YELLOW + "/waypoints add <name>");
          }
        }

        if (args[0].equalsIgnoreCase("remove")) {
          if (p.hasPermission("bettervanilla.waypoints.remove")) {
            if (args.length == 2) {
              String waypointName = args[1];

              if (waypointsManager.checkWaypointExists(worldName, waypointName)) {
                // if the waypoint exists -> remove it
                waypointsManager.removeWaypoint(worldName, waypointName);

                p.sendMessage(Main.getPrefix() + "The waypoint " + ChatColor.YELLOW + waypointName + ChatColor.GRAY
                    + " was successfully removed!");
              } else {
                // send a message that the waypoint doesnt exist
                p.sendMessage(Main.getPrefix() + ChatColor.RED + "Could not find a waypoint called " + ChatColor.YELLOW
                    + waypointName + ChatColor.RED
                    + ". Please try an existing one!");
              }
            } else {
              p.sendMessage(Main.getPrefix() + ChatColor.RED + "To remove an existing waypoint please use: "
                  + ChatColor.YELLOW + "/waypoints remove <name>");
            }
          } else {
            p.sendMessage(Main.getPrefix() + ChatColor.RED
                + "Sorry! You don't have permissions to remove existing waypoints. Please ask to gain "
                + ChatColor.YELLOW + "bettervanilla.waypoints.remove");
          }
        }

        if (args[0].equalsIgnoreCase("cancel")) {
          if (args.length == 1) {
            if (waypointsManager.checkPlayerActiveWaypointNavigation(p)) {
              waypointsManager.removePlayerActiveWaypointNavigation(p);

              p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                  ChatColor.RED + "You've canceled navigation!"));
            } else {
              p.sendMessage(Main.getPrefix() + ChatColor.RED + "You have no current destination!");
            }
          } else {
            p.sendMessage(Main.getPrefix() + ChatColor.RED + "To cancel navigation to an existing waypoint please use: "
                + ChatColor.YELLOW + "/waypoints cancel");
          }
        }

        if (!args[0].equalsIgnoreCase("add") && !args[0].equalsIgnoreCase("remove")
            && !args[0].equalsIgnoreCase("cancel")) {
          String waypointName = args[0];

          // check if the waypoint exists in the file configuration
          if (waypointsManager.checkWaypointExists(worldName, waypointName)) {
            HashMap<String, Integer> waypointCoordinates = waypointsManager.getSpecificWaypoint(worldName,
                waypointName);

            int waypointX = waypointCoordinates.get("x");
            int waypointY = waypointCoordinates.get("y");
            int waypointZ = waypointCoordinates.get("z");

            Location waypointLoc = new Location(p.getWorld(), waypointX, waypointY, waypointZ);

            p.sendMessage(
                Main.getPrefix() + ChatColor.YELLOW + waypointName + ChatColor.GRAY + " is at " + ChatColor.YELLOW
                    + "X: " + ChatColor.GRAY
                    + waypointX + ChatColor.YELLOW
                    + " Y: " + ChatColor.GRAY + waypointY + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + waypointZ);

            if (waypointsManager.checkPlayerActiveWaypointNavigation(p)) {
              waypointsManager.removePlayerActiveWaypointNavigation(p);
            }

            if (waypointsManager.checkPlayerActiveToggleLocationNavigation(p)) {
              waypointsManager.removePlayerActiveToggleLocationNavigation(p);
            }

            waypointsManager.addPlayerActiveWaypointNavigation(p, waypointLoc, waypointName);

            int locX = p.getLocation().getBlockX();
            int locY = p.getLocation().getBlockY();
            int locZ = p.getLocation().getBlockZ();

            Location waypointLocation = new Location(p.getWorld(), waypointX, waypointY, waypointZ);

            String displayCoordsWp = ChatColor.YELLOW + "" + ChatColor.BOLD + waypointName.toUpperCase() + ": "
                + ChatColor.RESET
                + ChatColor.YELLOW
                + "X: " + ChatColor.GRAY
                + waypointX + ChatColor.YELLOW
                + " Y: " + ChatColor.GRAY + waypointY + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + waypointZ;

            String displayCoordsCurrent = ChatColor.RED + "" + ChatColor.BOLD + " | " + ChatColor.YELLOW
                + ChatColor.BOLD
                + "CURRENT: " + ChatColor.RESET + ChatColor.YELLOW + "X: "
                + ChatColor.GRAY
                + locX + ChatColor.YELLOW
                + " Y: " + ChatColor.GRAY + locY + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + locZ;

            String distanceToTarget = ChatColor.RED + "" + ChatColor.BOLD + " | " + ChatColor.YELLOW + ChatColor.BOLD
                + "DISTANCE: "
                + ChatColor.RESET + ChatColor.GRAY + Math.round(p.getLocation().distance(waypointLocation));

            String displayText = displayCoordsWp + displayCoordsCurrent + distanceToTarget;

            waypointsManager.displayActionBar(p, displayText);
          } else {
            // send a message that the waypoint doesnt exist
            p.sendMessage(Main.getPrefix() + ChatColor.RED + "Could not find a waypoint called " + ChatColor.YELLOW
                + waypointName + ChatColor.RED
                + ". Please try an existing one!");
          }
        }
      }

      return true;
    }
    return false;
  }

  @Override
  public List<String> onTabComplete(CommandSender cs, Command c, String label, String[] args) {
    if (args.length == 1) {
      Player p = (Player) cs;
      WaypointsManager waypointsManager = Main.getInstance().getWaypointsManager();

      // filter waypoints based on current args[0] input
      List<String> waypoints = new ArrayList<String>(waypointsManager.getAllWaypoints(p.getWorld().getName()));
      waypoints.removeIf(waypointName -> !waypointName.toLowerCase().startsWith(args[0].toLowerCase()));

      List<String> filteredWaypoints = new ArrayList<String>(waypoints);

      return filteredWaypoints;
    }

    return new ArrayList<>();
  }
}
