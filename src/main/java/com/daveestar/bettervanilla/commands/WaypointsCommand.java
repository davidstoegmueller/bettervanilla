package com.daveestar.bettervanilla.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.models.CustomGUI;
import com.daveestar.bettervanilla.models.NavigationManager;
import com.daveestar.bettervanilla.models.SettingsManager;
import com.daveestar.bettervanilla.models.WaypointsManager;
import com.daveestar.bettervanilla.utils.ActionBarManager;
import com.daveestar.bettervanilla.utils.NavigationData;

public class WaypointsCommand implements TabExecutor {
  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {

    if (c.getName().equalsIgnoreCase("waypoints") && cs instanceof Player) {
      Player p = (Player) cs;
      String worldName = p.getWorld().getName();

      ActionBarManager actionBarManager = Main.getInstance().getActionBarManager();
      WaypointsManager waypointsManager = Main.getInstance().getWaypointsManager();
      NavigationManager navigationManager = Main.getInstance().getNavigationManager();

      if (args.length == 0) {
        _displayWaypointsGUI(p, waypointsManager.getWaypoints(worldName));
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
              waypointsManager.addWaypoint(worldName, waypointName, pLocX, pLocY, pLocZ);

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
                  waypointsManager.addWaypoint(worldName, waypointName, pLocX, pLocY, pLocZ);

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
            if (navigationManager.checkActiveNavigation(p)) {
              navigationManager.stopNavigation(p);

              actionBarManager.sendActionBarOnce(p, ChatColor.RED + "You've canceled active navigation!");
            } else {
              p.sendMessage(Main.getPrefix() + ChatColor.RED + "You have no current destination!");
            }
          } else {
            p.sendMessage(Main.getPrefix() + ChatColor.RED + "To cancel navigation to an existing waypoint please use: "
                + ChatColor.YELLOW + "/waypoints cancel");
          }
        }

        if (args[0].equalsIgnoreCase("list")) {
          if (args.length == 1) {
            p.sendMessage(
                Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "All waypoints in " + worldName + ":");
            p.sendMessage("");

            // get all waypoints
            List<String> allWaypointNames = waypointsManager.getWaypoints(worldName);

            // loop through all waypoints and send them to the player
            if (allWaypointNames != null && allWaypointNames.size() > 0) {
              for (String waypointName : allWaypointNames) {
                HashMap<String, Integer> waypointCoordinates = waypointsManager.getWaypointByName(worldName,
                    waypointName);

                Location waypointLocation = new Location(Bukkit.getWorld(worldName), waypointCoordinates.get("x"),
                    waypointCoordinates.get("y"), waypointCoordinates.get("z"));

                p.sendMessage(
                    Main.getPrefix() + ChatColor.YELLOW + waypointName + ChatColor.GRAY + " is at " + ChatColor.YELLOW
                        + "X: " + ChatColor.GRAY
                        + waypointCoordinates.get("x") + ChatColor.YELLOW
                        + " Y: " + ChatColor.GRAY + waypointCoordinates.get("y") + ChatColor.YELLOW + " Z: "
                        + ChatColor.GRAY + waypointCoordinates.get("z")
                        + ChatColor.RED + " » " + ChatColor.YELLOW
                        + Math.round(p.getLocation().distance(waypointLocation)) + "m");
              }
            } else {
              p.sendMessage(Main.getPrefix() + ChatColor.RED + "There are no existing waypoints!");
            }
          } else {
            p.sendMessage(Main.getPrefix() + ChatColor.RED + "To list all existing waypoints please use: "
                + ChatColor.YELLOW + "/waypoints list");
          }
        }

        if (!args[0].equalsIgnoreCase("add") && !args[0].equalsIgnoreCase("remove")
            && !args[0].equalsIgnoreCase("cancel") && !args[0].equalsIgnoreCase("list")) {
          String waypointName = args[0];

          if (waypointsManager.checkWaypointExists(worldName, waypointName)) {
            _handleStartNavigation(p, waypointName);
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

  private void _handleStartNavigation(Player p, String waypointName) {
    SettingsManager settingsManager = Main.getInstance().getSettingsManager();
    WaypointsManager waypointsManager = Main.getInstance().getWaypointsManager();
    NavigationManager navigationManager = Main.getInstance().getNavigationManager();
    String worldName = p.getWorld().getName();

    // check if the waypoint exists in the file configuration
    if (waypointsManager.checkWaypointExists(worldName, waypointName)) {
      HashMap<String, Integer> waypointCoordinates = waypointsManager.getWaypointByName(worldName,
          waypointName);

      int waypointX = waypointCoordinates.get("x");
      int waypointY = waypointCoordinates.get("y");
      int waypointZ = waypointCoordinates.get("z");

      Location waypointLocation = new Location(p.getWorld(), waypointX, waypointY, waypointZ);

      p.sendMessage(
          Main.getPrefix() + ChatColor.GRAY + "Start navigation to " + ChatColor.YELLOW + waypointName + ChatColor.GRAY
              + " at " + ChatColor.YELLOW
              + "X: " + ChatColor.GRAY
              + waypointX + ChatColor.YELLOW
              + " Y: " + ChatColor.GRAY + waypointY + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + waypointZ
              + ChatColor.RED + " » " + ChatColor.YELLOW
              + Math.round(p.getLocation().distance(waypointLocation)) + "m");

      settingsManager.setToggleLocation(p, false);
      NavigationData navigationData = new NavigationData(waypointName, waypointLocation, Color.YELLOW);
      navigationManager.startNavigation(p, navigationData);
    }
  }

  private void _displayWaypointsGUI(Player p, List<String> allWaypointNames) {
    HashMap<String, ItemStack> pageEntries = new HashMap<>();

    String worldName = p.getWorld().getName();
    WaypointsManager waypointsManager = Main.getInstance().getWaypointsManager();

    // Create entries for each waypoint
    for (String waypointName : allWaypointNames) {
      HashMap<String, Integer> waypointData = waypointsManager.getWaypointByName(worldName, waypointName);

      int waypointX = waypointData.get("x");
      int waypointY = waypointData.get("y");
      int waypointZ = waypointData.get("z");

      Location waypointLocation = new Location(p.getWorld(), waypointX, waypointY, waypointZ);

      long distance = Math.round(p.getLocation().distance(waypointLocation));

      ItemStack waypointItem = new ItemStack(Material.PAPER);
      ItemMeta waypointItemMeta = waypointItem.getItemMeta();
      waypointItemMeta.setDisplayName(
          ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "" + ChatColor.BOLD + waypointName);

      List<String> waypointItemLore = new ArrayList<>();
      waypointItemLore.add("");
      waypointItemLore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "X: " + ChatColor.YELLOW + waypointX);
      waypointItemLore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Y: " + ChatColor.YELLOW + waypointY);
      waypointItemLore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Z: " + ChatColor.YELLOW + waypointZ);
      waypointItemLore.add("");
      waypointItemLore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Distance: " + ChatColor.YELLOW + distance
          + ChatColor.GRAY + " blocks");
      waypointItemLore.add("");
      waypointItemLore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Start navigation");
      waypointItemMeta.setLore(waypointItemLore);

      waypointItem.setItemMeta(waypointItemMeta);

      pageEntries.put(waypointName, waypointItem);
    }

    BiConsumer<Player, String> onItemClick = (player, waypointName) -> {
      _handleStartNavigation(p, waypointName);
      p.closeInventory();

      p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
    };

    HashMap<String, ItemStack> sortedPageEntries = pageEntries.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (oldValue, newValue) -> oldValue,
            LinkedHashMap::new // Use LinkedHashMap to maintain order
        ));

    CustomGUI waypointsGUI = new CustomGUI(Main.getInstance(), p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Waypoints",
        sortedPageEntries, onItemClick);
    waypointsGUI.open(p);
  }

  @Override
  public List<String> onTabComplete(CommandSender cs, Command c, String label, String[] args) {
    if (args.length == 1) {
      Player p = (Player) cs;
      WaypointsManager waypointsManager = Main.getInstance().getWaypointsManager();

      // filter waypoints based on current args[0] input
      List<String> allWaypointNames = waypointsManager.getWaypoints(p.getWorld().getName());

      if (allWaypointNames != null) {
        allWaypointNames.removeIf(waypointName -> !waypointName.toLowerCase().startsWith(args[0].toLowerCase()));

        List<String> filteredWaypoints = new ArrayList<String>(allWaypointNames);
        return filteredWaypoints;
      }

    }

    return new ArrayList<>();
  }
}
