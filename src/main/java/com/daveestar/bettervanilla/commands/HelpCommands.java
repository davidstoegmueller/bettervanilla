package com.daveestar.bettervanilla.commands;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
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

      p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "COMMANDS:");
      p.sendMessage("");

      Main plugin = Main.getInstance();
      InputStream stream = plugin.getResource("plugin.yml");

      if (stream != null) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
        ConfigurationSection commands = yaml.getConfigurationSection("commands");

        if (commands != null) {
          for (String name : commands.getKeys(false)) {
            ConfigurationSection data = commands.getConfigurationSection(name);
            String desc = data != null ? data.getString("description", "") : "";

            p.sendMessage(Main.getShortPrefix() + "/" + name + " - " + desc);
          }
        }
      }

      p.sendMessage("");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Vein Mining & Vein Chopping: " + ChatColor.GRAY
          + "If the feature is enabled, you can mine or chop a whole vein of ores or logs by breaking one block of it. Make sure you have to SNEAK while breaking a block.");
      p.sendMessage("");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Chest-Sort: " + ChatColor.GRAY
          + "If you want to sort your chests inventories, you can right-click outside the chest inventory to sort it. You can toggle this feature in the settings.");
      p.sendMessage("");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Sittable-Stairs: " + ChatColor.GRAY
          + "If you want to sit down in a stair (chair) use YOUR EMPTY HAND and right-click any kind of stairs.");
      p.sendMessage("");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "Sleeping Rain: " + ChatColor.GRAY
          + "You can skip rainy days by sleeping in a bed. If you sleep in a bed while it is raining the rain will stop and the day will be skipped.");
      p.sendMessage("");
      p.sendMessage(Main.getShortPrefix() + ChatColor.YELLOW + "DeathChest-Feature: " + ChatColor.GRAY
          + "If you die a chest will be created on the location where you died. You can come back to the chest and open it. This enables us to never lose our items. If you close the chest inventory -> items will be dropped. If you break the chest -> items will be dropped.");

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

      p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "ADMIN COMMANDS:");
      p.sendMessage(Main.getShortPrefix() + "/kick <player> [reason] - Kick a player from the server");
      p.sendMessage(Main.getShortPrefix() + "/ban <player> [duration] [reason] - Ban a player from the server");
      p.sendMessage(Main.getShortPrefix() + "/unban <player> - Remove a player's ban");
      p.sendMessage(Main.getShortPrefix() + "/mute <player> [duration] [reason] - Mute a player");
      p.sendMessage(Main.getShortPrefix() + "/unmute <player> - Remove a player's mute");
      p.sendMessage("");
      p.sendMessage(Main.getShortPrefix() + "/timer resume - Resume the timer");
      p.sendMessage(Main.getShortPrefix() + "/timer pause - Pause the timer");
      p.sendMessage(Main.getShortPrefix() + "/timer reset - Reset the timer");
      p.sendMessage(Main.getShortPrefix() + "/timer set <time> - Set the timer to a specific time");
      p.sendMessage("");
      p.sendMessage(Main.getShortPrefix() + "/invsee <name> - See the inventory of a given player");
      p.sendMessage("");
      p.sendMessage(Main.getShortPrefix() + "/settings - Open the settings GUI and open the admin settings GUI");
      p.sendMessage("");
      p.sendMessage(Main.getShortPrefix()
          + "/permissions group <addperm | removeperm | delete> <username> [<permission>] - Add or remove a permission from a group");
      p.sendMessage(Main.getShortPrefix()
          + "/permissions user <addperm | removeperm> <username> <permission> - Add or remove a permission from a user");
      p.sendMessage(
          Main.getShortPrefix()
              + "/permissions user <setgroup> <username> <group> - Set the group of a user");
      p.sendMessage(
          Main.getShortPrefix()
              + "/permissions assignments - List all user and group permission assignments");
      p.sendMessage(Main.getShortPrefix() + "/permissions list - List all available permissions");
      p.sendMessage(Main.getShortPrefix()
          + "/permissions reload - Reload the permissions and reapply them to all players");

      return true;
    }
  }
}
