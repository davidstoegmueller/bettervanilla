package com.daveestar.bettervanilla.gui;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Comparator;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.NavigationType;
import com.daveestar.bettervanilla.manager.DeathPointsManager;
import com.daveestar.bettervanilla.manager.NavigationManager;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.CustomGUI;
import com.daveestar.bettervanilla.utils.NavigationData;
import com.daveestar.bettervanilla.utils.Theme;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class DeathPointsGUI {
  private final Main _plugin;
  private final DeathPointsManager _deathPointsManager;
  private final NavigationManager _navigationManager;
  private final SettingsManager _settingsManager;

  public DeathPointsGUI() {
    _plugin = Main.getInstance();
    _deathPointsManager = _plugin.getDeathPointsManager();
    _navigationManager = _plugin.getNavigationManager();
    _settingsManager = _plugin.getSettingsManager();
  }

  public void displayGUI(Player p) {
    String[] deathPointUUIDs = _deathPointsManager.getDeathPointUUIDs(p);
    Map<String, DeathPointSortData> sortData = _buildDeathPointSortData(p, deathPointUUIDs);

    Map<String, ItemStack> deathPointEntries = Arrays.stream(deathPointUUIDs)
        .collect(Collectors.toMap(pointUUID -> pointUUID, pointUUID -> _createDeathPointItem(p, pointUUID), (a, b) -> a,
            LinkedHashMap::new));

    String title = Theme.titlePrefix() + Main.tr(p, "gui-death-points-title");
    CustomGUI deathPointsGUI = new CustomGUI(_plugin, p, title, deathPointEntries, 3, null, null,
        EnumSet.of(CustomGUI.Option.ENABLE_SORT));
    deathPointsGUI.setSortOptions(_createDeathPointSortOptions(p, sortData));
    deathPointsGUI.setSortButtonSlot(_footerSortSlot(3));
    deathPointsGUI.open(p);

    Map<String, CustomGUI.ClickAction> clickActions = new HashMap<>();
    for (String pointUUID : deathPointUUIDs) {
      clickActions.put(pointUUID, new CustomGUI.ClickAction() {
        public void onLeftClick(Player p) {
          _handleNavigation(p, pointUUID);
          p.closeInventory();
        }

        public void onRightClick(Player p) {
          _displayOptionsGUI(p, pointUUID, deathPointsGUI);
        }
      });
    }

    deathPointsGUI.setClickActions(clickActions);
  }

  private int _footerSortSlot(int rows) {
    return (rows * 9) - 9 + 7;
  }

  // ---------------
  // PRIVATE METHODS
  // ---------------

  private void _displayOptionsGUI(Player p, String pointUUID, CustomGUI parentMenu) {
    String title = Theme.titlePrefix() + Main.tr(p, "gui-death-points-options-title");

    // create a new GUI for the options-menu
    Map<String, ItemStack> optionPageEntries = new HashMap<>();
    Map<String, Integer> customSlots = new HashMap<>();

    boolean inventoryAvailable = _deathPointsManager.hasDeathPointInventory(p.getUniqueId().toString(), pointUUID);

    if (inventoryAvailable) {
      optionPageEntries.put("items", _createListItem(p));
      customSlots.put("items", 3);
    }

    optionPageEntries.put("delete", _createDeleteItem(p));
    customSlots.put("delete", inventoryAvailable ? 5 : 4);

    CustomGUI optionsGUI = new CustomGUI(_plugin, p, title, optionPageEntries, 2, customSlots, parentMenu,
        EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    Map<String, CustomGUI.ClickAction> optionClickActions = new HashMap<>();

    if (inventoryAvailable) {
      optionClickActions.put("items", new CustomGUI.ClickAction() {
        public void onLeftClick(Player p) {
          _displayItemsGUI(p, pointUUID, optionsGUI);
        }
      });
    }

    optionClickActions.put("delete", new CustomGUI.ClickAction() {
      public void onLeftClick(Player p) {
        _handleRemove(p, pointUUID);
        p.closeInventory();
      }
    });

    optionsGUI.setClickActions(optionClickActions);
    optionsGUI.open(p);
  }

  private void _handleNavigation(Player p, String pointUUID) {
    _settingsManager.setPlayerToggleLocation(p.getUniqueId(), false);

    String playerUUID = p.getUniqueId().toString();
    Location deathPointLocation = _deathPointsManager.getDeathPointLocation(playerUUID, pointUUID);

    NavigationData navigationData = new NavigationData(
        Main.tr(p, "gui-death-points-navigation-destination-name"), deathPointLocation,
        NavigationType.WAYPOINT, Color.RED);
    _navigationManager.startNavigation(p, navigationData);
    p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);

  }

  private void _handleRemove(Player p, String pointUUID) {
    String playerUUID = p.getUniqueId().toString();
    _deathPointsManager.removeDeathPoint(playerUUID, pointUUID);
    p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);

    p.sendMessage(Main.getPrefix() + Main.tr(p, "gui-death-points-removed-message"));
  }

  private void _displayItemsGUI(Player p, String pointUUID, CustomGUI parentMenu) {
    String title = Theme.titlePrefix() + Main.tr(p, "gui-death-points-items-title");

    ItemStack[] deathPointItems = _deathPointsManager.getDeathPointItems(p, pointUUID);

    Map<String, ItemStack> itemPageEntries = Arrays.stream(deathPointItems)
        .collect(Collectors.toMap(item -> UUID.randomUUID().toString(), item -> item));

    CustomGUI itemsGUI = new CustomGUI(_plugin, p, title, itemPageEntries, 6, null, parentMenu,
        EnumSet.of(CustomGUI.Option.PRESERVE_ITEM_TOOLTIPS));
    Map<String, CustomGUI.ClickAction> itemClickAtions = new HashMap<>();
    itemsGUI.setClickActions(itemClickAtions);

    itemsGUI.open(p);
  }

  // ----------------
  // CREATE GUI ITEMS
  // ----------------

  private ItemStack _createDeathPointItem(Player p, String pointUUID) {
    String playerUUID = p.getUniqueId().toString();
    Location deathPointLocation = _deathPointsManager.getDeathPointLocation(playerUUID, pointUUID);
    String deathPointDateTime = _deathPointsManager.getDeathPointDateTime(p, pointUUID);

    Location playerLocation = p.getLocation().toBlockLocation();
    World deathPointWorld = deathPointLocation.getWorld();
    String worldName = deathPointWorld != null ? deathPointWorld.getName() : Main.tr(p, "common-value-unknown");

    String distanceString = "";
    if (deathPointWorld != null && deathPointWorld.equals(playerLocation.getWorld())) {
      long distance = Math.round(playerLocation.distance(deathPointLocation));

      distanceString = Main.tr(p, "gui-common-distance-blocks", "distance", Long.toString(distance));
    } else {
      distanceString = Main.tr(p, "gui-common-value-not-available");
    }

    ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(Theme.titlePrefix() + Main.tr(p, "gui-death-points-item-title",
          "details", Theme.primary() + "(" + deathPointDateTime + ")")));
      meta.lore(Arrays.asList(
          "",
          Theme.textPrefix() + Main.tr(p, "gui-common-world", "world", Theme.highlight() + worldName),
          "",
          Theme.textPrefix() + Main.tr(p, "gui-common-coordinate-x",
              "x", Theme.highlight() + Integer.toString(deathPointLocation.getBlockX())),
          Theme.textPrefix() + Main.tr(p, "gui-common-coordinate-y",
              "y", Theme.highlight() + Integer.toString(deathPointLocation.getBlockY())),
          Theme.textPrefix() + Main.tr(p, "gui-common-coordinate-z",
              "z", Theme.highlight() + Integer.toString(deathPointLocation.getBlockZ())),
          "",
          Theme.textPrefix() + Main.tr(p, "gui-common-distance", "distance", Theme.highlight() + distanceString),
          "",
          Theme.textPrefix() + Main.tr(p, "gui-common-action-start-navigation"),
          Theme.textPrefix() + Main.tr(p, "gui-common-action-open-options")).stream()
          .map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createListItem(Player viewer) {
    ItemStack item = new ItemStack(Material.ENDER_EYE);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(Theme.titlePrefix() + Main.tr(viewer, "gui-death-points-inventory-item-title")));
      meta.lore(Arrays.asList(
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-death-points-action-view-inventory"))
          .stream().map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createDeleteItem(Player viewer) {
    ItemStack item = new ItemStack(Material.BARRIER);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(Theme.titlePrefix() + Main.tr(viewer, "gui-death-points-delete-item-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + Theme.error() + ChatColor.BOLD + Main.tr(viewer, "gui-common-warning-label")
              + ChatColor.RESET + Theme.primary() + " "
              + Main.tr(viewer, "gui-death-points-delete-warning"),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-death-points-action-delete"))
          .stream().map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  // ---------
  // SORTING
  // ---------

  private Map<String, DeathPointSortData> _buildDeathPointSortData(Player p, String[] pointUUIDs) {
    Map<String, DeathPointSortData> sortData = new HashMap<>();
    String playerUUID = p.getUniqueId().toString();
    Location playerLocation = p.getLocation().toBlockLocation();

    for (String pointUUID : pointUUIDs) {
      Location deathPointLocation = _deathPointsManager.getDeathPointLocation(playerUUID, pointUUID);
      long timestamp = _deathPointsManager.getDeathPointTimestamp(playerUUID, pointUUID);

      long distance = Long.MAX_VALUE;
      if (deathPointLocation != null && deathPointLocation.getWorld() != null
          && deathPointLocation.getWorld().equals(playerLocation.getWorld())) {
        distance = Math.round(playerLocation.distance(deathPointLocation));
      }

      sortData.put(pointUUID, new DeathPointSortData(timestamp, distance));
    }

    return sortData;
  }

  private List<CustomGUI.SortOption> _createDeathPointSortOptions(Player viewer,
      Map<String, DeathPointSortData> sortData) {
    Comparator<Map.Entry<String, ItemStack>> byNewest = Comparator.<Map.Entry<String, ItemStack>>comparingLong(
        entry -> sortData.get(entry.getKey()).timestamp())
        .reversed();

    Comparator<Map.Entry<String, ItemStack>> byOldest = Comparator.<Map.Entry<String, ItemStack>>comparingLong(
        entry -> sortData.get(entry.getKey()).timestamp());

    Comparator<Map.Entry<String, ItemStack>> byNearest = Comparator.<Map.Entry<String, ItemStack>>comparingLong(
        entry -> sortData.get(entry.getKey()).distance());

    Comparator<Map.Entry<String, ItemStack>> byFarthest = byNearest.reversed();

    return List.of(
        new CustomGUI.SortOption(Main.tr(viewer, "gui-death-points-sort-newest-first"), byNewest),
        new CustomGUI.SortOption(Main.tr(viewer, "gui-death-points-sort-oldest-first"), byOldest),
        new CustomGUI.SortOption(Main.tr(viewer, "gui-common-sort-distance-nearest-first"), byNearest),
        new CustomGUI.SortOption(Main.tr(viewer, "gui-common-sort-distance-farthest-first"), byFarthest));
  }

  private record DeathPointSortData(long timestamp, long distance) {
  }
}
