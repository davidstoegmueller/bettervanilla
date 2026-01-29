package com.daveestar.bettervanilla.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import com.daveestar.bettervanilla.enums.WaypointVisibility;
import com.daveestar.bettervanilla.utils.Config;

public class WaypointsManager {
  private final static String KEY_NAME = "name";
  private final static String KEY_X = "x";
  private final static String KEY_Y = "y";
  private final static String KEY_Z = "z";
  private final static String KEY_OWNER = "owner";
  private final static String KEY_VISIBILITY = "visibility";
  private final static String KEY_ICON = "icon";
  private final Config _config;
  private final FileConfiguration _fileConfig;

  public WaypointsManager(Config config) {
    _config = config;
    _fileConfig = config.getFileConfig();
  }

  public List<String> getWaypoints(String worldName) {
    ConfigurationSection waypointsConfigSection = _fileConfig.getConfigurationSection(worldName);
    Set<String> allWaypointIds = waypointsConfigSection != null ? waypointsConfigSection.getKeys(false) : null;

    if (allWaypointIds == null) {
      return new ArrayList<>();
    }

    List<String> sortedWaypointIds = new ArrayList<>(allWaypointIds);
    Collections.sort(sortedWaypointIds,
        (left, right) -> {
          String leftName = getWaypointDisplayName(worldName, left);
          String rightName = getWaypointDisplayName(worldName, right);
          int compare = leftName.compareToIgnoreCase(rightName);
          if (compare != 0) {
            return compare;
          }
          return left.compareToIgnoreCase(right);
        });

    return sortedWaypointIds;
  }

  public String getWaypointDisplayName(String worldName, String waypointId) {
    String path = _getWaypointBasePath(worldName, waypointId) + "." + KEY_NAME;
    String storedName = _fileConfig.getString(path);

    if (storedName != null && !storedName.isEmpty()) {
      return storedName;
    }

    return waypointId;
  }

  public HashMap<String, Integer> getWaypointCoordinates(String worldName, String waypointId) {
    String basePath = _getWaypointBasePath(worldName, waypointId);
    ConfigurationSection waypoint = _fileConfig.getConfigurationSection(basePath);

    HashMap<String, Integer> coordinates = new HashMap<>();

    coordinates.put(KEY_X, waypoint != null ? waypoint.getInt(KEY_X, 0) : 0);
    coordinates.put(KEY_Y, waypoint != null ? waypoint.getInt(KEY_Y, 0) : 0);
    coordinates.put(KEY_Z, waypoint != null ? waypoint.getInt(KEY_Z, 0) : 0);

    return coordinates;
  }

  public String getWaypointOwnerName(String worldName, String waypointId) {
    String path = _getWaypointBasePath(worldName, waypointId) + "." + KEY_OWNER;

    if (_fileConfig.contains(path)) {
      String ownerString = _fileConfig.getString(path);

      if (ownerString != null && !ownerString.isEmpty()) {
        try {
          UUID ownerUUID = UUID.fromString(ownerString);
          OfflinePlayer p = Bukkit.getOfflinePlayer(ownerUUID);

          return p.getName();
        } catch (IllegalArgumentException ex) {
          return "unknown";
        }
      }
    }

    return "unknown";
  }

  public Optional<UUID> getWaypointOwnerId(String worldName, String waypointId) {
    String path = _getWaypointBasePath(worldName, waypointId) + "." + KEY_OWNER;

    if (_fileConfig.contains(path)) {
      String ownerString = _fileConfig.getString(path);

      if (ownerString != null && !ownerString.isEmpty()) {
        try {
          return Optional.of(UUID.fromString(ownerString));
        } catch (IllegalArgumentException ex) {
          return Optional.empty();
        }
      }
    }

    return Optional.empty();
  }

  public WaypointVisibility getWaypointVisibility(String worldName, String waypointId) {
    String path = _getWaypointBasePath(worldName, waypointId) + "." + KEY_VISIBILITY;

    if (_fileConfig.contains(path)) {
      String visibilityString = _fileConfig.getString(path);

      if (visibilityString != null && !visibilityString.isEmpty()) {
        return WaypointVisibility.fromString(visibilityString).orElse(WaypointVisibility.PUBLIC);
      }
    }

    return WaypointVisibility.PUBLIC;
  }

  public ItemStack getWaypointIcon(String worldName, String waypointId) {
    String path = _getWaypointBasePath(worldName, waypointId) + "." + KEY_ICON;

    if (_fileConfig.contains(path)) {
      Object raw = _fileConfig.get(path);
      Map<String, Object> map = null;

      if (raw instanceof Map) {
        @SuppressWarnings("unchecked")
        Map<String, Object> temp = (Map<String, Object>) raw;
        map = temp;
      } else if (raw instanceof ConfigurationSection) {
        map = ((ConfigurationSection) raw).getValues(false);
      }

      if (map != null) {
        return ItemStack.deserialize(map);
      }
    }

    return new ItemStack(Material.PAPER);
  }

  public void addWaypoint(String worldName, String waypointName, UUID playerId, WaypointVisibility visibility,
      int x, int y, int z) {
    String waypointId = _createWaypointId(worldName);
    String basePath = _getWaypointBasePath(worldName, waypointId);

    _fileConfig.set(basePath + "." + KEY_NAME, waypointName);
    _fileConfig.set(basePath + "." + KEY_X, x);
    _fileConfig.set(basePath + "." + KEY_Y, y);
    _fileConfig.set(basePath + "." + KEY_Z, z);
    _fileConfig.set(basePath + "." + KEY_OWNER, playerId.toString());
    _fileConfig.set(basePath + "." + KEY_VISIBILITY, visibility.name());
    _config.save();
  }

  public void removeWaypoint(String worldName, String waypointId) {
    _fileConfig.set(_getWaypointBasePath(worldName, waypointId), null);
    _config.save();
  }

  public void renameWaypoint(String worldName, String waypointId, String newName) {
    String basePath = _getWaypointBasePath(worldName, waypointId);
    _fileConfig.set(basePath + "." + KEY_NAME, newName);
    _config.save();
  }

  public void setWaypointVisibility(String worldName, String waypointId, WaypointVisibility visibility) {
    _fileConfig.set(_getWaypointBasePath(worldName, waypointId) + "." + KEY_VISIBILITY, visibility.name());
    _config.save();
  }

  public void setWaypointIcon(String worldName, String waypointId, ItemStack iconItem) {
    Map<String, Object> serializedIcon = iconItem.serialize();
    _fileConfig.set(_getWaypointBasePath(worldName, waypointId) + "." + KEY_ICON, serializedIcon);
    _config.save();
  }

  public boolean isPublicNameAvailable(String worldName, String waypointName, String excludeWaypointId) {
    for (String waypointId : getWaypoints(worldName)) {
      if (excludeWaypointId != null && excludeWaypointId.equals(waypointId)) {
        continue;
      }

      if (getWaypointVisibility(worldName, waypointId) == WaypointVisibility.PUBLIC
          && getWaypointDisplayName(worldName, waypointId).equals(waypointName)) {
        return false;
      }
    }

    return true;
  }

  public boolean checkWaypointExists(String worldName, String waypointId) {
    return _fileConfig.contains(_getWaypointBasePath(worldName, waypointId));
  }

  private String _getWaypointBasePath(String worldName, String waypointId) {
    return worldName + "." + waypointId;
  }

  private String _createWaypointId(String worldName) {
    String uuidString = UUID.randomUUID().toString();
    return uuidString;
  }
}
