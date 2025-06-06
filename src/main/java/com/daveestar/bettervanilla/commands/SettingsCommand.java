package com.daveestar.bettervanilla.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.MaintenanceManager;
import com.daveestar.bettervanilla.manager.SettingsManager;

import net.md_5.bungee.api.ChatColor;

public class SettingsCommand implements TabExecutor {

  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final MaintenanceManager _maintenanceManager;

  public SettingsCommand() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
    _maintenanceManager = _plugin.getMaintenanceManager();
  }

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {

    if (cs instanceof Player) {
      Player p = (Player) cs;

      if (args.length == 0) {
        // list all current settings whith their values/states
        p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "SETTINGS:");
        p.sendMessage("");
        p.sendMessage(
            Main.getShortPrefix() + "/settings maintenance [message] - Toggle maintenance mode and set a message");
        p.sendMessage(ChatColor.YELLOW + "     » " + ChatColor.YELLOW + ChatColor.BOLD + "VALUE: " + ChatColor.GRAY
            + (_settingsManager.getMaintenance() ? "ON" : "OFF"));
        p.sendMessage(ChatColor.YELLOW + "     » " + ChatColor.YELLOW + ChatColor.BOLD + "MESSAGE: " + ChatColor.GRAY
            + _settingsManager.getMaintenanceMessage());
        p.sendMessage("");
        p.sendMessage(
            Main.getShortPrefix() + "/settings creeperdamage - Toggle creeper entity damage");
        p.sendMessage(ChatColor.YELLOW + "     » " + ChatColor.YELLOW + ChatColor.BOLD + "VALUE: " + ChatColor.GRAY
            + (_settingsManager.getToggleCreeperDamage() ? "ON" : "OFF"));
        p.sendMessage("");
        p.sendMessage(
            Main.getShortPrefix() + "/settings toggleend - Toggle 'the end' entry");
        p.sendMessage(ChatColor.YELLOW + "     » " + ChatColor.YELLOW + ChatColor.BOLD + "VALUE: " + ChatColor.GRAY
            + (_settingsManager.getToggleEnd() ? "ON" : "OFF"));
        p.sendMessage("");
        p.sendMessage(
            Main.getShortPrefix() + "/settings sleepingrain - Toggle sleep during rain");
        p.sendMessage(ChatColor.YELLOW + "     » " + ChatColor.YELLOW + ChatColor.BOLD + "VALUE: " + ChatColor.GRAY
            + (_settingsManager.getSleepingRain() ? "ON" : "OFF"));
        p.sendMessage("");
        p.sendMessage(
            Main.getShortPrefix()
                + "/settings afktime <minutes> - Set the time in minutes until a player is marked as AFK");
        p.sendMessage(ChatColor.YELLOW + "     » " + ChatColor.YELLOW + ChatColor.BOLD + "VALUE: " + ChatColor.GRAY
            + _settingsManager.getAFKTime() + " minutes");

        return true;
      }

      if (args[0].equalsIgnoreCase("maintenance")) {
        _toggleMaintenance(p, args);
        return true;
      }

      if (args[0].equalsIgnoreCase("creeperdamage")) {
        if (args.length > 1) {
          p.sendMessage(
              Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/settings creeperdamage");
          return true;
        }

        _toggleCreeperDamage(p);
        return true;
      }

      if (args[0].equalsIgnoreCase("toggleend")) {
        if (args.length > 1) {
          p.sendMessage(
              Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/settings toggleend");
          return true;
        }

        _toggleEnd(p);
        return true;
      }

      if (args[0].equalsIgnoreCase("sleepingrain")) {
        if (args.length > 1) {
          p.sendMessage(
              Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/settings sleepingrain");
          return true;
        }

        _toggleSleepingRain(p);
        return true;
      }

      if (args[0].equalsIgnoreCase("afktime")) {
        if (args.length != 2) {
          p.sendMessage(
              Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/settings afktime <minutes>");
          return true;
        }

        _setAFKTime(p, args);
        return true;
      }

      return true;
    }

    return false;
  }

  @Override
  public List<String> onTabComplete(CommandSender cs, Command c, String label, String[] args) {
    if (args.length == 1) {
      List<String> availableSettings = Arrays.asList("maintenance", "creeperdamage", "toggleend", "sleepingrain",
          "afktime");
      return availableSettings;
    }

    return new ArrayList<>();
  }

  private void _toggleMaintenance(Player p, String[] args) {
    String message = null;

    // check if more than one argument is passed (message)
    if (args.length > 1) {
      StringBuilder sb = new StringBuilder();

      // concat all arguments to one string
      for (int i = 1; i < args.length; i++) {
        sb.append(args[i]);
        if (i < args.length - 1) {
          sb.append(" ");
        }
      }

      message = sb.toString();
    }

    // set new maintenance state based on the current maintenance state
    Boolean newState = !_maintenanceManager.getState();

    _maintenanceManager.setState(newState, message);

    String stateText = newState ? "ON" : "OFF";

    p.sendMessage(
        Main.getPrefix() + "The maintenance mode is now turned: " + ChatColor.YELLOW + ChatColor.BOLD + stateText);

    if (newState && message != null) {
      p.sendMessage(Main.getPrefix() + "Message was set to: " + ChatColor.YELLOW + message);
    }

    _maintenanceManager.kickAll(_plugin.getServer().getOnlinePlayers());
  }

  private void _toggleCreeperDamage(Player p) {
    Boolean newState = !_settingsManager.getToggleCreeperDamage();
    String stateText = newState ? "ON" : "OFF";

    _settingsManager.setToggleCreeperDamage(newState);

    p.sendMessage(Main.getPrefix() + "Creeper damage is now turned: " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleEnd(Player p) {
    Boolean newState = !_settingsManager.getToggleEnd();
    String stateText = newState ? "ON" : "OFF";

    _settingsManager.setToggleEnd(newState);

    p.sendMessage(Main.getPrefix() + "The End is now turned: " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleSleepingRain(Player p) {
    Boolean newState = !_settingsManager.getSleepingRain();
    String stateText = newState ? "ON" : "OFF";

    _settingsManager.setSleepingRain(newState);

    p.sendMessage(Main.getPrefix() + "Sleeping Rain is now turned: " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _setAFKTime(Player p, String[] args) {
    int minutes;

    try {
      minutes = Integer.parseInt(args[1]);
    } catch (NumberFormatException e) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Please provide a valid number of minutes.");
      return;
    }

    _settingsManager.setAFKTime(minutes);

    p.sendMessage(
        Main.getPrefix() + "AFK time was set to: " + ChatColor.YELLOW + ChatColor.BOLD + minutes + " minutes");
  }
}