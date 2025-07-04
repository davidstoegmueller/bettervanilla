package com.daveestar.bettervanilla.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.gui.PlayerSettingsGUI;

public class PlayerSettingsCommand implements CommandExecutor {
  private final PlayerSettingsGUI _playerSettingsGUI;

  public PlayerSettingsCommand() {
    _playerSettingsGUI = new PlayerSettingsGUI();
  }

  @Override
  public boolean onCommand(CommandSender cs, Command command, String label, String[] args) {
    if (!(cs instanceof Player))
      return false;

    Player p = (Player) cs;
    _playerSettingsGUI.displayGUI(p);
    return true;
  }
}
