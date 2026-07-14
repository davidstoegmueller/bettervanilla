package com.daveestar.bettervanilla.gui;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.TimerManager;
import com.daveestar.bettervanilla.utils.CustomGUI;
import com.daveestar.bettervanilla.utils.Theme;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class PlayTimeGUI {
  private final Main _plugin;
  private final TimerManager _timerManager;

  public PlayTimeGUI() {
    _plugin = Main.getInstance();
    _timerManager = _plugin.getTimerManager();
  }

  public void displayGUI(Player p) {
    Map<String, ItemStack> entries = new LinkedHashMap<>();
    Map<String, CustomGUI.ClickAction> actions = new HashMap<>();
    Map<String, PlaytimeSortData> sortData = new HashMap<>();

    String selfKey = p.getUniqueId().toString();
    entries.put(selfKey, _createPlayerItem(p, p));
    sortData.put(selfKey, _buildPlaytimeSortData(p));
    actions.put(selfKey, new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _sendPlaytimeMessage(p, p);
      }
    });

    for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
      if (op.getUniqueId().equals(p.getUniqueId()))
        continue;

      if (op.getName() == null)
        continue;

      String key = op.getUniqueId().toString();
      entries.put(key, _createPlayerItem(p, op));
      sortData.put(key, _buildPlaytimeSortData(op));
      actions.put(key, new CustomGUI.ClickAction() {
        @Override
        public void onLeftClick(Player p) {
          _sendPlaytimeMessage(p, op);
        }
      });
    }

    CustomGUI gui = new CustomGUI(_plugin, p,
        Theme.titlePrefix() + Main.tr(p, "gui-playtime-title"),
        entries, 3, null, null, EnumSet.of(CustomGUI.Option.ENABLE_SEARCH, CustomGUI.Option.ENABLE_SORT));

    gui.setSearchButtonSlot(_footerSearchSlot(3));

    gui.setSortOptions(_createPlaytimeSortOptions(p, sortData));
    gui.setSortButtonSlot(_footerSortSlot(3));

    gui.setClickActions(actions);
    gui.open(p);
  }

  private int _footerSearchSlot(int rows) {
    return (rows * 9) - 9 + 7;
  }

  private int _footerSortSlot(int rows) {
    return (rows * 9) - 9 + 6;
  }

  private void _sendPlaytimeMessage(Player viewer, OfflinePlayer target) {
    int playTime = _timerManager.getPlayTime(target.getUniqueId());
    int afkTime = _timerManager.getAFKTime(target.getUniqueId());

    String playerName = target.getName() != null ? target.getName() : Main.tr(viewer, "common-value-unknown");
    viewer.sendMessage(Main.getPrefix() + Theme.highlight() + ChatColor.BOLD
        + Main.tr(viewer, "gui-playtime-summary-title",
            "player", ChatColor.RESET + "" + Theme.primary() + playerName));
    viewer.sendMessage(Main.getShortPrefix() + Main.tr(viewer, "playtime-total",
        "time", Theme.highlight() + _timerManager.formatTime(viewer, playTime + afkTime)));
    viewer.sendMessage(Main.getShortPrefix() + Main.tr(viewer, "playtime-active",
        "time", Theme.highlight() + _timerManager.formatTime(viewer, playTime)));
    viewer.sendMessage(Main.getShortPrefix() + Main.tr(viewer, "playtime-afk",
        "time", Theme.highlight() + _timerManager.formatTime(viewer, afkTime)));
  }

  private ItemStack _createPlayerItem(Player viewer, OfflinePlayer op) {
    int playTime = _timerManager.getPlayTime(op.getUniqueId());
    int afkTime = _timerManager.getAFKTime(op.getUniqueId());

    ItemStack item = new ItemStack(Material.PLAYER_HEAD);
    ItemMeta meta = item.getItemMeta();

    if (meta instanceof SkullMeta skullMeta) {
      skullMeta.setOwningPlayer(op);

      String status = Main.tr(viewer, "gui-playtime-status-format",
          "status", (op.isOnline() ? Theme.highlight() : Theme.error())
              + Main.tr(viewer, op.isOnline() ? "gui-playtime-status-online" : "gui-playtime-status-offline")
              + Theme.primary());
      String playerName = op.getName() != null ? op.getName() : Main.tr(viewer, "common-value-unknown");

      skullMeta.displayName(
          Component.text(
              Theme.titlePrefix()
                  + playerName + Theme.primary() + " " + status));

      skullMeta.lore(
          Arrays.asList(
              "",
              Theme.textPrefix() + Main.tr(viewer, "playtime-total",
                  "time", Theme.highlight() + _timerManager.formatTime(viewer, playTime + afkTime)),
              Theme.textPrefix() + Main.tr(viewer, "playtime-active",
                  "time", Theme.highlight() + _timerManager.formatTime(viewer, playTime)),
              Theme.textPrefix() + Main.tr(viewer, "playtime-afk",
                  "time", Theme.highlight() + _timerManager.formatTime(viewer, afkTime)),
              "",
              Theme.textPrefix() + Main.tr(viewer, "gui-playtime-action-show-summary")).stream()
              .filter(Objects::nonNull)
              .map(Component::text)
              .collect(Collectors.toList()));

      item.setItemMeta(skullMeta);
    }

    return item;
  }

  // ---------
  // SORTING
  // ---------

  private PlaytimeSortData _buildPlaytimeSortData(OfflinePlayer op) {
    int playTime = _timerManager.getPlayTime(op.getUniqueId());
    int afkTime = _timerManager.getAFKTime(op.getUniqueId());
    long totalTime = (long) playTime + afkTime;
    String name = op.getName() != null ? op.getName() : "";
    return new PlaytimeSortData(name, playTime, afkTime, totalTime);
  }

  private List<CustomGUI.SortOption> _createPlaytimeSortOptions(Player viewer,
      Map<String, PlaytimeSortData> sortData) {
    Comparator<Map.Entry<String, ItemStack>> byTotalDesc = Comparator.<Map.Entry<String, ItemStack>>comparingLong(
        entry -> sortData.get(entry.getKey()).totalTime())
        .reversed();

    Comparator<Map.Entry<String, ItemStack>> byTotalAsc = Comparator.<Map.Entry<String, ItemStack>>comparingLong(
        entry -> sortData.get(entry.getKey()).totalTime());

    Comparator<Map.Entry<String, ItemStack>> byPlayDesc = Comparator.<Map.Entry<String, ItemStack>>comparingLong(
        entry -> sortData.get(entry.getKey()).playTime())
        .reversed();

    Comparator<Map.Entry<String, ItemStack>> byPlayAsc = Comparator.<Map.Entry<String, ItemStack>>comparingLong(
        entry -> sortData.get(entry.getKey()).playTime());

    Comparator<Map.Entry<String, ItemStack>> byAfkDesc = Comparator.<Map.Entry<String, ItemStack>>comparingLong(
        entry -> sortData.get(entry.getKey()).afkTime())
        .reversed();

    Comparator<Map.Entry<String, ItemStack>> byAfkAsc = Comparator.<Map.Entry<String, ItemStack>>comparingLong(
        entry -> sortData.get(entry.getKey()).afkTime());

    Comparator<Map.Entry<String, ItemStack>> byNameAsc = Comparator.<Map.Entry<String, ItemStack>, String>comparing(
        entry -> sortData.get(entry.getKey()).name().toLowerCase());

    Comparator<Map.Entry<String, ItemStack>> byNameDesc = byNameAsc.reversed();

    return List.of(
        new CustomGUI.SortOption(Main.tr(viewer, "gui-playtime-sort-total-descending"), byTotalDesc),
        new CustomGUI.SortOption(Main.tr(viewer, "gui-playtime-sort-total-ascending"), byTotalAsc),
        new CustomGUI.SortOption(Main.tr(viewer, "gui-playtime-sort-active-descending"), byPlayDesc),
        new CustomGUI.SortOption(Main.tr(viewer, "gui-playtime-sort-active-ascending"), byPlayAsc),
        new CustomGUI.SortOption(Main.tr(viewer, "gui-playtime-sort-afk-descending"), byAfkDesc),
        new CustomGUI.SortOption(Main.tr(viewer, "gui-playtime-sort-afk-ascending"), byAfkAsc),
        new CustomGUI.SortOption(Main.tr(viewer, "gui-common-sort-name-ascending"), byNameAsc),
        new CustomGUI.SortOption(Main.tr(viewer, "gui-common-sort-name-descending"), byNameDesc));
  }

  private record PlaytimeSortData(String name, long playTime, long afkTime, long totalTime) {
  }
}
