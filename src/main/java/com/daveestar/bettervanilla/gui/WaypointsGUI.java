package com.daveestar.bettervanilla.gui;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.NavigationType;
import com.daveestar.bettervanilla.manager.NavigationManager;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.manager.WaypointsManager;
import com.daveestar.bettervanilla.utils.CustomGUI;
import com.daveestar.bettervanilla.utils.NavigationData;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.ChatColor;

import java.util.*;
import java.util.stream.Collectors;

public class WaypointsGUI implements Listener {

  private final WaypointsManager _waypointsManager;
  private final NavigationManager _navigationManager;
  private final SettingsManager _settingsManager;
  private final Map<UUID, String> _renamePending;

  public WaypointsGUI() {
    Main plugin = Main.getInstance();
    this._waypointsManager = plugin.getWaypointsManager();
    this._navigationManager = plugin.getNavigationManager();
    this._settingsManager = plugin.getSettingsManager();
    this._renamePending = new HashMap<>();
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  public void displayGUI(Player p) {
    String worldName = p.getWorld().getName();
    List<String> allWaypointNames = _waypointsManager.getWaypoints(worldName);
    Location playerLocation = p.getLocation().toBlockLocation();

    // map to store GUI entries
    Map<String, ItemStack> pageEntries = allWaypointNames.parallelStream()
        .collect(Collectors.toMap(
            waypointName -> waypointName,
            waypointName -> _createWaypointItem(playerLocation, worldName, waypointName),
            (oldValue, newValue) -> oldValue,
            LinkedHashMap::new));

    // create and open the GUI
    CustomGUI waypointsGUI = new CustomGUI(Main.getInstance(), p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Waypoints",
        pageEntries, 3, null, null, null);

    // map to store click actions
    Map<String, CustomGUI.ClickAction> clickActions = new HashMap<>();
    for (String waypointName : allWaypointNames) {
      clickActions.put(waypointName, new CustomGUI.ClickAction() {
        public void onLeftClick(Player player) {
          _handleNavigation(p, waypointName);
          p.closeInventory();
          p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
        }

        public void onRightClick(Player player) {
          _displayOptionsGUI(player, waypointName, waypointsGUI);
        }
      });
    }

    waypointsGUI.setClickActions(clickActions);
    waypointsGUI.open(p);
  }

  private void _displayOptionsGUI(Player p, String waypointName, CustomGUI parentMenu) {
    // create a new GUI for the options-menu
    Map<String, ItemStack> optionPageEntries = new HashMap<>();
    optionPageEntries.put("rename", _createRenameItem(waypointName));
    optionPageEntries.put("delete", _createDeleteItem(waypointName));
    optionPageEntries.put("seticon", _createSetIconItem(waypointName));

    Map<String, Integer> customSlots = new HashMap<>();
    customSlots.put("rename", 2);
    customSlots.put("seticon", 4);
    customSlots.put("delete", 6);

    CustomGUI optionsGUI = new CustomGUI(Main.getInstance(), p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Waypoint Options",
        optionPageEntries, 2, customSlots, parentMenu, EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    Map<String, CustomGUI.ClickAction> optionClickActions = new HashMap<>();
    optionClickActions.put("rename", new CustomGUI.ClickAction() {
      public void onLeftClick(Player player) {
        player.sendMessage(
            Main.getPrefix() + "Enter the new name for the waypoint " + ChatColor.YELLOW + waypointName + ChatColor.GRAY
                + ":");
        _renamePending.put(player.getUniqueId(), waypointName);
        player.closeInventory();
      }
    });

    optionClickActions.put("delete", new CustomGUI.ClickAction() {
      public void onLeftClick(Player player) {
        _handleRemove(player, waypointName);
        player.closeInventory();
      }
    });

    optionClickActions.put("seticon", new CustomGUI.ClickAction() {
      public void onLeftClick(Player player) {
        _displaySetIconGUI(player, waypointName, optionsGUI);
      }
    });

    optionsGUI.setClickActions(optionClickActions);
    optionsGUI.open(p);
  }

  private ItemStack _createRenameItem(String waypointName) {
    ItemStack item = new ItemStack(Material.NAME_TAG);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Remove"));
      meta.lore(Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Click to reaname the waypoint: " + ChatColor.YELLOW
              + waypointName)
          .stream().map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }
    return item;
  }

  private ItemStack _createDeleteItem(String waypointName) {
    ItemStack item = new ItemStack(Material.BARRIER);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Delete"));
      meta.lore(Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Click to delete the waypoint: " + ChatColor.YELLOW
              + waypointName)
          .stream().map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }
    return item;
  }

  private ItemStack _createSetIconItem(String waypointName) {
    ItemStack item = new ItemStack(Material.ENDER_EYE);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Set Icon"));
      meta.lore(Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Click to set custom icon for: " + ChatColor.YELLOW + waypointName)
          .stream().map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createWaypointItem(Location playerLocation, String worldName, String waypointName) {
    Map<String, Integer> waypointData = _waypointsManager.getWaypointByName(worldName, waypointName);
    int x = waypointData.get("x");
    int y = waypointData.get("y");
    int z = waypointData.get("z");
    Location waypointLocation = new Location(playerLocation.getWorld(), x, y, z);
    long distance = Math.round(playerLocation.distance(waypointLocation));

    ItemStack item = _waypointsManager.getWaypointIcon(worldName, waypointName);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + waypointName));
      meta.lore(Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "X: " + ChatColor.YELLOW + x,
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Y: " + ChatColor.YELLOW + y,
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Z: " + ChatColor.YELLOW + z,
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Distance: " + ChatColor.YELLOW + distance + ChatColor.GRAY
              + " blocks",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Start navigation",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Right-Click: Options").stream()
          .map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private void _handleNavigation(Player p, String waypointName) {
    String world = p.getWorld().getName();

    if (!_waypointsManager.checkWaypointExists(world, waypointName)) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Could not find a waypoint called " + ChatColor.YELLOW
          + waypointName + ChatColor.RED + ". Please try an existing one!");
      return;
    }

    Map<String, Integer> coords = _waypointsManager.getWaypointByName(world, waypointName);
    Location destination = new Location(p.getWorld(), coords.get("x"), coords.get("y"), coords.get("z"));

    _settingsManager.setToggleLocation(p, false);
    NavigationData navigationData = new NavigationData(waypointName, destination, NavigationType.WAYPOINT,
        Color.YELLOW);
    _navigationManager.startNavigation(p, navigationData);

    p.sendMessage(Main.getPrefix() + ChatColor.GRAY + "Start navigation to " + ChatColor.YELLOW + waypointName
        + ChatColor.GRAY + " at " + ChatColor.YELLOW + "X: " + ChatColor.GRAY + coords.get("x") + ChatColor.YELLOW
        + " Y: " + ChatColor.GRAY + coords.get("y") + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + coords.get("z"));
  }

  private void _handleRemove(Player p, String waypointName) {
    if (!p.hasPermission("bettervanilla.waypoints.remove")) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED
          + "Sorry! You don't have permissions to remove existing waypoints.");
      return;
    }

    String world = p.getWorld().getName();

    if (_waypointsManager.checkWaypointExists(world, waypointName)) {
      _waypointsManager.removeWaypoint(world, waypointName);
      p.sendMessage(Main.getPrefix() + "The waypoint " + ChatColor.YELLOW + waypointName + ChatColor.GRAY
          + " was successfully removed!");
    } else {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Could not find a waypoint called " + ChatColor.YELLOW
          + waypointName + ChatColor.RED + ". Please try an existing one!");
    }
  }

  private void _displaySetIconGUI(Player player, String waypointName, CustomGUI parentGUI) {
    Material[] allMaterials = Material.values();
    Map<String, ItemStack> itemEntries = new LinkedHashMap<>();

    for (Material m : allMaterials) {
      if (m == Material.AIR)
        continue;
      try {
        ItemStack item = new ItemStack(m);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(
            Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + m.name()));
        meta.lore(Arrays.asList(
            "",
            ChatColor.YELLOW + "» " + ChatColor.GRAY + "Click to set this as the icon for: " + ChatColor.YELLOW
                + waypointName)
            .stream().map(Component::text).collect(Collectors.toList()));
        item.setItemMeta(meta);

        itemEntries.put(m.name(), item);
      } catch (Exception ex) {
      }
    }

    CustomGUI itemsGui = new CustomGUI(Main.getInstance(), player,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Icon for: " + ChatColor.GRAY + waypointName,
        itemEntries, 6, null, parentGUI, null);
    Map<String, CustomGUI.ClickAction> itemClickActions = new HashMap<>();

    for (String key : itemEntries.keySet()) {
      itemClickActions.put(key, new CustomGUI.ClickAction() {
        public void onLeftClick(Player p) {
          Material selMat = Material.matchMaterial(key);
          if (selMat == null)
            return;

          ItemStack icon = new ItemStack(selMat);

          _waypointsManager.setWaypointIcon(p.getWorld().getName(), waypointName,
              icon);

          p.sendMessage(Main.getPrefix() + "Custom icon set for waypoint " + ChatColor.YELLOW + waypointName
              + ChatColor.GRAY + ".");
          p.closeInventory();
        }
      });
    }

    itemsGui.setClickActions(itemClickActions);
    itemsGui.open(player);
  }

  @EventHandler
  public void onPlayerChat(AsyncChatEvent e) {
    Player player = e.getPlayer();
    UUID playerId = player.getUniqueId();

    if (_renamePending.containsKey(playerId)) {
      e.setCancelled(true);
      String oldName = _renamePending.remove(playerId);
      String newName = ((TextComponent) e.message()).content();

      if (newName.split(" ").length > 1 || newName.isEmpty()
          || _waypointsManager.checkWaypointExists(player.getWorld().getName(), newName)) {
        player.sendMessage(
            Main.getPrefix() + ChatColor.RED + "Invalid name or waypoint already exists.");
        return;
      }

      _waypointsManager.renameWaypoint(player.getWorld().getName(), oldName, newName);
      player.sendMessage(Main.getPrefix() + "The waypoint " + ChatColor.YELLOW + oldName + ChatColor.GRAY
          + " has been renamed to " + ChatColor.YELLOW + newName + ChatColor.GRAY + ".");
      return;
    }
  }
}