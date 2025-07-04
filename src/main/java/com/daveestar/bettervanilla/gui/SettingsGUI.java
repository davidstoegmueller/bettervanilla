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

    Map<String, CustomGUI.ClickAction> actions = new HashMap<>();
    actions.put("togglelocation", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player player) {
        if (!player.hasPermission("bettervanilla.togglelocation")) {
          player.playSound(player, org.bukkit.Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }
        _toggleLocation(player);
        displayGUI(player);
      }
    });

    actions.put("togglecompass", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player player) {
        if (!player.hasPermission("bettervanilla.togglecompass")) {
          player.playSound(player, org.bukkit.Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          return;
        }
        _toggleCompass(player);
        displayGUI(player);
      }
    });

    if (isAdmin) {
      actions.put("adminsettings", new CustomGUI.ClickAction() {
        @Override
        public void onLeftClick(Player player) {
          _adminSettingsGUI.displayGUI(player, gui);
        }
      });
    }

    gui.setClickActions(actions);
    gui.open(p);
  }

  private ItemStack _createToggleLocationItem(Player p) {
    boolean state = _settingsManager.getToggleLocation(p);
    ItemStack item = new ItemStack(Material.FILLED_MAP);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Action-Bar Location"));
      meta.lore(java.util.Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: " + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
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
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Bossbar Compass"));
      meta.lore(java.util.Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: " + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
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
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Admin Settings"));
      meta.lore(java.util.Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Open global settings")
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
      Biome playerBiome = p.getWorld().getBiome(p.getLocation().toBlockLocation());
      String locationText = ChatColor.YELLOW + "X: " + ChatColor.GRAY + p.getLocation().toBlockLocation().getBlockX()
          + ChatColor.YELLOW + " Y: " + ChatColor.GRAY + p.getLocation().toBlockLocation().getBlockY()
          + ChatColor.YELLOW + " Z: " + ChatColor.GRAY + p.getLocation().toBlockLocation().getBlockZ() + ChatColor.RED
          + ChatColor.BOLD + " » " + ChatColor.GRAY + playerBiome.getKey();
      _actionBar.sendActionBar(p, locationText);
      newState = true;
    }
    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Action-Bar location is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleCompass(Player p) {
    boolean newState;
    if (_compassManager.checkPlayerActiveCompass(p)) {
      _compassManager.removePlayerFromCompass(p);
      _settingsManager.setToggleCompass(p, false);
      newState = false;
    } else {
      _compassManager.addPlayerToCompass(p);
      _settingsManager.setToggleCompass(p, true);
      newState = true;
    }
    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Bossbar compass is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }
}
