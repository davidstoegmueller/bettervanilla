package com.daveestar.bettervanilla.commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.ModerationManager;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class ModerationCommands {
  public static class KickCommand implements CommandExecutor {
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

      String reason = null;
      if (args.length > 1) {
        reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
      }

      String kickMsg = ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + "KICKED\n\n"
          + ChatColor.GRAY
          + "You were kicked from the server.\n\n"
          + (reason != null ? ChatColor.YELLOW + "" + ChatColor.BOLD + "Reason: " + ChatColor.GRAY + reason
              : "");

      target.kick(Component.text(kickMsg));
      sender.sendMessage(Main.getPrefix() + ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " has been kicked.");
      return true;
    }
  }

  public static class BanCommand implements CommandExecutor {
    private final ModerationManager _moderationManager;

    public BanCommand() {
      _moderationManager = Main.getInstance().getModerationManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length < 1) {
        sender.sendMessage(
            Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW
                + "/ban <player> [duration] [reason] ");
        sender.sendMessage(
            Main.getPrefix() + "Example: " + ChatColor.YELLOW + "/ban playername 1d15m30s Inappropriate behavior");
        return true;
      }

      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
      long duration = -1;

      String reason = null;
      if (args.length > 1) {
        reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        if (args.length > 2) {
          try {
            duration = Long.parseLong(args[args.length - 1]);
            reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length - 1));
          } catch (NumberFormatException ignore) {
            duration = -1;
          }
        }
      }

      if (duration > 0) {
        _moderationManager.tempBanPlayer(target, reason, duration * 1000);
      } else {
        _moderationManager.banPlayer(target, reason);
      }

      if (target.isOnline()) {
        String msg = ChatColor.GRAY + "[" + ChatColor.YELLOW + "BetterVanilla" + ChatColor.GRAY + "] "
            + ChatColor.RED + "You are banned!";
        if (!reason.isEmpty()) {
          msg += "\n" + ChatColor.GRAY + "Reason: " + ChatColor.YELLOW + reason;
        }
        if (duration > 0) {
          msg += "\n" + ChatColor.GRAY + "Expires in: " + ChatColor.YELLOW + duration + "s";
        }
        target.getPlayer().kick(Component.text(msg));
      }

      if (duration > 0) {
        sender.sendMessage(Main.getPrefix() + ChatColor.YELLOW + target.getName() + ChatColor.GRAY
            + " has been temp-banned for " + ChatColor.YELLOW + duration + ChatColor.GRAY + " seconds.");
      } else {
        sender
            .sendMessage(Main.getPrefix() + ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " has been banned.");
      }
      return true;
    }
  }

  /** Remove a ban from a player. */
  public static class UnbanCommand implements CommandExecutor {
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
      modManager.unbanPlayer(target);
      sender
          .sendMessage(Main.getPrefix() + ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " has been unbanned.");
      return true;
    }
  }

  /** Mute or temp-mute a player. */
  public static class MuteCommand implements CommandExecutor {
    private final ModerationManager modManager;

    public MuteCommand() {
      modManager = Main.getInstance().getModerationManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length < 1) {
        sender.sendMessage(
            Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/mute <player> [reason] [seconds]");
        return true;
      }

      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
      long duration = -1;
      String reason = "Muted";

      if (args.length > 1) {
        reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (args.length > 2) {
          String last = args[args.length - 1];
          try {
            duration = Long.parseLong(last);
            reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length - 1));
          } catch (NumberFormatException ignore) {
            duration = -1;
          }
        }
      }

      if (duration > 0) {
        modManager.tempMutePlayer(target, reason, duration * 1000);
      } else {
        modManager.mutePlayer(target, reason);
      }

      if (duration > 0) {
        sender.sendMessage(Main.getPrefix() + ChatColor.YELLOW + target.getName() + ChatColor.GRAY
            + " has been muted for " + ChatColor.YELLOW + duration + ChatColor.GRAY + " seconds.");
      } else {
        sender
            .sendMessage(Main.getPrefix() + ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " has been muted.");
      }
      return true;
    }
  }

  /** Remove a mute from a player. */
  public static class UnmuteCommand implements CommandExecutor {
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
      sender
          .sendMessage(Main.getPrefix() + ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " has been unmuted.");
      return true;
    }
  }
}
