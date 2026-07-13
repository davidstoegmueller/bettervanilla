package com.daveestar.bettervanilla.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.utils.Theme;

public class PingCommand implements CommandExecutor {
  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (!(cs instanceof Player)) {
      cs.sendMessage(Main.getNoPlayerMessage());
      return true;
    }

    Player p = (Player) cs;

    if (!p.hasPermission(Permissions.PING.getName())) {
      p.sendMessage(Main.getNoPermissionMessage(p, Permissions.PING));
      return true;
    }

    if (args.length == 0) {
      // show ping for current player
      p.sendMessage(
          Main.getPrefix() + Main.tr(p, "command-ping-self", "ping", Theme.highlight().toString() + p.getPing()));
    } else if (args.length == 1) {
      // show ping for specific player
      Player targetPlayer = (Player) Bukkit.getPlayer(args[0]);

      if (targetPlayer != null) {
        p.sendMessage(Main.getPrefix() + Main.tr(p, "command-ping-other",
            "player", Theme.highlight() + targetPlayer.getName() + Theme.primary(),
            "ping", Theme.highlight().toString() + targetPlayer.getPing()));
      } else {
        p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "common-error-player-offline",
            "player", Theme.highlight() + args[0] + Theme.error()));
      }

    } else {
      p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "command-ping-usage"));
    }

    return true;
  }
}
