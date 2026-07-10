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
        p.sendMessage(Main.getNoPermissionMessage(Permissions.PERMISSIONS));
        return true;
      }
    }

    if (args.length < 1) {
      cs.sendMessage(Main.getPrefix() + Theme.error() + "Usage: "
          + Theme.highlight() + "/permissions <group | user | assignments | default | list | reload>");
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
        cs.sendMessage(Main.getPrefix() + Theme.error() + "Usage: "
            + Theme.highlight() + "/permissions <group | user | assignments | default | list | reload>");
        break;
    }

    return true;
  }

  // --------------
  // GROUP COMMANDS
  // --------------

  private void handleGroupCommand(CommandSender sender, String[] args) {
    if (args.length < 3) {
      sender.sendMessage(Main.getPrefix() + Theme.error() + "Usage: " + Theme.highlight()
          + "/permissions group <addperm | removeperm | delete> <group> [<permission>]");
      return;
    }

    String group = args[2];
    String permission = args.length == 4 ? args[3] : "";

    String action = args[1].toLowerCase();
    switch (action) {
      case "addperm":
        // syntax: /permissions group addperm <group> <permission>
        if (args.length != 4) {
          sender.sendMessage(Main.getPrefix() + Theme.error() + "Usage: " + Theme.highlight()
              + " /permissions group addperm <group> <permission>");
          return;
        }

        if (!permissionsManager.hasGroupPermission(group, permission)) {
          permissionsManager.addPermissionToGroup(group, permission);
          sender.sendMessage(Main.getPrefix() + "Permission " + Theme.highlight() + permission + Theme.primary()
              + " added to group " + Theme.highlight() + group);
        } else {
          sender.sendMessage(
              Main.getPrefix() + Theme.error() + "Permission " + Theme.highlight() + permission + Theme.error()
                  + " has already been added to group " + Theme.highlight() + group);
        }
        break;
      case "removeperm":
        // syntax: /permissions group removeperm <group> <permission>
        if (args.length != 4) {
          sender.sendMessage(Main.getPrefix() + Theme.error() + "Usage: " + Theme.highlight()
              + "/permissions group removeperm <group> <permission>");
          return;
        }

        if (permissionsManager.hasGroupPermission(group, permission)) {
          permissionsManager.removePermissionFromGroup(group, permission);
          sender.sendMessage(Main.getPrefix() + "Permission " + Theme.highlight() + permission + Theme.primary()
              + " removed from group " + Theme.highlight() + group);
        } else {
          sender.sendMessage(Main.getPrefix() + Theme.error() + "Permission " + Theme.highlight() + permission
              + Theme.error() + " is not assigned to group " + Theme.highlight() + group);
        }
        break;
      case "delete":
        // syntax: /permissions group delete <group>
        if (args.length != 3) {
          sender.sendMessage(Main.getPrefix() + Theme.error() + "Usage: " + Theme.highlight()
              + "/permissions group delete <group>");
          return;
        }

        if (permissionsManager.getAllGroupNames().contains(group)) {
          if (group.equalsIgnoreCase(permissionsManager.getDefaultGroupName())) {
            sender.sendMessage(Main.getPrefix() + Theme.error() + "You cannot delete the default group.");
            return;
          }

          permissionsManager.removeGroup(group);
          sender.sendMessage(Main.getPrefix() + "Group " + Theme.highlight() + group + Theme.primary()
              + " has been deleted.");
        } else {
          sender.sendMessage(Main.getPrefix() + Theme.error() + "Group " + Theme.highlight() + group
              + Theme.error() + " does not exist.");
        }
        break;
      default:
        sender.sendMessage(Main.getPrefix() + Theme.error() + "Usage: " + Theme.highlight()
            + " /permissions group <addperm | removeperm | delete> <group> [<permission>]");
        break;
    }
  }

  private void handleDefaultGroupCommand(CommandSender sender, String[] args) {
    if (args.length == 1) {
      sender.sendMessage(
          Main.getPrefix() + Theme.primary() + "Current default group: " + Theme.highlight()
              + permissionsManager.getDefaultGroupName());
      sender.sendMessage(Main.getShortPrefix() + Theme.primary()
          + "Set another group with " + Theme.highlight() + "/permissions default <group>");
      return;
    }

    if (args.length != 2) {
      sender.sendMessage(Main.getPrefix() + Theme.error() + "Usage: " + Theme.highlight()
          + "/permissions default <group>");
      return;
    }

    String groupName = args[1];
    boolean groupExists = permissionsManager.groupExists(groupName);

    permissionsManager.setDefaultGroup(groupName);
    sender.sendMessage(Main.getPrefix() + Theme.primary() + "Default group set to " + Theme.highlight() + groupName
        + Theme.primary() + ".");

    if (!groupExists) {
      sender.sendMessage(Main.getShortPrefix() + Theme.primary() + "Created new group " + Theme.highlight() + groupName
          + Theme.primary() + ". Add permissions with " + Theme.highlight()
          + "/permissions group addperm " + groupName + " <permission>");
    }
  }

  // -------------
  // USER COMMANDS
  // -------------

  private void handleUserCommand(CommandSender sender, String[] args) {
    if (args.length != 4) {
      sender.sendMessage(Main.getPrefix() + Theme.error() + "Usage: " + Theme.highlight()
          + "/permissions user <addperm | removeperm | setgroup> <username> <permission | group>");
      return;
    }

    String action = args[1].toLowerCase();
    OfflinePlayer p = Bukkit.getOfflinePlayer(args[2]);
    String permission = args[3];
    String group = args[3];

    switch (action) {
      case "addperm":
        // syntax: /permissions user addperm <username> <permission>
        if (args.length != 4) {
          sender.sendMessage(Main.getPrefix() + Theme.error() + "Usage: " + Theme.highlight()
              + "/permissions user addperm <username> <permission>");
          return;
        }

        if (!permissionsManager.hasUserPermission(p, permission)) {
          permissionsManager.addPermissionToUser(p, permission);
          sender.sendMessage(Main.getPrefix() + "Permission " + Theme.highlight() + permission + Theme.primary()
              + " added to user " + Theme.highlight() + p.getName());
        } else {
          sender.sendMessage(Main.getPrefix() + Theme.error() + "Permission " + Theme.highlight() + permission
              + Theme.error() + " has already been added to user " + Theme.highlight() + p.getName());
        }
        break;
      case "removeperm":
        // syntax: /permissions user removeperm <username> <permission>
        if (args.length != 4) {
          sender.sendMessage(Main.getPrefix() + Theme.error() + "Usage: " + Theme.highlight()
              + "/permissions user removeperm <username> <permission>");
          return;
        }

        if (permissionsManager.hasUserPermission(p, permission)) {
          permissionsManager.removePermissionFromUser(p, permission);
          sender.sendMessage(Main.getPrefix() + "Permission " + Theme.highlight() + permission + Theme.primary()
              + " removed from user " + Theme.highlight() + p.getName());
        } else {
          sender.sendMessage(Main.getPrefix() + Theme.error() + "Permission " + Theme.highlight() + permission
              + Theme.error() + " is not assigned to user " + Theme.highlight() + p.getName());
        }
        break;
      case "setgroup":
        // syntax: /permissions user setgroup <username> <group>
        if (args.length != 4) {
          sender.sendMessage(Main.getPrefix() + Theme.error() + "Usage: " + Theme.highlight()
              + "/permissions user setgroup <username> <group>");
          return;
        }

        if (!permissionsManager.getUserGroup(p).equalsIgnoreCase(group)) {
          permissionsManager.assignUserToGroup(p, group);
          sender.sendMessage(Main.getPrefix() + "User " + Theme.highlight() + p.getName() + Theme.primary()
              + " assigned to group " + Theme.highlight() + group);
        } else {
          sender.sendMessage(Main.getPrefix() + Theme.error() + "User " + Theme.highlight() + p.getName() + Theme.error()
              + " is already assigned to group " + Theme.highlight() + group);
        }
        break;
      default:
        sender.sendMessage(Main.getPrefix() + Theme.error() + "Usage: " + Theme.highlight()
            + "/permissions user <addperm | removeperm | setgroup> <username> <permission | group>");
        break;
    }
  }

  // ------------
  // LIST COMMAND
  // ------------

  private void handleAssignmentsCommand(CommandSender sender) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(Main.getPrefix() + Theme.error() + "You must be a player to use this command.");
      return;
    }

    Player p = (Player) sender;
    Set<String> groupNames = permissionsManager.getAllGroupNames();
    Set<String> userNames = permissionsManager.getAllUserIds();

    p.sendMessage(Main.getPrefix() + Theme.highlight() + ChatColor.BOLD + "PERMISSIONS: Group Assignments");
    p.sendMessage(Main.getShortPrefix() + Theme.primary() + "Current default group: " + Theme.highlight()
        + permissionsManager.getDefaultGroupName());
    p.sendMessage("");

    for (String group : groupNames) {
      List<String> groupPerms = permissionsManager.getGroupPermissions(group);

      p.sendMessage(" " + Theme.textPrefix() + "Group: " + group);
      p.sendMessage("     " + Theme.textPrefix() + "Permissions: " + groupPerms);

      List<String> usersInGroup = new ArrayList<>();
      for (String uid : userNames) {
        OfflinePlayer user = Bukkit.getOfflinePlayer(UUID.fromString(uid));

        if (permissionsManager.getUserGroup(user).equals(group)) {
          usersInGroup.add(user.getName());
        }
      }

      p.sendMessage("     " + Theme.textPrefix() + "Users: " + usersInGroup);
      p.sendMessage("");
    }

    p.sendMessage("");

    p.sendMessage(Main.getPrefix() + Theme.highlight() + ChatColor.BOLD + "PERMISSIONS: User Assignments");

    for (String uid : userNames) {
      OfflinePlayer user = Bukkit.getOfflinePlayer(UUID.fromString(uid));

      String userGroup = permissionsManager.getUserGroup(user);
      List<String> userPerms = permissionsManager.getUserPermissions(user);

      p.sendMessage(" " + Theme.textPrefix() + "User: " + user.getName());
      p.sendMessage("     " + Theme.textPrefix() + "Group: " + userGroup);
      p.sendMessage("     " + Theme.textPrefix() + "Permissions: " + userPerms);
      p.sendMessage("");
    }
  }

  // -------------------
  // ASSIGNMENTS COMMAND
  // -------------------

  public void handleListCommand(CommandSender sender) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(Main.getPrefix() + Theme.error() + "You must be a player to use this command.");
      return;
    }

    Player p = (Player) sender;

    p.sendMessage(Main.getPrefix() + Theme.highlight() + ChatColor.BOLD + "PERMISSIONS: List of Permissions");
    for (Permissions permission : Permissions.values()) {
      p.sendMessage(Main.getShortPrefix() + permission.getName());
    }
  }

  // ---------------------
  // RELOAD COMMAND METHOD
  // ---------------------

  private void handleReloadCommand(CommandSender sender, String[] args) {
    permissionsManager.reloadPermissions();
    sender.sendMessage(Main.getPrefix() + "Permissions reloaded successfully.");
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
