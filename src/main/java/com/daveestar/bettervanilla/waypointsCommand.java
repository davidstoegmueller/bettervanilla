package com.daveestar.bettervanilla;

import java.util.HashMap;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.utils.Config;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

class LocationName {
  private Location waypointLoc;
  private String waypointName;

  public LocationName(Location waypointLoc, String waypointName) {
    this.waypointLoc = waypointLoc;
    this.waypointName = waypointName;
  }

  public Location getLoc() {
    return this.waypointLoc;
  }

  public String getName() {
    return this.waypointName;
  }

}

public class WaypointsCommand implements CommandExecutor {
  public static HashMap<Player, LocationName> showWaypointCoords = new HashMap<Player, LocationName>();

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {

    if (c.getName().equalsIgnoreCase("waypoints") && cs instanceof Player) {
      Player p = (Player) cs;

      Config waypoints = new Config("waypoints.yml", Main.getInstance().getDataFolder());
      FileConfiguration cfgn = waypoints.getFileCfgrn();

      if (args.length == 0) {
        // no agruments -> list all waypoints

        p.sendMessage(
            Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "All Waypoints:");
        p.sendMessage("");

        Set<String> allWaypoints = cfgn.getKeys(false);

        if (allWaypoints.size() > 0) {
          for (String wpName : allWaypoints) {
            int wpX = cfgn.getInt(wpName + ".x");
            int wpY = cfgn.getInt(wpName + ".y");
            int wpZ = cfgn.getInt(wpName + ".z");

            p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + wpName + ChatColor.GRAY + " is at " + ChatColor.YELLOW
                + "X: " + ChatColor.GRAY
                + wpX + ChatColor.YELLOW
                + " Y: " + ChatColor.GRAY + wpY + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + wpZ);
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

            if (!cfgn.contains(waypointName)) {
              // if the waypoint doesnt already exist in the waypoints yml
              cfgn.set(waypointName + ".x", pLocX);
              cfgn.set(waypointName + ".y", pLocY);
              cfgn.set(waypointName + ".z", pLocZ);
              waypoints.save();

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
                  cfgn.set(waypointName + ".x", pLocX);
                  cfgn.set(waypointName + ".y", pLocY);
                  cfgn.set(waypointName + ".z", pLocZ);
                  waypoints.save();

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

              if (cfgn.contains(waypointName)) {
                // if the waypoint exists -> remove it
                cfgn.set(waypointName, null);
                waypoints.save();

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
            if (showWaypointCoords.containsKey(p)) {
              showWaypointCoords.remove(p);
              WaypointsMove.waypointScheduler.cancelTasks(Main.getInstance());

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

          if (cfgn.contains(waypointName)) {
            int wpX = cfgn.getInt(waypointName + ".x");
            int wpY = cfgn.getInt(waypointName + ".y");
            int wpZ = cfgn.getInt(waypointName + ".z");

            Location waypointLoc = new Location(p.getWorld(), wpX, wpY, wpZ);

            p.sendMessage(
                Main.getPrefix() + ChatColor.YELLOW + waypointName + ChatColor.GRAY + " is at " + ChatColor.YELLOW
                    + "X: " + ChatColor.GRAY
                    + wpX + ChatColor.YELLOW
                    + " Y: " + ChatColor.GRAY + wpY + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + wpZ);

            if (showWaypointCoords.containsKey(p)) {
              showWaypointCoords.remove(p);
            }

            showWaypointCoords.put(p, new LocationName(waypointLoc, waypointName));

            int locX = p.getLocation().getBlockX();
            int locY = p.getLocation().getBlockY();
            int locZ = p.getLocation().getBlockZ();

            String displayCoordsWp = ChatColor.YELLOW + "" + ChatColor.BOLD + waypointName.toUpperCase() + ": "
                + ChatColor.RESET
                + ChatColor.YELLOW
                + "X: " + ChatColor.GRAY
                + wpX + ChatColor.YELLOW
                + " Y: " + ChatColor.GRAY + wpY + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + wpZ;

            String displayCoordsCurrent = ChatColor.RED + "" + ChatColor.BOLD + " | " + ChatColor.YELLOW
                + ChatColor.BOLD
                + "CURRENT: " + ChatColor.RESET + ChatColor.YELLOW + "X: "
                + ChatColor.GRAY
                + locX + ChatColor.YELLOW
                + " Y: " + ChatColor.GRAY + locY + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + locZ;

            String displayText = displayCoordsWp + displayCoordsCurrent;

            WaypointsMove.displayActionBar(p, displayText);
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
}
