package com.daveestar.bettervanilla.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.gui.HeadsGUI;
import com.daveestar.bettervanilla.manager.HeadsManager;
import com.daveestar.bettervanilla.manager.SettingsManager;

import net.md_5.bungee.api.ChatColor;

public class HeadsCommand implements CommandExecutor {
  private final HeadsGUI _headsGUI;
  private final HeadsManager _headsManager;
  private final SettingsManager _settingsManager;

  public HeadsCommand() {
    Main plugin = Main.getInstance();
    _headsGUI = new HeadsGUI();
    _headsManager = plugin.getHeadsManager();
    _settingsManager = plugin.getSettingsManager();
  }

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (!(cs instanceof Player)) {
      cs.sendMessage(Main.getNoPlayerMessage());
      return true;
    }

    Player p = (Player) cs;

    if (!p.hasPermission(Permissions.HEADS.getName())) {
      p.sendMessage(Main.getNoPermissionMessage(Permissions.HEADS));
      return true;
    }

    if (args.length > 0) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/heads");
      return true;
    }

    if (!_settingsManager.getHeadsExplorerEnabled()) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Heads Explorer is disabled.");
      return true;
    }

    if (_headsManager.getTotalCustomHeads() <= 0 || _headsManager.getTotalCustomHeadCategories() <= 0) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Heads data is not available yet. Try again later.");
      return true;
    }

    _headsGUI.displayHeadsGUI(p);
    return true;
  }
}
