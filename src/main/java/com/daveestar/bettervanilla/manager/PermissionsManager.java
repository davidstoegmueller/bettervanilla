package com.daveestar.bettervanilla.manager;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.utils.Config;

import java.util.*;

public class PermissionsManager {
  private static final String GROUPS_PATH = "groups";
  private static final String USERS_PATH = "users";
  private static final String CONFIG_PATH = "config";
  private static final String DEFAULT_GROUP_KEY = CONFIG_PATH + ".default-group";

  private String defaultGroupName = "player";

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
    boolean updated = false;

    if (!_fileConfig.contains(CONFIG_PATH)) {
      _fileConfig.createSection(CONFIG_PATH);
      updated = true;
    }

    if (!_fileConfig.contains(GROUPS_PATH)) {
      _fileConfig.createSection(GROUPS_PATH);
      updated = true;
    }

    if (!_fileConfig.contains(USERS_PATH)) {
      _fileConfig.createSection(USERS_PATH);
      updated = true;
    }

    if (!_fileConfig.contains(DEFAULT_GROUP_KEY)) {
      _fileConfig.set(DEFAULT_GROUP_KEY, defaultGroupName);
      updated = true;
    }

    defaultGroupName = _fileConfig.getString(DEFAULT_GROUP_KEY, defaultGroupName);

    migrateLegacyDefaultGroupIfNeeded();

    addGroup(defaultGroupName);
    seedRecommendedGroups();

    if (updated) {
      _save();
    }
  }

  private void _save() {
    _config.save();
  }

  private void migrateLegacyDefaultGroupIfNeeded() {
    String legacyGroupName = "default";
    String targetGroupName = "player";

    if (!groupExists(legacyGroupName)) {
      return;
    }

    boolean legacyWasDefault = legacyGroupName.equalsIgnoreCase(defaultGroupName);
    boolean targetExisted = groupExists(targetGroupName);
    boolean changed = false;

    List<String> legacyPermissions = _fileConfig.getStringList(GROUPS_PATH + "." + legacyGroupName + ".permissions");

    addGroup(targetGroupName);

    if (!legacyPermissions.isEmpty()) {
      if (!targetExisted) {
        _fileConfig.set(GROUPS_PATH + "." + targetGroupName + ".permissions", new ArrayList<>(legacyPermissions));
        changed = true;
      } else {
        List<String> currentTarget = _fileConfig.getStringList(GROUPS_PATH + "." + targetGroupName + ".permissions");
        LinkedHashSet<String> merged = new LinkedHashSet<>(currentTarget);
        int originalSize = merged.size();
        merged.addAll(legacyPermissions);
        if (merged.size() != originalSize) {
          _fileConfig.set(GROUPS_PATH + "." + targetGroupName + ".permissions", new ArrayList<>(merged));
          changed = true;
        }
      }
    }

    var userSection = _fileConfig.getConfigurationSection(USERS_PATH);
    if (userSection != null) {
      for (String userId : userSection.getKeys(false)) {
        String path = USERS_PATH + "." + userId + ".group";
        String assignedGroup = _fileConfig.getString(path);
        if (assignedGroup != null && legacyGroupName.equalsIgnoreCase(assignedGroup)) {
          _fileConfig.set(path, targetGroupName);
          changed = true;
        }
      }
    }

    _fileConfig.set(GROUPS_PATH + "." + legacyGroupName, null);
    changed = true;

    if (legacyWasDefault) {
      defaultGroupName = targetGroupName;
      _fileConfig.set(DEFAULT_GROUP_KEY, defaultGroupName);
    }

    if (changed) {
      _save();
    }
  }

  private void seedRecommendedGroups() {
    Map<String, List<String>> recommended = buildRecommendedGroupPermissions();
    boolean updated = false;

    for (Map.Entry<String, List<String>> entry : recommended.entrySet()) {
      String groupName = entry.getKey();
      List<String> recommendedPerms = entry.getValue();

      addGroup(groupName);

      String permissionsPath = GROUPS_PATH + "." + groupName + ".permissions";
      List<String> existingPermissions = new ArrayList<>(_fileConfig.getStringList(permissionsPath));

      LinkedHashSet<String> merged = new LinkedHashSet<>(existingPermissions);
      boolean changed = false;

      for (String perm : recommendedPerms) {
        if (!merged.contains(perm)) {
          merged.add(perm);
          changed = true;
        }
      }

      if (changed) {
        _fileConfig.set(permissionsPath, new ArrayList<>(merged));
        updated = true;
      }
    }

    if (updated) {
      _save();
    }
  }

  private Map<String, List<String>> buildRecommendedGroupPermissions() {
    List<String> defaultPermissions = new ArrayList<>();
    List<String> moderatorOnlyPermissions = new ArrayList<>();
    List<String> adminOnlyPermissions = new ArrayList<>();

    for (Permissions permission : Permissions.values()) {
      switch (permission.getCategory()) {
        case PLAYER:
          defaultPermissions.add(permission.getName());
          break;
        case MODERATOR:
          moderatorOnlyPermissions.add(permission.getName());
          break;
        case ADMIN:
          adminOnlyPermissions.add(permission.getName());
          break;
      }
    }

    LinkedHashSet<String> defaultGroupPermissions = new LinkedHashSet<>(defaultPermissions);
    LinkedHashSet<String> moderatorGroupPermissions = new LinkedHashSet<>(defaultGroupPermissions);
    moderatorGroupPermissions.addAll(moderatorOnlyPermissions);

    LinkedHashSet<String> adminGroupPermissions = new LinkedHashSet<>(moderatorGroupPermissions);
    adminGroupPermissions.addAll(adminOnlyPermissions);

    Map<String, List<String>> recommended = new LinkedHashMap<>();
    recommended.put("player", new ArrayList<>(defaultGroupPermissions));
    recommended.put("moderator", new ArrayList<>(moderatorGroupPermissions));
    recommended.put("admin", new ArrayList<>(adminGroupPermissions));

    return recommended;
  }

  // ------------------------
  // GROUP PERMISSION METHODS
  // ------------------------

  public void addGroup(String groupName) {
    String groupPath = GROUPS_PATH + "." + groupName;
    if (!_fileConfig.contains(groupPath)) {
      _fileConfig.createSection(groupPath);
      _fileConfig.set(groupPath + ".permissions", new ArrayList<String>());
      _save();
    }
  }

  public void removeGroup(String groupName) {
    _fileConfig.set(GROUPS_PATH + "." + groupName, null);

    var userSection = _fileConfig.getConfigurationSection(USERS_PATH);
    if (userSection != null) {
      Set<String> userIds = userSection.getKeys(false);

      for (String userId : userIds) {
        if (_fileConfig.contains(USERS_PATH + "." + userId + ".group") &&
            _fileConfig.getString(USERS_PATH + "." + userId + ".group").equals(groupName)) {
          _fileConfig.set(USERS_PATH + "." + userId + ".group", defaultGroupName);
        }
      }
    }

    _save();
  }

  public void addPermissionToGroup(String groupName, String permission) {
    addGroup(groupName);

    List<String> perms = _fileConfig.getStringList(GROUPS_PATH + "." + groupName + ".permissions");

    if (!perms.contains(permission)) {
      perms.add(permission);
      _fileConfig.set(GROUPS_PATH + "." + groupName + ".permissions", perms);
      _save();
    }
  }

  public void removePermissionFromGroup(String groupName, String permission) {
    if (_fileConfig.contains(GROUPS_PATH + "." + groupName + ".permissions")) {
      List<String> perms = _fileConfig.getStringList(GROUPS_PATH + "." + groupName + ".permissions");

      if (perms.contains(permission)) {
        perms.remove(permission);
        _fileConfig.set(GROUPS_PATH + "." + groupName + ".permissions", perms);
        _save();
      }
    }
  }

  public boolean hasGroupPermission(String groupName, String permission) {
    List<String> perms = _fileConfig.getStringList(GROUPS_PATH + "." + groupName + ".permissions");
    return perms.contains(permission);
  }

  public Set<String> getAllGroupNames() {
    if (_fileConfig.contains(GROUPS_PATH)) {
      return _fileConfig.getConfigurationSection(GROUPS_PATH).getKeys(false);
    }

    return new HashSet<>();
  }

  public List<String> getGroupPermissions(String groupName) {
    return _fileConfig.getStringList(GROUPS_PATH + "." + groupName + ".permissions");
  }

  public boolean groupExists(String groupName) {
    return _fileConfig.contains(GROUPS_PATH + "." + groupName);
  }

  public String getDefaultGroupName() {
    return defaultGroupName;
  }

  public void setDefaultGroup(String groupName) {
    boolean existedBefore = groupExists(groupName);
    addGroup(groupName);

    if (!existedBefore) {
      List<String> recommendedDefaults = buildRecommendedGroupPermissions().getOrDefault("player",
          Collections.emptyList());
      if (!recommendedDefaults.isEmpty()) {
        _fileConfig.set(GROUPS_PATH + "." + groupName + ".permissions", new ArrayList<>(recommendedDefaults));
      }
    }

    String previousDefault = defaultGroupName;
    defaultGroupName = groupName;
    _fileConfig.set(DEFAULT_GROUP_KEY, defaultGroupName);

    if (!previousDefault.equals(defaultGroupName)) {
      var userSection = _fileConfig.getConfigurationSection(USERS_PATH);
      if (userSection != null) {
        for (String userId : userSection.getKeys(false)) {
          String currentGroup = _fileConfig.getString(USERS_PATH + "." + userId + ".group", previousDefault);
          if (currentGroup.equals(previousDefault)) {
            _fileConfig.set(USERS_PATH + "." + userId + ".group", defaultGroupName);
          }
        }
      }
    }

    _save();
    reloadPermissions();
  }

  // -----------------------
  // USER PERMISSION METHODS
  // -----------------------

  public void assignUserToGroup(OfflinePlayer p, String groupName) {
    addGroup(groupName);

    String uuid = p.getUniqueId().toString();

    _fileConfig.set(USERS_PATH + "." + uuid + ".group", groupName);
    _save();
  }

  public void addPermissionToUser(OfflinePlayer p, String permission) {
    String uuid = p.getUniqueId().toString();

    List<String> perms = _fileConfig.getStringList(USERS_PATH + "." + uuid + ".permissions");

    if (!perms.contains(permission)) {
      perms.add(permission);
      _fileConfig.set(USERS_PATH + "." + uuid + ".permissions", perms);
      _save();
    }
  }

  public void removePermissionFromUser(OfflinePlayer p, String permission) {
    String uuid = p.getUniqueId().toString();

    List<String> perms = _fileConfig.getStringList(USERS_PATH + "." + uuid + ".permissions");

    if (perms.contains(permission)) {
      perms.remove(permission);
      _fileConfig.set(USERS_PATH + "." + uuid + ".permissions", perms);
      _save();
    }
  }

  public boolean hasUserPermission(OfflinePlayer p, String permission) {
    String uuid = p.getUniqueId().toString();

    List<String> perms = _fileConfig.getStringList(USERS_PATH + "." + uuid + ".permissions");
    return perms.contains(permission);
  }

  public Set<String> getAllUserIds() {
    if (_fileConfig.contains(USERS_PATH)) {
      return _fileConfig.getConfigurationSection(USERS_PATH).getKeys(false);
    }

    return new HashSet<>();
  }

  public List<String> getUserPermissions(OfflinePlayer p) {
    String uuid = p.getUniqueId().toString();

    return _fileConfig.getStringList(USERS_PATH + "." + uuid + ".permissions");
  }

  public String getUserGroup(OfflinePlayer p) {
    String uuid = p.getUniqueId().toString();

    return _fileConfig.getString(USERS_PATH + "." + uuid + ".group", defaultGroupName);
  }

  public List<String> getEffectivePermissions(OfflinePlayer p) {
    String uuid = p.getUniqueId().toString();

    Set<String> effectivePermissions = new HashSet<>();

    // get group permissions (default if no group is set).
    String group = _fileConfig.getString(USERS_PATH + "." + uuid + ".group", defaultGroupName);
    List<String> groupPerms = _fileConfig.getStringList(GROUPS_PATH + "." + group + ".permissions");
    effectivePermissions.addAll(groupPerms);

    // overlay user-specific permissions.
    List<String> userPerms = _fileConfig.getStringList(USERS_PATH + "." + uuid + ".permissions");
    effectivePermissions.addAll(userPerms);

    return new ArrayList<>(effectivePermissions);
  }

  // -------------------------
  // PLAYER JOIN/LEAVE MEHTODS
  // -------------------------

  public void onPlayerJoined(Player p) {
    String uuid = p.getUniqueId().toString();

    if (!_fileConfig.contains(USERS_PATH + "." + uuid)) {
      _fileConfig.createSection(USERS_PATH + "." + uuid);
      _fileConfig.set(USERS_PATH + "." + uuid + ".group", defaultGroupName);
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
