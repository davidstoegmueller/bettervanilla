package com.daveestar.bettervanilla.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;

import net.md_5.bungee.api.ChatColor;

public class PingCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (c.getName().equalsIgnoreCase("ping") && cs instanceof Player) {
      Player p = (Player) cs;

      if (args.length == 0) {
        // show ping for current player
        p.sendMessage(
            Main.getPrefix() + "Your ping is " + ChatColor.YELLOW + p.getPing() + "ms");
      } else if (args.length == 1) {
        // show ping for specific player
        Player targetPlayer = (Player) Bukkit.getPlayer(args[0]);

        if (targetPlayer != null) {
          p.sendMessage(
              Main.getPrefix() + "The ping of " + ChatColor.YELLOW + targetPlayer.getName() + ChatColor.GRAY + " is "
                  + ChatColor.YELLOW + targetPlayer.getPing() + "ms");
        } else {
          p.sendMessage(Main.getPrefix() + ChatColor.RED + "The requested player " + ChatColor.YELLOW + args[0]
              + ChatColor.RED + " is currently not online!");
        }

      } else {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Please use: " + ChatColor.YELLOW + "/ping [name]");
      }

      return true;
    }
    return false;
  }
}
