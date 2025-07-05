package com.daveestar.bettervanilla.gui;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.daveestar.bettervanilla.Main;
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
    boolean isAdmin = p.hasPermission("bettervanilla.adminsettings");
    // two entry rows for admins, one for normal players (plus navigation row)
    int rows = isAdmin ? 3 : 2;

    Map<String, ItemStack> entries = new HashMap<>();
    entries.put("togglelocation", _createToggleLocationItem(p));
    entries.put("togglecompass", _createToggleCompassItem(p));

    if (isAdmin) {
      entries.put("adminsettings", _createAdminSettingsItem());
    }

    Map<String, Integer> customSlots = new HashMap<>();
    customSlots.put("togglelocation", 2);
    customSlots.put("togglecompass", 6);

    if (isAdmin) {
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
        if (!p.hasPermission("bettervanilla.togglelocation")) {
          p.sendMessage(
              Main.getPrefix() + ChatColor.RED + "You do not have permission to toggle the Action-Bar location.");
          p.playSound(p, org.bukkit.Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleLocation(p);
        displayGUI(p);
      }
    });

    clickActions.put("togglecompass", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        if (!p.hasPermission("bettervanilla.togglecompass")) {
          p.sendMessage(Main.getPrefix() + ChatColor.RED
              + "You do not have permission to toggle the Bossbar-Compass.");
          p.playSound(p, org.bukkit.Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }

        _toggleCompass(p);
        displayGUI(p);
      }
    });

    if (isAdmin) {
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
    boolean state = _settingsManager.getToggleLocation(p);
    ItemStack item = new ItemStack(Material.FILLED_MAP);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Action-Bar Location"));
      meta.lore(java.util.Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().map(Component::text).toList());
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createToggleCompassItem(Player p) {
    boolean state = _compassManager.checkPlayerActiveCompass(p);
    ItemStack item = new ItemStack(Material.COMPASS);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Bossbar Compass"));
      meta.lore(java.util.Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().map(Component::text).toList());
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
      meta.lore(java.util.Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Open admin settings")
          .stream().map(Component::text).toList());
      item.setItemMeta(meta);
    }

    return item;
  }

  private void _toggleLocation(Player p) {
    boolean newState;

    if (_settingsManager.getToggleLocation(p)) {
      _settingsManager.setToggleLocation(p, false);
      _actionBar.removeActionBar(p);
      newState = false;
    } else {
      _navigationManager.stopNavigation(p);
      _settingsManager.setToggleLocation(p, true);

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

    _settingsManager.setToggleCompass(p, newState);

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Bossbar-Compass is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }
}
