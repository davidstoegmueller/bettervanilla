package com.daveestar.bettervanilla.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.manager.NameTagManager;
import com.daveestar.bettervanilla.manager.TagManager;
import com.daveestar.bettervanilla.manager.TabListManager;

import net.md_5.bungee.api.ChatColor;

public class TagCommand implements TabExecutor {
  private final TagManager _tagManager;
  private final NameTagManager _nameTagManager;
  private final TabListManager _tabListManager;

  public TagCommand() {
    Main plugin = Main.getInstance();
    _tagManager = plugin.getTagManager();
    _nameTagManager = plugin.getNameTagManager();
    _tabListManager = plugin.getTabListManager();
  }

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (!(cs instanceof Player)) {
      cs.sendMessage(Main.getNoPlayerMessage());
      return true;
    }

    Player p = (Player) cs;

    if (!p.hasPermission(Permissions.TAG.getName())) {
      p.sendMessage(Main.getNoPermissionMessage(Permissions.TAG));
      return true;
    }

    if (args.length == 0) {
      String currentTag = _tagManager.getTag(p);

      if (currentTag == null) {
        p.sendMessage(Main.getPrefix() + ChatColor.GRAY + "You don't have a tag set.");
      } else {
        p.sendMessage(Main.getPrefix() + ChatColor.GRAY + "Your tag: " + ChatColor.YELLOW + currentTag);
      }

      sendUsageMessage(p);

      return true;
    }

    switch (args[0].toLowerCase()) {
      case "set":
        handleSet(p, args);
        break;
      case "clear":
        handleClear(p, args);
        break;
      default:
        sendUsageMessage(p);
        break;
    }

    return true;
  }

  private void handleClear(Player p, String[] args) {
    Player target = p;
    boolean hasTagAdminPermission = p.hasPermission(Permissions.TAG_ADMIN.getName());

    if (args.length >= 2) {
      if (!hasTagAdminPermission) {
        p.sendMessage(Main.getNoPermissionMessage(Permissions.TAG_ADMIN));
        return;
      }

      target = Bukkit.getPlayer(args[1]);

      if (target == null) {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Player not found.");
        return;
      }
    }

    _tagManager.removeTag(target);
    _nameTagManager.updateNameTag(target);
    _tabListManager.refreshPlayerListEntry(target);

    if (target == p) {
      p.sendMessage(Main.getPrefix() + ChatColor.GRAY + "Tag cleared.");
    } else {
      p.sendMessage(Main.getPrefix() + ChatColor.GRAY + "Cleared tag for " + ChatColor.YELLOW + target.getName());
      target.sendMessage(Main.getPrefix() + ChatColor.GRAY + "Your tag was cleared by an admin.");
    }
  }

  private void handleSet(Player p, String[] args) {
    if (args.length < 2) {
      sendUsageMessage(p);
      return;
    }

    Player target = p;
    String tag;
    boolean hasTagAdminPermission = p.hasPermission(Permissions.TAG_ADMIN.getName());

    // Check if last arg is a player name (for admin)
    if (args.length >= 3 && hasTagAdminPermission) {
      Player potentialTarget = Bukkit.getPlayer(args[args.length - 1]);

      if (potentialTarget != null) {
        target = potentialTarget;
        tag = String.join("", Arrays.copyOfRange(args, 1, args.length - 1)).trim();
      } else {
        tag = String.join("", Arrays.copyOfRange(args, 1, args.length)).trim();
      }
    } else {
      tag = String.join("", Arrays.copyOfRange(args, 1, args.length)).trim();
    }

    if (tag.isEmpty()) {
      sendUsageMessage(p);
      return;
    }

    if (tag.length() > 10) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Tag too long! Maximum length is 10 characters.");
      return;
    }

    _tagManager.setTag(target, tag);
    _nameTagManager.updateNameTag(target);
    _tabListManager.refreshPlayerListEntry(target);

    if (target == p) {
      p.sendMessage(Main.getPrefix() + ChatColor.GRAY + "Tag set to: " + ChatColor.YELLOW + tag);
    } else {
      p.sendMessage(Main.getPrefix() + ChatColor.GRAY + "Set tag for " + ChatColor.YELLOW + target.getName()
          + ChatColor.GRAY + " to: " + ChatColor.YELLOW + tag);
      target.sendMessage(Main.getPrefix() + ChatColor.GRAY + "Your tag was set to " + ChatColor.YELLOW + tag
          + ChatColor.GRAY + " by an admin.");
    }
  }

  private void sendUsageMessage(Player p) {
    boolean hasTagAdminPermission = p.hasPermission(Permissions.TAG_ADMIN.getName());

    if (hasTagAdminPermission) {
      p.sendMessage(
          Main.getPrefix() + ChatColor.GRAY + "Usage: " + ChatColor.YELLOW + "/tag set <name> [player]" + ChatColor.GRAY
              + " or " + ChatColor.YELLOW + "/tag clear [player]");
    } else {
      p.sendMessage(
          Main.getPrefix() + ChatColor.GRAY + "Usage: " + ChatColor.YELLOW + "/tag set <name>" + ChatColor.GRAY
              + " or " + ChatColor.YELLOW + "/tag clear");
    }
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    if (args.length == 1) {
      return Arrays.asList("set", "clear");
    }

    if (sender instanceof Player) {
      Player p = (Player) sender;
      boolean hasTagAdminPermission = p.hasPermission(Permissions.TAG_ADMIN.getName());

      if (hasTagAdminPermission) {
        if (args[0].equalsIgnoreCase("clear") && args.length == 2) {
          return Bukkit.getOnlinePlayers().stream()
              .map(Player::getName)
              .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
              .collect(Collectors.toList());
        }

        if (args[0].equalsIgnoreCase("set") && args.length >= 3) {
          return Bukkit.getOnlinePlayers().stream()
              .map(Player::getName)
              .filter(name -> name.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
              .collect(Collectors.toList());
        }
      }
    }

    return new ArrayList<>();
  }
}
