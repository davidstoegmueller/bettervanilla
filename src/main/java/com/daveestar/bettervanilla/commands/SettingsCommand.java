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

public class SettingsCommand implements TabExecutor {

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {

    if (c.getName().equalsIgnoreCase("settings") && cs instanceof Player) {
      Player p = (Player) cs;

      if (args.length == 0) {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Please use: " + ChatColor.YELLOW
            + "/settings <maintenance>");
        return true;
      }

      if (args.length == 1 && args[0].equalsIgnoreCase("maintenance")) {
        _toggleMaintenance(p);
        return true;
      }

      // TODO: spot to implement more global settings
      return true;
    }

    return false;
  }

  @Override
  public List<String> onTabComplete(CommandSender cs, Command c, String label, String[] args) {
    if (args.length == 1) {
      List<String> availableSettings = Arrays.asList("maintenance");
      return availableSettings;
    }

    return new ArrayList<>();
  }

  private void _toggleMaintenance(Player p) {
    // set new maintenance state based on the current maintenance state
    MaintenanceManager maintenance = Main.getInstance().getMaintenanceManager();
    Boolean newState = !maintenance.getState();

    maintenance.setState(newState);

    String stateText = newState ? "ON" : "OFF";

    p.sendMessage(
        Main.getPrefix() + "The maintenance mode is now turned " + ChatColor.YELLOW + ChatColor.BOLD + stateText);

    maintenance.kickAll(Bukkit.getOnlinePlayers());
  }
}