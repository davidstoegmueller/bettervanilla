package com.daveestar.bettervanilla.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;

import net.md_5.bungee.api.ChatColor;

public class HelpCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (c.getName().equalsIgnoreCase("help") && cs instanceof Player) {
      Player p = (Player) cs;

      p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "COMMANDS:");
      p.sendMessage("");
      p.sendMessage(Main.getShortPrefix() + "/waypoints - Open the waypoints GUI");
      p.sendMessage(Main.getShortPrefix() + "/waypoints help - Display the waypoints help");
      p.sendMessage("");
      p.sendMessage(Main.getShortPrefix() + "/lastdeath - Navigate to your latest death point");
      p.sendMessage(Main.getShortPrefix() + "/lastdeath cancel - Cancel the death point navigation");
      p.sendMessage("");
      p.sendMessage(Main.getShortPrefix() + "/ping - Display your ping");
      p.sendMessage(Main.getShortPrefix() + "/ping <name> - Display the ping of a player");
      p.sendMessage("");
      p.sendMessage(Main.getShortPrefix() + "/togglelocation - Display your current location in action bar");
      p.sendMessage(Main.getShortPrefix() + "/togglecompass - Display you directions as a compass in the bossbar");
      p.sendMessage("");
      p.sendMessage(
          Main.getShortPrefix() + "/playtime <player> - Display the playtime of yourself or another player");
      p.sendMessage("");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Sittable-Stairs: " + ChatColor.GRAY
          + "If you want to sit down in a stair (chair) use YOUR EMPTY HAND and right-clicl any kind of stairs.");
      p.sendMessage("");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Sleeping Rain: " + ChatColor.GRAY
          + "You can skip rainy days by sleeping in a bed. If you sleep in a bed while it is raining the rain will stop and the day will be skipped.");
      p.sendMessage("");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "DeathChest-Feature: " + ChatColor.GRAY
          + "If you die a chest will be created on the location where you died. You can come back to the chest and open it. This enables us to never lose our items. If you close the chest inventory -> items will be dropped. If you break the chest -> items will be dropped.");

      return true;
    }

    if (c.getName().equalsIgnoreCase("adminhelp") && cs instanceof Player) {
      Player p = (Player) cs;

      p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "ADMIN COMMANDS:");
      p.sendMessage(Main.getShortPrefix() + "/waypoints remove <name> - Remove an existing waypoint");
      p.sendMessage("");
      p.sendMessage(Main.getShortPrefix() + "/timer resume - Resume the timer");
      p.sendMessage(Main.getShortPrefix() + "/timer pause - Pause the timer");
      p.sendMessage(Main.getShortPrefix() + "/timer reset - Reset the timer");
      p.sendMessage(Main.getShortPrefix() + "/timer set <time> - Set the timer to a specific time");
      p.sendMessage("");
      p.sendMessage(Main.getShortPrefix() + "/invsee <name> - See the inventory of a given player");
      p.sendMessage("");
      p.sendMessage(Main.getShortPrefix() + "/settings <settingname> - Set and list global settings");
      p.sendMessage("");
      p.sendMessage(Main.getShortPrefix() + ChatColor.RED + "Info: " + ChatColor.GRAY
          + "Users need to rejoin to apply changes to their permissions");
      p.sendMessage(Main.getShortPrefix()
          + "/permissions group <addperm | removeperm> <username> <permission> - Add or remove a permission from a group");
      p.sendMessage(Main.getShortPrefix()
          + "/permissions user <addperm | removeperm> <username> <permission> - Add or remove a permission from a user");
      p.sendMessage(
          Main.getShortPrefix() + " » " + "/permissions user <setgroup> <username> <group> - Set the group of a user");
      p.sendMessage(Main.getShortPrefix() + "/permissions list - List all permissions");

      return true;
    }
    return false;
  }
}
