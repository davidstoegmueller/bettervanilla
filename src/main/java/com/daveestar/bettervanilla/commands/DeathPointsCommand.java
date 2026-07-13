package com.daveestar.bettervanilla.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.gui.DeathPointsGUI;
import com.daveestar.bettervanilla.utils.Theme;

public class DeathPointsCommand implements TabExecutor {

  private final DeathPointsGUI _deathPointsGUI;

  public DeathPointsCommand() {
    _deathPointsGUI = new DeathPointsGUI();
  }

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (!(cs instanceof Player)) {
      cs.sendMessage(Main.getNoPlayerMessage());
      return true;
    }

    Player p = (Player) cs;

    if (!p.hasPermission(Permissions.DEATHPOINTS.getName())) {
      p.sendMessage(Main.getNoPermissionMessage(p, Permissions.DEATHPOINTS));
      return true;
    }

    if (args.length == 0) {
      _deathPointsGUI.displayGUI(p);
    } else {
      p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "command-deathpoints-usage"));
    }

    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender cs, Command c, String label, String[] args) {
    return new ArrayList<>();
  }
}
