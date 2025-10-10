package com.daveestar.bettervanilla.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.manager.MessageManager;

import net.md_5.bungee.api.ChatColor;

public class ReplyCommand implements CommandExecutor {
  private final Main _plugin;
  private final MessageManager _messageManager;

  public ReplyCommand() {
    _plugin = Main.getInstance();
    _messageManager = _plugin.getMessageManager();
  }

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (!(cs instanceof Player)) {
      cs.sendMessage(Main.getNoPlayerMessage());
      return true;
    }

    Player p = (Player) cs;

    if (!p.hasPermission(Permissions.MSG.getName())) {
      p.sendMessage(Main.getNoPermissionMessage(Permissions.MSG));
      return true;
    }

    if (args.length >= 1) {
      Player target = _messageManager.getReplyTarget(p);

      if (target != null) {
        String message = String.join(" ", args);
        _messageManager.sendPrivateMessage(p, target, message);
      } else {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "You have no one to reply to.");
      }
    } else {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/r <message>");
    }

    return true;
  }
}
