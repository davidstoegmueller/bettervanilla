package com.daveestar.bettervanilla.gui;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
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
import com.daveestar.bettervanilla.enums.WaypointFilter;
import com.daveestar.bettervanilla.enums.WaypointVisibility;
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

  private final String KEY_ADD_BTN = "action::addWaypoint";
  private final String KEY_FILTER_BTN = "action::filterWaypoint";
  private final String KEY_CANCEL_BTN = "action::cancelWaypoint";

  private final String KEY_OPTION_RENAME = "option::renameWaypoint";
  private final String KEY_OPTION_DELETE = "option::deleteWaypoint";
  private final String KEY_OPTION_SET_ICON = "option::setWaypointIcon";
  private final String KEY_OPTION_SET_VISIBILITY = "option::setWaypointVisibility";

  private final String KEY_COORD_X = "x";
  private final String KEY_COORD_Y = "y";
  private final String KEY_COORD_Z = "z";

  private final String WAYPOINT_ADD_DIALOG_FIELD_NAME = "name";
  private final String WAYPOINT_ADD_DIALOG_VISIBILITY = "visibility";
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
  private final Map<UUID, WaypointFilter> _playerFilterModes;

  public WaypointsGUI() {
    _plugin = Main.getInstance();
    _waypointsManager = _plugin.getWaypointsManager();
    _navigationManager = _plugin.getNavigationManager();
    _settingsManager = _plugin.getSettingsManager();
    _playerFilterModes = new HashMap<>();
  }

  public void displayWaypointsGUI(Player p) {
    CustomGUI gui = _createWaypointsGUI(p);
    gui.open(p);
  }

  // -----------
  // CREATE GUIS
  // -----------

  private CustomGUI _createWaypointsGUI(Player p) {
    WaypointFilter filterMode = _getFilterMode(p);

    String worldName = p.getWorld().getName();
    List<String> waypointIds = _waypointsManager.getWaypoints(worldName);

    List<String> filteredWaypointIds = _getFilteredWaypoints(worldName, waypointIds, p, filterMode);

    Map<String, ItemStack> entries = new LinkedHashMap<>();
    for (String waypointId : filteredWaypointIds) {
      entries.put(_waypointKey(waypointId), _createWaypointItem(p, worldName, waypointId));
    }

    Location pLoc = p.getLocation().toBlockLocation();
    String inputX = Integer.toString(pLoc.getBlockX());
    String inputY = Integer.toString(pLoc.getBlockY());
    String inputZ = Integer.toString(pLoc.getBlockZ());

    String waypointGUITitle = "Waypoints " + ChatColor.GRAY + "(" + filterMode.getColoredName() + ChatColor.GRAY + ")";
    CustomGUI waypointsGUI = _createGUI(p, waypointGUITitle, MAIN_GUI_ROWS, entries, null, null, null);

    Map<String, CustomGUI.ClickAction> actions = new LinkedHashMap<>();
    for (String waypointId : filteredWaypointIds) {
      String key = _waypointKey(waypointId);

      if (_canEditWaypointOptions(worldName, waypointId, p)) {
        actions.put(key, _clickAction(
            player -> _handleNavigation(player, waypointId),
            player -> _displayOptionsGUI(player, waypointId, waypointsGUI),
            null,
            null));
      } else {
        actions.put(key, _clickAction(
            player -> _handleNavigation(player, waypointId),
            null,
            null,
            null));
      }
    }

    actions.put(KEY_CANCEL_BTN, _clickAction(
        player -> _handleCancelNavigation(player),
        null,
        null,
        null));

    actions.put(KEY_ADD_BTN, _clickAction(
        player -> _openAddWaypointDialog(player, null, "", WaypointVisibility.PUBLIC.getName(), inputX, inputY, inputZ),
        null,
        null,
        null));

    actions.put(KEY_FILTER_BTN, _clickAction(
        player -> _handleNextFilterCycle(player),
        player -> _handlePreviousFilterCycle(player),
        null,
        null));

    waypointsGUI.addFooterEntry(KEY_CANCEL_BTN, _createCancelNavigationItem(), _footerCancelSlot(MAIN_GUI_ROWS));
    waypointsGUI.addFooterEntry(KEY_ADD_BTN, _createAddWaypointItem(), _footerAddWaypointSlot(MAIN_GUI_ROWS));
    waypointsGUI.addFooterEntry(KEY_FILTER_BTN, _createFilterItem(filterMode), _footerFilterSlot(MAIN_GUI_ROWS));

    waypointsGUI.setClickActions(actions);
    return waypointsGUI;
  }

  private void _displayOptionsGUI(Player p, String waypointId, CustomGUI parentGUI) {
    String worldName = p.getWorld().getName();
    String waypointName = _waypointsManager.getWaypointDisplayName(worldName, waypointId);
    Map<String, ItemStack> entries = new LinkedHashMap<>();
    entries.put(KEY_OPTION_RENAME, _createRenameItem(waypointName));
    entries.put(KEY_OPTION_SET_VISIBILITY, _createSetVisibilityItem(p, waypointId));
    entries.put(KEY_OPTION_SET_ICON, _createSetIconItem(waypointName));
    entries.put(KEY_OPTION_DELETE, _createDeleteItem(waypointName));

    Map<String, Integer> customSlots = new LinkedHashMap<>();
    customSlots.put(KEY_OPTION_RENAME, 1);
    customSlots.put(KEY_OPTION_SET_VISIBILITY, 3);
    customSlots.put(KEY_OPTION_SET_ICON, 5);
    customSlots.put(KEY_OPTION_DELETE, 7);

    CustomGUI optionsGUI = _createGUI(p, "Options: " + waypointName, OPTIONS_GUI_ROWS, entries, customSlots,
        parentGUI, OPTIONS_GUI_FLAGS);

    Map<String, CustomGUI.ClickAction> actions = new LinkedHashMap<>();
    actions.put(KEY_OPTION_RENAME, _clickAction(
        player -> _openWaypointRenameDialog(player, waypointId, null, waypointName),
        null,
        null,
        null));

    actions.put(KEY_OPTION_SET_VISIBILITY, _clickAction(
        player -> _handleVisibilityCycle(player, waypointId, true),
        player -> _handleVisibilityCycle(player, waypointId, false),
        null,
        null));

    actions.put(KEY_OPTION_SET_ICON, _clickAction(
        player -> _displaySetIconGUI(player, waypointId, optionsGUI),
        null,
        null,
        null));

    actions.put(KEY_OPTION_DELETE, _clickAction(
        player -> _handleRemove(player, waypointId),
        null,
        null,
        null));

    optionsGUI.setClickActions(actions);
    optionsGUI.open(p);
  }

  private void _displaySetIconGUI(Player p, String waypointId, CustomGUI parentGUI) {
    String worldName = p.getWorld().getName();
    String waypointName = _waypointsManager.getWaypointDisplayName(worldName, waypointId);
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
        player -> _handleSetWaypointIcon(player, waypointId, material),
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

  private ItemStack _createSetVisibilityItem(Player p, String waypointId) {
    String worldName = p.getWorld().getName();
    WaypointVisibility visibility = _waypointsManager.getWaypointVisibility(worldName, waypointId);
    ChatColor visibilityColor = visibility == WaypointVisibility.PUBLIC ? ChatColor.GREEN : ChatColor.RED;

    return _createItem(
        Material.SPYGLASS,
        Component.text(GUI_ITEM_PREFIX + "Visibility"),
        _createLore(
            "",
            GUI_LORE_PREFIX + "Current: " + visibilityColor + visibility.getDisplayName() + ChatColor.GRAY,
            "",
            GUI_LORE_PREFIX + "Left-Click: Next visibility",
            GUI_LORE_PREFIX + "Right-Click: Previous visibility"),
        meta -> meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES));
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

  private ItemStack _createCancelNavigationItem() {
    return _createItem(
        Material.BARRIER,
        Component.text(GUI_ITEM_PREFIX + "Cancel Navigation"),
        _createLore(
            GUI_LORE_PREFIX + "Cancel your current active navigation.",
            "",
            GUI_LORE_PREFIX + "Left-Click: Cancel navigation"),
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

  private ItemStack _createFilterItem(WaypointFilter filterMode) {
    return _createItem(
        Material.HOPPER,
        Component.text(GUI_ITEM_PREFIX + "Filter"),
        _createLore(
            GUI_LORE_PREFIX + "Current mode: " + filterMode.getColoredName(),
            "",
            GUI_LORE_PREFIX + "Left-Click: Next filter",
            GUI_LORE_PREFIX + "Right-Click: Previous filter"),
        meta -> meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES));
  }

  private ItemStack _createWaypointItem(Player p, String worldName, String waypointId) {
    String waypointName = _waypointsManager.getWaypointDisplayName(worldName, waypointId);
    Map<String, Integer> waypointData = _waypointsManager.getWaypointCoordinates(worldName, waypointId);
    int x = waypointData.getOrDefault(KEY_COORD_X, 0);
    int y = waypointData.getOrDefault(KEY_COORD_Y, 0);
    int z = waypointData.getOrDefault(KEY_COORD_Z, 0);

    Location pLoc = p.getLocation().toBlockLocation();

    Location waypointLocation = new Location(pLoc.getWorld(), x, y, z);
    long distance = Math.round(pLoc.distance(waypointLocation));

    ItemStack item = _waypointsManager.getWaypointIcon(worldName, waypointId).clone();
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      WaypointVisibility visibility = _waypointsManager.getWaypointVisibility(worldName, waypointId);
      String visibilityString = "";
      if (visibility == WaypointVisibility.PRIVATE) {
        visibilityString = ChatColor.GRAY + " (" + ChatColor.RED + visibility.getDisplayName() + ChatColor.GRAY + ")";
      } else if (visibility == WaypointVisibility.PUBLIC) {
        visibilityString = ChatColor.GRAY + " (" + ChatColor.GREEN + visibility.getDisplayName() + ChatColor.GRAY + ")";
      }

      meta.displayName(
          Component.text(GUI_ITEM_PREFIX + waypointName + visibilityString));
      List<Component> lore = _createLore(
          "",
          GUI_LORE_PREFIX + "X: " + ChatColor.YELLOW + x,
          GUI_LORE_PREFIX + "Y: " + ChatColor.YELLOW + y,
          GUI_LORE_PREFIX + "Z: " + ChatColor.YELLOW + z,
          "",
          GUI_LORE_PREFIX + "Distance: " + ChatColor.YELLOW + distance + ChatColor.GRAY + " blocks",
          "",
          GUI_LORE_PREFIX + "Owner: " + ChatColor.YELLOW
              + _waypointsManager.getWaypointOwnerName(worldName, waypointId),
          "",
          GUI_LORE_PREFIX + "Left-Click: Start navigation");

      if (_canEditWaypointOptions(worldName, waypointId, p)) {
        lore.add(Component.text(GUI_LORE_PREFIX + "Right-Click: Options"));
      }

      meta.lore(lore);
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

  private void _handleSetWaypointIcon(Player p, String waypointId, Material material) {
    ItemStack icon = new ItemStack(material);
    String worldName = p.getWorld().getName();
    String waypointName = _waypointsManager.getWaypointDisplayName(worldName, waypointId);

    _waypointsManager.setWaypointIcon(worldName, waypointId, icon);

    p.sendMessage(Main.getPrefix() + "Custom icon set for waypoint " + ChatColor.YELLOW + waypointName
        + ChatColor.GRAY + ".");
    _playSuccessSound(p);

    displayWaypointsGUI(p);
  }

  private void _handleVisibilityCycle(Player p, String waypointId, boolean isNext) {
    String worldName = p.getWorld().getName();
    WaypointVisibility currentVisibility = _waypointsManager.getWaypointVisibility(worldName, waypointId);
    WaypointVisibility targetVisibility = isNext ? currentVisibility.next() : currentVisibility.previous();
    String waypointName = _waypointsManager.getWaypointDisplayName(worldName, waypointId);

    if (targetVisibility == currentVisibility) {
      _playErrorSound(p);
      return;
    }

    if (targetVisibility == WaypointVisibility.PUBLIC
        && !_waypointsManager.isPublicNameAvailable(worldName, waypointName, waypointId)) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "A public waypoint with that name already exists.");
      _playErrorSound(p);
      return;
    }

    _waypointsManager.setWaypointVisibility(worldName, waypointId, targetVisibility);

    ChatColor visibilityColor = targetVisibility == WaypointVisibility.PUBLIC ? ChatColor.GREEN : ChatColor.RED;

    p.sendMessage(Main.getPrefix() + "Waypoint " + ChatColor.YELLOW + waypointName + ChatColor.GRAY
        + " visibility set to " + visibilityColor + targetVisibility.getDisplayName() + ChatColor.GRAY + ".");
    _playSuccessSound(p);

    CustomGUI refreshedParentGUI = _createWaypointsGUI(p);
    _displayOptionsGUI(p, waypointId, refreshedParentGUI);
  }

  private void _handleNavigation(Player p, String waypointId) {
    String world = p.getWorld().getName();
    String waypointName = _waypointsManager.getWaypointDisplayName(world, waypointId);

    if (!_waypointsManager.checkWaypointExists(world, waypointId)) {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "Could not find a waypoint called " + ChatColor.YELLOW
          + waypointName + ChatColor.RED + ". Please try an existing one!");
      return;
    }

    Map<String, Integer> coords = _waypointsManager.getWaypointCoordinates(world, waypointId);
    Location destination = new Location(p.getWorld(), coords.get(KEY_COORD_X), coords.get(KEY_COORD_Y),
        coords.get(KEY_COORD_Z));

    _settingsManager.setPlayerToggleLocation(p.getUniqueId(), false);
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

  private void _handleCancelNavigation(Player p) {
    if (_navigationManager.checkActiveNavigation(p)) {
      _navigationManager.stopNavigation(p);
      p.sendMessage(Main.getPrefix() + "You've canceled your active navigation!");
      _playSuccessSound(p);
      p.closeInventory();
    } else {
      p.sendMessage(Main.getPrefix() + ChatColor.RED + "You have no current destination!");
      _playErrorSound(p);
      displayWaypointsGUI(p);
    }
  }

  private void _handleRemove(Player p, String waypointId) {
    String world = p.getWorld().getName();
    String waypointName = _waypointsManager.getWaypointDisplayName(world, waypointId);

    if (_waypointsManager.checkWaypointExists(world, waypointId)) {
      _waypointsManager.removeWaypoint(world, waypointId);
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

  private void _openAddWaypointDialog(Player p, String errorMessage, String initialName,
      String initialVisibility, String initialX, String initialY, String initialZ) {
    Map<String, String> visibilityOptions = WaypointVisibility.toMap();

    Dialog dialog = CustomDialog.createConfirmationDialog(
        "Add Waypoint",
        "Set the name and coordinates for the new waypoint.",
        errorMessage,
        List.of(
            CustomDialog.createTextInput(WAYPOINT_ADD_DIALOG_FIELD_NAME,
                GUI_DIALOG_INPUT_PREFIX + "Waypoint Name", initialName),
            CustomDialog.createSelectInput(WAYPOINT_ADD_DIALOG_VISIBILITY,
                GUI_DIALOG_INPUT_PREFIX + "Visibility", visibilityOptions, initialVisibility),
            CustomDialog.createTextInput(WAYPOINT_ADD_DIALOG_FIELD_X,
                GUI_DIALOG_INPUT_PREFIX + "X Coordinate", initialX),
            CustomDialog.createTextInput(WAYPOINT_ADD_DIALOG_FIELD_Y,
                GUI_DIALOG_INPUT_PREFIX + "Y Coordinate", initialY),
            CustomDialog.createTextInput(WAYPOINT_ADD_DIALOG_FIELD_Z,
                GUI_DIALOG_INPUT_PREFIX + "Z Coordinate", initialZ)),
        (view, audience) -> _addWaypointDialogCB(view, audience),
        null);

    p.showDialog(dialog);
  }

  private void _openWaypointRenameDialog(Player p, String waypointId, String errorMessage, String initialValue) {
    Dialog dialog = CustomDialog.createConfirmationDialog(
        "Waypoint Name",
        "Set the new name for the waypoint.",
        errorMessage,
        List.of(
            CustomDialog.createTextInput(WAYPOINT_ADD_DIALOG_FIELD_NAME,
                GUI_DIALOG_INPUT_PREFIX + "Rename Waypoint",
                initialValue)),
        (view, audience) -> _renameWaypointDialogCB(view, audience, waypointId),
        null);

    p.showDialog(dialog);
  }

  // ----------------
  // DIALOG CALLBACKS
  // ----------------

  private void _addWaypointDialogCB(DialogResponseView view, Audience audience) {
    Player p = (Player) audience;

    String nameInput = Optional.ofNullable(view.getText(WAYPOINT_ADD_DIALOG_FIELD_NAME)).map(String::trim).orElse("");
    String visibilityInput = Optional.ofNullable(view.getText(WAYPOINT_ADD_DIALOG_VISIBILITY)).map(String::trim)
        .orElse("");
    String xInput = Optional.ofNullable(view.getText(WAYPOINT_ADD_DIALOG_FIELD_X)).map(String::trim).orElse("");
    String yInput = Optional.ofNullable(view.getText(WAYPOINT_ADD_DIALOG_FIELD_Y)).map(String::trim).orElse("");
    String zInput = Optional.ofNullable(view.getText(WAYPOINT_ADD_DIALOG_FIELD_Z)).map(String::trim).orElse("");

    if (nameInput.isEmpty()) {
      _openAddWaypointDialog(p, "Please provide a waypoint name.", nameInput, visibilityInput, xInput, yInput, zInput);
      _playErrorSound(p);
      return;
    }

    int x, y, z;
    try {
      x = Integer.parseInt(xInput);
      y = Integer.parseInt(yInput);
      z = Integer.parseInt(zInput);
    } catch (NumberFormatException ex) {
      _openAddWaypointDialog(p, "Please provide valid integer coordinates.", nameInput, visibilityInput, xInput, yInput,
          zInput);
      _playErrorSound(p);
      return;
    }

    String world = p.getWorld().getName();
    WaypointVisibility visibility = WaypointVisibility.fromString(visibilityInput).orElse(WaypointVisibility.PUBLIC);
    if (visibility == WaypointVisibility.PUBLIC
        && !_waypointsManager.isPublicNameAvailable(world, nameInput, null)) {
      _openAddWaypointDialog(p, "A public waypoint with that name already exists.", nameInput, visibilityInput, xInput,
          yInput, zInput);
      _playErrorSound(p);
      return;
    }

    _waypointsManager.addWaypoint(world, nameInput, p.getUniqueId(), visibility, x, y, z);

    p.sendMessage(Main.getPrefix() + "The waypoint: " + ChatColor.YELLOW + nameInput + ChatColor.GRAY
        + " was successfully added!");
    p.sendMessage(Main.getPrefix() + ChatColor.GRAY + "Coordinates: " + ChatColor.YELLOW + "X: " + ChatColor.GRAY + x
        + ChatColor.YELLOW + " Y: " + ChatColor.GRAY + y + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + z);
    _playSuccessSound(p);

    displayWaypointsGUI(p);
  }

  private void _renameWaypointDialogCB(DialogResponseView view, Audience audience, String waypointId) {
    Player p = (Player) audience;

    String nameInput = Optional.ofNullable(view.getText(WAYPOINT_ADD_DIALOG_FIELD_NAME)).map(String::trim).orElse("");

    if (nameInput.isEmpty()) {
      _openWaypointRenameDialog(p, waypointId, "Please provide a waypoint name.", nameInput);
      _playErrorSound(p);
      return;
    }

    String world = p.getWorld().getName();
    WaypointVisibility visibility = _waypointsManager.getWaypointVisibility(world, waypointId);

    if (visibility == WaypointVisibility.PUBLIC
        && !_waypointsManager.isPublicNameAvailable(world, nameInput, waypointId)) {
      _openWaypointRenameDialog(p, waypointId, "A public waypoint with that name already exists.", nameInput);
      _playErrorSound(p);
      return;
    }

    _waypointsManager.renameWaypoint(world, waypointId, nameInput);

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

  private String _waypointKey(String waypointId) {
    return KEY_WAYPOINT_PREFIX + waypointId;
  }

  private String _iconKey(Material material) {
    return KEY_ICON_PREFIX + material.name();
  }

  private int _footerAddWaypointSlot(int rows) {
    return (rows * 9) - 9 + 4;
  }

  private int _footerFilterSlot(int rows) {
    return (rows * 9) - 9 + 7;
  }

  private int _footerCancelSlot(int rows) {
    return (rows * 9) - 9;
  }

  private boolean _canEditWaypointOptions(String worldName, String waypointId, Player p) {
    WaypointVisibility visibility = _waypointsManager.getWaypointVisibility(worldName, waypointId);
    Optional<UUID> ownerId = _waypointsManager.getWaypointOwnerId(worldName, waypointId);

    boolean isOwner = ownerId.equals(Optional.of(p.getUniqueId()));

    if (visibility == WaypointVisibility.PUBLIC) {
      return isOwner || p.hasPermission(Permissions.WAYPOINTS_ADMIN.getName());
    } else if (visibility == WaypointVisibility.PRIVATE) {
      return isOwner;
    }

    return false;
  }

  // ------------
  // SOUND HELPER
  // ------------

  private void _playSuccessSound(Player p) {
    p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
  }

  private void _playErrorSound(Player p) {
    p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
  }

  // -------------
  // FILTER HELPER
  // -------------

  private void _handleNextFilterCycle(Player p) {
    UUID playerId = p.getUniqueId();
    WaypointFilter nextMode = _getFilterMode(p).next();

    _playerFilterModes.put(playerId, nextMode);

    displayWaypointsGUI(p);
  }

  private void _handlePreviousFilterCycle(Player p) {
    UUID playerId = p.getUniqueId();
    WaypointFilter previousMode = _getFilterMode(p).previous();

    _playerFilterModes.put(playerId, previousMode);

    displayWaypointsGUI(p);
  }

  private List<String> _getFilteredWaypoints(String worldName, List<String> waypointIds, Player p,
      WaypointFilter filterMode) {

    List<String> filtered = new ArrayList<>();
    for (String waypointId : waypointIds) {
      WaypointVisibility visibility = _waypointsManager.getWaypointVisibility(worldName, waypointId);

      if (filterMode == WaypointFilter.PRIVATE) {
        Optional<UUID> ownerId = _waypointsManager.getWaypointOwnerId(worldName, waypointId);

        if (!ownerId.equals(Optional.of(p.getUniqueId()))) {
          continue;
        }
      }

      if (filterMode == WaypointFilter.ALL) {
        if (visibility == WaypointVisibility.PRIVATE) {
          Optional<UUID> ownerId = _waypointsManager.getWaypointOwnerId(worldName, waypointId);

          if (!ownerId.equals(Optional.of(p.getUniqueId()))) {
            continue;
          }
        }
      }

      if (filterMode.matches(visibility)) {
        filtered.add(waypointId);
      }
    }

    return filtered;
  }

  private WaypointFilter _getFilterMode(Player p) {
    return _playerFilterModes.getOrDefault(p.getUniqueId(), WaypointFilter.ALL);
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
      public void onLeftClick(Player p) {
        if (onLeft != null) {
          onLeft.accept(p);
        }
      }

      @Override
      public void onRightClick(Player p) {
        if (onRight != null) {
          onRight.accept(p);
        }
      }

      @Override
      public void onShiftLeftClick(Player p) {
        if (onShiftLeft != null) {
          onShiftLeft.accept(p);
        }
      }

      @Override
      public void onShiftRightClick(Player p) {
        if (onShiftRight != null) {
          onShiftRight.accept(p);
        }
      }
    };
  }
}
