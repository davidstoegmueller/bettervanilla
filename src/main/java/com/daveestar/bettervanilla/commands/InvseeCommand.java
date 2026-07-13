package com.daveestar.bettervanilla.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.utils.Theme;

public class InvseeCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (!(cs instanceof Player)) {
      cs.sendMessage(Main.getNoPlayerMessage());
      return true;
    }

    Player p = (Player) cs;

    if (!p.hasPermission(Permissions.INVSEE.getName())) {
      p.sendMessage(Main.getNoPermissionMessage(p, Permissions.INVSEE));
      return true;
    }

    if (args.length == 1) {
      Player targetPlayer = (Player) Bukkit.getPlayer(args[0]);

      if (targetPlayer != null) {
        p.openInventory(targetPlayer.getInventory());
      } else {
        p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "common-error-player-offline",
            "player", Theme.highlight() + args[0] + Theme.error()));
      }
    } else {
      p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "command-invsee-usage"));
    }

    return true;
  }
}
