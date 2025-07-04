package com.daveestar.bettervanilla.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.gui.SettingsGUI;

public class SettingsCommand implements CommandExecutor {
  private final SettingsGUI _settingsGUI;

  public SettingsCommand() {
    _settingsGUI = new SettingsGUI();
  }

  @Override
  public boolean onCommand(CommandSender cs, Command command, String label, String[] args) {
    if (!(cs instanceof Player))
      return false;

    Player p = (Player) cs;
    _settingsGUI.displayGUI(p);
    return true;
  }
}
