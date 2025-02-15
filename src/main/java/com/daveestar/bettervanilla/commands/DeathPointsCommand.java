package com.daveestar.bettervanilla.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.gui.DeathPointsGUI;

import net.md_5.bungee.api.ChatColor;

public class DeathPointsCommand implements TabExecutor {

  private final DeathPointsGUI _deathPointsGUI;

  public DeathPointsCommand() {
    _deathPointsGUI = new DeathPointsGUI();
  }

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (!(cs instanceof Player)) {
      return true;
    }

    Player p = (Player) cs;

    if (args.length == 0) {
      _deathPointsGUI.displayGUI(p);
    } else {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/deathpoints");
    }

    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender cs, Command c, String label, String[] args) {
    return new ArrayList<>();
  }
}