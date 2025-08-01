package com.daveestar.bettervanilla.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.ModerationManager;
import com.daveestar.bettervanilla.manager.TimerManager;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class ModerationCommands {
  public static class KickCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length < 1) {
        sender.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/kick <player> [reason]");
        return true;
      }

      Player target = Bukkit.getPlayer(args[0]);
      if (target == null) {
        sender.sendMessage(
            Main.getPrefix() + ChatColor.RED + "Player " + ChatColor.YELLOW + args[0] + ChatColor.RED + " not found.");
        return true;
      }

      if (sender instanceof Player && target.getUniqueId().equals(((Player) sender).getUniqueId())) {
        sender.sendMessage(Main.getPrefix() + ChatColor.RED + "You cannot kick yourself.");
        return true;
      }

      String reason = null;
      if (args.length > 1) {
        reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
      }

      String kickMsg = ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + "KICKED\n\n" + ChatColor.GRAY
          + "You were kicked from the server.\n\n"
          + (reason != null ? ChatColor.YELLOW + "" + ChatColor.BOLD + "Reason: " + ChatColor.GRAY + reason : "");

      target.kick(Component.text(kickMsg));
      sender.sendMessage(Main.getPrefix() + ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " has been kicked.");
      return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      if (args.length == 1) {
        List<String> names = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        if (sender instanceof Player) {
          names.remove(((Player) sender).getName());
        }
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], names, completions);
        return completions;
      }
      return Collections.emptyList();
    }
  }

  public static class BanCommand implements TabExecutor {
    private final ModerationManager _moderationManager;
    private final TimerManager _timerManager;

    public BanCommand() {
      _moderationManager = Main.getInstance().getModerationManager();
      _timerManager = Main.getInstance().getTimerManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length < 1) {
        sender.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW
            + "/ban <player> [duration] [reason]");
        sender.sendMessage(Main.getPrefix() + "Example: " + ChatColor.YELLOW
            + "/ban playername 1d2h15m30s Inappropriate behavior");
        return true;
      }

      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
      if (sender instanceof Player && target.getUniqueId().equals(((Player) sender).getUniqueId())) {
        sender.sendMessage(Main.getPrefix() + ChatColor.RED + "You cannot ban yourself.");
        return true;
      }

      long durationSeconds = -1;
      String reason = null;

      if (args.length > 1) {
        long parsed = parseDuration(args[1]);
        if (parsed > 0) {
          durationSeconds = parsed;
          if (args.length > 2) {
            reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
          }
        } else {
          reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        }
      }

      if (durationSeconds > 0) {
        _moderationManager.tempBanPlayer(target, reason, durationSeconds * 1000);
      } else {
        _moderationManager.banPlayer(target, reason);
      }

      if (target.isOnline()) {
        String banMsg = ChatColor.YELLOW + "" + ChatColor.BOLD + "BANNED\n\n" + ChatColor.GRAY
            + "You were banned from the server.\n\n";

        if (reason != null && !reason.isEmpty()) {
          banMsg += ChatColor.YELLOW + "" + ChatColor.BOLD + "Reason: " + ChatColor.GRAY + reason + "\n";
        }

        if (durationSeconds > 0) {
          String time = _timerManager.formatTime((int) durationSeconds);
          banMsg += ChatColor.YELLOW + "" + ChatColor.BOLD + "Expires in: " + ChatColor.GRAY + time;
        }

        target.getPlayer().kick(Component.text(banMsg));
      }

      if (durationSeconds > 0) {
        String time = _timerManager.formatTime((int) durationSeconds);
        sender.sendMessage(Main.getPrefix() + ChatColor.YELLOW + target.getName() + ChatColor.GRAY
            + " has been temp-banned for " + ChatColor.YELLOW + time + ChatColor.GRAY + ".");
      } else {
        sender.sendMessage(Main.getPrefix() + ChatColor.YELLOW + target.getName() + ChatColor.GRAY
            + " has been banned.");
      }
      return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      if (args.length == 1) {
        List<String> names = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        if (sender instanceof Player) names.remove(((Player) sender).getName());
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], names, completions);
        return completions;
      }
      return Collections.emptyList();
    }
  }

  /** Remove a ban from a player. */
  public static class UnbanCommand implements TabExecutor {
    private final ModerationManager modManager;

    public UnbanCommand() {
      modManager = Main.getInstance().getModerationManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length < 1) {
        sender.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/unban <player>");
        return true;
      }

      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
      if (!modManager.isBanned(target)) {
        sender.sendMessage(Main.getPrefix() + ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " is not banned.");
        return true;
      }
      modManager.unbanPlayer(target);
      sender.sendMessage(
          Main.getPrefix() + ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " has been unbanned.");
      return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      if (args.length == 1) {
        List<String> names = modManager.getBannedPlayerNames();
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], names, completions);
        return completions;
      }
      return Collections.emptyList();
    }
  }

  /** Mute or temp-mute a player. */
  public static class MuteCommand implements TabExecutor {
    private final ModerationManager modManager;
    private final TimerManager _timerManager;

    public MuteCommand() {
      modManager = Main.getInstance().getModerationManager();
      _timerManager = Main.getInstance().getTimerManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length < 1) {
        sender.sendMessage(
            Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/mute <player> [duration] [reason]");
        return true;
      }

      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
      if (sender instanceof Player && target.getUniqueId().equals(((Player) sender).getUniqueId())) {
        sender.sendMessage(Main.getPrefix() + ChatColor.RED + "You cannot mute yourself.");
        return true;
      }

      long durationSeconds = -1;
      String reason = null;

      if (args.length > 1) {
        long parsed = parseDuration(args[1]);
        if (parsed > 0) {
          durationSeconds = parsed;
          if (args.length > 2) {
            reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
          }
        } else {
          reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        }
      }

      if (durationSeconds > 0) {
        modManager.tempMutePlayer(target, reason, durationSeconds * 1000);
      } else {
        modManager.mutePlayer(target, reason);
      }

      if (target.isOnline()) {
        String msg = Main.getPrefix() + ChatColor.RED + "You have been muted.";
        if (reason != null && !reason.isEmpty()) {
          msg += " Reason: " + ChatColor.YELLOW + reason;
        } else {
          msg += " No reason given.";
        }
        if (durationSeconds > 0) {
          String time = _timerManager.formatTime((int) durationSeconds);
          msg += " Expires in: " + ChatColor.YELLOW + time;
        }
        target.getPlayer().sendMessage(msg);
      }

      if (durationSeconds > 0) {
        String time = _timerManager.formatTime((int) durationSeconds);
        sender.sendMessage(Main.getPrefix() + ChatColor.YELLOW + target.getName() + ChatColor.GRAY
            + " has been muted for " + ChatColor.YELLOW + time + ChatColor.GRAY + ".");
      } else {
        sender.sendMessage(Main.getPrefix() + ChatColor.YELLOW + target.getName() + ChatColor.GRAY
            + " has been muted.");
      }
      return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      if (args.length == 1) {
        List<String> names = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        if (sender instanceof Player) names.remove(((Player) sender).getName());
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], names, completions);
        return completions;
      }
      return Collections.emptyList();
    }
  }

  /** Remove a mute from a player. */
  public static class UnmuteCommand implements TabExecutor {
    private final ModerationManager modManager;

    public UnmuteCommand() {
      modManager = Main.getInstance().getModerationManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length < 1) {
        sender.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/unmute <player>");
        return true;
      }

      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
      modManager.unmutePlayer(target);
      sender.sendMessage(
          Main.getPrefix() + ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " has been unmuted.");
      return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      if (args.length == 1) {
        List<String> names = modManager.getMutedPlayerNames();
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], names, completions);
        return completions;
      }
      return Collections.emptyList();
    }
  }

  static long parseDuration(String input) {
    Matcher matcher = Pattern.compile("(\\d+)([dhms])", Pattern.CASE_INSENSITIVE).matcher(input);
    long total = 0;
    int matched = 0;
    while (matcher.find()) {
      long value = Long.parseLong(matcher.group(1));
      matched += matcher.group().length();
      switch (matcher.group(2).toLowerCase()) {
        case "d":
          total += value * 86400;
          break;
        case "h":
          total += value * 3600;
          break;
        case "m":
          total += value * 60;
          break;
        case "s":
          total += value;
          break;
        default:
          return -1;
      }
    }
    return matched == input.length() ? total : -1;
  }
}
