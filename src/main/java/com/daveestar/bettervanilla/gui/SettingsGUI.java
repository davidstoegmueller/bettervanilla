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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.InventorySortMode;
import com.daveestar.bettervanilla.enums.Language;
import com.daveestar.bettervanilla.enums.Permissions;
import com.daveestar.bettervanilla.manager.CompassManager;
import com.daveestar.bettervanilla.manager.NameTagManager;
import com.daveestar.bettervanilla.manager.NavigationManager;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.manager.TabListManager;
import com.daveestar.bettervanilla.manager.TagManager;
import com.daveestar.bettervanilla.manager.TranslationManager;
import com.daveestar.bettervanilla.utils.ActionBar;
import com.daveestar.bettervanilla.utils.CustomDialog;
import com.daveestar.bettervanilla.utils.CustomGUI;
import com.daveestar.bettervanilla.utils.Theme;

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
  private final TranslationManager _translations;

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
    _translations = _plugin.getTranslationManager();
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
    entries.put("navigationtrail", _createNavigationTrailItem(viewer, target));
    entries.put("navigationautocancel", _createNavigationAutoCancelItem(viewer, target));

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
    entries.put("language", _createLanguageItem(viewer, target));

    // fourth row
    if (showAdminSettings) {
      entries.put("adminsettings", _createAdminSettingsItem(viewer));
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
    customSlots.put("language", 31);

    // fourth row
    if (showAdminSettings) {
      customSlots.put("adminsettings", rows * 9 - 10);
    }

    CustomGUI gui = new CustomGUI(_plugin, viewer,
        Theme.titlePrefix() + _translations.translate(viewer, "settings-gui-title", "player", target.getName()),
        entries, rows, customSlots, null,
        EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    Map<String, CustomGUI.ClickAction> clickActions = new HashMap<>();
    clickActions.put("language", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _openLanguageDialog(p, target);
      }
    });
    clickActions.put("togglelocation", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        if (!target.hasPermission(Permissions.TOGGLELOCATION.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(p, Permissions.TOGGLELOCATION));
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
          p.sendMessage(Main.getNoPermissionMessage(p, Permissions.ITEM_RESTOCK));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        if (!_settingsManager.getItemRestockEnabled()) {
          p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "settings-error-item-restock-disabled"));
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
          p.sendMessage(Main.getNoPermissionMessage(p, Permissions.ACTIONBAR_TIMER));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        if (!_settingsManager.getActionBarTimerEnabled()) {
          p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "settings-error-action-bar-timer-disabled"));
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
          p.sendMessage(Main.getNoPermissionMessage(p, Permissions.TAG));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        if (!_settingsManager.getTagsEnabled()) {
          p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "settings-error-tags-disabled"));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _openPlayerTagDialog(p, gui, null, target);
      }

      @Override
      public void onRightClick(Player p) {
        if (!target.hasPermission(Permissions.TAG.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(p, Permissions.TAG));
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
          p.sendMessage(Main.getNoPermissionMessage(p, Permissions.TOGGLECOMPASS));
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
          p.sendMessage(Main.getNoPermissionMessage(p, Permissions.CHESTSORT));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleChestSort(target);
        displayGUI(p, target);
      }

      @Override
      public void onRightClick(Player p) {
        if (!target.hasPermission(Permissions.CHESTSORT.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(p, Permissions.CHESTSORT));
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
          p.sendMessage(Main.getNoPermissionMessage(p, Permissions.INVENTORYSORT));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleInventorySort(target);
        displayGUI(p, target);
      }

      @Override
      public void onRightClick(Player p) {
        if (!target.hasPermission(Permissions.INVENTORYSORT.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(p, Permissions.INVENTORYSORT));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _cycleInventorySortMode(target);
        displayGUI(p, target);
      }

      @Override
      public void onShiftLeftClick(Player p) {
        if (!target.hasPermission(Permissions.INVENTORYSORT.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(p, Permissions.INVENTORYSORT));
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
          p.sendMessage(Main.getNoPermissionMessage(p, Permissions.BACKPACK));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleBackpackSort(target);
        displayGUI(p, target);
      }

      @Override
      public void onRightClick(Player p) {
        if (!target.hasPermission(Permissions.BACKPACK.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(p, Permissions.BACKPACK));
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
          p.sendMessage(Main.getNoPermissionMessage(p, Permissions.WAYPOINTS));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleNavigationAutoCancel(target);
        displayGUI(p, target);
      }

      @Override
      public void onRightClick(Player p) {
        if (!target.hasPermission(Permissions.WAYPOINTS.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(p, Permissions.WAYPOINTS));
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
          p.sendMessage(Main.getNoPermissionMessage(p, Permissions.VEINMINER));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        boolean globalState = _settingsManager.getVeinMinerEnabled();
        if (!globalState) {
          p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "settings-error-vein-miner-disabled"));
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
          p.sendMessage(Main.getNoPermissionMessage(p, Permissions.VEINCHOPPER));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        boolean globalState = _settingsManager.getVeinChopperEnabled();
        if (!globalState) {
          p.sendMessage(Main.getPrefix() + Theme.error() + Main.tr(p, "settings-error-vein-chopper-disabled"));
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
          p.sendMessage(Main.getNoPermissionMessage(p, Permissions.DOUBLE_DOOR));
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

  private ItemStack _createLanguageItem(Player viewer, Player target) {
    String code = _settingsManager.getPlayerLanguage(target.getUniqueId());
    String language = Language.fromCode(code).getDisplayName(viewer);
    ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.displayName(Component.text(Theme.titlePrefix()
          + _translations.translate(viewer, "settings-language-item-title")));
      meta.lore(List.of(
          Component.text(Theme.textPrefix() + _translations.translate(viewer,
              "settings-language-item-description")),
          Component.empty(),
          Component.text(Theme.textPrefix() + _translations.translate(viewer,
              "settings-language-item-current", "language", language)),
          Component.empty(),
          Component.text(Theme.textPrefix() + _translations.translate(viewer,
              "settings-language-item-action"))));
      item.setItemMeta(meta);
    }
    return item;
  }

  private void _openLanguageDialog(Player viewer, Player target) {
    DialogInput input = CustomDialog.createSelectInput("language",
        Theme.textPrefix() + _translations.translate(viewer, "settings-language-dialog-input-label"),
        _translations.getLanguageOptions(viewer), _settingsManager.getPlayerLanguage(target.getUniqueId()));
    Dialog dialog = CustomDialog.createConfirmationDialog(
        _translations.translate(viewer, "settings-language-dialog-title"),
        _translations.translate(viewer, "settings-language-dialog-description"),
        null, List.of(input), (view, audience) -> {
          Language selectedLanguage = Language.fromCode(view.getText("language"));
          String code = selectedLanguage.getCode();
          _settingsManager.setPlayerLanguage(target.getUniqueId(), code);
          String name = selectedLanguage.getDisplayName(target);
          target.sendMessage(Main.getPrefix() + _translations.translate(target,
              "settings-language-changed-message", "language", name));
          displayGUI((Player) audience, target);
        }, null, _translations.translate(viewer, "dialog-button-apply"),
        _translations.translate(viewer, "dialog-button-cancel"));
    viewer.showDialog(dialog);
  }

  private ItemStack _createToggleLocationItem(Player viewer, Player target) {
    boolean state = _settingsManager.getPlayerToggleLocation(target.getUniqueId());
    ItemStack item = new ItemStack(Material.FILLED_MAP);
    ItemMeta meta = item.getItemMeta();

    boolean hasPermission = target.hasPermission(Permissions.TOGGLELOCATION.getName());

    if (meta != null) {
      meta.displayName(
          Component.text(Theme.titlePrefix() + _t(viewer, "settings-location-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t(viewer, "settings-location-description"),
          (!hasPermission ? Main.getShortNoPermissionMessage(viewer, Permissions.TOGGLELOCATION) : null),
          "",
          Theme.textPrefix() + _t(viewer, "gui-common-state", "state", _state(viewer, state)),
          "",
          Theme.textPrefix() + _t(viewer, "gui-common-action-toggle"))
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
          Component.text(Theme.titlePrefix() + _t(viewer, "settings-action-bar-timer-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t(viewer, "settings-action-bar-timer-description"),
          (!hasPermission ? Main.getShortNoPermissionMessage(viewer, Permissions.ACTIONBAR_TIMER)
              : !globalState ? Theme.error() + _t(viewer, "settings-error-action-bar-timer-disabled") : null),
          "",
          Theme.textPrefix() + _t(viewer, "gui-common-state", "state", _state(viewer, state)),
          "",
          Theme.textPrefix() + _t(viewer, "gui-common-action-toggle"))
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
        ? Theme.error() + _t(viewer, "common-value-none")
        : (tagColor != null ? Theme.primary() + "[" + tagColor : ChatColor.AQUA) + tagName + Theme.primary() + "]";

    if (meta != null) {
      meta.displayName(
          Component.text(Theme.titlePrefix() + _t(viewer, "settings-player-tag-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t(viewer, "settings-player-tag-description"),
          (!hasPermission ? Main.getShortNoPermissionMessage(viewer, Permissions.TAG)
              : !globalEnabled ? Theme.error() + _t(viewer, "settings-error-tags-disabled") : null),
          "",
          Theme.textPrefix() + _t(viewer, "settings-player-tag-current", "tag", tagDisplay),
          "",
          Theme.textPrefix() + _t(viewer, "settings-player-tag-action-set"),
          Theme.textPrefix() + _t(viewer, "settings-player-tag-action-clear"))
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
          Component.text(Theme.titlePrefix() + _t(viewer, "settings-compass-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t(viewer, "settings-compass-description"),
          (!hasPermission ? Main.getShortNoPermissionMessage(viewer, Permissions.TOGGLECOMPASS) : null),
          "",
          Theme.textPrefix() + _t(viewer, "gui-common-state", "state", _state(viewer, state)),
          "",
          Theme.textPrefix() + _t(viewer, "gui-common-action-toggle"))
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
          Component.text(Theme.titlePrefix() + _t(viewer, "settings-chest-sorting-title")));

    List<String> lore = new java.util.ArrayList<>();
    lore.add(Theme.textPrefix() + _t(viewer, "settings-chest-sorting-description"));
    if (!hasPermission) {
      lore.add(Main.getShortNoPermissionMessage(viewer, Permissions.CHESTSORT));
    }

    lore.add("");
    lore.add(Theme.textPrefix() + _t(viewer, "gui-common-state", "state", _state(viewer, state)));

    lore.add("");
    lore.add(Theme.textPrefix() + _t(viewer, "settings-sorting-mode-label"));
    for (InventorySortMode option : InventorySortMode.values()) {
      ChatColor color = option == mode ? Theme.highlight() : Theme.primary();
      lore.add(Theme.textPrefix() + color + option.getLabel(viewer));
    }

    lore.add("");
    lore.add(Theme.textPrefix() + _t(viewer, "gui-common-action-toggle"));
    lore.add(Theme.textPrefix() + _t(viewer, "settings-sorting-action-next-mode"));

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
          Component.text(Theme.titlePrefix() + _t(viewer, "settings-inventory-sorting-title")));

    List<String> lore = new java.util.ArrayList<>();
    lore.add(Theme.textPrefix() + _t(viewer, "settings-inventory-sorting-description"));
    if (!hasPermission) {
      lore.add(Main.getShortNoPermissionMessage(viewer, Permissions.INVENTORYSORT));
    }

    lore.add("");
    lore.add(Theme.textPrefix() + _t(viewer, "gui-common-state", "state", _state(viewer, state)));
    lore.add(Theme.textPrefix() + _t(viewer, "settings-inventory-sorting-hotbar", "state",
        (includeHotbar ? Theme.highlight() : Theme.error())
            + _t(viewer, includeHotbar ? "common-state-included" : "common-state-excluded")));

    lore.add("");
    lore.add(Theme.textPrefix() + _t(viewer, "settings-sorting-mode-label"));
    for (InventorySortMode option : InventorySortMode.values()) {
      ChatColor color = option == mode ? Theme.highlight() : Theme.primary();
      lore.add(Theme.textPrefix() + color + option.getLabel(viewer));
    }

    lore.add("");
    lore.add(Theme.textPrefix() + _t(viewer, "gui-common-action-toggle"));
    lore.add(Theme.textPrefix() + _t(viewer, "settings-sorting-action-next-mode"));
    lore.add(Theme.textPrefix() + _t(viewer, "settings-inventory-sorting-action-hotbar"));

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
          Component.text(Theme.titlePrefix() + _t(viewer, "settings-backpack-sorting-title")));

    List<String> lore = new java.util.ArrayList<>();
    lore.add(Theme.textPrefix() + _t(viewer, "settings-backpack-sorting-description"));
    if (!hasPermission) {
      lore.add(Main.getShortNoPermissionMessage(viewer, Permissions.BACKPACK));
    }

    lore.add("");
    lore.add(Theme.textPrefix() + _t(viewer, "gui-common-state", "state", _state(viewer, state)));

    lore.add("");
    lore.add(Theme.textPrefix() + _t(viewer, "settings-sorting-mode-label"));
    for (InventorySortMode option : InventorySortMode.values()) {
      ChatColor color = option == mode ? Theme.highlight() : Theme.primary();
      lore.add(Theme.textPrefix() + color + option.getLabel(viewer));
    }

    lore.add("");
    lore.add(Theme.textPrefix() + _t(viewer, "gui-common-action-toggle"));
    lore.add(Theme.textPrefix() + _t(viewer, "settings-sorting-action-next-mode"));

    meta.lore(lore.stream().filter(Objects::nonNull).map(Component::text).toList());
    item.setItemMeta(meta);
    return item;
  }

  private ItemStack _createNavigationTrailItem(Player viewer, Player target) {
    boolean state = _settingsManager.getPlayerNavigationTrail(target.getUniqueId());
    ItemStack item = new ItemStack(Material.BLAZE_POWDER);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(Theme.titlePrefix() + _t(viewer, "settings-navigation-particles-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t(viewer, "settings-navigation-particles-description"),
          "",
          Theme.textPrefix() + _t(viewer, "gui-common-state", "state", _state(viewer, state)),
          "",
          Theme.textPrefix() + _t(viewer, "gui-common-action-toggle"))
          .stream().filter(Objects::nonNull).map(Component::text).toList());
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createNavigationAutoCancelItem(Player viewer, Player target) {
    boolean state = _settingsManager.getPlayerNavigationAutoCancel(target.getUniqueId());
    int radius = _settingsManager.getPlayerNavigationReachedRadius(target.getUniqueId());
    ItemStack item = new ItemStack(Material.REPEATER);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(Theme.titlePrefix() + _t(viewer, "settings-navigation-auto-cancel-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t(viewer, "settings-navigation-auto-cancel-description"),
          "",
          Theme.textPrefix() + _t(viewer, "gui-common-state", "state", _state(viewer, state)),
          Theme.textPrefix() + _t(viewer, "settings-navigation-radius", "radius", radius),
          "",
          Theme.textPrefix() + _t(viewer, "gui-common-action-toggle"),
          Theme.textPrefix() + _t(viewer, "settings-navigation-action-set-radius"))
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
      meta.displayName(
          Component.text(Theme.titlePrefix() + _t(viewer, "settings-vein-miner-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t(viewer, "settings-vein-miner-description"),
          (!hasPermission ? Main.getShortNoPermissionMessage(viewer, Permissions.VEINMINER)
              : !globalState ? Theme.error() + _t(viewer, "settings-error-vein-miner-disabled") : null),
          "",
          Theme.textPrefix() + _t(viewer, "gui-common-state", "state", _state(viewer, state)),
          "",
          Theme.textPrefix() + _t(viewer, "gui-common-action-toggle"))
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
      meta.displayName(
          Component.text(Theme.titlePrefix() + _t(viewer, "settings-vein-chopper-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t(viewer, "settings-vein-chopper-description"),
          (!hasPermission ? Main.getShortNoPermissionMessage(viewer, Permissions.VEINCHOPPER)
              : !globalState ? Theme.error() + _t(viewer, "settings-error-vein-chopper-disabled") : null),
          "",
          Theme.textPrefix() + _t(viewer, "gui-common-state", "state", _state(viewer, state)),
          "",
          Theme.textPrefix() + _t(viewer, "gui-common-action-toggle"))
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
          Component.text(Theme.titlePrefix() + _t(viewer, "settings-item-restock-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t(viewer, "settings-item-restock-description"),
          (!hasPermission ? Main.getShortNoPermissionMessage(viewer, Permissions.ITEM_RESTOCK)
              : !globalState ? Theme.error() + _t(viewer, "settings-error-item-restock-disabled") : null),
          "",
          Theme.textPrefix() + _t(viewer, "gui-common-state", "state", _state(viewer, state)),
          "",
          Theme.textPrefix() + _t(viewer, "gui-common-action-toggle"))
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
          Component.text(Theme.titlePrefix() + _t(viewer, "settings-double-door-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t(viewer, "settings-double-door-description"),
          (!hasPermission ? Main.getShortNoPermissionMessage(viewer, Permissions.DOUBLE_DOOR) : null),
          "",
          Theme.textPrefix() + _t(viewer, "gui-common-state", "state", _state(viewer, state)),
          "",
          Theme.textPrefix() + _t(viewer, "gui-common-action-toggle"))
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createAdminSettingsItem(Player viewer) {
    ItemStack item = new ItemStack(Material.REDSTONE_TORCH);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(Theme.titlePrefix() + _t(viewer, "settings-admin-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t(viewer, "settings-admin-description"),
          "",
          Theme.textPrefix() + _t(viewer, "gui-common-action-open"))
          .stream().filter(Objects::nonNull).map(Component::text).toList());
      item.setItemMeta(meta);
    }

    return item;
  }

  private void _openPlayerTagDialog(Player viewer, CustomGUI parentMenu, String errorMessage, Player target) {
    String currentName = Optional.ofNullable(_tagManager.getTag(target)).orElse("");
    String currentColorName = _settingsManager.getPlayerTagColor(target.getUniqueId());

    DialogInput inputName = CustomDialog.createTextInput("tagname",
        Theme.textPrefix() + _t(viewer, "settings-tag-dialog-name-label"),
        currentName);

    DialogInput inputColor = CustomDialog.createSelectInput("tagcolor",
        Theme.textPrefix() + _t(viewer, "settings-tag-dialog-color-label"),
        _buildTagColorOptions(viewer),
        currentColorName);

    Dialog dialog = CustomDialog.createConfirmationDialog(
        _t(viewer, "settings-tag-dialog-title"),
        _t(viewer, "settings-tag-dialog-description"),
        errorMessage,
        List.of(inputName, inputColor),
        (view, audience) -> _setPlayerTagDialogCB(view, audience, parentMenu, target),
        null, _t(viewer, "dialog-button-apply"), _t(viewer, "dialog-button-cancel"));

    viewer.showDialog(dialog);
  }

  private void _openNavigationReachedRadiusDialog(Player viewer, CustomGUI parentMenu, Player target) {
    int radius = _settingsManager.getPlayerNavigationReachedRadius(target.getUniqueId());

    DialogInput inputRadius = CustomDialog.createNumberInput("radius",
        Theme.textPrefix() + _t(viewer, "settings-navigation-dialog-radius-label"),
        1, 200, 1, (float) radius);

    Dialog dialog = CustomDialog.createConfirmationDialog(
        _t(viewer, "settings-navigation-dialog-title"),
        _t(viewer, "settings-navigation-dialog-description"),
        null,
        List.of(inputRadius),
        (view, audience) -> _setNavigationReachedRadiusDialogCB(view, audience, parentMenu, target),
        null, _t(viewer, "dialog-button-apply"), _t(viewer, "dialog-button-cancel"));

    viewer.showDialog(dialog);
  }

  private void _setPlayerTagDialogCB(DialogResponseView view, Audience audience, CustomGUI parentMenu,
      Player target) {
    Player viewer = (Player) audience;
    String name = Optional.ofNullable(view.getText("tagname")).map(String::trim).orElse("");
    String colorKey = Optional.ofNullable(view.getText("tagcolor")).map(String::trim).orElse("AQUA");

    if (name.isEmpty()) {
      _openPlayerTagDialog(viewer, parentMenu, _t(viewer, "settings-tag-error-empty"), target);
      return;
    }

    if (name.length() > 10) {
      _openPlayerTagDialog(viewer, parentMenu, _t(viewer, "settings-tag-error-too-long"), target);
      return;
    }

    ChatColor color = Theme.minecraftColors().get(colorKey);
    _tagManager.setTag(target, name, colorKey);
    _nameTagManager.updateNameTag(target);
    _tabListManager.refreshPlayerListEntry(target);

    target.sendMessage(Main.getPrefix() + Theme.primary() + Main.tr(target, "settings-tag-set-message",
        "tag", Theme.primary() + "[" + color + name + Theme.primary() + "]"));
    target.playSound(target, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);

    displayGUI(viewer, target);
  }

  private void _setNavigationReachedRadiusDialogCB(DialogResponseView view, Audience audience, CustomGUI parentMenu,
      Player target) {
    Player viewer = (Player) audience;
    int radius = Math.round(view.getFloat("radius"));

    _settingsManager.setPlayerNavigationReachedRadius(target.getUniqueId(), radius);

    target.sendMessage(Main.getPrefix() + Main.tr(target, "settings-navigation-radius-set-message", "radius", radius));
    target.playSound(target, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);

    displayGUI(viewer, target);
  }

  private void _clearPlayerTag(Player target) {
    _tagManager.removeTag(target);
    _nameTagManager.updateNameTag(target);
    _tabListManager.refreshPlayerListEntry(target);

    target.sendMessage(Main.getPrefix() + Theme.primary() + Main.tr(target, "settings-tag-cleared-message"));
  }

  private Map<String, String> _buildTagColorOptions(Player viewer) {
    Map<String, String> options = new LinkedHashMap<>();
    for (Map.Entry<String, ChatColor> entry : Theme.minecraftColors().entrySet()) {
      String translationKey = "enum-chat-color-" + entry.getKey().toLowerCase().replace('_', '-');
      String name = _t(viewer, translationKey);
      options.put(entry.getKey(), entry.getValue() + name);
    }

    return options;
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
      String locationText = Theme.highlight() + Main.tr(p, "coordinate-label-x") + " " + Theme.primary() + blockLoc.getBlockX()
          + Theme.highlight() + " " + Main.tr(p, "coordinate-label-y") + " " + Theme.primary() + blockLoc.getBlockY()
          + Theme.highlight() + " " + Main.tr(p, "coordinate-label-z") + " " + Theme.primary() + blockLoc.getBlockZ() + Theme.textSymbol()
          + ChatColor.BOLD + " » " + Theme.primary() + biome.getKey();

      _actionBar.sendActionBar(p, locationText);
      newState = true;
    }

    _sendToggleMessage(p, "settings-location-toggle-message", newState);
  }

  private void _toggleActionBarTimer(Player p) {
    boolean newState = !_settingsManager.getPlayerActionBarTimer(p.getUniqueId());
    _settingsManager.setPlayerActionBarTimer(p.getUniqueId(), newState);

    _sendToggleMessage(p, "settings-action-bar-timer-toggle-message", newState);
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

    _sendToggleMessage(p, "settings-compass-toggle-message", newState);
  }

  private void _toggleChestSort(Player p) {
    boolean newState = !_settingsManager.getPlayerChestSort(p.getUniqueId());
    _settingsManager.setPlayerChestSort(p.getUniqueId(), newState);

    _sendToggleMessage(p, "settings-chest-sorting-toggle-message", newState);
  }

  private void _cycleChestSortMode(Player p) {
    InventorySortMode current = _settingsManager.getPlayerChestSortMode(p.getUniqueId());
    InventorySortMode next = current.next();
    _settingsManager.setPlayerChestSortMode(p.getUniqueId(), next);

    p.sendMessage(Main.getPrefix()
        + Main.tr(p, "settings-chest-sorting-mode-message", "mode", next.getLabel(p)));
  }

  private String _t(Player viewer, String key, Object... replacements) {
    return _translations.translate(viewer, key, replacements);
  }

  private String _state(Player viewer, boolean enabled) {
    return (enabled ? Theme.highlight() : Theme.error())
        + _t(viewer, enabled ? "common-state-enabled" : "common-state-disabled");
  }

  private void _sendToggleMessage(Player player, String key, boolean enabled) {
    player.sendMessage(Main.getPrefix() + Main.tr(player, key, "state",
        Theme.highlight().toString() + ChatColor.BOLD
            + Main.tr(player, enabled ? "common-state-enabled" : "common-state-disabled")));
  }

  private void _toggleBackpackSort(Player p) {
    boolean newState = !_settingsManager.getPlayerBackpackSort(p.getUniqueId());
    _settingsManager.setPlayerBackpackSort(p.getUniqueId(), newState);

    _sendToggleMessage(p, "settings-backpack-sorting-toggle-message", newState);
  }

  private void _cycleBackpackSortMode(Player p) {
    InventorySortMode current = _settingsManager.getPlayerBackpackSortMode(p.getUniqueId());
    InventorySortMode next = current.next();
    _settingsManager.setPlayerBackpackSortMode(p.getUniqueId(), next);

    p.sendMessage(Main.getPrefix()
        + Main.tr(p, "settings-backpack-sorting-mode-message", "mode", next.getLabel(p)));
  }

  private void _toggleInventorySort(Player p) {
    boolean newState = !_settingsManager.getPlayerInventorySort(p.getUniqueId());
    _settingsManager.setPlayerInventorySort(p.getUniqueId(), newState);

    _sendToggleMessage(p, "settings-inventory-sorting-toggle-message", newState);
  }

  private void _cycleInventorySortMode(Player p) {
    InventorySortMode current = _settingsManager.getPlayerInventorySortMode(p.getUniqueId());
    InventorySortMode next = current.next();
    _settingsManager.setPlayerInventorySortMode(p.getUniqueId(), next);

    p.sendMessage(Main.getPrefix()
        + Main.tr(p, "settings-inventory-sorting-mode-message", "mode", next.getLabel(p)));
  }

  private void _toggleInventorySortIncludeHotbar(Player p) {
    boolean newState = !_settingsManager.getPlayerInventorySortIncludeHotbar(p.getUniqueId());
    _settingsManager.setPlayerInventorySortIncludeHotbar(p.getUniqueId(), newState);

    p.sendMessage(Main.getPrefix() + Main.tr(p, "settings-inventory-sorting-hotbar-message", "state",
        Main.tr(p, newState ? "common-state-included" : "common-state-excluded")));
  }

  private void _toggleNavigationTrail(Player p) {
    boolean newState = !_settingsManager.getPlayerNavigationTrail(p.getUniqueId());
    _settingsManager.setPlayerNavigationTrail(p.getUniqueId(), newState);

    _sendToggleMessage(p, "settings-navigation-particles-toggle-message", newState);
  }

  private void _toggleNavigationAutoCancel(Player p) {
    boolean newState = !_settingsManager.getPlayerNavigationAutoCancel(p.getUniqueId());
    _settingsManager.setPlayerNavigationAutoCancel(p.getUniqueId(), newState);

    _sendToggleMessage(p, "settings-navigation-auto-cancel-toggle-message", newState);
  }

  private void _toggleVeinMiner(Player p) {
    boolean newState = !_settingsManager.getPlayerVeinMiner(p.getUniqueId());
    _settingsManager.setPlayerVeinMiner(p.getUniqueId(), newState);

    _sendToggleMessage(p, "settings-vein-miner-toggle-message", newState);
  }

  private void _toggleVeinChopper(Player p) {
    boolean newState = !_settingsManager.getPlayerVeinChopper(p.getUniqueId());
    _settingsManager.setPlayerVeinChopper(p.getUniqueId(), newState);

    _sendToggleMessage(p, "settings-vein-chopper-toggle-message", newState);
  }

  private void _toggleDoubleDoor(Player p) {
    boolean newState = !_settingsManager.getPlayerDoubleDoorSync(p.getUniqueId());
    _settingsManager.setPlayerDoubleDoorSync(p.getUniqueId(), newState);

    _sendToggleMessage(p, "settings-double-door-toggle-message", newState);
  }

  private void _toggleItemRestock(Player p) {
    boolean newState = !_settingsManager.getPlayerItemRestock(p.getUniqueId());
    _settingsManager.setPlayerItemRestock(p.getUniqueId(), newState);

    _sendToggleMessage(p, "settings-item-restock-toggle-message", newState);
  }

}
