package com.daveestar.bettervanilla;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MaintenanceCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {

    if (c.getName().equalsIgnoreCase("maintenance") && cs instanceof Player) {
      Player p = (Player) cs;

      if (args.length > 0) {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Please use: " + ChatColor.YELLOW
            + "/maintenance");
        return true;
      }

      // set new maintenance state based on the current maintenance state
      Maintenance maintenance = Main.getInstance().getMaintenance();
      Boolean newState = !maintenance.getState();

      maintenance.setState(newState);

      String stateText = newState ? "ON" : "OFF";

      p.sendMessage(
          Main.getPrefix() + "The maintenance mode is now turned " + ChatColor.YELLOW + ChatColor.BOLD + stateText);

      maintenance.kickAll(Bukkit.getOnlinePlayers());

      return true;
    }

    return false;
  }
}