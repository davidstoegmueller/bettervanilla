package com.daveestar.bettervanilla;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleLocationCommand implements CommandExecutor {
  public static HashMap<Player, Location> showLocation = new HashMap<Player, Location>();

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (c.getName().equalsIgnoreCase("togglelocation") && cs instanceof Player) {
      Player p = (Player) cs;

      if (args.length == 0) {
        if (showLocation.containsKey(p)) {
          showLocation.remove(p);
          PlayerMove.cancelTask(p);
        } else {
          showLocation.put(p, p.getLocation());
          WaypointsCommand.showWaypointCoords.remove(p);

          String displayCoordsCurrent = ChatColor.YELLOW + "X: "
              + ChatColor.GRAY
              + p.getLocation().getBlockX() + ChatColor.YELLOW
              + " Y: " + ChatColor.GRAY + p.getLocation().getBlockY() + ChatColor.YELLOW + " Z: " + ChatColor.GRAY
              + p.getLocation().getBlockZ();

          PlayerMove.displayActionBar(p, displayCoordsCurrent);
        }
      } else {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Plase use: " + ChatColor.YELLOW + "/togglelocation");
      }

      return true;
    }
    return false;
  }
}
