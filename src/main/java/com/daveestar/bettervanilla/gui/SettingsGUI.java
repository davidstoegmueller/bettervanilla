package com.daveestar.bettervanilla.gui;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.InventorySortMode;
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.manager.CompassManager;
import com.daveestar.bettervanilla.manager.NameTagManager;
import com.daveestar.bettervanilla.manager.NavigationManager;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.manager.TabListManager;
import com.daveestar.bettervanilla.manager.TagManager;
import com.daveestar.bettervanilla.utils.ActionBar;
import com.daveestar.bettervanilla.utils.CustomDialog;
import com.daveestar.bettervanilla.utils.CustomGUI;
import com.daveestar.bettervanilla.utils.Theme;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class SettingsGUI {
  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final NavigationManager _navigationManager;
  private final CompassManager _compassManager;
  private final ActionBar _actionBar;
  private final AdminSettingsGUI _adminSettingsGUI;
  private final TagManager _tagManager;
  private final NameTagManager _nameTagManager;
  private final TabListManager _tabListManager;

  public SettingsGUI() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
    _navigationManager = _plugin.getNavigationManager();
    _compassManager = _plugin.getCompassManager();
    _actionBar = _plugin.getActionBar();
    _adminSettingsGUI = new AdminSettingsGUI();
    _tagManager = _plugin.getTagManager();
    _nameTagManager = _plugin.getNameTagManager();
    _tabListManager = _plugin.getTabListManager();
  }

  public void displayGUI(Player p) {
    displayGUI(p, p);
  }

  public void displayGUI(Player viewer, Player target) {
    boolean showAdminSettings = target.hasPermission(Permissions.ADMINSETTINGS.getName());
    int rows = showAdminSettings ? 5 : 4;

    Map<String, ItemStack> entries = new HashMap<>();
    // first row
    entries.put("togglelocation", _createToggleLocationItem(viewer, target));
    entries.put("togglecompass", _createToggleCompassItem(viewer, target));
    entries.put("navigationtrail", _createNavigationTrailItem(target));
    entries.put("navigationautocancel", _createNavigationAutoCancelItem(target));

    // second row
    entries.put("itemrestock", _createItemRestockItem(viewer, target));
    entries.put("veinminer", _createVeinMinerItem(viewer, target));
    entries.put("veinchopper", _createVeinChopperItem(viewer, target));
    entries.put("doubledoor", _createDoubleDoorItem(viewer, target));

    // third row
    entries.put("chestsort", _createChestSortItem(viewer, target));
    entries.put("inventorysort", _createInventorySortItem(viewer, target));
    entries.put("backpacksort", _createBackpackSortItem(viewer, target));
    entries.put("actionbartimer", _createActionBarTimerItem(viewer, target));
    entries.put("playertag", _createPlayerTagItem(viewer, target));

    // fourth row
    if (showAdminSettings) {
      entries.put("adminsettings", _createAdminSettingsItem());
    }

    Map<String, Integer> customSlots = new HashMap<>();
    // first row
    customSlots.put("togglelocation", 1);
    customSlots.put("togglecompass", 3);
    customSlots.put("navigationtrail", 5);
    customSlots.put("navigationautocancel", 7);

    // second row
    customSlots.put("itemrestock", 9);
    customSlots.put("veinminer", 11);
    customSlots.put("veinchopper", 13);
    customSlots.put("doubledoor", 15);
    customSlots.put("actionbartimer", 17);

    // third row
    customSlots.put("chestsort", 19);
    customSlots.put("inventorysort", 21);
    customSlots.put("backpacksort", 23);
    customSlots.put("playertag", 25);

    // fourth row
    if (showAdminSettings) {
      customSlots.put("adminsettings", rows * 9 - 10);
    }

    CustomGUI gui = new CustomGUI(_plugin, viewer,
        Theme.titlePrefix() + "Settings " + Theme.primary() + "(" + target.getName() + ")",
        entries, rows, customSlots, null,
        EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    Map<String, CustomGUI.ClickAction> clickActions = new HashMap<>();
    clickActions.put("togglelocation", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        if (!target.hasPermission(Permissions.TOGGLELOCATION.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.TOGGLELOCATION));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleLocation(target);
        displayGUI(p, target);
      }
    });

    clickActions.put("itemrestock", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        if (!target.hasPermission(Permissions.ITEM_RESTOCK.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.ITEM_RESTOCK));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        if (!_settingsManager.getItemRestockEnabled()) {
          p.sendMessage(Main.getPrefix() + Theme.error() + "Item Restock is globally disabled on the server.");
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleItemRestock(target);
        displayGUI(p, target);
      }
    });

    clickActions.put("actionbartimer", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        if (!target.hasPermission(Permissions.ACTIONBAR_TIMER.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.ACTIONBAR_TIMER));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        if (!_settingsManager.getActionBarTimerEnabled()) {
          p.sendMessage(Main.getPrefix() + Theme.error() + "Action-Bar timer is globally disabled on the server.");
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleActionBarTimer(target);
        displayGUI(p, target);
      }
    });

    clickActions.put("playertag", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        if (!target.hasPermission(Permissions.TAG.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.TAG));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        if (!_settingsManager.getTagsEnabled()) {
          p.sendMessage(Main.getPrefix() + Theme.error() + "Tags are globally disabled on the server.");
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _openPlayerTagDialog(p, gui, null, target);
      }

      @Override
      public void onRightClick(Player p) {
        if (!target.hasPermission(Permissions.TAG.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.TAG));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _clearPlayerTag(target);
        displayGUI(p, target);
      }
    });

    clickActions.put("togglecompass", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        if (!target.hasPermission(Permissions.TOGGLECOMPASS.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.TOGGLECOMPASS));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleCompass(target);
        displayGUI(p, target);
      }
    });

    clickActions.put("chestsort", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        if (!target.hasPermission(Permissions.CHESTSORT.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.CHESTSORT));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleChestSort(target);
        displayGUI(p, target);
      }

      @Override
      public void onRightClick(Player p) {
        if (!target.hasPermission(Permissions.CHESTSORT.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.CHESTSORT));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _cycleChestSortMode(target);
        displayGUI(p, target);
      }
    });

    clickActions.put("inventorysort", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        if (!target.hasPermission(Permissions.INVENTORYSORT.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.INVENTORYSORT));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleInventorySort(target);
        displayGUI(p, target);
      }

      @Override
      public void onRightClick(Player p) {
        if (!target.hasPermission(Permissions.INVENTORYSORT.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.INVENTORYSORT));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _cycleInventorySortMode(target);
        displayGUI(p, target);
      }

      @Override
      public void onShiftLeftClick(Player p) {
        if (!target.hasPermission(Permissions.INVENTORYSORT.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.INVENTORYSORT));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleInventorySortIncludeHotbar(target);
        displayGUI(p, target);
      }
    });

    clickActions.put("backpacksort", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        if (!target.hasPermission(Permissions.BACKPACK.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.BACKPACK));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleBackpackSort(target);
        displayGUI(p, target);
      }

      @Override
      public void onRightClick(Player p) {
        if (!target.hasPermission(Permissions.BACKPACK.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.BACKPACK));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _cycleBackpackSortMode(target);
        displayGUI(p, target);
      }
    });

    clickActions.put("navigationtrail", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleNavigationTrail(target);
        displayGUI(p, target);
      }
    });

    clickActions.put("navigationautocancel", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        if (!target.hasPermission(Permissions.WAYPOINTS.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.WAYPOINTS));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleNavigationAutoCancel(target);
        displayGUI(p, target);
      }

      @Override
      public void onRightClick(Player p) {
        if (!target.hasPermission(Permissions.WAYPOINTS.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.WAYPOINTS));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _openNavigationReachedRadiusDialog(p, gui, target);
      }
    });

    clickActions.put("veinminer", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        if (!target.hasPermission(Permissions.VEINMINER.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.VEINMINER));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        boolean globalState = _settingsManager.getVeinMinerEnabled();
        if (!globalState) {
          p.sendMessage(Main.getPrefix() + Theme.error() + "Vein Miner is globally disabled on the server.");
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleVeinMiner(target);
        displayGUI(p, target);
      }
    });

    clickActions.put("veinchopper", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        if (!target.hasPermission(Permissions.VEINCHOPPER.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.VEINCHOPPER));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        boolean globalState = _settingsManager.getVeinChopperEnabled();
        if (!globalState) {
          p.sendMessage(Main.getPrefix() + Theme.error() + "Vein Chopper is globally disabled on the server.");
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleVeinChopper(target);
        displayGUI(p, target);
      }
    });

    clickActions.put("doubledoor", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        if (!target.hasPermission(Permissions.DOUBLE_DOOR.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.DOUBLE_DOOR));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleDoubleDoor(target);
        displayGUI(p, target);
      }
    });

    if (showAdminSettings) {
      clickActions.put("adminsettings", new CustomGUI.ClickAction() {
        @Override
        public void onLeftClick(Player p) {
          _adminSettingsGUI.displayGUI(p, gui, player -> displayGUI(player, target));
        }
      });
    }

    gui.setClickActions(clickActions);
    gui.open(viewer);
  }

  private ItemStack _createToggleLocationItem(Player viewer, Player target) {
    boolean state = _settingsManager.getPlayerToggleLocation(target.getUniqueId());
    ItemStack item = new ItemStack(Material.FILLED_MAP);
    ItemMeta meta = item.getItemMeta();

    boolean hasPermission = target.hasPermission(Permissions.TOGGLELOCATION.getName());

    if (meta != null) {
      meta.displayName(
          Component.text(Theme.titlePrefix() + "Action-Bar Location"));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + "Show your current location in the actionbar.",
          (!hasPermission ? Main.getShortNoPermissionMessage(Permissions.TOGGLELOCATION) : null),
          "",
          Theme.textPrefix() + "State: "
              + (state ? Theme.highlight() + "ENABLED" : Theme.error() + "DISABLED"),
          "",
          Theme.textPrefix() + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).toList());
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createActionBarTimerItem(Player viewer, Player target) {
    boolean state = _settingsManager.getPlayerActionBarTimer(target.getUniqueId());
    ItemStack item = new ItemStack(Material.CLOCK);
    ItemMeta meta = item.getItemMeta();

    boolean globalState = _settingsManager.getActionBarTimerEnabled();
    boolean hasPermission = target.hasPermission(Permissions.ACTIONBAR_TIMER.getName());

    if (meta != null) {
      meta.displayName(
          Component.text(Theme.titlePrefix() + "Action-Bar Timer"));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + "Show the server timer in the actionbar.",
          (!hasPermission ? Main.getShortNoPermissionMessage(Permissions.ACTIONBAR_TIMER)
              : !globalState ? Theme.error() + "Action-Bar timer is globally disabled on the server." : null),
          "",
          Theme.textPrefix() + "State: "
              + (state ? Theme.highlight() + "ENABLED" : Theme.error() + "DISABLED"),
          "",
          Theme.textPrefix() + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createPlayerTagItem(Player viewer, Player target) {
    String tagName = _tagManager.getTag(target);
    ChatColor tagColor = _tagManager.getTagColor(target);
    boolean hasPermission = target.hasPermission(Permissions.TAG.getName());
    boolean globalEnabled = _settingsManager.getTagsEnabled();

    ItemStack item = new ItemStack(Material.NAME_TAG);
    ItemMeta meta = item.getItemMeta();

    String tagDisplay = (tagName == null || tagName.isEmpty())
        ? Theme.error() + "NONE"
        : (tagColor != null ? Theme.primary() + "[" + tagColor : ChatColor.AQUA) + tagName + Theme.primary() + "]";

    if (meta != null) {
      meta.displayName(
          Component.text(Theme.titlePrefix() + "Player Tag"));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + "Set the tag displayed with your name.",
          (!hasPermission ? Main.getShortNoPermissionMessage(Permissions.TAG)
              : !globalEnabled ? Theme.error() + "Tags are globally disabled on the server." : null),
          "",
          Theme.textPrefix() + "Tag: " + tagDisplay,
          "",
          Theme.textPrefix() + "Left-Click: Set Tag",
          Theme.textPrefix() + "Right-Click: Clear Tag")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createToggleCompassItem(Player viewer, Player target) {
    boolean state = _compassManager.checkPlayerActiveCompass(target);
    ItemStack item = new ItemStack(Material.COMPASS);
    ItemMeta meta = item.getItemMeta();

    boolean hasPermission = target.hasPermission(Permissions.TOGGLECOMPASS.getName());

    if (meta != null) {
      meta.displayName(
          Component.text(Theme.titlePrefix() + "Bossbar Compass"));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + "Shows a compass in the bossbar",
          (!hasPermission ? Main.getShortNoPermissionMessage(Permissions.TOGGLECOMPASS) : null),
          "",
          Theme.textPrefix() + "State: "
              + (state ? Theme.highlight() + "ENABLED" : Theme.error() + "DISABLED"),
          "",
          Theme.textPrefix() + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).toList());
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createChestSortItem(Player viewer, Player target) {
    boolean state = _settingsManager.getPlayerChestSort(target.getUniqueId());
    InventorySortMode mode = _settingsManager.getPlayerChestSortMode(target.getUniqueId());
    ItemStack item = new ItemStack(Material.CHEST);
    ItemMeta meta = item.getItemMeta();

    boolean hasPermission = target.hasPermission(Permissions.CHESTSORT.getName());

    meta.displayName(
        Component.text(Theme.titlePrefix() + "Chest Sorting"));

    List<String> lore = new java.util.ArrayList<>();
    lore.add(Theme.textPrefix() + "Right-Click outside of a chest inventory to sort it!");
    if (!hasPermission) {
      lore.add(Main.getShortNoPermissionMessage(Permissions.CHESTSORT));
    }

    lore.add("");
    lore.add(Theme.textPrefix() + "State: "
        + (state ? Theme.highlight() + "ENABLED" : Theme.error() + "DISABLED"));

    lore.add("");
    lore.add(Theme.textPrefix() + "Sorting Mode:");
    for (InventorySortMode option : InventorySortMode.values()) {
      ChatColor color = option == mode ? Theme.highlight() : Theme.primary();
      lore.add(Theme.textPrefix() + color + option.getLabel());
    }

    lore.add("");
    lore.add(Theme.textPrefix() + "Left-Click: Toggle");
    lore.add(Theme.textPrefix() + "Right-Click: Next mode");

    meta.lore(lore.stream().filter(Objects::nonNull).map(Component::text).toList());
    item.setItemMeta(meta);

    return item;
  }

  private ItemStack _createInventorySortItem(Player viewer, Player target) {
    boolean state = _settingsManager.getPlayerInventorySort(target.getUniqueId());
    boolean includeHotbar = _settingsManager.getPlayerInventorySortIncludeHotbar(target.getUniqueId());
    InventorySortMode mode = _settingsManager.getPlayerInventorySortMode(target.getUniqueId());
    ItemStack item = new ItemStack(Material.BARREL);
    ItemMeta meta = item.getItemMeta();

    boolean hasPermission = target.hasPermission(Permissions.INVENTORYSORT.getName());

    meta.displayName(
        Component.text(Theme.titlePrefix() + "Inventory Sorting"));

    List<String> lore = new java.util.ArrayList<>();
    lore.add(Theme.textPrefix() + "Right-Click outside of your inventory to sort it!");
    if (!hasPermission) {
      lore.add(Main.getShortNoPermissionMessage(Permissions.INVENTORYSORT));
    }

    lore.add("");
    lore.add(Theme.textPrefix() + "State: "
        + (state ? Theme.highlight() + "ENABLED" : Theme.error() + "DISABLED"));
    lore.add(Theme.textPrefix() + "Include Hotbar: "
        + (includeHotbar ? Theme.highlight() + "INCLUDE" : Theme.error() + "NOT INCLUDE"));

    lore.add("");
    lore.add(Theme.textPrefix() + "Sorting Mode:");
    for (InventorySortMode option : InventorySortMode.values()) {
      ChatColor color = option == mode ? Theme.highlight() : Theme.primary();
      lore.add(Theme.textPrefix() + color + option.getLabel());
    }

    lore.add("");
    lore.add(Theme.textPrefix() + "Left-Click: Toggle");
    lore.add(Theme.textPrefix() + "Right-Click: Next mode");
    lore.add(Theme.textPrefix() + "Shift-Left-Click: Toggle hotbar");

    meta.lore(lore.stream().filter(Objects::nonNull).map(Component::text).toList());
    item.setItemMeta(meta);

    return item;
  }

  private ItemStack _createBackpackSortItem(Player viewer, Player target) {
    boolean state = _settingsManager.getPlayerBackpackSort(target.getUniqueId());
    InventorySortMode mode = _settingsManager.getPlayerBackpackSortMode(target.getUniqueId());
    ItemStack item = new ItemStack(Material.BUNDLE);
    ItemMeta meta = item.getItemMeta();

    boolean hasPermission = target.hasPermission(Permissions.BACKPACK.getName());

    meta.displayName(
        Component.text(Theme.titlePrefix() + "Backpack Sorting"));

    List<String> lore = new java.util.ArrayList<>();
    lore.add(Theme.textPrefix() + "Right-Click outside of a backpack to sort its current page!");
    if (!hasPermission) {
      lore.add(Main.getShortNoPermissionMessage(Permissions.BACKPACK));
    }

    lore.add("");
    lore.add(Theme.textPrefix() + "State: "
        + (state ? Theme.highlight() + "ENABLED" : Theme.error() + "DISABLED"));

    lore.add("");
    lore.add(Theme.textPrefix() + "Sorting Mode:");
    for (InventorySortMode option : InventorySortMode.values()) {
      ChatColor color = option == mode ? Theme.highlight() : Theme.primary();
      lore.add(Theme.textPrefix() + color + option.getLabel());
    }

    lore.add("");
    lore.add(Theme.textPrefix() + "Left-Click: Toggle");
    lore.add(Theme.textPrefix() + "Right-Click: Next mode");

    meta.lore(lore.stream().filter(Objects::nonNull).map(Component::text).toList());
    item.setItemMeta(meta);
    item.setData(DataComponentTypes.TOOLTIP_DISPLAY,
        TooltipDisplay.tooltipDisplay().addHiddenComponents(DataComponentTypes.BUNDLE_CONTENTS));

    return item;
  }

  private ItemStack _createNavigationTrailItem(Player target) {
    boolean state = _settingsManager.getPlayerNavigationTrail(target.getUniqueId());
    ItemStack item = new ItemStack(Material.BLAZE_POWDER);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(Theme.titlePrefix() + "Navigation Particles"));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + "Show particle trails when navigating to a location.",
          "",
          Theme.textPrefix() + "State: "
              + (state ? Theme.highlight() + "ENABLED" : Theme.error() + "DISABLED"),
          "",
          Theme.textPrefix() + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).toList());
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createNavigationAutoCancelItem(Player target) {
    boolean state = _settingsManager.getPlayerNavigationAutoCancel(target.getUniqueId());
    int radius = _settingsManager.getPlayerNavigationReachedRadius(target.getUniqueId());
    ItemStack item = new ItemStack(Material.REPEATER);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(Theme.titlePrefix() + "Navigation Auto Cancel"));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + "Automatically stop navigation when you reach the destination.",
          "",
          Theme.textPrefix() + "State: "
              + (state ? Theme.highlight() + "ENABLED" : Theme.error() + "DISABLED"),
          Theme.textPrefix() + "Radius: " + Theme.highlight() + radius + Theme.primary()
              + " blocks",
          "",
          Theme.textPrefix() + "Left-Click: Toggle",
          Theme.textPrefix() + "Right-Click: Set radius")
          .stream().filter(Objects::nonNull).map(Component::text).toList());
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createVeinMinerItem(Player viewer, Player target) {
    boolean state = _settingsManager.getPlayerVeinMiner(target.getUniqueId());
    ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
    ItemMeta meta = item.getItemMeta();

    boolean globalState = _settingsManager.getVeinMinerEnabled();
    boolean hasPermission = target.hasPermission(Permissions.VEINMINER.getName());

    if (meta != null) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      meta.displayName(
          Component.text(Theme.titlePrefix() + "Vein Miner"));
      meta.lore(Arrays.asList(
          Theme.textPrefix()
              + "While sneaking, mine all ores of the same type if using a pickaxe.",
          (!hasPermission ? Main.getShortNoPermissionMessage(Permissions.VEINMINER)
              : !globalState ? Theme.error() + "Vein Miner is globally disabled on the server." : null),
          "",
          Theme.textPrefix() + "State: "
              + (state ? Theme.highlight() + "ENABLED" : Theme.error() + "DISABLED"),
          "",
          Theme.textPrefix() + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createVeinChopperItem(Player viewer, Player target) {
    boolean state = _settingsManager.getPlayerVeinChopper(target.getUniqueId());
    ItemStack item = new ItemStack(Material.DIAMOND_AXE);
    ItemMeta meta = item.getItemMeta();

    boolean globalState = _settingsManager.getVeinChopperEnabled();
    boolean hasPermission = target.hasPermission(Permissions.VEINCHOPPER.getName());

    if (meta != null) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      meta.displayName(
          Component.text(Theme.titlePrefix() + "Vein Chopper"));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + "While sneaking, chop all logs of the same type if using an axe.",
          (!hasPermission ? Main.getShortNoPermissionMessage(Permissions.VEINCHOPPER)
              : !globalState ? Theme.error() + "Vein Chopper is globally disabled on the server." : null),
          "",
          Theme.textPrefix() + "State: "
              + (state ? Theme.highlight() + "ENABLED" : Theme.error() + "DISABLED"),
          "",
          Theme.textPrefix() + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createItemRestockItem(Player viewer, Player target) {
    boolean state = _settingsManager.getPlayerItemRestock(target.getUniqueId());
    ItemStack item = new ItemStack(Material.HOPPER);
    ItemMeta meta = item.getItemMeta();

    boolean globalState = _settingsManager.getItemRestockEnabled();
    boolean hasPermission = target.hasPermission(Permissions.ITEM_RESTOCK.getName());

    if (meta != null) {
      meta.displayName(
          Component.text(Theme.titlePrefix() + "Item Restock"));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + "Refill your hotbar slot with matching items automatically.",
          (!hasPermission ? Main.getShortNoPermissionMessage(Permissions.ITEM_RESTOCK)
              : !globalState ? Theme.error() + "Item Restock is globally disabled on the server." : null),
          "",
          Theme.textPrefix() + "State: "
              + (state ? Theme.highlight() + "ENABLED" : Theme.error() + "DISABLED"),
          "",
          Theme.textPrefix() + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createDoubleDoorItem(Player viewer, Player target) {
    boolean state = _settingsManager.getPlayerDoubleDoorSync(target.getUniqueId());
    ItemStack item = new ItemStack(Material.OAK_DOOR);
    ItemMeta meta = item.getItemMeta();

    boolean hasPermission = target.hasPermission(Permissions.DOUBLE_DOOR.getName());

    if (meta != null) {
      meta.displayName(
          Component.text(Theme.titlePrefix() + "Double Door Sync"));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + "Interact with one door to toggle the paired door.",
          (!hasPermission ? Main.getShortNoPermissionMessage(Permissions.DOUBLE_DOOR) : null),
          "",
          Theme.textPrefix() + "State: "
              + (state ? Theme.highlight() + "ENABLED" : Theme.error() + "DISABLED"),
          "",
          Theme.textPrefix() + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createAdminSettingsItem() {
    ItemStack item = new ItemStack(Material.REDSTONE_TORCH);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(Theme.titlePrefix() + "Admin Settings"));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + "Manage admin and server settings.",
          "",
          Theme.textPrefix() + "Left-Click: Open")
          .stream().filter(Objects::nonNull).map(Component::text).toList());
      item.setItemMeta(meta);
    }

    return item;
  }

  private void _openPlayerTagDialog(Player viewer, CustomGUI parentMenu, String errorMessage, Player target) {
    String currentName = Optional.ofNullable(_tagManager.getTag(target)).orElse("");
    String currentColorName = _settingsManager.getPlayerTagColor(target.getUniqueId());

    DialogInput inputName = CustomDialog.createTextInput("tagname",
        Theme.textPrefix() + "Tag Name",
        currentName);

    DialogInput inputColor = CustomDialog.createSelectInput("tagcolor",
        Theme.textPrefix() + "Tag Color",
        _buildTagColorOptions(),
        currentColorName);

    Dialog dialog = CustomDialog.createConfirmationDialog(
        "Player Tag",
        "Set your tag name and color.",
        errorMessage,
        List.of(inputName, inputColor),
        (view, audience) -> _setPlayerTagDialogCB(view, audience, parentMenu, target),
        null);

    viewer.showDialog(dialog);
  }

  private void _openNavigationReachedRadiusDialog(Player viewer, CustomGUI parentMenu, Player target) {
    int radius = _settingsManager.getPlayerNavigationReachedRadius(target.getUniqueId());

    DialogInput inputRadius = CustomDialog.createNumberInput("radius",
        Theme.textPrefix() + "Reached Radius (blocks)",
        1, 200, 1, (float) radius);

    Dialog dialog = CustomDialog.createConfirmationDialog(
        "Waypoints Auto Cancel Radius",
        "Set how close you must be for a destination to count as reached.",
        null,
        List.of(inputRadius),
        (view, audience) -> _setNavigationReachedRadiusDialogCB(view, audience, parentMenu, target),
        null);

    viewer.showDialog(dialog);
  }

  private void _setPlayerTagDialogCB(DialogResponseView view, Audience audience, CustomGUI parentMenu,
      Player target) {
    Player viewer = (Player) audience;
    String name = Optional.ofNullable(view.getText("tagname")).map(String::trim).orElse("");
    String colorKey = Optional.ofNullable(view.getText("tagcolor")).map(String::trim).orElse("AQUA");

    if (name.isEmpty()) {
      _openPlayerTagDialog(viewer, parentMenu, "Tag name cannot be empty.", target);
      return;
    }

    if (name.length() > 10) {
      _openPlayerTagDialog(viewer, parentMenu, "Tag too long! Maximum length is 10 characters.", target);
      return;
    }

    ChatColor color = Theme.minecraftColors().get(colorKey);
    _tagManager.setTag(target, name, colorKey);
    _nameTagManager.updateNameTag(target);
    _tabListManager.refreshPlayerListEntry(target);

    target.sendMessage(Main.getPrefix() + Theme.primary() + "Tag set to: " + Theme.primary() + "[" + color + name
        + Theme.primary() + "]");
    target.playSound(target, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);

    displayGUI(viewer, target);
  }

  private void _setNavigationReachedRadiusDialogCB(DialogResponseView view, Audience audience, CustomGUI parentMenu,
      Player target) {
    Player viewer = (Player) audience;
    int radius = Math.round(view.getFloat("radius"));

    _settingsManager.setPlayerNavigationReachedRadius(target.getUniqueId(), radius);

    target.sendMessage(Main.getPrefix() + "Navigation reach radius set to " + Theme.highlight() + radius
        + Theme.primary() + " blocks.");
    target.playSound(target, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);

    displayGUI(viewer, target);
  }

  private void _clearPlayerTag(Player target) {
    _tagManager.removeTag(target);
    _nameTagManager.updateNameTag(target);
    _tabListManager.refreshPlayerListEntry(target);

    target.sendMessage(Main.getPrefix() + Theme.primary() + "Tag cleared.");
  }

  private Map<String, String> _buildTagColorOptions() {
    Map<String, String> options = new LinkedHashMap<>();
    for (Map.Entry<String, ChatColor> entry : Theme.minecraftColors().entrySet()) {
      String name = _formatColorName(entry.getKey());
      options.put(entry.getKey(), entry.getValue() + name);
    }

    return options;
  }

  private String _formatColorName(String name) {
    if (name == null || name.isBlank()) {
      return "";
    }

    return Arrays.stream(name.split("_"))
        .filter(part -> part != null && !part.isBlank())
        .map(part -> part.substring(0, 1) + part.substring(1).toLowerCase())
        .collect(Collectors.joining(" "));
  }


  private void _toggleLocation(Player p) {
    boolean newState;

    if (_settingsManager.getPlayerToggleLocation(p.getUniqueId())) {
      _settingsManager.setPlayerToggleLocation(p.getUniqueId(), false);
      _actionBar.removeActionBar(p);
      newState = false;
    } else {
      _navigationManager.stopNavigation(p);
      _settingsManager.setPlayerToggleLocation(p.getUniqueId(), true);

      var blockLoc = p.getLocation().toBlockLocation();
      Biome biome = p.getWorld().getBiome(blockLoc);
      String locationText = Theme.highlight() + "X: " + Theme.primary() + blockLoc.getBlockX()
          + Theme.highlight() + " Y: " + Theme.primary() + blockLoc.getBlockY()
          + Theme.highlight() + " Z: " + Theme.primary() + blockLoc.getBlockZ() + Theme.textSymbol()
          + ChatColor.BOLD + " » " + Theme.primary() + biome.getKey();

      _actionBar.sendActionBar(p, locationText);
      newState = true;
    }

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Action-Bar location is now " + Theme.highlight() + ChatColor.BOLD + stateText);
  }

  private void _toggleActionBarTimer(Player p) {
    boolean newState = !_settingsManager.getPlayerActionBarTimer(p.getUniqueId());
    _settingsManager.setPlayerActionBarTimer(p.getUniqueId(), newState);

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Action-Bar timer is now " + Theme.highlight() + ChatColor.BOLD + stateText);
  }

  private void _toggleCompass(Player p) {
    boolean currentlyActive = _compassManager.checkPlayerActiveCompass(p);
    boolean newState = !currentlyActive;

    if (currentlyActive) {
      _compassManager.removePlayerFromCompass(p);
    } else {
      _compassManager.addPlayerToCompass(p);
    }

    _settingsManager.setPlayerToggleCompass(p.getUniqueId(), newState);

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Bossbar-Compass is now " + Theme.highlight() + ChatColor.BOLD + stateText);
  }

  private void _toggleChestSort(Player p) {
    boolean newState = !_settingsManager.getPlayerChestSort(p.getUniqueId());
    _settingsManager.setPlayerChestSort(p.getUniqueId(), newState);

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Chest sorting is now " + Theme.highlight() + ChatColor.BOLD + stateText);
  }

  private void _cycleChestSortMode(Player p) {
    InventorySortMode current = _settingsManager.getPlayerChestSortMode(p.getUniqueId());
    InventorySortMode next = current.next();
    _settingsManager.setPlayerChestSortMode(p.getUniqueId(), next);

    p.sendMessage(Main.getPrefix() + "Chest sort mode set to " + Theme.highlight() + next.getLabel());
  }

  private void _toggleBackpackSort(Player p) {
    boolean newState = !_settingsManager.getPlayerBackpackSort(p.getUniqueId());
    _settingsManager.setPlayerBackpackSort(p.getUniqueId(), newState);

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Backpack sorting is now " + Theme.highlight() + ChatColor.BOLD + stateText);
  }

  private void _cycleBackpackSortMode(Player p) {
    InventorySortMode current = _settingsManager.getPlayerBackpackSortMode(p.getUniqueId());
    InventorySortMode next = current.next();
    _settingsManager.setPlayerBackpackSortMode(p.getUniqueId(), next);

    p.sendMessage(Main.getPrefix() + "Backpack sort mode set to " + Theme.highlight() + next.getLabel());
  }

  private void _toggleInventorySort(Player p) {
    boolean newState = !_settingsManager.getPlayerInventorySort(p.getUniqueId());
    _settingsManager.setPlayerInventorySort(p.getUniqueId(), newState);

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Inventory sorting is now " + Theme.highlight() + ChatColor.BOLD + stateText);
  }

  private void _cycleInventorySortMode(Player p) {
    InventorySortMode current = _settingsManager.getPlayerInventorySortMode(p.getUniqueId());
    InventorySortMode next = current.next();
    _settingsManager.setPlayerInventorySortMode(p.getUniqueId(), next);

    p.sendMessage(Main.getPrefix() + "Inventory sort mode set to " + Theme.highlight() + next.getLabel());
  }

  private void _toggleInventorySortIncludeHotbar(Player p) {
    boolean newState = !_settingsManager.getPlayerInventorySortIncludeHotbar(p.getUniqueId());
    _settingsManager.setPlayerInventorySortIncludeHotbar(p.getUniqueId(), newState);

    String stateText = newState ? "INCLUDED" : "EXCLUDED";
    p.sendMessage(
        Main.getPrefix() + "Inventory sorting hotbar is now " + Theme.highlight() + ChatColor.BOLD + stateText);
  }

  private void _toggleNavigationTrail(Player p) {
    boolean newState = !_settingsManager.getPlayerNavigationTrail(p.getUniqueId());
    _settingsManager.setPlayerNavigationTrail(p.getUniqueId(), newState);

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Navigation particles are now " + Theme.highlight() + ChatColor.BOLD + stateText);
  }

  private void _toggleNavigationAutoCancel(Player p) {
    boolean newState = !_settingsManager.getPlayerNavigationAutoCancel(p.getUniqueId());
    _settingsManager.setPlayerNavigationAutoCancel(p.getUniqueId(), newState);

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Navigation auto cancel is now " + Theme.highlight() + ChatColor.BOLD + stateText);
  }

  private void _toggleVeinMiner(Player p) {
    boolean newState = !_settingsManager.getPlayerVeinMiner(p.getUniqueId());
    _settingsManager.setPlayerVeinMiner(p.getUniqueId(), newState);

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Vein Miner is now " + Theme.highlight() + ChatColor.BOLD + stateText);
  }

  private void _toggleVeinChopper(Player p) {
    boolean newState = !_settingsManager.getPlayerVeinChopper(p.getUniqueId());
    _settingsManager.setPlayerVeinChopper(p.getUniqueId(), newState);

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Vein Chopper is now " + Theme.highlight() + ChatColor.BOLD + stateText);
  }

  private void _toggleDoubleDoor(Player p) {
    boolean newState = !_settingsManager.getPlayerDoubleDoorSync(p.getUniqueId());
    _settingsManager.setPlayerDoubleDoorSync(p.getUniqueId(), newState);

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(
        Main.getPrefix() + "Double door sync is now " + Theme.highlight() + ChatColor.BOLD + stateText);
  }

  private void _toggleItemRestock(Player p) {
    boolean newState = !_settingsManager.getPlayerItemRestock(p.getUniqueId());
    _settingsManager.setPlayerItemRestock(p.getUniqueId(), newState);

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Item restock is now " + Theme.highlight() + ChatColor.BOLD + stateText);
  }

}
