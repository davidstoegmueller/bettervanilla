package com.daveestar.bettervanilla.gui;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
import com.daveestar.bettervanilla.manager.NavigationManager;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.ActionBar;
import com.daveestar.bettervanilla.utils.CustomGUI;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class SettingsGUI {
  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final NavigationManager _navigationManager;
  private final CompassManager _compassManager;
  private final ActionBar _actionBar;
  private final AdminSettingsGUI _adminSettingsGUI;

  public SettingsGUI() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
    _navigationManager = _plugin.getNavigationManager();
    _compassManager = _plugin.getCompassManager();
    _actionBar = _plugin.getActionBar();
    _adminSettingsGUI = new AdminSettingsGUI();
  }

  public void displayGUI(Player p) {
    boolean showAdminSettings = p.hasPermission(Permissions.ADMINSETTINGS.getName());
    // two entry rows for admins, one for normal players (plus navigation row)
    int rows = showAdminSettings ? 4 : 3;

    Map<String, ItemStack> entries = new HashMap<>();
    // first row
    entries.put("togglelocation", _createToggleLocationItem(p));
    entries.put("togglecompass", _createToggleCompassItem(p));
    entries.put("navigationtrail", _createNavigationTrailItem(p));
    entries.put("chestsort", _createChestSortItem(p));

    // second row
    entries.put("veinminer", _createVeinMinerItem(p));
    entries.put("veinchopper", _createVeinChopperItem(p));

    // thrid row
    if (showAdminSettings) {
      entries.put("adminsettings", _createAdminSettingsItem());
    }

    Map<String, Integer> customSlots = new HashMap<>();
    // first row
    customSlots.put("togglelocation", 1);
    customSlots.put("togglecompass", 3);
    customSlots.put("navigationtrail", 5);
    customSlots.put("chestsort", 7);

    customSlots.put("veinminer", 11);
    customSlots.put("veinchopper", 15);

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
          p.sendMessage(
              Main.getPrefix() + ChatColor.RED + "You do not have permission to toggle the Action-Bar location.");
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleLocation(p);
        displayGUI(p);
      }
    });

    clickActions.put("togglecompass", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        if (!p.hasPermission(Permissions.TOGGLECOMPASS.getName())) {
          p.sendMessage(Main.getPrefix() + ChatColor.RED
              + "You do not have permission to toggle the Bossbar-Compass.");
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
          p.sendMessage(
              Main.getPrefix() + ChatColor.RED + "You do not have permission to toggle chest sorting.");
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
          p.sendMessage(Main.getPrefix() + ChatColor.RED + "You do not have permission to toggle Vein Miner.");
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
          p.sendMessage(Main.getPrefix() + ChatColor.RED + "You do not have permission to toggle Vein Chopper.");
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

    if (showAdminSettings) {
      clickActions.put("adminsettings", new CustomGUI.ClickAction() {
        @Override
        public void onLeftClick(Player p) {
          _adminSettingsGUI.displayGUI(p, gui);
        }
      });
    }

    gui.setClickActions(clickActions);
    gui.open(p);
  }

  private ItemStack _createToggleLocationItem(Player p) {
    boolean state = _settingsManager.getToggleLocation(p.getUniqueId());
    ItemStack item = new ItemStack(Material.FILLED_MAP);
    ItemMeta meta = item.getItemMeta();

    boolean hasPermission = p.hasPermission(Permissions.TOGGLELOCATION.getName());

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Action-Bar Location"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Show your current location in the actionbar.",
          (!hasPermission ? ChatColor.RED + "You do not have permission for this setting." : null),
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).toList());
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
          (!hasPermission ? ChatColor.RED + "You do not have permission for this setting." : null),
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
    boolean state = _settingsManager.getChestSort(p.getUniqueId());
    ItemStack item = new ItemStack(Material.CHEST);
    ItemMeta meta = item.getItemMeta();

    boolean hasPermission = p.hasPermission(Permissions.CHESTSORT.getName());

    meta.displayName(
        Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Chest Sorting"));
    meta.lore(Arrays.asList(
        ChatColor.YELLOW + "» " + ChatColor.GRAY + "Right-Click outside of a chest inventory to sort it!",
        (!hasPermission ? ChatColor.RED + "You do not have permission for this setting." : null),
        "",
        ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
            + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
        ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
        .stream().filter(Objects::nonNull).map(Component::text).toList());
    item.setItemMeta(meta);

    return item;
  }

  private ItemStack _createNavigationTrailItem(Player p) {
    boolean state = _settingsManager.getNavigationTrail(p.getUniqueId());
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
          (!hasPermission ? ChatColor.RED + "You do not have permission for this setting."
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
          (!hasPermission ? ChatColor.RED + "You do not have permission for this setting."
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

  private void _toggleLocation(Player p) {
    boolean newState;

    if (_settingsManager.getToggleLocation(p.getUniqueId())) {
      _settingsManager.setToggleLocation(p.getUniqueId(), false);
      _actionBar.removeActionBar(p);
      newState = false;
    } else {
      _navigationManager.stopNavigation(p);
      _settingsManager.setToggleLocation(p.getUniqueId(), true);

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

  private void _toggleCompass(Player p) {
    boolean currentlyActive = _compassManager.checkPlayerActiveCompass(p);
    boolean newState = !currentlyActive;

    if (currentlyActive) {
      _compassManager.removePlayerFromCompass(p);
    } else {
      _compassManager.addPlayerToCompass(p);
    }

    _settingsManager.setToggleCompass(p.getUniqueId(), newState);

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Bossbar-Compass is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleChestSort(Player p) {
    boolean newState = !_settingsManager.getChestSort(p.getUniqueId());
    _settingsManager.setChestSort(p.getUniqueId(), newState);

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Chest sorting is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleNavigationTrail(Player p) {
    boolean newState = !_settingsManager.getNavigationTrail(p.getUniqueId());
    _settingsManager.setNavigationTrail(p.getUniqueId(), newState);

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
}
