package com.daveestar.bettervanilla.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;

public class HelpCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (c.getName().equalsIgnoreCase("help") && cs instanceof Player) {
      Player p = (Player) cs;

      p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "Commands:");
      p.sendMessage("");
      p.sendMessage(Main.getPrefix() + "/waypoints - List all waypoints");
      p.sendMessage(Main.getPrefix() + "/waypoints add <name> - Add a waypoint to the list");
      p.sendMessage(Main.getPrefix() + "/waypoints <name> - Start navigation to waypoint");
      p.sendMessage(Main.getPrefix() + "/waypoints cancel - Cancel the current navigation");
      p.sendMessage("");
      p.sendMessage(Main.getPrefix() + "/lastdeath - Navigate to your latest death point");
      p.sendMessage(Main.getPrefix() + "/lastdeath cancel - Cancel the death point navigation");
      p.sendMessage("");
      p.sendMessage(Main.getPrefix() + "/ping - Display your ping");
      p.sendMessage(Main.getPrefix() + "/ping <name> - Display the ping of a player");
      p.sendMessage("");
      p.sendMessage(Main.getPrefix() + "/togglelocation - Display your current location in action bar");
      p.sendMessage(Main.getPrefix() + "/togglecompass - Display you directions as a compass in the bossbar");
      p.sendMessage("");
      p.sendMessage(Main.getPrefix() + "/playerhead <name> - Get a head of a player");
      p.sendMessage("");
      p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + "Sittable-Stairs: " + ChatColor.GRAY
          + "If you want to sit down in a stair (chair) use YOUR EMPTY HAND and right-clicl any kind of stairs.");
      p.sendMessage("");
      p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + "DeathChest-Feature: " + ChatColor.GRAY
          + "If you die a chest will be created on the location where you died. You can come back to the chest and open it. This enables us to never lose our items. If you close the chest inventory -> items will be dropped. If you break the chest -> items will be dropped.");

      return true;
    }

    if (c.getName().equalsIgnoreCase("adminhelp") && cs instanceof Player) {
      Player p = (Player) cs;

      p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "Admin Commands:");
      p.sendMessage(Main.getPrefix() + "/waypoints remove <name> - Remove an existing waypoint");
      p.sendMessage("");
      p.sendMessage(Main.getPrefix() + "/timer resume - Resume the timer");
      p.sendMessage(Main.getPrefix() + "/timer pause - Pause the timer");
      p.sendMessage(Main.getPrefix() + "/timer reset - Reset the timer");
      p.sendMessage(Main.getPrefix() + "/timer set <time> - Set the timer to a specific time");
      p.sendMessage("");
      p.sendMessage(Main.getPrefix() + "/invsee <name> - See the inventory of a given player");
      p.sendMessage("");
      p.sendMessage(Main.getPrefix() + "/maintenance - Toggle the maintenance mode of the server");

      return true;
    }
    return false;
  }
}
