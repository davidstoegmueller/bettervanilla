package com.daveestar.bettervanilla.gui;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.NavigationType;
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.manager.NavigationManager;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.manager.WaypointsManager;
import com.daveestar.bettervanilla.utils.CustomDialog;
import com.daveestar.bettervanilla.utils.CustomGUI;
import com.daveestar.bettervanilla.utils.NavigationData;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

import java.util.*;
import java.util.stream.Collectors;

public class WaypointsGUI {

  private final Main _plugin;
  private final WaypointsManager _waypointsManager;
  private final NavigationManager _navigationManager;
  private final SettingsManager _settingsManager;

  public WaypointsGUI() {
    _plugin = Main.getInstance();
    _waypointsManager = _plugin.getWaypointsManager();
    _navigationManager = _plugin.getNavigationManager();
    _settingsManager = _plugin.getSettingsManager();
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
    CustomGUI waypointsGUI = new CustomGUI(_plugin, p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Waypoints",
        pageEntries, 3, null, null, null);

    // map to store click actions
    Map<String, CustomGUI.ClickAction> clickActions = new HashMap<>();
    for (String waypointName : allWaypointNames) {
      clickActions.put(waypointName, new CustomGUI.ClickAction() {
        public void onLeftClick(Player p) {
          _handleNavigation(p, waypointName);
          p.closeInventory();
          p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
        }

        public void onRightClick(Player p) {
          _displayOptionsGUI(p, waypointName, waypointsGUI);
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

    CustomGUI optionsGUI = new CustomGUI(_plugin, p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Waypoint Options",
        optionPageEntries, 2, customSlots, parentMenu, EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    Map<String, CustomGUI.ClickAction> optionClickActions = new HashMap<>();
    optionClickActions.put("rename", new CustomGUI.ClickAction() {
      public void onLeftClick(Player p) {
        _openWaypointRenameDialog(p, waypointName);
      }
    });

    optionClickActions.put("delete", new CustomGUI.ClickAction() {
      public void onLeftClick(Player p) {
        _handleRemove(p, waypointName);
        p.closeInventory();
      }
    });

    optionClickActions.put("seticon", new CustomGUI.ClickAction() {
      public void onLeftClick(Player p) {
        _displaySetIconGUI(p, waypointName, optionsGUI);
      }
    });

    optionsGUI.setClickActions(optionClickActions);
    optionsGUI.open(p);
  }

  private ItemStack _createRenameItem(String waypointName) {
    ItemStack item = new ItemStack(Material.NAME_TAG);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Rename"));
      meta.lore(Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Reaname waypoint: " + ChatColor.YELLOW
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
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Delete waypoint: " + ChatColor.YELLOW
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
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Set custom icon for: " + ChatColor.YELLOW
              + waypointName)
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

    _settingsManager.setToggleLocation(p.getUniqueId(), false);
    NavigationData navigationData = new NavigationData(waypointName, destination, NavigationType.WAYPOINT,
        Color.YELLOW);
    _navigationManager.startNavigation(p, navigationData);

    p.sendMessage(Main.getPrefix() + ChatColor.GRAY + "Start navigation to " + ChatColor.YELLOW + waypointName
        + ChatColor.GRAY + " at " + ChatColor.YELLOW + "X: " + ChatColor.GRAY + coords.get("x") + ChatColor.YELLOW
        + " Y: " + ChatColor.GRAY + coords.get("y") + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + coords.get("z"));
  }

  private void _handleRemove(Player p, String waypointName) {
    if (!p.hasPermission(Permissions.WAYPOINTS_REMOVE.getName())) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED
          + "Sorry! You don't have permissions to remove existing waypoints.");
      return;
    }

    String world = p.getWorld().getName();

    if (_waypointsManager.checkWaypointExists(world, waypointName)) {
      _waypointsManager.removeWaypoint(world, waypointName);
      p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);

      p.sendMessage(Main.getPrefix() + "The waypoint " + ChatColor.YELLOW + waypointName + ChatColor.GRAY
          + " was successfully removed!");
    } else {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Could not find a waypoint called " + ChatColor.YELLOW
          + waypointName + ChatColor.RED + ". Please try an existing one!");
    }
  }

  private void _displaySetIconGUI(Player p, String waypointName, CustomGUI parentGUI) {
    Material[] allMaterials = Material.values();
    Map<String, ItemStack> itemEntries = new LinkedHashMap<>();

    for (Material m : allMaterials) {
      if (m == Material.AIR)
        continue;
      try {
        ItemStack item = new ItemStack(m);
        ItemMeta meta = item.getItemMeta();

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
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

    CustomGUI itemsGui = new CustomGUI(_plugin, p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Icon for: " + waypointName,
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
          p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);

          p.sendMessage(Main.getPrefix() + "Custom icon set for waypoint " + ChatColor.YELLOW + waypointName
              + ChatColor.GRAY + ".");
          displayGUI(p);
        }
      });
    }

    itemsGui.setClickActions(itemClickActions);
    itemsGui.open(p);
  }

  // -------
  // DIALOGS
  // -------

  private void _openWaypointRenameDialog(Player p, String waypointName) {
    DialogInput inputName = DialogInput
        .text("name", Component.text(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Rename Waypoint"))
        .initial(waypointName)
        .maxLength(Integer.MAX_VALUE)
        .build();

    Dialog dialog = CustomDialog.createConfirmationDialog(
        "Waypoint Name",
        "Set the new name for the waypoint.",
        null,
        List.of(inputName),
        (view, audience) -> _setWaypointNameDialogCB(view, audience, waypointName),
        null);

    p.showDialog(dialog);
  }

  // ----------------
  // DIALOG CALLBACKS
  // ----------------

  private void _setWaypointNameDialogCB(DialogResponseView view, Audience audience, String waypointName) {
    Player p = (Player) audience;
    String name = view.getText("name");

    _waypointsManager.renameWaypoint(p.getWorld().getName(), waypointName, name);

    p.sendMessage(Component.text(Main.getPrefix() + "Waypoint name set to: " + ChatColor.YELLOW + name));
    p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);

    displayGUI(p);
  }
}