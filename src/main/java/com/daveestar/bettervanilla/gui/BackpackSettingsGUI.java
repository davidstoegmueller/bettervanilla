package com.daveestar.bettervanilla.gui;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.BackpackManager;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.CustomGUI;
import com.daveestar.bettervanilla.utils.Theme;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class BackpackSettingsGUI implements Listener {
  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final BackpackManager _backpackManager;

  public BackpackSettingsGUI() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
    _backpackManager = _plugin.getBackpackManager();
    Bukkit.getPluginManager().registerEvents(this, _plugin);
  }

  public void displayGUI(Player p, CustomGUI parentMenu, Consumer<Player> backAction) {
    Map<String, ItemStack> entries = new HashMap<>();
    entries.put("pages", _createPagesItem());
    entries.put("enabled", _createEnabledItem());
    entries.put("rows", _createRowsItem());

    Map<String, Integer> customSlots = new HashMap<>();
    customSlots.put("pages", 2);
    customSlots.put("rows", 6);
    customSlots.put("enabled", 4);

    CustomGUI gui = new CustomGUI(_plugin, p,
        Theme.titlePrefix() + "Backpack Settings",
        entries, 2, customSlots, parentMenu,
        EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    if (backAction != null) {
      gui.setBackAction(backAction);
    }

    Map<String, CustomGUI.ClickAction> actions = new HashMap<>();

    actions.put("pages", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        int newPages = _settingsManager.getBackpackPages() + 1;
        _settingsManager.setBackpackPages(newPages);
        _backpackManager.setPages(newPages);
        displayGUI(p, parentMenu, backAction);
      }

      @Override
      public void onRightClick(Player p) {
        int newPages = Math.max(1, _settingsManager.getBackpackPages() - 1);
        _settingsManager.setBackpackPages(newPages);
        _backpackManager.setPages(newPages);
        displayGUI(p, parentMenu, backAction);
      }
    });
    actions.put("enabled", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleEnabled(p);
        displayGUI(p, parentMenu, backAction);
      }
    });
    actions.put("rows", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        int newRows = Math.min(6, _settingsManager.getBackpackRows() + 1);
        _settingsManager.setBackpackRows(newRows);
        _backpackManager.setRows(newRows);
        displayGUI(p, parentMenu, backAction);
      }

      @Override
      public void onRightClick(Player p) {
        int newRows = Math.max(1, _settingsManager.getBackpackRows() - 1);
        _settingsManager.setBackpackRows(newRows);
        _backpackManager.setRows(newRows);
        displayGUI(p, parentMenu, backAction);
      }
    });

    gui.setClickActions(actions);
    gui.open(p);
  }

  private ItemStack _createPagesItem() {
    int pages = _settingsManager.getBackpackPages();
    ItemStack item = new ItemStack(Material.PAPER);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(Theme.titlePrefix() + "Backpack Pages"));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + "Set number of pages.",
          Theme.textPrefix() + Theme.error() + ChatColor.BOLD + "ATTENTION: " + ChatColor.RESET + Theme.primary()
              + "Changing this can cause items in backpacks to be lost!",
          "",
          Theme.textPrefix() + "Current: " + Theme.highlight() + pages + Theme.primary() + " pages",
          "",
          Theme.textPrefix() + "Left-Click: +1 Page",
          Theme.textPrefix() + "Right-Click: -1 Page")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createEnabledItem() {
    boolean state = _settingsManager.getBackpackEnabled();
    ItemStack item = new ItemStack(Material.BARREL);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(Theme.titlePrefix() + "Backpacks"));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + "Enable player backpacks.",
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

  private ItemStack _createRowsItem() {
    int rows = _settingsManager.getBackpackRows();
    ItemStack item = new ItemStack(Material.CHEST);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(Theme.titlePrefix() + "Backpack Rows"));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + "Set number of rows per page.",
          Theme.textPrefix() + Theme.error() + ChatColor.BOLD + "ATTENTION: " + ChatColor.RESET + Theme.primary()
              + "Changing this can cause items in backpacks to be lost!",
          "",
          Theme.textPrefix() + "Current: " + Theme.highlight() + rows + Theme.primary() + " rows",
          "",
          Theme.textPrefix() + "Left-Click: +1 Row",
          Theme.textPrefix() + "Right-Click: -1 Row")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private void _toggleEnabled(Player p) {
    boolean newState = !_settingsManager.getBackpackEnabled();
    _settingsManager.setBackpackEnabled(newState);
    _backpackManager.setEnabled(newState);

    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Backpacks are now " + Theme.highlight() + ChatColor.BOLD + stateText);
  }
}
