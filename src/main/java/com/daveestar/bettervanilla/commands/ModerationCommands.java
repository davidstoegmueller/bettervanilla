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
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.manager.ModerationManager;
import com.daveestar.bettervanilla.manager.TimerManager;
import com.daveestar.bettervanilla.utils.Theme;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

public class ModerationCommands {
  public static class KickCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
      if (cs instanceof Player) {
        Player p = (Player) cs;

        if (!p.hasPermission(Permissions.MODERATION.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(p, Permissions.MODERATION));
          return true;
        }
      }

      if (args.length < 1) {
        cs.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(cs, "command-kick-usage"));
        return true;
      }

      Player target = Bukkit.getPlayer(args[0]);
      if (target == null) {
        cs.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(cs, "command-kick-error-player-not-found",
            "player", Theme.highlight() + args[0] + Theme.error()));
        return true;
      }

      if (cs instanceof Player && target.getUniqueId().equals(((Player) cs).getUniqueId())) {
        cs.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(cs, "command-kick-error-self"));
        return true;
      }

      String reason = null;
      if (args.length > 1) {
        reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
      }

      String kickMsg = Theme.highlight().toString() + ChatColor.BOLD + Main.tr(target, "command-kick-disconnect-title")
          + "\n\n" + Theme.primary() + Main.tr(target, "command-kick-disconnect-message");
      if (reason != null && !reason.isEmpty()) {
        kickMsg += "\n\n" + Theme.highlight() + ChatColor.BOLD.toString()
            + Main.tr(target, "command-kick-disconnect-reason", "reason", Theme.primary() + reason);
      }

      target.kick(LegacyComponentSerializer.legacySection().deserialize(kickMsg));
      cs.sendMessage(Main.getPrefix() + Theme.primary() + Main.tr(cs, "command-kick-success",
          "player", Theme.highlight() + target.getName() + Theme.primary()));

      return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command c, String alias, String[] args) {
      if (args.length == 1) {
        List<String> names = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());

        if (cs instanceof Player) {
          names.remove(((Player) cs).getName());
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
    public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
      if (cs instanceof Player) {
        Player p = (Player) cs;

        if (!p.hasPermission(Permissions.MODERATION.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(p, Permissions.MODERATION));
          return true;
        }
      }

      if (args.length < 1) {
        cs.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(cs, "command-ban-usage"));
        cs.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(cs, "command-ban-example"));
        return true;
      }

      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
      String targetName = target.getName() != null ? target.getName() : args[0];
      if (cs instanceof Player && target.getUniqueId().equals(((Player) cs).getUniqueId())) {
        cs.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(cs, "command-ban-error-self"));
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

      Player onlineTarget = target.getPlayer();
      if (onlineTarget != null) {
        String banMsg = Theme.highlight() + "" + ChatColor.BOLD + Main.tr(onlineTarget, "command-ban-disconnect-title")
            + "\n\n" + Theme.primary() + Main.tr(onlineTarget, "command-ban-disconnect-message");

        if (reason != null && !reason.isEmpty()) {
          banMsg += "\n\n" + Theme.highlight() + ChatColor.BOLD.toString()
              + Main.tr(onlineTarget, "command-ban-disconnect-reason", "reason", Theme.primary() + reason);
        }

        if (durationSeconds > 0) {
          String time = _timerManager.formatTime(onlineTarget, (int) durationSeconds);
          banMsg += "\n" + Theme.highlight() + ChatColor.BOLD.toString()
              + Main.tr(onlineTarget, "command-ban-disconnect-expiry", "duration", Theme.primary() + time);
        }

        onlineTarget.kick(LegacyComponentSerializer.legacySection().deserialize(banMsg));
      }

      if (durationSeconds > 0) {
        String time = _timerManager.formatTime(cs, (int) durationSeconds);
        String key = reason != null && !reason.isEmpty()
            ? "command-ban-success-temporary-with-reason"
            : "command-ban-success-temporary";
        cs.sendMessage(Main.getPrefix() + Theme.primary() + Main.tr(cs, key,
            "player", Theme.highlight() + targetName + Theme.primary(),
            "duration", Theme.highlight() + time + Theme.primary(),
            "reason", Theme.highlight() + String.valueOf(reason) + Theme.primary()));
      } else {
        String key = reason != null && !reason.isEmpty()
            ? "command-ban-success-permanent-with-reason"
            : "command-ban-success-permanent";
        cs.sendMessage(Main.getPrefix() + Theme.primary() + Main.tr(cs, key,
            "player", Theme.highlight() + targetName + Theme.primary(),
            "reason", Theme.highlight() + String.valueOf(reason) + Theme.primary()));
      }

      return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command c, String alias, String[] args) {
      if (args.length == 1) {
        List<String> names = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());

        if (cs instanceof Player)
          names.remove(((Player) cs).getName());

        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], names, completions);

        return completions;
      }

      return Collections.emptyList();
    }
  }

  public static class UnbanCommand implements TabExecutor {
    private final ModerationManager _moderationManager;

    public UnbanCommand() {
      _moderationManager = Main.getInstance().getModerationManager();
    }

    @Override
    public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
      if (cs instanceof Player) {
        Player p = (Player) cs;

        if (!p.hasPermission(Permissions.MODERATION.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(p, Permissions.MODERATION));
          return true;
        }
      }

      if (args.length < 1) {
        cs.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(cs, "command-unban-usage"));
        return true;
      }

      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
      String targetName = target.getName() != null ? target.getName() : args[0];
      if (!_moderationManager.isBanned(target)) {
        cs.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(cs, "command-unban-error-not-banned",
            "player", Theme.highlight() + targetName + Theme.error()));
        return true;
      }

      _moderationManager.unbanPlayer(target);
      cs.sendMessage(Main.getPrefix() + Theme.primary() + Main.tr(cs, "command-unban-success",
          "player", Theme.highlight() + targetName + Theme.primary()));

      return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command c, String alias, String[] args) {
      if (args.length == 1) {
        List<String> names = _moderationManager.getBannedPlayerNames();
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], names, completions);

        return completions;
      }

      return Collections.emptyList();
    }
  }

  public static class MuteCommand implements TabExecutor {
    private final ModerationManager _moderationManager;
    private final TimerManager _timerManager;

    public MuteCommand() {
      _moderationManager = Main.getInstance().getModerationManager();
      _timerManager = Main.getInstance().getTimerManager();
    }

    @Override
    public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
      if (cs instanceof Player) {
        Player p = (Player) cs;

        if (!p.hasPermission(Permissions.MODERATION.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(p, Permissions.MODERATION));
          return true;
        }
      }

      if (args.length < 1) {
        cs.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(cs, "command-mute-usage"));
        return true;
      }

      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
      String targetName = target.getName() != null ? target.getName() : args[0];
      if (cs instanceof Player && target.getUniqueId().equals(((Player) cs).getUniqueId())) {
        cs.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(cs, "command-mute-error-self"));
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
        _moderationManager.tempMutePlayer(target, reason, durationSeconds * 1000);
      } else {
        _moderationManager.mutePlayer(target, reason);
      }

      Player onlineTarget = target.getPlayer();
      if (onlineTarget != null) {
        String duration = durationSeconds > 0
            ? _timerManager.formatTime(onlineTarget, (int) durationSeconds)
            : "";
        String key = durationSeconds > 0
            ? (reason != null && !reason.isEmpty()
                ? "command-mute-notification-temporary-with-reason"
                : "command-mute-notification-temporary")
            : (reason != null && !reason.isEmpty()
                ? "command-mute-notification-permanent-with-reason"
                : "command-mute-notification-permanent");
        onlineTarget.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(onlineTarget, key,
            "duration", Theme.highlight() + duration + Theme.error(),
            "reason", Theme.highlight() + String.valueOf(reason) + Theme.error()));
      }

      String duration = durationSeconds > 0 ? _timerManager.formatTime(cs, (int) durationSeconds) : "";
      String key = durationSeconds > 0
          ? (reason != null && !reason.isEmpty()
              ? "command-mute-success-temporary-with-reason"
              : "command-mute-success-temporary")
          : (reason != null && !reason.isEmpty()
              ? "command-mute-success-permanent-with-reason"
              : "command-mute-success-permanent");
      cs.sendMessage(Main.getPrefix() + Theme.primary() + Main.tr(cs, key,
          "player", Theme.highlight() + targetName + Theme.primary(),
          "duration", Theme.highlight() + duration + Theme.primary(),
          "reason", Theme.highlight() + String.valueOf(reason) + Theme.primary()));

      return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command c, String alias, String[] args) {
      if (args.length == 1) {
        List<String> names = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());

        if (cs instanceof Player)
          names.remove(((Player) cs).getName());

        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], names, completions);

        return completions;
      }

      return Collections.emptyList();
    }
  }

  /** Remove a mute from a player. */
  public static class UnmuteCommand implements TabExecutor {
    private final ModerationManager _moderationManager;

    public UnmuteCommand() {
      _moderationManager = Main.getInstance().getModerationManager();
    }

    @Override
    public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
      if (cs instanceof Player) {
        Player p = (Player) cs;

        if (!p.hasPermission(Permissions.MODERATION.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(p, Permissions.MODERATION));
          return true;
        }
      }

      if (args.length < 1) {
        cs.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(cs, "command-unmute-usage"));
        return true;
      }

      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
      String targetName = target.getName() != null ? target.getName() : args[0];
      _moderationManager.unmutePlayer(target);

      Player onlineTarget = target.getPlayer();
      if (onlineTarget != null) {
        onlineTarget.sendMessage(Main.getPrefix() + Theme.primary()
            + Main.tr(onlineTarget, "command-unmute-notification"));
      }

      cs.sendMessage(Main.getPrefix() + Theme.primary() + Main.tr(cs, "command-unmute-success",
          "player", Theme.highlight() + targetName + Theme.primary()));

      return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command c, String alias, String[] args) {
      if (args.length == 1) {
        List<String> names = _moderationManager.getMutedPlayerNames();
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
