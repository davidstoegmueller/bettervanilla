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

/**
 * Collection of moderation related commands grouped in a single file.
 */
public class ModerationCommands {
  /** Kick a player from the server. */
  public static class KickCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length < 1) {
        sender.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/kick <player> [reason]");
        return true;
      }

      Player target = Bukkit.getPlayer(args[0]);
      if (target == null) {
        sender.sendMessage(Main.getPrefix() + ChatColor.RED + "Player not found.");
        return true;
      }

      String reason = "Kicked";
      if (args.length > 1) {
        reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
      }

      String msg = ChatColor.GRAY + "[" + ChatColor.YELLOW + "BetterVanilla" + ChatColor.GRAY + "] "
          + ChatColor.RED + "You were kicked!";
      if (!reason.isEmpty()) {
        msg += "\n" + ChatColor.GRAY + "Reason: " + ChatColor.YELLOW + reason;
      }

      target.kick(Component.text(msg));
      sender.sendMessage(Main.getPrefix() + ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " has been kicked.");
      return true;
    }
  }

  /** Ban or temp-ban a player. */
  public static class BanCommand implements CommandExecutor {
    private final ModerationManager modManager;

    public BanCommand() {
      modManager = Main.getInstance().getModerationManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length < 1) {
        sender.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/ban <player> [reason] [seconds]");
        return true;
      }

      OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
      long duration = -1;
      String reason = "Banned";

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
        modManager.tempBanPlayer(target, reason, duration * 1000);
      } else {
        modManager.banPlayer(target, reason);
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
        sender.sendMessage(Main.getPrefix() + ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " has been banned.");
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
      sender.sendMessage(Main.getPrefix() + ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " has been unbanned.");
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
        sender.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW + "/mute <player> [reason] [seconds]");
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
        sender.sendMessage(Main.getPrefix() + ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " has been muted.");
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
      sender.sendMessage(Main.getPrefix() + ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " has been unmuted.");
      return true;
    }
  }
}
