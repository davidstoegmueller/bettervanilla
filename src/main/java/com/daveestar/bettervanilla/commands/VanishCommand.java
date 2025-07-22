package com.daveestar.bettervanilla.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.VanishManager;

import net.md_5.bungee.api.ChatColor;

public class VanishCommand implements CommandExecutor {
  private final VanishManager _vanishManager;

  public VanishCommand() {
    _vanishManager = Main.getInstance().getVanishManager();
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      return true;
    }

    Player p = (Player) sender;

    if (_vanishManager.isVanished(p)) {
      _vanishManager.unvanish(p);
      p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + "You are now visible.");
    } else {
      _vanishManager.vanish(p);
      p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + "You vanished.");
    }

    return true;
  }
}
