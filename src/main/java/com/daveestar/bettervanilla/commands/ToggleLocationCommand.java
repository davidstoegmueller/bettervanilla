package com.daveestar.bettervanilla.commands;

import org.bukkit.ChatColor;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.models.WaypointsManager;

public class ToggleLocationCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (c.getName().equalsIgnoreCase("togglelocation") && cs instanceof Player) {
      Player p = (Player) cs;

      WaypointsManager waypointsManager = Main.getInstance().getWaypointsManager();

      if (args.length == 0) {
        if (waypointsManager.checkPlayerActiveToggleLocationNavigation(p)) {
          waypointsManager.removePlayerActiveToggleLocationNavigation(p);
        } else {
          waypointsManager.addPlayerActiveToggleLocationNavigation(p, p.getLocation());
          waypointsManager.removePlayerActiveWaypointNavigation(p);

          Biome playerBiome = p.getWorld().getBiome(p.getLocation());

          String displayCoordsCurrent = ChatColor.YELLOW + "X: "
              + ChatColor.GRAY
              + p.getLocation().getBlockX() + ChatColor.YELLOW
              + " Y: " + ChatColor.GRAY + p.getLocation().getBlockY() + ChatColor.YELLOW + " Z: " + ChatColor.GRAY
              + p.getLocation().getBlockZ() + ChatColor.RED + ChatColor.BOLD + " | "
              + ChatColor.GRAY + playerBiome.name();

          waypointsManager.displayActionBar(p, displayCoordsCurrent);
        }
      } else {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Plase use: " + ChatColor.YELLOW + "/togglelocation");
      }

      return true;
    }
    return false;
  }
}
