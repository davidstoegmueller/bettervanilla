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
    entries.put("pages", _createPagesItem(p));
    entries.put("enabled", _createEnabledItem(p));
    entries.put("rows", _createRowsItem(p));

    Map<String, Integer> customSlots = new HashMap<>();
    customSlots.put("pages", 2);
    customSlots.put("rows", 6);
    customSlots.put("enabled", 4);

    CustomGUI gui = new CustomGUI(_plugin, p,
        Theme.titlePrefix() + Main.tr(p, "gui-backpack-settings-title"),
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

  private ItemStack _createPagesItem(Player viewer) {
    int pages = _settingsManager.getBackpackPages();
    ItemStack item = new ItemStack(Material.PAPER);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(Theme.titlePrefix() + Main.tr(viewer, "gui-backpack-settings-pages-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + Main.tr(viewer, "gui-backpack-settings-pages-description"),
          Theme.textPrefix() + Theme.error() + ChatColor.BOLD
              + Main.tr(viewer, "gui-common-warning-label") + ChatColor.RESET + Theme.primary()
              + " " + Main.tr(viewer, "gui-backpack-settings-storage-warning"),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-backpack-settings-pages-current",
              "pages", Theme.highlight() + Integer.toString(pages) + Theme.primary()),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-backpack-settings-pages-action-increase"),
          Theme.textPrefix() + Main.tr(viewer, "gui-backpack-settings-pages-action-decrease"))
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createEnabledItem(Player viewer) {
    boolean state = _settingsManager.getBackpackEnabled();
    ItemStack item = new ItemStack(Material.BARREL);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(Theme.titlePrefix() + Main.tr(viewer, "gui-backpack-settings-enabled-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + Main.tr(viewer, "gui-backpack-settings-enabled-description"),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-common-state",
              "state", _stateText(viewer, state)),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-common-action-toggle"))
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createRowsItem(Player viewer) {
    int rows = _settingsManager.getBackpackRows();
    ItemStack item = new ItemStack(Material.CHEST);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(Theme.titlePrefix() + Main.tr(viewer, "gui-backpack-settings-rows-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + Main.tr(viewer, "gui-backpack-settings-rows-description"),
          Theme.textPrefix() + Theme.error() + ChatColor.BOLD
              + Main.tr(viewer, "gui-common-warning-label") + ChatColor.RESET + Theme.primary()
              + " " + Main.tr(viewer, "gui-backpack-settings-storage-warning"),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-backpack-settings-rows-current",
              "rows", Theme.highlight() + Integer.toString(rows) + Theme.primary()),
          "",
          Theme.textPrefix() + Main.tr(viewer, "gui-backpack-settings-rows-action-increase"),
          Theme.textPrefix() + Main.tr(viewer, "gui-backpack-settings-rows-action-decrease"))
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private void _toggleEnabled(Player p) {
    boolean newState = !_settingsManager.getBackpackEnabled();
    _settingsManager.setBackpackEnabled(newState);
    _backpackManager.setEnabled(newState);

    p.sendMessage(Main.getPrefix() + Main.tr(p, "gui-backpack-settings-enabled-changed",
        "state", Theme.highlight() + "" + ChatColor.BOLD + Main.tr(p,
            newState ? "common-state-enabled" : "common-state-disabled")));
  }

  private String _stateText(Player viewer, boolean state) {
    return (state ? Theme.highlight() : Theme.error())
        + Main.tr(viewer, state ? "common-state-enabled" : "common-state-disabled");
  }
}
