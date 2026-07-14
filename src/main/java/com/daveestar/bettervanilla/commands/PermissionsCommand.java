package com.daveestar.bettervanilla.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.manager.PermissionsManager;
import com.daveestar.bettervanilla.utils.Theme;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PermissionsCommand implements TabExecutor {

  private final Main _plugin;
  private final PermissionsManager permissionsManager;

  public PermissionsCommand() {
    _plugin = Main.getInstance();
    permissionsManager = _plugin.getPermissionsManager();
  }

  @Override
  public boolean onCommand(CommandSender cs, Command c, String label, String[] args) {
    if (cs instanceof Player) {
      Player p = (Player) cs;
      if (!p.hasPermission(Permissions.PERMISSIONS.getName())) {
        p.sendMessage(Main.getNoPermissionMessage(p, Permissions.PERMISSIONS));
        return true;
      }
    }

    if (args.length < 1) {
      cs.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(cs, "command-permissions-usage"));
      return true;
    }

    String section = args[0].toLowerCase();
    switch (section) {
      case "group":
        handleGroupCommand(cs, args);
        break;
      case "user":
        handleUserCommand(cs, args);
        break;
      case "assignments":
        handleAssignmentsCommand(cs);
        break;
      case "default":
        handleDefaultGroupCommand(cs, args);
        break;
      case "list":
        handleListCommand(cs);
        break;
      case "reload":
        handleReloadCommand(cs, args);
        break;
      default:
        cs.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(cs, "command-permissions-usage"));
        break;
    }

    return true;
  }

  // --------------
  // GROUP COMMANDS
  // --------------

  private void handleGroupCommand(CommandSender sender, String[] args) {
    if (args.length < 3) {
      sender.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(sender, "command-permissions-group-usage"));
      return;
    }

    String group = args[2];
    String permission = args.length == 4 ? args[3] : "";

    String action = args[1].toLowerCase();
    switch (action) {
      case "addperm":
        // syntax: /permissions group addperm <group> <permission>
        if (args.length != 4) {
          sender.sendMessage(Main.getPrefix() + Theme.error()
              + Main.tr(sender, "command-permissions-group-add-permission-usage"));
          return;
        }

        if (!permissionsManager.hasGroupPermission(group, permission)) {
          permissionsManager.addPermissionToGroup(group, permission);
          sender.sendMessage(Main.getPrefix() + Main.tr(sender, "command-permissions-group-add-permission-success",
              "permission", Theme.highlight() + permission + Theme.primary(),
              "group", Theme.highlight() + group + Theme.primary()));
        } else {
          sender.sendMessage(Main.getPrefix() + Theme.error()
              + Main.tr(sender, "command-permissions-group-add-permission-error-already-assigned",
                  "permission", Theme.highlight() + permission + Theme.error(),
                  "group", Theme.highlight() + group + Theme.error()));
        }
        break;
      case "removeperm":
        // syntax: /permissions group removeperm <group> <permission>
        if (args.length != 4) {
          sender.sendMessage(Main.getPrefix() + Theme.error()
              + Main.tr(sender, "command-permissions-group-remove-permission-usage"));
          return;
        }

        if (permissionsManager.hasGroupPermission(group, permission)) {
          permissionsManager.removePermissionFromGroup(group, permission);
          sender.sendMessage(Main.getPrefix() + Main.tr(sender, "command-permissions-group-remove-permission-success",
              "permission", Theme.highlight() + permission + Theme.primary(),
              "group", Theme.highlight() + group + Theme.primary()));
        } else {
          sender.sendMessage(Main.getPrefix() + Theme.error()
              + Main.tr(sender, "command-permissions-group-remove-permission-error-not-assigned",
                  "permission", Theme.highlight() + permission + Theme.error(),
                  "group", Theme.highlight() + group + Theme.error()));
        }
        break;
      case "delete":
        // syntax: /permissions group delete <group>
        if (args.length != 3) {
          sender.sendMessage(Main.getPrefix() + Theme.error()
              + Main.tr(sender, "command-permissions-group-delete-usage"));
          return;
        }

        if (permissionsManager.getAllGroupNames().contains(group)) {
          if (group.equalsIgnoreCase(permissionsManager.getDefaultGroupName())) {
            sender.sendMessage(Main.getPrefix() + Theme.error()
                + Main.tr(sender, "command-permissions-group-delete-error-default-group"));
            return;
          }

          permissionsManager.removeGroup(group);
          sender.sendMessage(Main.getPrefix() + Main.tr(sender, "command-permissions-group-delete-success",
              "group", Theme.highlight() + group + Theme.primary()));
        } else {
          sender.sendMessage(Main.getPrefix() + Theme.error()
              + Main.tr(sender, "command-permissions-group-delete-error-not-found",
                  "group", Theme.highlight() + group + Theme.error()));
        }
        break;
      default:
        sender.sendMessage(Main.getPrefix() + Theme.error()
            + Main.tr(sender, "command-permissions-group-usage"));
        break;
    }
  }

  private void handleDefaultGroupCommand(CommandSender sender, String[] args) {
    if (args.length == 1) {
      sender.sendMessage(Main.getPrefix() + Main.tr(sender, "command-permissions-default-group-current",
          "group", Theme.highlight() + permissionsManager.getDefaultGroupName() + Theme.primary()));
      sender.sendMessage(Main.getShortPrefix() + Main.tr(sender, "command-permissions-default-group-change-hint"));
      return;
    }

    if (args.length != 2) {
      sender.sendMessage(Main.getPrefix() + Theme.error()
          + Main.tr(sender, "command-permissions-default-group-usage"));
      return;
    }

    String groupName = args[1];
    boolean groupExists = permissionsManager.groupExists(groupName);

    permissionsManager.setDefaultGroup(groupName);
    sender.sendMessage(Main.getPrefix() + Main.tr(sender, "command-permissions-default-group-set-success",
        "group", Theme.highlight() + groupName + Theme.primary()));

    if (!groupExists) {
      sender.sendMessage(Main.getShortPrefix() + Main.tr(sender, "command-permissions-default-group-created-hint",
          "group", Theme.highlight() + groupName + Theme.primary()));
    }
  }

  // -------------
  // USER COMMANDS
  // -------------

  private void handleUserCommand(CommandSender sender, String[] args) {
    if (args.length != 4) {
      sender.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(sender, "command-permissions-user-usage"));
      return;
    }

    String action = args[1].toLowerCase();
    OfflinePlayer p = Bukkit.getOfflinePlayer(args[2]);
    String playerName = p.getName() != null ? p.getName() : args[2];
    String permission = args[3];
    String group = args[3];

    switch (action) {
      case "addperm":
        // syntax: /permissions user addperm <username> <permission>
        if (args.length != 4) {
          sender.sendMessage(Main.getPrefix() + Theme.error()
              + Main.tr(sender, "command-permissions-user-add-permission-usage"));
          return;
        }

        if (!permissionsManager.hasUserPermission(p, permission)) {
          permissionsManager.addPermissionToUser(p, permission);
          sender.sendMessage(Main.getPrefix() + Main.tr(sender, "command-permissions-user-add-permission-success",
              "permission", Theme.highlight() + permission + Theme.primary(),
              "player", Theme.highlight() + playerName + Theme.primary()));
        } else {
          sender.sendMessage(Main.getPrefix() + Theme.error()
              + Main.tr(sender, "command-permissions-user-add-permission-error-already-assigned",
                  "permission", Theme.highlight() + permission + Theme.error(),
                  "player", Theme.highlight() + playerName + Theme.error()));
        }
        break;
      case "removeperm":
        // syntax: /permissions user removeperm <username> <permission>
        if (args.length != 4) {
          sender.sendMessage(Main.getPrefix() + Theme.error()
              + Main.tr(sender, "command-permissions-user-remove-permission-usage"));
          return;
        }

        if (permissionsManager.hasUserPermission(p, permission)) {
          permissionsManager.removePermissionFromUser(p, permission);
          sender.sendMessage(Main.getPrefix() + Main.tr(sender, "command-permissions-user-remove-permission-success",
              "permission", Theme.highlight() + permission + Theme.primary(),
              "player", Theme.highlight() + playerName + Theme.primary()));
        } else {
          sender.sendMessage(Main.getPrefix() + Theme.error()
              + Main.tr(sender, "command-permissions-user-remove-permission-error-not-assigned",
                  "permission", Theme.highlight() + permission + Theme.error(),
                  "player", Theme.highlight() + playerName + Theme.error()));
        }
        break;
      case "setgroup":
        // syntax: /permissions user setgroup <username> <group>
        if (args.length != 4) {
          sender.sendMessage(Main.getPrefix() + Theme.error()
              + Main.tr(sender, "command-permissions-user-set-group-usage"));
          return;
        }

        if (!permissionsManager.getUserGroup(p).equalsIgnoreCase(group)) {
          permissionsManager.assignUserToGroup(p, group);
          sender.sendMessage(Main.getPrefix() + Main.tr(sender, "command-permissions-user-set-group-success",
              "player", Theme.highlight() + playerName + Theme.primary(),
              "group", Theme.highlight() + group + Theme.primary()));
        } else {
          sender.sendMessage(Main.getPrefix() + Theme.error()
              + Main.tr(sender, "command-permissions-user-set-group-error-already-assigned",
                  "player", Theme.highlight() + playerName + Theme.error(),
                  "group", Theme.highlight() + group + Theme.error()));
        }
        break;
      default:
        sender.sendMessage(Main.getPrefix() + Theme.error()
            + Main.tr(sender, "command-permissions-user-usage"));
        break;
    }
  }

  // ------------
  // LIST COMMAND
  // ------------

  private void handleAssignmentsCommand(CommandSender sender) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(Main.getNoPlayerMessage());
      return;
    }

    Player p = (Player) sender;
    Set<String> groupNames = permissionsManager.getAllGroupNames();
    Set<String> userNames = permissionsManager.getAllUserIds();

    p.sendMessage(Main.getPrefix() + Theme.highlight() + ChatColor.BOLD
        + Main.tr(p, "command-permissions-assignments-groups-title"));
    p.sendMessage(Main.getShortPrefix() + Main.tr(p, "command-permissions-default-group-current",
        "group", Theme.highlight() + permissionsManager.getDefaultGroupName() + Theme.primary()));
    p.sendMessage("");

    for (String group : groupNames) {
      List<String> groupPerms = permissionsManager.getGroupPermissions(group);

      p.sendMessage(" " + Theme.textPrefix() + Main.tr(p, "command-permissions-assignments-group-entry",
          "group", group));
      p.sendMessage("     " + Theme.textPrefix()
          + Main.tr(p, "command-permissions-assignments-permissions-entry", "permissions", groupPerms));

      List<String> usersInGroup = new ArrayList<>();
      for (String uid : userNames) {
        OfflinePlayer user = Bukkit.getOfflinePlayer(UUID.fromString(uid));

        if (permissionsManager.getUserGroup(user).equals(group)) {
          usersInGroup.add(user.getName() != null ? user.getName() : uid);
        }
      }

      p.sendMessage("     " + Theme.textPrefix()
          + Main.tr(p, "command-permissions-assignments-users-entry", "players", usersInGroup));
      p.sendMessage("");
    }

    p.sendMessage("");

    p.sendMessage(Main.getPrefix() + Theme.highlight() + ChatColor.BOLD
        + Main.tr(p, "command-permissions-assignments-players-title"));

    for (String uid : userNames) {
      OfflinePlayer user = Bukkit.getOfflinePlayer(UUID.fromString(uid));

      String userGroup = permissionsManager.getUserGroup(user);
      List<String> userPerms = permissionsManager.getUserPermissions(user);

      String playerName = user.getName() != null ? user.getName() : uid;
      p.sendMessage(" " + Theme.textPrefix()
          + Main.tr(p, "command-permissions-assignments-player-entry", "player", playerName));
      p.sendMessage("     " + Theme.textPrefix()
          + Main.tr(p, "command-permissions-assignments-group-entry", "group", userGroup));
      p.sendMessage("     " + Theme.textPrefix()
          + Main.tr(p, "command-permissions-assignments-permissions-entry", "permissions", userPerms));
      p.sendMessage("");
    }
  }

  // -------------------
  // ASSIGNMENTS COMMAND
  // -------------------

  public void handleListCommand(CommandSender sender) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(Main.getNoPlayerMessage());
      return;
    }

    Player p = (Player) sender;

    p.sendMessage(Main.getPrefix() + Theme.highlight() + ChatColor.BOLD
        + Main.tr(p, "command-permissions-list-title"));
    for (Permissions permission : Permissions.values()) {
      p.sendMessage(Main.getShortPrefix() + permission.getName());
    }
  }

  // ---------------------
  // RELOAD COMMAND METHOD
  // ---------------------

  private void handleReloadCommand(CommandSender sender, String[] args) {
    permissionsManager.reloadPermissions();
    sender.sendMessage(Main.getPrefix() + Main.tr(sender, "command-permissions-reload-success"));
  }

  // --------------
  // TAB COMPLETION
  // --------------

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    List<String> completions = new ArrayList<>();
    Set<String> groupNames = permissionsManager.getAllGroupNames();

    if (args.length == 1) {
      List<String> sections = List.of("group", "user", "assignments", "default", "list", "reload");
      completions.addAll(sections);
    } else if (args.length == 2) {
      if (args[0].equalsIgnoreCase("group")) {
        List<String> groupActions = List.of("addperm", "removeperm", "delete");
        completions.addAll(groupActions);
      } else if (args[0].equalsIgnoreCase("user")) {
        List<String> userActions = List.of("addperm", "removeperm", "setgroup");
        completions.addAll(userActions);
      } else if (args[0].equalsIgnoreCase("default")) {
        LinkedHashSet<String> suggestions = new LinkedHashSet<>(groupNames);
        suggestions.add("player");
        suggestions.add("moderator");
        suggestions.add("admin");
        completions.addAll(suggestions);
      }
    } else if (args.length == 3) {
      if (args[0].equalsIgnoreCase("group")) {
        completions.addAll(groupNames);
      } else if (args[0].equalsIgnoreCase("user")) {
        completions.addAll(getAvailableUsernames());
      }
    } else if (args.length == 4) {
      if (args[0].equalsIgnoreCase("user")) {
        String action = args[1].toLowerCase();

        if (action.equals("setgroup")) {
          completions.addAll(groupNames);
        }

        if (action.equals("addperm")) {
          Permissions[] availablePermission = Permissions.values();

          completions.addAll(Arrays.stream(availablePermission)
              .map(Permissions::getName)
              .collect(Collectors.toList()));
        }

        if (action.equals("removeperm")) {
          OfflinePlayer user = Bukkit.getOfflinePlayer(args[2]);

          if (user != null) {
            completions.addAll(permissionsManager.getUserPermissions(user));
          }
        }
      } else if (args[0].equalsIgnoreCase("group")) {
        String action = args[1].toLowerCase();

        if (action.equals("addperm")) {
          Permissions[] availablePermission = Permissions.values();

          completions.addAll(Arrays.stream(availablePermission)
              .map(Permissions::getName)
              .collect(Collectors.toList()));
        }

        if (action.equals("removeperm")) {
          completions.addAll(permissionsManager.getGroupPermissions(args[2]));
        }

        if (action.equals("delete")) {
          completions.addAll(permissionsManager.getAllGroupNames());
        }
      }
    }

    return completions;
  }

  private List<String> getAvailableUsernames() {
    List<String> userNames = new ArrayList<>();

    for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
      if (p.getName() != null) {
        userNames.add(p.getName());
      }
    }

    return userNames;
  }
}
