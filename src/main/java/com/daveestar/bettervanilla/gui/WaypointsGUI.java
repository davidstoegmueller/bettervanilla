package com.daveestar.bettervanilla.gui;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

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
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class WaypointsGUI {
  private final String GUI_TITLE_PREFIX = ChatColor.YELLOW + "" + ChatColor.BOLD + "» ";
  private final String GUI_ITEM_PREFIX = ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW;
  private final String GUI_LORE_PREFIX = ChatColor.YELLOW + "» " + ChatColor.GRAY;
  private final String GUI_DIALOG_INPUT_PREFIX = ChatColor.YELLOW + "» " + ChatColor.GRAY;

  private final String KEY_WAYPOINT_PREFIX = "waypoint::";
  private final String KEY_ICON_PREFIX = "icon::";
  private final String KEY_ADD_WAYPOINT = "action::addWaypoint";
  private final String KEY_OPTION_RENAME = "option::renameWaypoint";
  private final String KEY_OPTION_DELETE = "option::deleteWaypoint";
  private final String KEY_OPTION_SET_ICON = "option::setWaypointIcon";

  private final String KEY_COORD_X = "x";
  private final String KEY_COORD_Y = "y";
  private final String KEY_COORD_Z = "z";

  private final String WAYPOINT_ADD_DIALOG_FIELD_NAME = "name";
  private final String WAYPOINT_ADD_DIALOG_FIELD_X = "x";
  private final String WAYPOINT_ADD_DIALOG_FIELD_Y = "y";
  private final String WAYPOINT_ADD_DIALOG_FIELD_Z = "z";

  private final int MAIN_GUI_ROWS = 3;
  private final int OPTIONS_GUI_ROWS = 2;
  private final int ICON_GUI_ROWS = 6;

  private static final EnumSet<CustomGUI.Option> OPTIONS_GUI_FLAGS = EnumSet
      .of(CustomGUI.Option.DISABLE_PAGE_BUTTON);

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

  public void displayWaypointsGUI(Player p) {
    CustomGUI gui = _createWaypointsGUI(p);
    gui.open(p);
  }

  // -----------
  // CREATE GUIS
  // -----------

  private CustomGUI _createWaypointsGUI(Player p) {
    String worldName = p.getWorld().getName();
    Location playerLocation = p.getLocation().toBlockLocation();
    List<String> waypointNames = _waypointsManager.getWaypoints(worldName);

    Map<String, ItemStack> entries = new LinkedHashMap<>();
    for (String waypointName : waypointNames) {
      entries.put(_waypointKey(waypointName), _createWaypointItem(playerLocation, worldName, waypointName));
    }

    CustomGUI waypointsGUI = _createGUI(p, "Waypoints", MAIN_GUI_ROWS, entries, null, null, null);

    Map<String, CustomGUI.ClickAction> actions = new LinkedHashMap<>();
    for (String waypointName : waypointNames) {
      String key = _waypointKey(waypointName);

      actions.put(key, _clickAction(
          player -> _handleNavigation(player, waypointName),
          player -> _displayOptionsGUI(player, waypointName, waypointsGUI),
          null,
          null));
    }

    actions.put(KEY_ADD_WAYPOINT, _clickAction(
        player -> _openAddWaypointDialog(player, null, ""),
        null,
        null,
        null));

    waypointsGUI.addFooterEntry(KEY_ADD_WAYPOINT, _createAddWaypointItem(), _footerAddWaypointSlot(MAIN_GUI_ROWS));

    waypointsGUI.setClickActions(actions);
    return waypointsGUI;
  }

  private void _displayOptionsGUI(Player p, String waypointName, CustomGUI parentGUI) {
    Map<String, ItemStack> entries = new LinkedHashMap<>();
    entries.put(KEY_OPTION_RENAME, _createRenameItem(waypointName));
    entries.put(KEY_OPTION_SET_ICON, _createSetIconItem(waypointName));
    entries.put(KEY_OPTION_DELETE, _createDeleteItem(waypointName));

    Map<String, Integer> customSlots = new LinkedHashMap<>();
    customSlots.put(KEY_OPTION_RENAME, 2);
    customSlots.put(KEY_OPTION_SET_ICON, 4);
    customSlots.put(KEY_OPTION_DELETE, 6);

    CustomGUI optionsGUI = _createGUI(p, "Options: " + waypointName, OPTIONS_GUI_ROWS, entries, customSlots,
        parentGUI, OPTIONS_GUI_FLAGS);

    Map<String, CustomGUI.ClickAction> actions = new LinkedHashMap<>();
    actions.put(KEY_OPTION_RENAME, _clickAction(
        player -> _openWaypointRenameDialog(player, waypointName, null, waypointName),
        null,
        null,
        null));

    actions.put(KEY_OPTION_SET_ICON, _clickAction(
        player -> _displaySetIconGUI(player, waypointName, optionsGUI),
        null,
        null,
        null));

    actions.put(KEY_OPTION_DELETE, _clickAction(
        player -> _handleRemove(player, waypointName),
        null,
        null,
        null));

    optionsGUI.setClickActions(actions);
    optionsGUI.open(p);
  }

  private void _displaySetIconGUI(Player p, String waypointName, CustomGUI parentGUI) {
    Map<String, ItemStack> entries = new LinkedHashMap<>();
    Map<String, Material> iconLookup = new LinkedHashMap<>();

    for (Material material : Material.values()) {
      if (material == Material.AIR) {
        continue;
      }

      try {
        ItemStack iconOption = _createIconSelectionItem(material, waypointName);
        if (iconOption == null) {
          continue;
        }

        String key = _iconKey(material);
        entries.put(key, iconOption);
        iconLookup.put(key, material);
      } catch (Exception ex) {
        // ignore invalid materials
      }
    }

    CustomGUI iconGUI = _createGUI(p, "Icon: " + waypointName, ICON_GUI_ROWS, entries, null, parentGUI, null);

    Map<String, CustomGUI.ClickAction> actions = new LinkedHashMap<>();
    iconLookup.forEach((key, material) -> actions.put(key, _clickAction(
        player -> _handleSetWaypointIcon(player, waypointName, material),
        null,
        null,
        null)));

    iconGUI.setClickActions(actions);
    iconGUI.open(p);
  }

  // ----------------
  // CREATE GUI ITEMS
  // ----------------

  private ItemStack _createRenameItem(String waypointName) {
    return _createItem(
        Material.NAME_TAG,
        Component.text(GUI_ITEM_PREFIX + "Rename"),
        _createLore(
            "",
            GUI_LORE_PREFIX + "Left-Click: Rename waypoint: " + ChatColor.YELLOW + waypointName),
        null);
  }

  private ItemStack _createDeleteItem(String waypointName) {
    return _createItem(
        Material.BARRIER,
        Component.text(GUI_ITEM_PREFIX + "Delete"),
        _createLore(
            "",
            GUI_LORE_PREFIX + "Left-Click: Delete waypoint: " + ChatColor.YELLOW + waypointName),
        null);
  }

  private ItemStack _createSetIconItem(String waypointName) {
    return _createItem(
        Material.ENDER_EYE,
        Component.text(GUI_ITEM_PREFIX + "Set Icon"),
        _createLore(
            "",
            GUI_LORE_PREFIX + "Left-Click: Set custom icon for: " + ChatColor.YELLOW + waypointName),
        null);
  }

  private ItemStack _createAddWaypointItem() {
    return _createItem(
        Material.EMERALD,
        Component.text(GUI_ITEM_PREFIX + "Add Waypoint"),
        _createLore(
            GUI_LORE_PREFIX + "Create a new waypoint using custom coordinates or current location.",
            "",
            GUI_LORE_PREFIX + "Left-Click: Add waypoint"),
        null);
  }

  private ItemStack _createWaypointItem(Location playerLocation, String worldName, String waypointName) {
    Map<String, Integer> waypointData = _waypointsManager.getWaypointByName(worldName, waypointName);
    int x = waypointData.getOrDefault(KEY_COORD_X, 0);
    int y = waypointData.getOrDefault(KEY_COORD_Y, 0);
    int z = waypointData.getOrDefault(KEY_COORD_Z, 0);

    Location waypointLocation = new Location(playerLocation.getWorld(), x, y, z);
    long distance = Math.round(playerLocation.distance(waypointLocation));

    ItemStack item = _waypointsManager.getWaypointIcon(worldName, waypointName).clone();
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(GUI_ITEM_PREFIX + waypointName));
      meta.lore(_createLore(
          "",
          GUI_LORE_PREFIX + "X: " + ChatColor.YELLOW + x,
          GUI_LORE_PREFIX + "Y: " + ChatColor.YELLOW + y,
          GUI_LORE_PREFIX + "Z: " + ChatColor.YELLOW + z,
          "",
          GUI_LORE_PREFIX + "Distance: " + ChatColor.YELLOW + distance + ChatColor.GRAY + " blocks",
          "",
          GUI_LORE_PREFIX + "Left-Click: Start navigation",
          GUI_LORE_PREFIX + "Right-Click: Options"));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createIconSelectionItem(Material material, String waypointName) {
    return _createItem(
        material,
        Component.text(GUI_ITEM_PREFIX + material.name()),
        _createLore(
            "",
            GUI_LORE_PREFIX + "Click to set this as the icon for: " + ChatColor.YELLOW + waypointName),
        meta -> meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES));

  }

  // ------------------
  // HANDLE GUI ACTIONS
  // ------------------

  private void _handleSetWaypointIcon(Player p, String waypointName, Material material) {
    ItemStack icon = new ItemStack(material);

    _waypointsManager.setWaypointIcon(p.getWorld().getName(), waypointName, icon);

    p.sendMessage(Main.getPrefix() + "Custom icon set for waypoint " + ChatColor.YELLOW + waypointName
        + ChatColor.GRAY + ".");
    _playSuccessSound(p);

    displayWaypointsGUI(p);
  }

  private void _handleNavigation(Player p, String waypointName) {
    String world = p.getWorld().getName();

    if (!_waypointsManager.checkWaypointExists(world, waypointName)) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Could not find a waypoint called " + ChatColor.YELLOW
          + waypointName + ChatColor.RED + ". Please try an existing one!");
      return;
    }

    Map<String, Integer> coords = _waypointsManager.getWaypointByName(world, waypointName);
    Location destination = new Location(p.getWorld(), coords.get(KEY_COORD_X), coords.get(KEY_COORD_Y),
        coords.get(KEY_COORD_Z));

    _settingsManager.setToggleLocation(p.getUniqueId(), false);
    NavigationData navigationData = new NavigationData(waypointName, destination, NavigationType.WAYPOINT,
        Color.YELLOW);
    _navigationManager.startNavigation(p, navigationData);

    p.sendMessage(Main.getPrefix() + ChatColor.GRAY + "Start navigation to " + ChatColor.YELLOW + waypointName
        + ChatColor.GRAY + " at " + ChatColor.YELLOW + "X: " + ChatColor.GRAY + coords.get(KEY_COORD_X)
        + ChatColor.YELLOW + " Y: " + ChatColor.GRAY + coords.get(KEY_COORD_Y)
        + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + coords.get(KEY_COORD_Z));
    _playSuccessSound(p);

    p.closeInventory();
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
      _playSuccessSound(p);

      p.sendMessage(Main.getPrefix() + "The waypoint " + ChatColor.YELLOW + waypointName + ChatColor.GRAY
          + " was successfully removed!");
    } else {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Could not find a waypoint called " + ChatColor.YELLOW
          + waypointName + ChatColor.RED + ". Please try an existing one!");
    }

    displayWaypointsGUI(p);
  }

  // -------
  // DIALOGS
  // -------

  private void _openAddWaypointDialog(Player player, String errorMessage, String initialName) {
    Location loc = player.getLocation().toBlockLocation();

    Dialog dialog = CustomDialog.createConfirmationDialog(
        "Add Waypoint",
        "Set the name and coordinates for the new waypoint.",
        errorMessage,
        List.of(
            CustomDialog.createTextInput(WAYPOINT_ADD_DIALOG_FIELD_NAME,
                GUI_DIALOG_INPUT_PREFIX + "Waypoint Name", initialName),
            CustomDialog.createTextInput(WAYPOINT_ADD_DIALOG_FIELD_X,
                GUI_DIALOG_INPUT_PREFIX + "X Coordinate", Integer.toString(loc.getBlockX())),
            CustomDialog.createTextInput(WAYPOINT_ADD_DIALOG_FIELD_Y,
                GUI_DIALOG_INPUT_PREFIX + "Y Coordinate", Integer.toString(loc.getBlockY())),
            CustomDialog.createTextInput(WAYPOINT_ADD_DIALOG_FIELD_Z,
                GUI_DIALOG_INPUT_PREFIX + "Z Coordinate", Integer.toString(loc.getBlockZ()))),
        (view, audience) -> _addWaypointDialogCB(view, audience),
        null);

    player.showDialog(dialog);
  }

  private void _openWaypointRenameDialog(Player p, String waypointName, String errorMessage, String initialValue) {
    Dialog dialog = CustomDialog.createConfirmationDialog(
        "Waypoint Name",
        "Set the new name for the waypoint.",
        errorMessage,
        List.of(
            CustomDialog.createTextInput(WAYPOINT_ADD_DIALOG_FIELD_NAME,
                GUI_DIALOG_INPUT_PREFIX + "Rename Waypoint",
                initialValue)),
        (view, audience) -> _renameWaypointDialogCB(view, audience, waypointName),
        null);

    p.showDialog(dialog);
  }

  // ----------------
  // DIALOG CALLBACKS
  // ----------------

  private void _addWaypointDialogCB(DialogResponseView view, Audience audience) {
    Player p = (Player) audience;

    String nameInput = Optional.ofNullable(view.getText(WAYPOINT_ADD_DIALOG_FIELD_NAME)).map(String::trim).orElse("");
    String xInput = Optional.ofNullable(view.getText(WAYPOINT_ADD_DIALOG_FIELD_X)).map(String::trim).orElse("");
    String yInput = Optional.ofNullable(view.getText(WAYPOINT_ADD_DIALOG_FIELD_Y)).map(String::trim).orElse("");
    String zInput = Optional.ofNullable(view.getText(WAYPOINT_ADD_DIALOG_FIELD_Z)).map(String::trim).orElse("");

    if (nameInput.isEmpty()) {
      _openAddWaypointDialog(p, "Please provide a waypoint name.", nameInput);
      _playErrorSound(p);
      return;
    }

    int x, y, z;
    try {
      x = Integer.parseInt(xInput);
      y = Integer.parseInt(yInput);
      z = Integer.parseInt(zInput);
    } catch (NumberFormatException ex) {
      _openAddWaypointDialog(p, "Please provide valid integer coordinates.", nameInput);
      _playErrorSound(p);
      return;
    }

    String world = p.getWorld().getName();
    if (_waypointsManager.checkWaypointExists(world, nameInput)) {
      _openAddWaypointDialog(p, "A waypoint with that name already exists.", nameInput);
      _playErrorSound(p);
      return;
    }

    _waypointsManager.addWaypoint(world, nameInput, x, y, z);

    p.sendMessage(Main.getPrefix() + "The waypoint: " + ChatColor.YELLOW + nameInput + ChatColor.GRAY
        + " was successfully added!");
    p.sendMessage(Main.getPrefix() + ChatColor.GRAY + "Coordinates: " + ChatColor.YELLOW + "X: " + ChatColor.GRAY + x
        + ChatColor.YELLOW + " Y: " + ChatColor.GRAY + y + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + z);
    _playSuccessSound(p);

    displayWaypointsGUI(p);
  }

  private void _renameWaypointDialogCB(DialogResponseView view, Audience audience, String waypointName) {
    Player p = (Player) audience;

    String nameInput = Optional.ofNullable(view.getText(WAYPOINT_ADD_DIALOG_FIELD_NAME)).map(String::trim).orElse("");

    if (nameInput.isEmpty()) {
      _openWaypointRenameDialog(p, waypointName, "Please provide a waypoint name.", nameInput);
      _playErrorSound(p);
      return;
    }

    String world = p.getWorld().getName();

    if (_waypointsManager.checkWaypointExists(world, nameInput)) {
      _openWaypointRenameDialog(p, waypointName, "A waypoint with that name already exists.", nameInput);
      _playErrorSound(p);
      return;
    }

    _waypointsManager.renameWaypoint(world, waypointName, nameInput);

    p.sendMessage(Component.text(Main.getPrefix() + "Waypoint name set to: " + ChatColor.YELLOW + nameInput));
    _playSuccessSound(p);

    displayWaypointsGUI(p);
  }

  // -----------
  // ITEM HELPER
  // -----------

  private ItemStack _createItem(Material material, Component displayName, List<Component> lore,
      Consumer<ItemMeta> metaCustomizer) {
    ItemStack item = new ItemStack(material);
    ItemMeta meta = item.getItemMeta();

    if (meta == null) {
      return item;
    }

    meta.displayName(displayName);

    if (lore != null && !lore.isEmpty()) {
      meta.lore(lore);
    }

    if (metaCustomizer != null) {
      metaCustomizer.accept(meta);
    }

    item.setItemMeta(meta);

    return item;
  }

  private List<Component> _createLore(String... lines) {
    List<Component> lore = new ArrayList<>(lines.length);
    for (String line : lines) {
      lore.add(Component.text(line));
    }
    return lore;
  }

  // -----------
  // MISC HELPER
  // -----------

  private String _waypointKey(String waypointName) {
    return KEY_WAYPOINT_PREFIX + waypointName;
  }

  private String _iconKey(Material material) {
    return KEY_ICON_PREFIX + material.name();
  }

  private int _footerAddWaypointSlot(int rows) {
    return (rows * 9) - 9 + 4;
  }

  // ------------
  // SOUND HELPER
  // ------------

  private void _playSuccessSound(Player player) {
    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
  }

  private void _playErrorSound(Player player) {
    player.playSound(player, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
  }

  // ----------
  // GUI HELPER
  // ----------

  private CustomGUI _createGUI(Player p, String title, int rows, Map<String, ItemStack> entries,
      Map<String, Integer> customSlots, CustomGUI parentGUI, Set<CustomGUI.Option> options) {
    CustomGUI gui = new CustomGUI(_plugin, p, GUI_TITLE_PREFIX + title, entries, rows, customSlots, parentGUI,
        options);

    return gui;
  }

  private CustomGUI.ClickAction _clickAction(Consumer<Player> onLeft, Consumer<Player> onRight,
      Consumer<Player> onShiftLeft, Consumer<Player> onShiftRight) {
    return new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player player) {
        if (onLeft != null) {
          onLeft.accept(player);
        }
      }

      @Override
      public void onRightClick(Player player) {
        if (onRight != null) {
          onRight.accept(player);
        }
      }

      @Override
      public void onShiftLeftClick(Player player) {
        if (onShiftLeft != null) {
          onShiftLeft.accept(player);
        }
      }

      @Override
      public void onShiftRightClick(Player player) {
        if (onShiftRight != null) {
          onShiftRight.accept(player);
        }
      }
    };
  }
}
