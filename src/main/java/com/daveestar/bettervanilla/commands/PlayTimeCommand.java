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
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.manager.TimerManager;
import com.daveestar.bettervanilla.gui.PlayTimeGUI;
import com.daveestar.bettervanilla.utils.Theme;

import net.md_5.bungee.api.ChatColor;

public class PlayTimeCommand implements TabExecutor {

  private final Main _plugin;
  private final TimerManager _timerManager;
  private final PlayTimeGUI _playtimeGUI;

  public PlayTimeCommand() {
    _plugin = Main.getInstance();
    _timerManager = _plugin.getTimerManager();
    _playtimeGUI = new PlayTimeGUI();
  }

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (!(cs instanceof Player)) {
      cs.sendMessage(Main.getNoPlayerMessage());
      return true;
    }

    Player p = (Player) cs;

    if (!p.hasPermission(Permissions.PLAYTIME.getName())) {
      p.sendMessage(Main.getNoPermissionMessage(p, Permissions.PLAYTIME));
      return true;
    }

    if (args.length == 0) {
      _playtimeGUI.displayGUI(p);
    } else if (args.length == 1) {
      OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[0]);

      if (!targetPlayer.hasPlayedBefore() && !targetPlayer.isOnline()) {
        p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "command-playtime-error-never-joined",
            "player", Theme.highlight() + args[0] + Theme.error()));
        return true;
      }

      int playTime = _timerManager.getPlayTime(targetPlayer.getUniqueId());
      int afkTime = _timerManager.getAFKTime(targetPlayer.getUniqueId());

      String playerName = targetPlayer.getName() != null ? targetPlayer.getName() : args[0];

      p.sendMessage(Main.getPrefix() + Theme.highlight() + ChatColor.BOLD
          + Main.tr(p, "command-playtime-summary-title",
              "player", ChatColor.RESET + Theme.primary().toString() + playerName));
      p.sendMessage(Main.getShortPrefix() + Main.tr(p, "playtime-total", "time",
          Theme.highlight() + _timerManager.formatTime(p, playTime + afkTime)));
      p.sendMessage(Main.getShortPrefix() + Main.tr(p, "playtime-active", "time",
          Theme.highlight() + _timerManager.formatTime(p, playTime)));
      p.sendMessage(Main.getShortPrefix() + Main.tr(p, "playtime-afk", "time",
          Theme.highlight() + _timerManager.formatTime(p, afkTime)));
    } else {
      p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "command-playtime-usage"));
    }

    return true;
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
