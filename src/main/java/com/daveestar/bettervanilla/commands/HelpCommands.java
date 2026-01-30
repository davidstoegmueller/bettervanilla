package com.daveestar.bettervanilla.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.Permissions;

import net.md_5.bungee.api.ChatColor;

public class HelpCommands {
  public static class HelpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
      if (!(cs instanceof Player)) {
        cs.sendMessage(Main.getNoPlayerMessage());
        return true;
      }

      Player p = (Player) cs;

      p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "BETTERVANILLA HELP");
      p.sendMessage(Main.getShortPrefix() + ChatColor.GRAY + "Tip: Most features are toggleable in /settings.");
      p.sendMessage("");

      p.sendMessage(Main.getShortPrefix() + ChatColor.AQUA + "Player Commands");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "/settings" + ChatColor.GRAY
          + " - Open your personal settings menu.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "/help" + ChatColor.GRAY + " - View this help page.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "/waypoints" + ChatColor.GRAY
          + " - Open waypoint GUI. Use " + ChatColor.YELLOW + "player <name>" + ChatColor.GRAY + " or "
          + ChatColor.YELLOW + "coords <x> <y> <z>" + ChatColor.GRAY + ".");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "/backpack" + ChatColor.GRAY + " - Open your backpack.");
      p.sendMessage(
          Main.getShortPrefix() + ChatColor.YELLOW + "/deathpoints" + ChatColor.GRAY + " - Open death points GUI.");
      p.sendMessage(
          Main.getShortPrefix() + ChatColor.YELLOW + "/heads" + ChatColor.GRAY + " - Open the heads explorer.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "/playtime [player]" + ChatColor.GRAY
          + " - View playtime (AFK included).");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "/ping [player]" + ChatColor.GRAY + " - Check latency.");
      p.sendMessage(
          Main.getShortPrefix() + ChatColor.YELLOW + "/sit" + ChatColor.GRAY + " - Sit anywhere; sneak to stand.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "/msg <player> <message>" + ChatColor.GRAY
          + " - Send a private message.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "/r <message>" + ChatColor.GRAY
          + " - Reply to your last message.");
      p.sendMessage(
          Main.getShortPrefix() + ChatColor.YELLOW + "/here" + ChatColor.GRAY + " - Share your current location.");
      p.sendMessage("");

      p.sendMessage(Main.getShortPrefix() + ChatColor.AQUA + "Gameplay Features & How To Use");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Vein Miner" + ChatColor.GRAY
          + " - Sneak-break ores to mine the full vein (if enabled).");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Tree Chopper" + ChatColor.GRAY
          + " - Sneak-break logs to chop the full trunk (if enabled).");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Backpacks" + ChatColor.GRAY
          + " - Expandable storage accessible via /backpack.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Waypoints" + ChatColor.GRAY
          + " - Create, share, filter, and navigate via GUI.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Waypoint Navigation" + ChatColor.GRAY
          + " - Optional particle trail/beam during navigation.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Death Points" + ChatColor.GRAY
          + " - View deaths and return via GUI.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Death Chest" + ChatColor.GRAY
          + " - A chest spawns on death; take items before closing/breaking to avoid drops.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Heads Explorer" + ChatColor.GRAY
          + " - Browse decorative heads by category in a GUI.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Chest Sorting" + ChatColor.GRAY
          + " - Right-click outside inventories to auto-sort.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Item Restock" + ChatColor.GRAY
          + " - Auto-refill empty hotbar slots with matching items.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Player Tags" + ChatColor.GRAY
          + " - Set your tag and colors in /settings.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Action-Bar HUD" + ChatColor.GRAY
          + " - Toggle live coordinates and timer overlay in /settings.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Bossbar Compass" + ChatColor.GRAY
          + " - Enable a minimal directional compass in /settings.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Colored Chat & Mentions" + ChatColor.GRAY
          + " - Use & color codes and @name mentions.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Sign Colors" + ChatColor.GRAY
          + " - Apply & color codes on signs.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Sittable Stairs" + ChatColor.GRAY
          + " - Empty-hand right-click stairs to sit.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Sit Anywhere" + ChatColor.GRAY
          + " - Use /sit and sneak to stand.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Sleep to Clear Rain" + ChatColor.GRAY
          + " - Sleeping during rain skips weather and advances the day.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Crop Protection" + ChatColor.GRAY
          + " - Prevents farmland trampling.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Right-Click Harvest" + ChatColor.GRAY
          + " - Harvest and replant crops with one click.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Double Door Sync" + ChatColor.GRAY
          + " - Opening one door opens the pair automatically.");
      p.sendMessage("");

      p.sendMessage(Main.getShortPrefix() + ChatColor.AQUA + "Server & World Features");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Dynamic Tab List" + ChatColor.GRAY
          + " - Shows time, weather, online, playtime, ping, TPS, MSPT, AFK tags, deaths, vanish.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Action-Bar Location" + ChatColor.GRAY
          + " - Live XYZ coordinates (toggle in /settings).");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Timer" + ChatColor.GRAY
          + " - Global stopwatch controlled by staff.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "AFK System" + ChatColor.GRAY
          + " - AFK tracking, protection, and tab/list indicators.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Recipe Sync" + ChatColor.GRAY
          + " - Custom recipes are synced to players when enabled.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Invisible Light" + ChatColor.GRAY
          + " - Craftable invisible light source.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Invisible Item Frame" + ChatColor.GRAY
          + " - Craftable invisible item frames.");
      p.sendMessage("");

      p.sendMessage(Main.getShortPrefix() + ChatColor.GRAY
          + "Need staff tools? Use " + ChatColor.YELLOW + "/adminhelp" + ChatColor.GRAY + " (permission required).");

      return true;
    }
  }

  public static class AdminHelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
      if (!(cs instanceof Player)) {
        cs.sendMessage(Main.getNoPlayerMessage());
        return true;
      }

      Player p = (Player) cs;

      if (!p.hasPermission(Permissions.ADMINHELP.getName())) {
        p.sendMessage(Main.getNoPermissionMessage(Permissions.ADMINHELP));
        return true;
      }

      p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "BETTERVANILLA ADMIN HELP");
      p.sendMessage(
          Main.getShortPrefix() + ChatColor.GRAY + "Tip: Most server toggles live in the Admin Settings GUI.");
      p.sendMessage("");

      p.sendMessage(Main.getShortPrefix() + ChatColor.AQUA + "Moderation Tools");
      p.sendMessage(
          Main.getShortPrefix() + ChatColor.YELLOW + "/kick <player> [reason]" + ChatColor.GRAY + " - Kick a player.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "/ban <player> [duration] [reason]" + ChatColor.GRAY
          + " - Ban a player (e.g. 1d2h).");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "/unban <player>" + ChatColor.GRAY + " - Remove a ban.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "/mute <player> [duration] [reason]" + ChatColor.GRAY
          + " - Mute a player.");
      p.sendMessage(
          Main.getShortPrefix() + ChatColor.YELLOW + "/unmute <player>" + ChatColor.GRAY + " - Remove a mute.");
      p.sendMessage("");

      p.sendMessage(Main.getShortPrefix() + ChatColor.AQUA + "Staff Utilities");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "/vanish" + ChatColor.GRAY
          + " - Toggle vanish (hides from players).");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "/invsee <player>" + ChatColor.GRAY
          + " - Inspect a player inventory.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "/timer resume|pause|reset|set <time>" + ChatColor.GRAY
          + " - Control the global timer.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "/settings" + ChatColor.GRAY
          + " - Open the settings GUI and enter Admin Settings.");
      p.sendMessage("");

      p.sendMessage(Main.getShortPrefix() + ChatColor.AQUA + "Permissions Manager");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW
          + "/permissions group addperm|removeperm|delete <group> [permission]" + ChatColor.GRAY
          + " - Manage group permissions.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW
          + "/permissions user addperm|removeperm <player> <permission>" + ChatColor.GRAY
          + " - Manage player permissions.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW
          + "/permissions user setgroup <player> <group>" + ChatColor.GRAY
          + " - Assign a player to a group.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW
          + "/permissions assignments" + ChatColor.GRAY + " - List all user/group assignments.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW
          + "/permissions list" + ChatColor.GRAY + " - Show all permission nodes.");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW
          + "/permissions reload" + ChatColor.GRAY + " - Reload permissions and reapply online.");
      p.sendMessage("");

      p.sendMessage(Main.getShortPrefix() + ChatColor.AQUA + "Admin Settings Highlights");
      p.sendMessage(Main.getShortPrefix() + ChatColor.GRAY
          + "Maintenance mode, MOTD, AFK protection, sleep %, locator bar, deathchest, recipes,"
          + " crop protection, right-click harvest, waypoints, heads explorer, backpacks,"
          + " vein miner/chopper, item restock, and more.");

      return true;
    }
  }
}
