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

  private final Main _plugin;
  private final TimerManager _timerManager;

  public PlayTimeCommand() {
    _plugin = Main.getInstance();
    _timerManager = _plugin.getTimerManager();
  }

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (cs instanceof Player) {
      Player p = (Player) cs;

      if (args.length >= 0 && args.length <= 1) {
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

        int playTime = _timerManager.getPlayTime(targetPlayer);
        int afkTime = _timerManager.getAFKTime(targetPlayer);

        p.sendMessage(
            Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "PLAYTIME" + ChatColor.RESET + ChatColor.YELLOW
                + " » " + ChatColor.GRAY + targetPlayer.getName());
        p.sendMessage(Main.getShortPrefix() +
            "Totaltime: " + ChatColor.YELLOW + _timerManager.formatTime(playTime + afkTime));
        p.sendMessage(Main.getShortPrefix() +
            "Playtime: " + ChatColor.YELLOW + _timerManager.formatTime(playTime));
        p.sendMessage(Main.getShortPrefix() +
            "AFK: " + ChatColor.YELLOW + _timerManager.formatTime(afkTime));
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
      Collection<? extends Player> onlinePlayers = _plugin.getServer().getOnlinePlayers();
      suggestions.addAll(onlinePlayers.stream().map(Player::getName).collect(Collectors.toList()));
    }

    return suggestions;
  }
}
