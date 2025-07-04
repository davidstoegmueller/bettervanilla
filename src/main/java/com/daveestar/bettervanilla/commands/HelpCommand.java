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

import net.md_5.bungee.api.ChatColor;

public class HelpCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (c.getName().equalsIgnoreCase("help") && cs instanceof Player) {
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
      p.sendMessage(Main.getShortPrefix()
          + "/permissions group <addperm | removeperm> <username> <permission> - Add or remove a permission from a group");
      p.sendMessage(Main.getShortPrefix()
          + "/permissions user <addperm | removeperm> <username> <permission> - Add or remove a permission from a user");
      p.sendMessage(
          Main.getShortPrefix() + " » "
              + "/permissions user <setgroup> <username> <group> - Set the group of a user");
      p.sendMessage(
          Main.getShortPrefix()
              + "/permissions assignments - List all user and group permission assignments");
      p.sendMessage(Main.getShortPrefix() + "/permissions list - List all available permissions");
      p.sendMessage(Main.getShortPrefix()
          + "/permissions reload - Reload the permissions and reapply them to all players");

      return true;
    }
    return false;
  }
}
