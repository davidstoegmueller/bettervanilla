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
  private final String _DEFAULT_GROUP_NAME = "default";

  private final Config _config;
  private final FileConfiguration _fileCfgn;
  private final JavaPlugin plugin;

  private final Map<UUID, PermissionAttachment> activeAttachments = new HashMap<>();

  public PermissionsManager(Config config) {
    this.plugin = Main.getInstance();
    this._config = config;
    this._fileCfgn = config.getFileCfgrn();

    _init();
  }

  private void _init() {
    // create sections for groups and users if not present
    if (!_fileCfgn.contains("groups")) {
      _fileCfgn.createSection("groups");
    }

    if (!_fileCfgn.contains("users")) {
      _fileCfgn.createSection("users");
    }

    // ensure the default group exists
    if (!_fileCfgn.contains("groups." + _DEFAULT_GROUP_NAME)) {
      addGroup(_DEFAULT_GROUP_NAME);
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
    if (!_fileCfgn.contains("groups." + groupName)) {
      _fileCfgn.createSection("groups." + groupName);
      _fileCfgn.set("groups." + groupName + ".permissions", new ArrayList<String>());
      _save();
    }
  }

  public void removeGroup(String groupName) {
    _fileCfgn.set("groups." + groupName, null);
    _save();
  }

  public void addPermissionToGroup(String groupName, String permission) {
    addGroup(groupName);

    List<String> perms = _fileCfgn.getStringList("groups." + groupName + ".permissions");

    if (!perms.contains(permission)) {
      perms.add(permission);
      _fileCfgn.set("groups." + groupName + ".permissions", perms);
      _save();
    }
  }

  public void removePermissionFromGroup(String groupName, String permission) {
    if (_fileCfgn.contains("groups." + groupName + ".permissions")) {
      List<String> perms = _fileCfgn.getStringList("groups." + groupName + ".permissions");

      if (perms.contains(permission)) {
        perms.remove(permission);
        _fileCfgn.set("groups." + groupName + ".permissions", perms);
        _save();
      }
    }
  }

  public boolean hasGroupPermission(String groupName, String permission) {
    List<String> perms = _fileCfgn.getStringList("groups." + groupName + ".permissions");
    return perms.contains(permission);
  }

  public Set<String> getAllGroupNames() {
    if (_fileCfgn.contains("groups")) {
      return _fileCfgn.getConfigurationSection("groups").getKeys(false);
    }

    return new HashSet<>();
  }

  public List<String> getGroupPermissions(String groupName) {
    return _fileCfgn.getStringList("groups." + groupName + ".permissions");
  }

  // -----------------------
  // USER PERMISSION METHODS
  // -----------------------

  public void assignUserToGroup(OfflinePlayer p, String groupName) {
    addGroup(groupName);

    String uuid = p.getUniqueId().toString();

    _fileCfgn.set("users." + uuid + ".group", groupName);
    _save();
  }

  public void addPermissionToUser(OfflinePlayer p, String permission) {
    String uuid = p.getUniqueId().toString();

    List<String> perms = _fileCfgn.getStringList("users." + uuid + ".permissions");

    if (!perms.contains(permission)) {
      perms.add(permission);
      _fileCfgn.set("users." + uuid + ".permissions", perms);
      _save();
    }
  }

  public void removePermissionFromUser(OfflinePlayer p, String permission) {
    String uuid = p.getUniqueId().toString();

    List<String> perms = _fileCfgn.getStringList("users." + uuid + ".permissions");

    if (perms.contains(permission)) {
      perms.remove(permission);
      _fileCfgn.set("users." + uuid + ".permissions", perms);
      _save();
    }
  }

  public boolean hasUserPermission(OfflinePlayer p, String permission) {
    String uuid = p.getUniqueId().toString();

    List<String> perms = _fileCfgn.getStringList("users." + uuid + ".permissions");
    return perms.contains(permission);
  }

  public Set<String> getAllUserIds() {
    if (_fileCfgn.contains("users")) {
      return _fileCfgn.getConfigurationSection("users").getKeys(false);
    }

    return new HashSet<>();
  }

  public List<String> getUserPermissions(OfflinePlayer p) {
    String uuid = p.getUniqueId().toString();

    return _fileCfgn.getStringList("users." + uuid + ".permissions");
  }

  public String getUserGroup(OfflinePlayer p) {
    String uuid = p.getUniqueId().toString();

    return _fileCfgn.getString("users." + uuid + ".group", _DEFAULT_GROUP_NAME);
  }

  public List<String> getEffectivePermissions(OfflinePlayer p) {
    String uuid = p.getUniqueId().toString();

    Set<String> effectivePermissions = new HashSet<>();

    // get group permissions (default if no group is set).
    String group = _fileCfgn.getString("users." + uuid + ".group", _DEFAULT_GROUP_NAME);
    List<String> groupPerms = _fileCfgn.getStringList("groups." + group + ".permissions");
    effectivePermissions.addAll(groupPerms);

    // overlay user-specific permissions.
    List<String> userPerms = _fileCfgn.getStringList("users." + uuid + ".permissions");
    effectivePermissions.addAll(userPerms);

    return new ArrayList<>(effectivePermissions);
  }
  // -------------------------
  // PLAYER JOIN/LEAVE MEHTODS
  // -------------------------

  public void onPlayerJoined(Player p) {
    String uuid = p.getUniqueId().toString();

    if (!_fileCfgn.contains("users." + uuid)) {
      _fileCfgn.createSection("users." + uuid);
      _fileCfgn.set("users." + uuid + ".group", _DEFAULT_GROUP_NAME);
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
}
