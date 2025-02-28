package com.daveestar.bettervanilla.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.CompassManager;

import net.md_5.bungee.api.ChatColor;

public class ToggleCompassCommand implements CommandExecutor {

  private final Main _plugin;
  private final CompassManager _compassManager;

  public ToggleCompassCommand() {
    _plugin = Main.getInstance();
    _compassManager = _plugin.getCompassManager();
  }

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (cs instanceof Player) {
      Player p = (Player) cs;

      if (args.length == 0) {
        if (_compassManager.checkPlayerActiveCompass(p)) {
          _compassManager.removePlayerFromCompass(p);
        } else {
          _compassManager.addPlayerToCompass(p);
        }
      } else {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/togglecompass");
      }

      return true;
    }

    return false;
  }
}
