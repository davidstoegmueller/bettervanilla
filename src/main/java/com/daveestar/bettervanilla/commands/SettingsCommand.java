package com.daveestar.bettervanilla.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.gui.SettingsGUI;

import net.md_5.bungee.api.ChatColor;

public class SettingsCommand implements CommandExecutor {
  private final SettingsGUI _settingsGUI;

  public SettingsCommand() {
    _settingsGUI = new SettingsGUI();
  }

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (!(cs instanceof Player)) {
      cs.sendMessage(Main.getNoPlayerMessage());
      return true;
    }

    Player p = (Player) cs;

    if (!p.hasPermission(Permissions.SETTINGS.getName()) && !p.hasPermission(Permissions.ADMINSETTINGS.getName())) {
      p.sendMessage(Main.getNoPermissionMessage(Permissions.SETTINGS));
      p.sendMessage(Main.getNoPermissionMessage(Permissions.ADMINSETTINGS));
      return true;
    }

    if (args.length == 0) {
      _settingsGUI.displayGUI(p);
    } else {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/settings");
    }

    return true;
  }
}
