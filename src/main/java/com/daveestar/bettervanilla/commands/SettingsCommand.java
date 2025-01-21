package com.daveestar.bettervanilla.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.models.MaintenanceManager;
import com.daveestar.bettervanilla.models.SettingsManager;

public class SettingsCommand implements TabExecutor {

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {

    if (cs instanceof Player) {
      Player p = (Player) cs;

      if (args.length == 0) {
        // list all current settings whith their values/states
        SettingsManager settingsManager = Main.getInstance().getSettingsManager();
        p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "SETTINGS:");
        p.sendMessage("");
        p.sendMessage(
            Main.getPrefix() + "/settings maintenance [message] - Toggle maintenance mode and set a message");
        p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "VALUE: " + ChatColor.GRAY
            + (settingsManager.getMaintenance() ? "ON" : "OFF"));
        p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "MESSAGE: " + ChatColor.GRAY
            + settingsManager.getMaintenanceMessage());
        p.sendMessage("");
        p.sendMessage(
            Main.getPrefix() + "/settings creeperdamage - Toggle creeper entity damage");
        p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "VALUE: " + ChatColor.GRAY
            + (settingsManager.getToggleCreeperDamage() ? "ON" : "OFF"));
        p.sendMessage("");
        p.sendMessage(
            Main.getPrefix() + "/settings toggleend - Toggle 'the end' entry");
        p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "VALUE: " + ChatColor.GRAY
            + (settingsManager.getToggleEnd() ? "ON" : "OFF"));

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

      return true;
    }

    return false;
  }

  @Override
  public List<String> onTabComplete(CommandSender cs, Command c, String label, String[] args) {
    if (args.length == 1) {
      List<String> availableSettings = Arrays.asList("maintenance", "creeperdamage", "toggleend");
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
    MaintenanceManager maintenance = Main.getInstance().getMaintenanceManager();
    Boolean newState = !maintenance.getState();

    maintenance.setState(newState, message);

    String stateText = newState ? "ON" : "OFF";

    p.sendMessage(
        Main.getPrefix() + "The maintenance mode is now turned: " + ChatColor.YELLOW + ChatColor.BOLD + stateText);

    if (newState && message != null) {
      p.sendMessage(Main.getPrefix() + "Message was set to: " + ChatColor.YELLOW + message);
    }

    maintenance.kickAll(Bukkit.getOnlinePlayers());
  }

  private void _toggleCreeperDamage(Player p) {
    SettingsManager settingsManager = Main.getInstance().getSettingsManager();

    Boolean newState = !settingsManager.getToggleCreeperDamage();
    String stateText = newState ? "ON" : "OFF";

    settingsManager.setToggleCreeperDamage(newState);

    p.sendMessage(Main.getPrefix() + "Creeper damage is now turned: " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleEnd(Player p) {
    SettingsManager settingsManager = Main.getInstance().getSettingsManager();

    Boolean newState = !settingsManager.getToggleEnd();
    String stateText = newState ? "ON" : "OFF";

    settingsManager.setToggleEnd(newState);

    p.sendMessage(Main.getPrefix() + "The End is now turned: " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }
}