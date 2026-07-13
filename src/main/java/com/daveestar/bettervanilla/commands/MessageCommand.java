package com.daveestar.bettervanilla.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.manager.MessageManager;
import com.daveestar.bettervanilla.utils.Theme;

public class MessageCommand implements TabExecutor {
  private final Main _plugin;
  private final MessageManager _messageManager;

  public MessageCommand() {
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
      p.sendMessage(Main.getNoPermissionMessage(p, Permissions.MSG));
      return true;
    }

    if (args.length >= 2) {
      Player target = Bukkit.getPlayer(args[0]);

      if (target == null || !target.isOnline()) {
        p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "common-error-player-offline",
            "player", Theme.highlight() + args[0] + Theme.error()));
      } else if (!target.getUniqueId().equals(p.getUniqueId())) {
          String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
          _messageManager.sendPrivateMessage(p, target, message);
      } else {
        p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "command-message-error-self"));
      }
    } else {
      p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "command-message-usage"));
    }

    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender cs, Command c, String label, String[] args) {
    List<String> suggestions = new ArrayList<>();

    if (args.length == 1) {
      Collection<? extends Player> onlinePlayers = _plugin.getServer().getOnlinePlayers();
      suggestions.addAll(onlinePlayers.stream().map(Player::getName).collect(Collectors.toList()));
    }

    return suggestions;
  }
}
