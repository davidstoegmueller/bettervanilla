package com.daveestar.bettervanilla.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.TimerManager;
import com.daveestar.bettervanilla.gui.PlaytimeGUI;

import net.md_5.bungee.api.ChatColor;

public class PlayTimeCommand implements TabExecutor {

  private final Main _plugin;
  private final TimerManager _timerManager;
  private final PlaytimeGUI _playtimeGUI;

  public PlayTimeCommand() {
    _plugin = Main.getInstance();
    _timerManager = _plugin.getTimerManager();
    _playtimeGUI = new PlaytimeGUI();
  }

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (cs instanceof Player) {
      Player p = (Player) cs;

      if (args.length == 0) {
        _playtimeGUI.displayGUI(p);
      } else if (args.length == 1) {
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[0]);

        if (!targetPlayer.hasPlayedBefore() && !targetPlayer.isOnline()) {
          p.sendMessage(Main.getPrefix() + ChatColor.RED + "The requested player " + ChatColor.YELLOW + args[0]
              + ChatColor.RED + " has never joined the server!");
          return true;
        }

        int playTime = _timerManager.getPlayTime(targetPlayer.getUniqueId());
        int afkTime = _timerManager.getAFKTime(targetPlayer.getUniqueId());

        String playerName = targetPlayer.getName() != null ? targetPlayer.getName() : args[0];

        p.sendMessage(
            Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "PLAYTIME" + ChatColor.RESET + ChatColor.YELLOW
                + " » " + ChatColor.GRAY + playerName);
        p.sendMessage(Main.getShortPrefix()
            + "Totaltime: " + ChatColor.YELLOW + _timerManager.formatTime(playTime + afkTime));
        p.sendMessage(Main.getShortPrefix() + "Playtime: " + ChatColor.YELLOW + _timerManager.formatTime(playTime));
        p.sendMessage(Main.getShortPrefix() + "AFK: " + ChatColor.YELLOW + _timerManager.formatTime(afkTime));
      } else {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/playtime [player]");
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
      for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
        if (op.getName() != null && !suggestions.contains(op.getName())) {
          suggestions.add(op.getName());
        }
      }
    }

    return suggestions;
  }
}
