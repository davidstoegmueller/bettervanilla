package com.daveestar.bettervanilla.manager;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.utils.Config;

import java.util.*;

public class PermissionsManager {
  public final String DEFAULT_GROUP_NAME = "default";

  private final Config _config;
  private final FileConfiguration _fileConfig;
  private final JavaPlugin plugin;

  private final Map<UUID, PermissionAttachment> activeAttachments = new HashMap<>();

  public PermissionsManager(Config config) {
    plugin = Main.getInstance();
    _config = config;
    _fileConfig = config.getFileConfig();

    _loadConfig();
  }

  private void _loadConfig() {
    // create sections for groups and users if not present
    if (!_fileConfig.contains("groups")) {
      _fileConfig.createSection("groups");
    }

    if (!_fileConfig.contains("users")) {
      _fileConfig.createSection("users");
    }

    // ensure the default group exists
    if (!_fileConfig.contains("groups." + DEFAULT_GROUP_NAME)) {
      addGroup(DEFAULT_GROUP_NAME);
    }

    _save();
  }

  private void _save() {
    _config.save();
  }

  // ------------------------
  // GROUP PERMISSION METHODS
  // ------------------------

  public void addGroup(String groupName) {
    if (!_fileConfig.contains("groups." + groupName)) {
      _fileConfig.createSection("groups." + groupName);
      _fileConfig.set("groups." + groupName + ".permissions", new ArrayList<String>());
      _save();
    }
  }

  public void removeGroup(String groupName) {
    _fileConfig.set("groups." + groupName, null);

    // remove all users assigned to this group
    Set<String> userIds = _fileConfig.getConfigurationSection("users").getKeys(false);
    for (String userId : userIds) {
      if (_fileConfig.contains("users." + userId + ".group") &&
          _fileConfig.getString("users." + userId + ".group").equals(groupName)) {
        _fileConfig.set("users." + userId + ".group", DEFAULT_GROUP_NAME);
      }
    }

    _save();
  }

  public void addPermissionToGroup(String groupName, String permission) {
    addGroup(groupName);

    List<String> perms = _fileConfig.getStringList("groups." + groupName + ".permissions");

    if (!perms.contains(permission)) {
      perms.add(permission);
      _fileConfig.set("groups." + groupName + ".permissions", perms);
      _save();
    }
  }

  public void removePermissionFromGroup(String groupName, String permission) {
    if (_fileConfig.contains("groups." + groupName + ".permissions")) {
      List<String> perms = _fileConfig.getStringList("groups." + groupName + ".permissions");

      if (perms.contains(permission)) {
        perms.remove(permission);
        _fileConfig.set("groups." + groupName + ".permissions", perms);
        _save();
      }
    }
  }

  public boolean hasGroupPermission(String groupName, String permission) {
    List<String> perms = _fileConfig.getStringList("groups." + groupName + ".permissions");
    return perms.contains(permission);
  }

  public Set<String> getAllGroupNames() {
    if (_fileConfig.contains("groups")) {
      return _fileConfig.getConfigurationSection("groups").getKeys(false);
    }

    return new HashSet<>();
  }

  public List<String> getGroupPermissions(String groupName) {
    return _fileConfig.getStringList("groups." + groupName + ".permissions");
  }

  // -----------------------
  // USER PERMISSION METHODS
  // -----------------------

  public void assignUserToGroup(OfflinePlayer p, String groupName) {
    addGroup(groupName);

    String uuid = p.getUniqueId().toString();

    _fileConfig.set("users." + uuid + ".group", groupName);
    _save();
  }

  public void addPermissionToUser(OfflinePlayer p, String permission) {
    String uuid = p.getUniqueId().toString();

    List<String> perms = _fileConfig.getStringList("users." + uuid + ".permissions");

    if (!perms.contains(permission)) {
      perms.add(permission);
      _fileConfig.set("users." + uuid + ".permissions", perms);
      _save();
    }
  }

  public void removePermissionFromUser(OfflinePlayer p, String permission) {
    String uuid = p.getUniqueId().toString();

    List<String> perms = _fileConfig.getStringList("users." + uuid + ".permissions");

    if (perms.contains(permission)) {
      perms.remove(permission);
      _fileConfig.set("users." + uuid + ".permissions", perms);
      _save();
    }
  }

  public boolean hasUserPermission(OfflinePlayer p, String permission) {
    String uuid = p.getUniqueId().toString();

    List<String> perms = _fileConfig.getStringList("users." + uuid + ".permissions");
    return perms.contains(permission);
  }

  public Set<String> getAllUserIds() {
    if (_fileConfig.contains("users")) {
      return _fileConfig.getConfigurationSection("users").getKeys(false);
    }

    return new HashSet<>();
  }

  public List<String> getUserPermissions(OfflinePlayer p) {
    String uuid = p.getUniqueId().toString();

    return _fileConfig.getStringList("users." + uuid + ".permissions");
  }

  public String getUserGroup(OfflinePlayer p) {
    String uuid = p.getUniqueId().toString();

    return _fileConfig.getString("users." + uuid + ".group", DEFAULT_GROUP_NAME);
  }

  public List<String> getEffectivePermissions(OfflinePlayer p) {
    String uuid = p.getUniqueId().toString();

    Set<String> effectivePermissions = new HashSet<>();

    // get group permissions (default if no group is set).
    String group = _fileConfig.getString("users." + uuid + ".group", DEFAULT_GROUP_NAME);
    List<String> groupPerms = _fileConfig.getStringList("groups." + group + ".permissions");
    effectivePermissions.addAll(groupPerms);

    // overlay user-specific permissions.
    List<String> userPerms = _fileConfig.getStringList("users." + uuid + ".permissions");
    effectivePermissions.addAll(userPerms);

    return new ArrayList<>(effectivePermissions);
  }

  // -------------------------
  // PLAYER JOIN/LEAVE MEHTODS
  // -------------------------

  public void onPlayerJoined(Player p) {
    String uuid = p.getUniqueId().toString();

    if (!_fileConfig.contains("users." + uuid)) {
      _fileConfig.createSection("users." + uuid);
      _fileConfig.set("users." + uuid + ".group", DEFAULT_GROUP_NAME);
      _save();
    }

    List<String> effectivePermissions = getEffectivePermissions(p);
    PermissionAttachment attachment = p.addAttachment(plugin);

    for (String perm : effectivePermissions) {
      attachment.setPermission(perm, true);
    }

    activeAttachments.put(p.getUniqueId(), attachment);
    p.updateCommands();
  }

  public void onPlayerLeft(Player p) {
    UUID uid = p.getUniqueId();

    if (activeAttachments.containsKey(uid)) {
      p.removeAttachment(activeAttachments.get(uid));
      activeAttachments.remove(uid);
    }
  }

  // -------------------------
  // RELOAD PERMISSIONS METHOD
  // -------------------------

  public void reloadPermissions() {
    // Iterate over all online players and re-apply permissions
    for (Player p : plugin.getServer().getOnlinePlayers()) {
      UUID uid = p.getUniqueId();

      // Remove existing permission attachment if present
      if (activeAttachments.containsKey(uid)) {
        p.removeAttachment(activeAttachments.get(uid));
        activeAttachments.remove(uid);
      }

      // Retrieve effective permissions and create new attachment
      List<String> effectivePermissions = getEffectivePermissions(p);
      PermissionAttachment attachment = p.addAttachment(plugin);
      for (String perm : effectivePermissions) {
        attachment.setPermission(perm, true);
      }
      activeAttachments.put(uid, attachment);
      p.updateCommands();
    }
  }
}
