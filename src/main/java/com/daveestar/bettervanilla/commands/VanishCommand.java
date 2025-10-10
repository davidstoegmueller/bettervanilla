package com.daveestar.bettervanilla.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.manager.VanishManager;

import net.md_5.bungee.api.ChatColor;

public class VanishCommand implements CommandExecutor {
  private final VanishManager _vanishManager;

  public VanishCommand() {
    _vanishManager = Main.getInstance().getVanishManager();
  }

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (!(cs instanceof Player)) {
      cs.sendMessage(Main.getNoPlayerMessage());
      return true;
    }

    Player p = (Player) cs;

    if (!p.hasPermission(Permissions.VANISH.getName())) {
      p.sendMessage(Main.getNoPermissionMessage(Permissions.VANISH));
      return true;
    }

    if (_vanishManager.isVanished(p)) {
      _vanishManager.unvanish(p);
      p.sendMessage(Main.getPrefix() + ChatColor.GRAY + "You are now visible.");
    } else {
      _vanishManager.vanish(p);
      p.sendMessage(Main.getPrefix() + ChatColor.GRAY + "You vanished.");
    }

    return true;
  }
}
