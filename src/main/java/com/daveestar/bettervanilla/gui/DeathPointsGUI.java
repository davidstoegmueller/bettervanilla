package com.daveestar.bettervanilla.gui;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.stream.Collectors;

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

    Map<String, ItemStack> deathPointEntries = Arrays.stream(deathPointUUIDs)
        .collect(Collectors.toMap(pointUUID -> pointUUID, pointUUID -> _createDeathPointItem(p, pointUUID), (a, b) -> a,
            LinkedHashMap::new));

    String title = ChatColor.YELLOW + "" + ChatColor.BOLD + "» Death Points";
    CustomGUI deathPointsGUI = new CustomGUI(_plugin, p, title, deathPointEntries, 3, null, null, null);
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

  // ---------------
  // PRIVATE METHODS
  // ---------------

  private void _displayOptionsGUI(Player p, String pointUUID, CustomGUI parentMenu) {
    String title = ChatColor.YELLOW + "" + ChatColor.BOLD + "» Deathpoint Options";

    // create a new GUI for the options-menu
    Map<String, ItemStack> optionPageEntries = new HashMap<>();
    Map<String, Integer> customSlots = new HashMap<>();

    boolean inventoryAvailable = _deathPointsManager.hasDeathPointInventory(p.getUniqueId().toString(), pointUUID);

    if (inventoryAvailable) {
      optionPageEntries.put("items", _createListItem());
      customSlots.put("items", 3);
    }

    optionPageEntries.put("delete", _createDeleteItem());
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

    NavigationData navigationData = new NavigationData("DEATH", deathPointLocation, NavigationType.WAYPOINT,
        Color.RED);
    _navigationManager.startNavigation(p, navigationData);
    p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);

  }

  private void _handleRemove(Player p, String pointUUID) {
    String playerUUID = p.getUniqueId().toString();
    _deathPointsManager.removeDeathPoint(playerUUID, pointUUID);
    p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);

    p.sendMessage(Main.getPrefix() + "Your death point has been successfully removed.");
  }

  private void _displayItemsGUI(Player p, String pointUUID, CustomGUI parentMenu) {
    String title = ChatColor.YELLOW + "" + ChatColor.BOLD + "» Deathpoint Items";

    ItemStack[] deathPointItems = _deathPointsManager.getDeathPointItems(p, pointUUID);

    Map<String, ItemStack> itemPageEntries = Arrays.stream(deathPointItems)
        .collect(Collectors.toMap(item -> UUID.randomUUID().toString(), item -> item));

    CustomGUI itemsGUI = new CustomGUI(_plugin, p, title, itemPageEntries, 6, null, parentMenu, null);
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
    String worldName = deathPointWorld != null ? deathPointWorld.getName() : "Unknown";

    String distanceString = "";
    if (deathPointWorld != null && deathPointWorld.equals(playerLocation.getWorld())) {
      long distance = Math.round(playerLocation.distance(deathPointLocation));

      distanceString = "" + distance + ChatColor.GRAY + " blocks";
    } else {
      distanceString = "Not Available";
    }

    ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Death Point"
          + ChatColor.GRAY + " (" + deathPointDateTime + ")"));
      meta.lore(Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "World: " + ChatColor.YELLOW + worldName,
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "X: " + ChatColor.YELLOW + deathPointLocation.getBlockX(),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Y: " + ChatColor.YELLOW + deathPointLocation.getBlockY(),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Z: " + ChatColor.YELLOW + deathPointLocation.getBlockZ(),
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Distance: " + ChatColor.YELLOW + distanceString,
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Start navigation",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Right-Click: Options").stream()
          .map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createListItem() {
    ItemStack item = new ItemStack(Material.ENDER_EYE);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Show Inventory"));
      meta.lore(Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: View inventory")
          .stream().map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createDeleteItem() {
    ItemStack item = new ItemStack(Material.BARRIER);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Delete"));
      meta.lore(Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.RED + ChatColor.BOLD + "ATTENTION: " + ChatColor.GRAY
              + "As you delete this death point, you will lose all items stored in it.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Delete death point")
          .stream().map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }
}
