package com.daveestar.bettervanilla;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (c.getName().equalsIgnoreCase("help") && cs instanceof Player) {
      Player p = (Player) cs;

      p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "Commands:");
      p.sendMessage(Main.getPrefix() + "");
      p.sendMessage(Main.getPrefix() + "/waypoints - List all waypoints");
      p.sendMessage(Main.getPrefix() + "/waypoints add <name> - Add a waypoint to the list");
      p.sendMessage(Main.getPrefix() + "/waypoints <name> - Start navigation to waypoint");
      p.sendMessage(Main.getPrefix() + "/waypoints - List all waypoints");
      p.sendMessage(Main.getPrefix() + "");
      p.sendMessage(Main.getPrefix() + "/ping - Display your ping");
      p.sendMessage(Main.getPrefix() + "/ping <name> - Display the ping of a player");
      p.sendMessage(Main.getPrefix() + "");
      p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + "DeathChest-Feature: " + ChatColor.GRAY
          + "If you die a chest will be created on the location where you died. You can come back to the chest and open it. This enables us to never lose our items. If you close the chest inventory -> items will be dropped. If you break the chest -> items will be dropped.");

      return true;
    }
    return false;
  }
}
