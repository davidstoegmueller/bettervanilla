package com.daveestar.bettervanilla.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.CompassManager;

import net.md_5.bungee.api.ChatColor;

public class ToggleCompassCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (cs instanceof Player) {
      Player p = (Player) cs;

      if (args.length == 0) {
        CompassManager compassManager = Main.getInstance().getCompassManager();

        if (compassManager.checkPlayerActiveCompass(p)) {
          compassManager.removePlayerFromCompass(p);
        } else {
          compassManager.addPlayerToCompass(p);
        }
      } else {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/togglecompass");
      }

      return true;
    }

    return false;
  }
}
