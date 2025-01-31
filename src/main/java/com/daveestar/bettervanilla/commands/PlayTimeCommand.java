package com.daveestar.bettervanilla.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.TimerManager;

import net.md_5.bungee.api.ChatColor;

public class PlayTimeCommand implements TabExecutor {
  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (cs instanceof Player) {
      Player p = (Player) cs;

      if (args.length >= 0 && args.length <= 1) {
        TimerManager timerManager = Main.getInstance().getTimerManager();
        Player targetPlayer;

        if (args.length == 1) {
          targetPlayer = Bukkit.getPlayer(args[0]);
        } else {
          targetPlayer = p;
        }

        if (targetPlayer == null) {
          p.sendMessage(Main.getPrefix() + ChatColor.RED + "The requested player " +
              ChatColor.YELLOW + args[0] + ChatColor.RED + " is currently not online!");
          return true;
        }

        int playTime = timerManager.getPlayTime(targetPlayer);
        int afkTime = timerManager.getAFKTime(targetPlayer);

        p.sendMessage(
            Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "PLAYTIME" + ChatColor.RESET + ChatColor.YELLOW
                + " of " + ChatColor.GRAY + targetPlayer.getName());
        p.sendMessage("");
        p.sendMessage(Main.getPrefix() +
            "Playtime (TOTAL): " + ChatColor.YELLOW + timerManager.formatTime(playTime + afkTime));
        p.sendMessage(Main.getPrefix() +
            "Playtime (REAL): " + ChatColor.YELLOW + timerManager.formatTime(playTime));
        p.sendMessage(Main.getPrefix() +
            "AFKtime: " + ChatColor.YELLOW + timerManager.formatTime(afkTime));
      } else {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " +
            ChatColor.YELLOW + "/playtime [player]");
      }

      return true;
    }

    return false;
  }

  @Override
  public List<String> onTabComplete(CommandSender cs, Command c, String label, String[] args) {
    List<String> suggestions = new ArrayList<>();

    if (args.length == 1) {
      Collection<? extends Player> onlinePlayers = Main.getInstance().getServer().getOnlinePlayers();
      suggestions.addAll(onlinePlayers.stream().map(Player::getName).collect(Collectors.toList()));
    }

    return suggestions;
  }
}
