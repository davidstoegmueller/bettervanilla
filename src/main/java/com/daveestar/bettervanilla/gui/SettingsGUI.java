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

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class SettingsGUI {
  private static final List<TagColorOption> TAG_COLORS = List.of(
      new TagColorOption("BLACK", ChatColor.BLACK),
      new TagColorOption("DARK_BLUE", ChatColor.DARK_BLUE),
      new TagColorOption("DARK_GREEN", ChatColor.DARK_GREEN),
      new TagColorOption("DARK_AQUA", ChatColor.DARK_AQUA),
      new TagColorOption("DARK_RED", ChatColor.DARK_RED),
      new TagColorOption("DARK_PURPLE", ChatColor.DARK_PURPLE),
      new TagColorOption("GOLD", ChatColor.GOLD),
      new TagColorOption("GRAY", ChatColor.GRAY),
      new TagColorOption("DARK_GRAY", ChatColor.DARK_GRAY),
      new TagColorOption("BLUE", ChatColor.BLUE),
      new TagColorOption("GREEN", ChatColor.GREEN),
      new TagColorOption("AQUA", ChatColor.AQUA),
      new TagColorOption("RED", ChatColor.RED),
      new TagColorOption("LIGHT_PURPLE", ChatColor.LIGHT_PURPLE),
      new TagColorOption("YELLOW", ChatColor.YELLOW),
      new TagColorOption("WHITE", ChatColor.WHITE));
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
    boolean showAdminSettings = p.hasPermission(Permissions.ADMINSETTINGS.getName());
    // two entry rows for admins, one for normal players (plus navigation row)
    int rows = showAdminSettings ? 5 : 4;

    Map<String, ItemStack> entries = new HashMap<>();
    // first row
    entries.put("togglelocation", _createToggleLocationItem(p));
    entries.put("togglecompass", _createToggleCompassItem(p));
    entries.put("navigationtrail", _createNavigationTrailItem(p));
    entries.put("chestsort", _createChestSortItem(p));

    // second row
    entries.put("veinminer", _createVeinMinerItem(p));
    entries.put("veinchopper", _createVeinChopperItem(p));
    entries.put("doubledoor", _createDoubleDoorItem(p));
    entries.put("itemrestock", _createItemRestockItem(p));

    // third row
    entries.put("actionbartimer", _createActionBarTimerItem(p));
    entries.put("playertag", _createPlayerTagItem(p));

    // fourth row
    if (showAdminSettings) {
      entries.put("adminsettings", _createAdminSettingsItem());
    }

    Map<String, Integer> customSlots = new HashMap<>();
    // first row
    customSlots.put("togglelocation", 1);
    customSlots.put("togglecompass", 3);
    customSlots.put("navigationtrail", 5);
    customSlots.put("chestsort", 7);

    // second row
    customSlots.put("veinminer", 11);
    customSlots.put("veinchopper", 15);
    customSlots.put("doubledoor", 13);
    customSlots.put("itemrestock", 9);

    // third row
    customSlots.put("actionbartimer", 21);
    customSlots.put("playertag", 23);

    // fourth row
    if (showAdminSettings) {
      customSlots.put("adminsettings", rows * 9 - 10);
    }

    CustomGUI gui = new CustomGUI(_plugin, p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Settings",
        entries, rows, customSlots, null,
        EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    Map<String, CustomGUI.ClickAction> clickActions = new HashMap<>();
    clickActions.put("togglelocation", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        if (!p.hasPermission(Permissions.TOGGLELOCATION.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.TOGGLELOCATION));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleLocation(p);
        displayGUI(p);
      }
    });

    clickActions.put("itemrestock", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        if (!p.hasPermission(Permissions.ITEM_RESTOCK.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.ITEM_RESTOCK));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        if (!_settingsManager.getItemRestockEnabled()) {
          p.sendMessage(Main.getPrefix() + ChatColor.RED + "Item Restock is globally disabled on the server.");
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleItemRestock(p);
        displayGUI(p);
      }
    });

    clickActions.put("actionbartimer", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        if (!p.hasPermission(Permissions.ACTIONBAR_TIMER.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.ACTIONBAR_TIMER));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        if (!_settingsManager.getActionBarTimerEnabled()) {
          p.sendMessage(Main.getPrefix() + ChatColor.RED + "Action-Bar timer is globally disabled on the server.");
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleActionBarTimer(p);
        displayGUI(p);
      }
    });

    clickActions.put("playertag", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        if (!p.hasPermission(Permissions.TAG.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.TAG));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        if (!_settingsManager.getTagsEnabled()) {
          p.sendMessage(Main.getPrefix() + ChatColor.RED + "Tags are globally disabled on the server.");
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _openPlayerTagDialog(p, gui, null);
      }

      @Override
      public void onRightClick(Player p) {
        if (!p.hasPermission(Permissions.TAG.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.TAG));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _clearPlayerTag(p);
        displayGUI(p);
      }
    });

    clickActions.put("togglecompass", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        if (!p.hasPermission(Permissions.TOGGLECOMPASS.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.TOGGLECOMPASS));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleCompass(p);
        displayGUI(p);
      }
    });

    clickActions.put("chestsort", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {

        if (!p.hasPermission(Permissions.CHESTSORT.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.CHESTSORT));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleChestSort(p);
        displayGUI(p);
      }
    });
    clickActions.put("navigationtrail", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleNavigationTrail(p);
        displayGUI(p);
      }
    });

    clickActions.put("veinminer", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        if (!p.hasPermission(Permissions.VEINMINER.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.VEINMINER));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        boolean globalState = _settingsManager.getVeinMinerEnabled();
        if (!globalState) {
          p.sendMessage(Main.getPrefix() + ChatColor.RED + "Vein Miner is globally disabled on the server.");
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleVeinMiner(p);
        displayGUI(p);
      }
    });

    clickActions.put("veinchopper", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        if (!p.hasPermission(Permissions.VEINCHOPPER.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.VEINCHOPPER));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        boolean globalState = _settingsManager.getVeinChopperEnabled();
        if (!globalState) {
          p.sendMessage(Main.getPrefix() + ChatColor.RED + "Vein Chopper is globally disabled on the server.");
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleVeinChopper(p);
        displayGUI(p);
      }
    });

    clickActions.put("doubledoor", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        if (!p.hasPermission(Permissions.DOUBLE_DOOR.getName())) {
          p.sendMessage(Main.getNoPermissionMessage(Permissions.DOUBLE_DOOR));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleDoubleDoor(p);
        displayGUI(p);
      }
    });

    if (showAdminSettings) {
      clickActions.put("adminsettings", new CustomGUI.ClickAction() {
        @Override
        public void onLeftClick(Player p) {
          _adminSettingsGUI.displayGUI(p, gui, player -> displayGUI(player));
        }
      });
    }

    gui.setClickActions(clickActions);
    gui.open(p);
  }

  private ItemStack _createToggleLocationItem(Player p) {
    boolean state = _settingsManager.getPlayerToggleLocation(p.getUniqueId());
    ItemStack item = new ItemStack(Material.FILLED_MAP);
    ItemMeta meta = item.getItemMeta();

    boolean hasPermission = p.hasPermission(Permissions.TOGGLELOCATION.getName());

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Action-Bar Location"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Show your current location in the actionbar.",
          (!hasPermission ? Main.getShortNoPermissionMessage(Permissions.TOGGLELOCATION) : null),
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).toList());
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createActionBarTimerItem(Player p) {
    boolean state = _settingsManager.getPlayerActionBarTimer(p.getUniqueId());
    ItemStack item = new ItemStack(Material.CLOCK);
    ItemMeta meta = item.getItemMeta();

    boolean globalState = _settingsManager.getActionBarTimerEnabled();
    boolean hasPermission = p.hasPermission(Permissions.ACTIONBAR_TIMER.getName());

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Action-Bar Timer"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Show the server timer in the actionbar.",
          (!hasPermission ? Main.getShortNoPermissionMessage(Permissions.ACTIONBAR_TIMER)
              : !globalState ? ChatColor.RED + "Action-Bar timer is globally disabled on the server." : null),
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createPlayerTagItem(Player p) {
    String tagName = _tagManager.getTag(p);
    ChatColor tagColor = _tagManager.getTagColor(p);
    boolean hasPermission = p.hasPermission(Permissions.TAG.getName());
    boolean globalEnabled = _settingsManager.getTagsEnabled();

    ItemStack item = new ItemStack(Material.NAME_TAG);
    ItemMeta meta = item.getItemMeta();

    String tagDisplay = (tagName == null || tagName.isEmpty())
        ? ChatColor.RED + "None"
        : (tagColor != null ? tagColor : ChatColor.AQUA) + tagName;

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Player Tag"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Set the tag displayed with your name.",
          (!hasPermission ? Main.getShortNoPermissionMessage(Permissions.TAG)
              : !globalEnabled ? ChatColor.RED + "Tags are globally disabled on the server." : null),
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Tag: " + ChatColor.GRAY + "[" + tagDisplay + ChatColor.GRAY + "]",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Set Tag",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Right-Click: Clear Tag")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createToggleCompassItem(Player p) {
    boolean state = _compassManager.checkPlayerActiveCompass(p);
    ItemStack item = new ItemStack(Material.COMPASS);
    ItemMeta meta = item.getItemMeta();

    boolean hasPermission = p.hasPermission(Permissions.TOGGLECOMPASS.getName());

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Bossbar Compass"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Shows a compass in the bossbar",
          (!hasPermission ? Main.getShortNoPermissionMessage(Permissions.TOGGLECOMPASS) : null),
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).toList());
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createChestSortItem(Player p) {
    boolean state = _settingsManager.getPlayerChestSort(p.getUniqueId());
    ItemStack item = new ItemStack(Material.CHEST);
    ItemMeta meta = item.getItemMeta();

    boolean hasPermission = p.hasPermission(Permissions.CHESTSORT.getName());

    meta.displayName(
        Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Chest Sorting"));
    meta.lore(Arrays.asList(
        ChatColor.YELLOW + "» " + ChatColor.GRAY + "Right-Click outside of a chest inventory to sort it!",
        (!hasPermission ? Main.getShortNoPermissionMessage(Permissions.CHESTSORT) : null),
        "",
        ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
            + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
        ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
        .stream().filter(Objects::nonNull).map(Component::text).toList());
    item.setItemMeta(meta);

    return item;
  }

  private ItemStack _createNavigationTrailItem(Player p) {
    boolean state = _settingsManager.getPlayerNavigationTrail(p.getUniqueId());
    ItemStack item = new ItemStack(Material.BLAZE_POWDER);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Navigation Particles"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Show particle trails when navigating to a location.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).toList());
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createVeinMinerItem(Player p) {
    boolean state = _settingsManager.getPlayerVeinMiner(p.getUniqueId());
    ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
    ItemMeta meta = item.getItemMeta();

    boolean globalState = _settingsManager.getVeinMinerEnabled();
    boolean hasPermission = p.hasPermission(Permissions.VEINMINER.getName());

    if (meta != null) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Vein Miner"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY
              + "While sneaking, mine all ores of the same type if using a pickaxe.",
          (!hasPermission ? Main.getShortNoPermissionMessage(Permissions.VEINMINER)
              : !globalState ? ChatColor.RED + "Vein Miner is globally disabled on the server." : null),
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createVeinChopperItem(Player p) {
    boolean state = _settingsManager.getPlayerVeinChopper(p.getUniqueId());
    ItemStack item = new ItemStack(Material.DIAMOND_AXE);
    ItemMeta meta = item.getItemMeta();

    boolean globalState = _settingsManager.getVeinChopperEnabled();
    boolean hasPermission = p.hasPermission(Permissions.VEINCHOPPER.getName());

    if (meta != null) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Vein Chopper"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "While sneaking, chop all logs of the same type if using an axe.",
          (!hasPermission ? Main.getShortNoPermissionMessage(Permissions.VEINCHOPPER)
              : !globalState ? ChatColor.RED + "Vein Chopper is globally disabled on the server." : null),
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createItemRestockItem(Player p) {
    boolean state = _settingsManager.getPlayerItemRestock(p.getUniqueId());
    ItemStack item = new ItemStack(Material.HOPPER);
    ItemMeta meta = item.getItemMeta();

    boolean globalState = _settingsManager.getItemRestockEnabled();
    boolean hasPermission = p.hasPermission(Permissions.ITEM_RESTOCK.getName());

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Item Restock"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Refill your hotbar slot with matching items automatically.",
          (!hasPermission ? Main.getShortNoPermissionMessage(Permissions.ITEM_RESTOCK)
              : !globalState ? ChatColor.RED + "Item Restock is globally disabled on the server." : null),
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createDoubleDoorItem(Player p) {
    boolean state = _settingsManager.getPlayerDoubleDoorSync(p.getUniqueId());
    ItemStack item = new ItemStack(Material.OAK_DOOR);
    ItemMeta meta = item.getItemMeta();

    boolean hasPermission = p.hasPermission(Permissions.DOUBLE_DOOR.getName());

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Double Door Sync"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Interact with one door to toggle the paired door.",
          (!hasPermission ? Main.getShortNoPermissionMessage(Permissions.DOUBLE_DOOR) : null),
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
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
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Admin Settings"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Manage admin and server settings.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Open")
          .stream().filter(Objects::nonNull).map(Component::text).toList());
      item.setItemMeta(meta);
    }

    return item;
  }

  private void _openPlayerTagDialog(Player p, CustomGUI parentMenu, String errorMessage) {
    String currentName = Optional.ofNullable(_tagManager.getTag(p)).orElse("");
    ChatColor currentColor = _tagManager.getTagColor(p);
    String currentColorName = _getTagColorKey(currentColor);

    DialogInput inputName = CustomDialog.createTextInput("tagname",
        ChatColor.YELLOW + "» " + ChatColor.GRAY + "Tag Name",
        currentName);

    DialogInput inputColor = CustomDialog.createSelectInput("tagcolor",
        ChatColor.YELLOW + "» " + ChatColor.GRAY + "Tag Color",
        _buildTagColorOptions(),
        currentColorName);

    Dialog dialog = CustomDialog.createConfirmationDialog(
        "Player Tag",
        "Set your tag name and color.",
        errorMessage,
        List.of(inputName, inputColor),
        (view, audience) -> _setPlayerTagDialogCB(view, audience, parentMenu),
        null);

    p.showDialog(dialog);
  }

  private void _setPlayerTagDialogCB(DialogResponseView view, Audience audience, CustomGUI parentMenu) {
    Player p = (Player) audience;
    String name = Optional.ofNullable(view.getText("tagname")).map(String::trim).orElse("");
    String colorKey = Optional.ofNullable(view.getText("tagcolor")).map(String::trim).orElse("AQUA");

    if (name.isEmpty()) {
      _openPlayerTagDialog(p, parentMenu, "Tag name cannot be empty.");
      return;
    }

    if (name.length() > 10) {
      _openPlayerTagDialog(p, parentMenu, "Tag too long! Maximum length is 10 characters.");
      return;
    }

    ChatColor color = _parseTagColor(colorKey);
    _tagManager.setTag(p, name, color);
    _nameTagManager.updateNameTag(p);
    _tabListManager.refreshPlayerListEntry(p);

    p.sendMessage(Main.getPrefix() + ChatColor.GRAY + "Tag set to: " + ChatColor.GRAY + "[" + color + name
        + ChatColor.GRAY + "]");
    p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);

    displayGUI(p);
  }

  private void _clearPlayerTag(Player p) {
    _tagManager.removeTag(p);
    _nameTagManager.updateNameTag(p);
    _tabListManager.refreshPlayerListEntry(p);

    p.sendMessage(Main.getPrefix() + ChatColor.GRAY + "Tag cleared.");
  }

  private Map<String, String> _buildTagColorOptions() {
    Map<String, String> options = new LinkedHashMap<>();
    for (TagColorOption option : TAG_COLORS) {
      String name = _formatColorName(option.key());
      options.put(option.key(), option.color() + name);
    }

    return options;
  }

  private ChatColor _parseTagColor(String colorKey) {
    if (colorKey == null || colorKey.isBlank()) {
      return ChatColor.AQUA;
    }

    for (TagColorOption option : TAG_COLORS) {
      if (option.key().equalsIgnoreCase(colorKey)) {
        return option.color();
      }
    }

    return ChatColor.AQUA;
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

  private String _getTagColorKey(ChatColor color) {
    if (color != null) {
      for (TagColorOption option : TAG_COLORS) {
        if (option.color().equals(color)) {
          return option.key();
        }
      }
    }

    return "AQUA";
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
      String locationText = ChatColor.YELLOW + "X: " + ChatColor.GRAY + blockLoc.getBlockX()
          + ChatColor.YELLOW + " Y: " + ChatColor.GRAY + blockLoc.getBlockY()
          + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + blockLoc.getBlockZ() + ChatColor.RED
          + ChatColor.BOLD + " » " + ChatColor.GRAY + biome.getKey();

      _actionBar.sendActionBar(p, locationText);
      newState = true;
    }

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Action-Bar location is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleActionBarTimer(Player p) {
    boolean newState = !_settingsManager.getPlayerActionBarTimer(p.getUniqueId());
    _settingsManager.setPlayerActionBarTimer(p.getUniqueId(), newState);

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Action-Bar timer is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
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
    p.sendMessage(Main.getPrefix() + "Bossbar-Compass is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleChestSort(Player p) {
    boolean newState = !_settingsManager.getPlayerChestSort(p.getUniqueId());
    _settingsManager.setPlayerChestSort(p.getUniqueId(), newState);

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Chest sorting is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleNavigationTrail(Player p) {
    boolean newState = !_settingsManager.getPlayerNavigationTrail(p.getUniqueId());
    _settingsManager.setPlayerNavigationTrail(p.getUniqueId(), newState);

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Navigation particles are now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleVeinMiner(Player p) {
    boolean newState = !_settingsManager.getPlayerVeinMiner(p.getUniqueId());
    _settingsManager.setPlayerVeinMiner(p.getUniqueId(), newState);

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Vein Miner is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleVeinChopper(Player p) {
    boolean newState = !_settingsManager.getPlayerVeinChopper(p.getUniqueId());
    _settingsManager.setPlayerVeinChopper(p.getUniqueId(), newState);

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Vein Chopper is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleDoubleDoor(Player p) {
    boolean newState = !_settingsManager.getPlayerDoubleDoorSync(p.getUniqueId());
    _settingsManager.setPlayerDoubleDoorSync(p.getUniqueId(), newState);

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(
        Main.getPrefix() + "Double door sync is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleItemRestock(Player p) {
    boolean newState = !_settingsManager.getPlayerItemRestock(p.getUniqueId());
    _settingsManager.setPlayerItemRestock(p.getUniqueId(), newState);

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Item restock is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private record TagColorOption(String key, ChatColor color) {
  }
}
