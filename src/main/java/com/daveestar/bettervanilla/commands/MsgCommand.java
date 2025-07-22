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
import com.daveestar.bettervanilla.manager.MessageManager;

import net.md_5.bungee.api.ChatColor;

public class MsgCommand implements TabExecutor {
  private final Main _plugin;
  private final MessageManager _messageManager;

  public MsgCommand() {
    _plugin = Main.getInstance();
    _messageManager = _plugin.getMessageManager();
  }

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (cs instanceof Player) {
      Player p = (Player) cs;

      if (args.length >= 2) {
        Player target = Bukkit.getPlayer(args[0]);

        if (!target.getUniqueId().equals(p.getUniqueId())) {
          if (target != null && target.isOnline()) {
            String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            _messageManager.sendPrivateMessage(p, target, message);
          } else {
            p.sendMessage(Main.getPrefix() + ChatColor.RED + "The requested player " + ChatColor.YELLOW + args[0]
                + ChatColor.RED + " is currently not online!");
          }
        } else {
          p.sendMessage(Main.getPrefix() + ChatColor.RED + "You cannot send a message to yourself!");
        }
      } else {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/msg <player> <message>");
      }

      return true;
    }

    return false;
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
