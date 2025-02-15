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

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
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
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length < 1) {
      sender.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: "
          + ChatColor.YELLOW + "/permissions <group | user | assignments | list | reload>");
      return true;
    }

    String section = args[0].toLowerCase();
    switch (section) {
      case "group":
        handleGroupCommand(sender, args);
        break;
      case "user":
        handleUserCommand(sender, args);
        break;
      case "assignments":
        handleAssignmentsCommand(sender);
        break;
      case "list":
        handleListCommand(sender);
        break;
      case "reload":
        handleReloadCommand(sender, args);
        break;
      default:
        sender.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: "
            + ChatColor.YELLOW + "/permissions <group | user | assignments | list | reload>");
        break;
    }
    return true;
  }

  // --------------
  // GROUP COMMANDS
  // --------------

  private void handleGroupCommand(CommandSender sender, String[] args) {
    if (args.length != 4) {
      sender.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW
          + " /permissions group <addperm | removeperm> <group> <permission>");
      return;
    }

    String group = args[2];
    String permission = args[3];

    String action = args[1].toLowerCase();
    switch (action) {
      case "addperm":
        // syntax: /permissions group addperm <group> <permission>
        if (args.length != 4) {
          sender.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW
              + " /permissions group addperm <group> <permission>");
          return;
        }

        if (!permissionsManager.hasGroupPermission(group, permission)) {
          permissionsManager.addPermissionToGroup(group, permission);
          sender.sendMessage(Main.getPrefix() + "Permission " + ChatColor.YELLOW + permission + ChatColor.GRAY
              + " added to group " + ChatColor.YELLOW + group);
        } else {
          sender.sendMessage(
              Main.getPrefix() + ChatColor.RED + "Permission " + ChatColor.YELLOW + permission + ChatColor.RED
                  + " has already been added to group " + ChatColor.YELLOW + group);
        }
        break;
      case "removeperm":
        // syntax: /permissions group removeperm <group> <permission>
        if (args.length != 4) {
          sender.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW
              + "/permissions group removeperm <group> <permission>");
          return;
        }

        if (permissionsManager.hasGroupPermission(group, permission)) {
          permissionsManager.removePermissionFromGroup(group, permission);
          sender.sendMessage(Main.getPrefix() + "Permission " + ChatColor.YELLOW + permission + ChatColor.GRAY
              + " removed from group " + ChatColor.YELLOW + group);
        } else {
          sender.sendMessage(Main.getPrefix() + ChatColor.RED + "Permission " + ChatColor.YELLOW + permission
              + ChatColor.RED + " is not assigned to group " + ChatColor.YELLOW + group);
        }
        break;
      default:
        sender.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW
            + " /permissions group <addperm | removeperm> <group> <permission>");
        break;
    }
  }

  // -------------
  // USER COMMANDS
  // -------------

  private void handleUserCommand(CommandSender sender, String[] args) {
    if (args.length != 4) {
      sender.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW
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
          sender.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW
              + "/permissions user addperm <username> <permission>");
          return;
        }

        if (!permissionsManager.hasUserPermission(p, permission)) {
          permissionsManager.addPermissionToUser(p, permission);
          sender.sendMessage(Main.getPrefix() + "Permission " + ChatColor.YELLOW + permission + ChatColor.GRAY
              + " added to user " + ChatColor.YELLOW + p.getName());
        } else {
          sender.sendMessage(Main.getPrefix() + ChatColor.RED + "Permission " + ChatColor.YELLOW + permission
              + ChatColor.RED + " has already been added to user " + ChatColor.YELLOW + p.getName());
        }
        break;
      case "removeperm":
        // syntax: /permissions user removeperm <username> <permission>
        if (args.length != 4) {
          sender.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW
              + "/permissions user removeperm <username> <permission>");
          return;
        }

        if (permissionsManager.hasUserPermission(p, permission)) {
          permissionsManager.removePermissionFromUser(p, permission);
          sender.sendMessage(Main.getPrefix() + "Permission " + ChatColor.YELLOW + permission + ChatColor.GRAY
              + " removed from user " + ChatColor.YELLOW + p.getName());
        } else {
          sender.sendMessage(Main.getPrefix() + ChatColor.RED + "Permission " + ChatColor.YELLOW + permission
              + ChatColor.RED + " is not assigned to user " + ChatColor.YELLOW + p.getName());
        }
        break;
      case "setgroup":
        // syntax: /permissions user setgroup <username> <group>
        if (args.length != 4) {
          sender.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW
              + "/permissions user setgroup <username> <group>");
          return;
        }

        if (!permissionsManager.getUserGroup(p).equalsIgnoreCase(group)) {
          permissionsManager.assignUserToGroup(p, group);
          sender.sendMessage(Main.getPrefix() + "User " + ChatColor.YELLOW + p.getName() + ChatColor.GRAY
              + " assigned to group " + ChatColor.YELLOW + group);
        } else {
          sender.sendMessage(Main.getPrefix() + ChatColor.RED + "User " + ChatColor.YELLOW + p.getName() + ChatColor.RED
              + " is already assigned to group " + ChatColor.YELLOW + group);
        }
        break;
      default:
        sender.sendMessage(Main.getPrefix() + ChatColor.RED + "Usage: " + ChatColor.YELLOW
            + "/permissions user <addperm | removeperm | setgroup> <username> <permission | group>");
        break;
    }
  }

  // ------------
  // LIST COMMAND
  // ------------

  private void handleAssignmentsCommand(CommandSender sender) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(Main.getPrefix() + ChatColor.RED + "You must be a player to use this command.");
      return;
    }

    Player p = (Player) sender;
    Set<String> groupNames = permissionsManager.getAllGroupNames();
    Set<String> userNames = permissionsManager.getAllUserIds();

    p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "PERMISSIONS: Group Assignments");

    for (String group : groupNames) {
      List<String> groupPerms = permissionsManager.getGroupPermissions(group);

      p.sendMessage(ChatColor.YELLOW + " » Group: " + ChatColor.GRAY + group);
      p.sendMessage(ChatColor.YELLOW + "     » Permissions: "
          + ChatColor.GRAY + groupPerms);

      List<String> usersInGroup = new ArrayList<>();
      for (String uid : userNames) {
        OfflinePlayer user = Bukkit.getOfflinePlayer(UUID.fromString(uid));

        if (permissionsManager.getUserGroup(user).equals(group)) {
          usersInGroup.add(user.getName());
        }
      }

      p.sendMessage(ChatColor.YELLOW + "     » Users: " + ChatColor.GRAY + usersInGroup);
    }
    p.sendMessage("");

    p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "PERMISSIONS: User Assignments");

    for (String uid : userNames) {
      OfflinePlayer user = Bukkit.getOfflinePlayer(UUID.fromString(uid));

      String userGroup = permissionsManager.getUserGroup(user);
      List<String> userPerms = permissionsManager.getUserPermissions(user);

      p.sendMessage(ChatColor.YELLOW + " » User: " + ChatColor.GRAY + user.getName());
      p.sendMessage(ChatColor.YELLOW + "     » Group: " + ChatColor.GRAY + userGroup);
      p.sendMessage(ChatColor.YELLOW + "     » Permissions: " + ChatColor.GRAY + userPerms);
    }
  }

  // -------------------
  // ASSIGNMENTS COMMAND
  // -------------------

  public void handleListCommand(CommandSender sender) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(Main.getPrefix() + ChatColor.RED + "You must be a player to use this command.");
      return;
    }

    Player p = (Player) sender;

    p.sendMessage(Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "PERMISSIONS: List of Permissions");
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
      List<String> sections = List.of("group", "user", "assignments", "list", "reload");
      completions.addAll(sections);
    } else if (args.length == 2) {
      if (args[0].equalsIgnoreCase("group")) {
        List<String> groupActions = List.of("addperm", "removeperm");
        completions.addAll(groupActions);
      } else if (args[0].equalsIgnoreCase("user")) {
        List<String> userActions = List.of("addperm", "removeperm", "setgroup");
        completions.addAll(userActions);
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
