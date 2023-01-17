package com.daveestar.bettervanilla;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvseeCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (c.getName().equalsIgnoreCase("invsee") && cs instanceof Player) {
      Player p = (Player) cs;

      if (args.length == 1) {
        Player targetPlayer = (Player) Bukkit.getPlayer(args[0]);

        if (targetPlayer != null) {
          p.openInventory(targetPlayer.getInventory());
        } else {
          p.sendMessage(Main.getPrefix() + ChatColor.RED + "The requested player " + ChatColor.YELLOW + args[0]
              + ChatColor.RED + " is currently not online!");
        }
      } else {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Please use: " + ChatColor.YELLOW + "/invsee <name>");
      }

      return true;
    }
    return false;
  }
}
