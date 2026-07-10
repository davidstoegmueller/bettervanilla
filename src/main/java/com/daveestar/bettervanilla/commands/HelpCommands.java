package com.daveestar.bettervanilla.commands;

import java.util.Locale;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.utils.Theme;

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

      p.sendMessage(Main.getPrefix() + Theme.highlight() + ChatColor.BOLD + Theme.name().toUpperCase(Locale.ROOT) + " HELP");
      p.sendMessage(Main.getShortPrefix() + Theme.primary() + "Tip: Most features are toggleable in /settings.");
      p.sendMessage("");

      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Player Commands");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "/settings" + Theme.primary()
          + " - Open your personal settings menu.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "/help" + Theme.primary() + " - View this help page.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "/waypoints" + Theme.primary()
          + " - Open waypoint GUI. Use " + Theme.highlight() + "player <name>" + Theme.primary() + " or "
          + Theme.highlight() + "coords <x> <y> <z>" + Theme.primary() + ".");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "/backpack" + Theme.primary() + " - Open your backpack.");
      p.sendMessage(
          Main.getShortPrefix() + Theme.highlight() + "/deathpoints" + Theme.primary() + " - Open death points GUI.");
      p.sendMessage(
          Main.getShortPrefix() + Theme.highlight() + "/heads" + Theme.primary() + " - Open the heads explorer.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "/playtime [player]" + Theme.primary()
          + " - View playtime (AFK included).");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "/ping [player]" + Theme.primary() + " - Check latency.");
      p.sendMessage(
          Main.getShortPrefix() + Theme.highlight() + "/sit" + Theme.primary() + " - Sit anywhere; sneak to stand.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "/msg <player> <message>" + Theme.primary()
          + " - Send a private message.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "/r <message>" + Theme.primary()
          + " - Reply to your last message.");
      p.sendMessage(
          Main.getShortPrefix() + Theme.highlight() + "/here" + Theme.primary() + " - Share your current location.");
      p.sendMessage("");

      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Gameplay Features & How To Use");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Vein Miner" + Theme.primary()
          + " - Sneak-break ores to mine the full vein (if enabled).");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Tree Chopper" + Theme.primary()
          + " - Sneak-break logs to chop the full trunk (if enabled).");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Backpacks" + Theme.primary()
          + " - Expandable storage accessible via /backpack.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Waypoints" + Theme.primary()
          + " - Create, share, filter, and navigate via GUI.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Waypoint Navigation" + Theme.primary()
          + " - Optional particle trail/beam during navigation.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Death Points" + Theme.primary()
          + " - View deaths and return via GUI.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Death Chest" + Theme.primary()
          + " - A chest spawns on death; take items before closing/breaking to avoid drops.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Heads Explorer" + Theme.primary()
          + " - Browse decorative heads by category in a GUI.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Container Sorting" + Theme.primary()
          + " - Right-click outside chests or backpacks to auto-sort.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Item Restock" + Theme.primary()
          + " - Auto-refill empty hotbar slots with matching items.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Player Tags" + Theme.primary()
          + " - Set your tag and colors in /settings.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Action-Bar HUD" + Theme.primary()
          + " - Toggle live coordinates and timer overlay in /settings.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Bossbar Compass" + Theme.primary()
          + " - Enable a minimal directional compass in /settings.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Colored Chat & Mentions" + Theme.primary()
          + " - Use & color codes and @name mentions.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Sign Colors" + Theme.primary()
          + " - Apply & color codes on signs.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Sittable Stairs" + Theme.primary()
          + " - Empty-hand right-click stairs to sit.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Sit Anywhere" + Theme.primary()
          + " - Use /sit and sneak to stand.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Sleep to Clear Rain" + Theme.primary()
          + " - Sleeping during rain skips weather and advances the day.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Crop Protection" + Theme.primary()
          + " - Prevents farmland trampling.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Right-Click Harvest" + Theme.primary()
          + " - Harvest and replant crops with one click.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Double Door Sync" + Theme.primary()
          + " - Opening one door opens the pair automatically.");
      p.sendMessage("");

      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Server & World Features");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Dynamic Tab List" + Theme.primary()
          + " - Shows time, weather, online, playtime, ping, TPS, MSPT, AFK tags, deaths, vanish.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Action-Bar Location" + Theme.primary()
          + " - Live XYZ coordinates (toggle in /settings).");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Timer" + Theme.primary()
          + " - Global stopwatch controlled by staff.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "AFK System" + Theme.primary()
          + " - AFK tracking, protection, and tab/list indicators.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Recipe Sync" + Theme.primary()
          + " - Custom recipes are synced to players when enabled.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Invisible Light" + Theme.primary()
          + " - Craftable invisible light source.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Invisible Item Frame" + Theme.primary()
          + " - Craftable invisible item frames.");
      p.sendMessage("");

      p.sendMessage(Main.getShortPrefix() + Theme.primary()
          + "Need staff tools? Use " + Theme.highlight() + "/adminhelp" + Theme.primary() + " (permission required).");

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

      p.sendMessage(Main.getPrefix() + Theme.highlight() + ChatColor.BOLD + Theme.name().toUpperCase(Locale.ROOT) + " ADMIN HELP");
      p.sendMessage(
          Main.getShortPrefix() + Theme.primary() + "Tip: Most server toggles live in the Admin Settings GUI.");
      p.sendMessage("");

      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Moderation Tools");
      p.sendMessage(
          Main.getShortPrefix() + Theme.highlight() + "/kick <player> [reason]" + Theme.primary() + " - Kick a player.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "/ban <player> [duration] [reason]" + Theme.primary()
          + " - Ban a player (e.g. 1d2h).");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "/unban <player>" + Theme.primary() + " - Remove a ban.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "/mute <player> [duration] [reason]" + Theme.primary()
          + " - Mute a player.");
      p.sendMessage(
          Main.getShortPrefix() + Theme.highlight() + "/unmute <player>" + Theme.primary() + " - Remove a mute.");
      p.sendMessage("");

      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Staff Utilities");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "/vanish" + Theme.primary()
          + " - Toggle vanish (hides from players).");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "/invsee <player>" + Theme.primary()
          + " - Inspect a player inventory.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "/timer resume|pause|reset|set <time>" + Theme.primary()
          + " - Control the global timer.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "/settings" + Theme.primary()
          + " - Open the settings GUI and enter Admin Settings.");
      p.sendMessage("");

      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Permissions Manager");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight()
          + "/permissions group addperm|removeperm|delete <group> [permission]" + Theme.primary()
          + " - Manage group permissions.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight()
          + "/permissions user addperm|removeperm <player> <permission>" + Theme.primary()
          + " - Manage player permissions.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight()
          + "/permissions user setgroup <player> <group>" + Theme.primary()
          + " - Assign a player to a group.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight()
          + "/permissions assignments" + Theme.primary() + " - List all user/group assignments.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight()
          + "/permissions list" + Theme.primary() + " - Show all permission nodes.");
      p.sendMessage(Main.getShortPrefix() + Theme.highlight()
          + "/permissions reload" + Theme.primary() + " - Reload permissions and reapply online.");
      p.sendMessage("");

      p.sendMessage(Main.getShortPrefix() + Theme.highlight() + "Admin Settings Highlights");
      p.sendMessage(Main.getShortPrefix() + Theme.primary()
          + "Maintenance mode, MOTD, AFK protection, sleep %, locator bar, deathchest, recipes,"
          + " crop protection, right-click harvest, waypoints, heads explorer, backpacks,"
          + " vein miner/chopper, item restock, and more.");

      return true;
    }
  }
}
