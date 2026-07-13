package com.daveestar.bettervanilla.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.utils.Theme;

public class HelpCommands {
  private static final List<String> PLAYER_HELP_KEYS = List.of(
      "help-player-tip",
      "help-player-section-commands",
      "help-player-command-settings", "help-player-command-help", "help-player-command-waypoints",
      "help-player-command-backpack", "help-player-command-deathpoints", "help-player-command-heads",
      "help-player-command-playtime", "help-player-command-ping", "help-player-command-sit",
      "help-player-command-message", "help-player-command-reply", "help-player-command-here",
      "help-player-section-gameplay",
      "help-player-feature-vein-miner", "help-player-feature-tree-chopper", "help-player-feature-backpacks",
      "help-player-feature-waypoints", "help-player-feature-waypoint-navigation",
      "help-player-feature-death-points", "help-player-feature-death-chest", "help-player-feature-heads",
      "help-player-feature-container-sorting", "help-player-feature-item-restock", "help-player-feature-tags",
      "help-player-feature-action-bar", "help-player-feature-compass", "help-player-feature-chat",
      "help-player-feature-sign-colors", "help-player-feature-sittable-stairs", "help-player-feature-sit",
      "help-player-feature-sleep-rain", "help-player-feature-crop-protection",
      "help-player-feature-right-click-harvest", "help-player-feature-double-door",
      "help-player-section-server",
      "help-player-server-tab-list", "help-player-server-action-bar", "help-player-server-timer",
      "help-player-server-afk", "help-player-server-recipe-sync", "help-player-server-invisible-light",
      "help-player-server-invisible-item-frame", "help-player-admin-hint");

  private static final List<String> ADMIN_HELP_KEYS = List.of(
      "help-admin-tip", "help-admin-section-moderation",
      "help-admin-command-kick", "help-admin-command-ban", "help-admin-command-unban",
      "help-admin-command-mute", "help-admin-command-unmute", "help-admin-section-utilities",
      "help-admin-command-vanish", "help-admin-command-invsee", "help-admin-command-timer",
      "help-admin-command-settings", "help-admin-section-permissions", "help-admin-permissions-group",
      "help-admin-permissions-user", "help-admin-permissions-set-group", "help-admin-permissions-assignments",
      "help-admin-permissions-list", "help-admin-permissions-reload", "help-admin-section-settings",
      "help-admin-settings-summary");

  private static void sendPage(Player player, String titleKey, List<String> lineKeys) {
    player.sendMessage(Main.getPrefix() + Theme.highlight() + Main.tr(player, titleKey, "plugin", Theme.name()));
    for (String key : lineKeys) {
      player.sendMessage(Main.getShortPrefix() + Main.tr(player, key));
    }
  }

  public static class HelpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!(sender instanceof Player player)) {
        sender.sendMessage(Main.getNoPlayerMessage());
        return true;
      }
      sendPage(player, "help-player-title", PLAYER_HELP_KEYS);
      return true;
    }
  }

  public static class AdminHelpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!(sender instanceof Player player)) {
        sender.sendMessage(Main.getNoPlayerMessage());
        return true;
      }
      if (!player.hasPermission(Permissions.ADMINHELP.getName())) {
        player.sendMessage(Main.getNoPermissionMessage(player, Permissions.ADMINHELP));
        return true;
      }
      sendPage(player, "help-admin-title", ADMIN_HELP_KEYS);
      return true;
    }
  }
}
