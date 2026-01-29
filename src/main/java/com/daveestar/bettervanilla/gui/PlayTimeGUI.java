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
    entries.put(selfKey, _createPlayerItem(p));
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
      entries.put(key, _createPlayerItem(op));
      sortData.put(key, _buildPlaytimeSortData(op));
      actions.put(key, new CustomGUI.ClickAction() {
        @Override
        public void onLeftClick(Player p) {
          _sendPlaytimeMessage(p, op);
        }
      });
    }

    CustomGUI gui = new CustomGUI(_plugin, p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Playtime",
        entries, 3, null, null, EnumSet.of(CustomGUI.Option.ENABLE_SEARCH, CustomGUI.Option.ENABLE_SORT));

    gui.setSearchButtonSlot(_footerSearchSlot(3));

    gui.setSortOptions(_createPlaytimeSortOptions(sortData));
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

    viewer.sendMessage(
        Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "PLAYTIME" + ChatColor.RESET + ChatColor.YELLOW
            + " » " + ChatColor.GRAY + (target.getName() != null ? target.getName() : "Unknown"));
    viewer.sendMessage(
        Main.getShortPrefix() + "Totaltime: " + ChatColor.YELLOW + _timerManager.formatTime(playTime + afkTime));
    viewer.sendMessage(Main.getShortPrefix() + "Playtime: " + ChatColor.YELLOW + _timerManager.formatTime(playTime));
    viewer.sendMessage(Main.getShortPrefix() + "AFK: " + ChatColor.YELLOW + _timerManager.formatTime(afkTime));
  }

  private ItemStack _createPlayerItem(OfflinePlayer op) {
    int playTime = _timerManager.getPlayTime(op.getUniqueId());
    int afkTime = _timerManager.getAFKTime(op.getUniqueId());

    ItemStack item = new ItemStack(Material.PLAYER_HEAD);
    ItemMeta meta = item.getItemMeta();

    if (meta instanceof SkullMeta skullMeta) {
      skullMeta.setOwningPlayer(op);

      String status = op.isOnline()
          ? ChatColor.GRAY + " (" + ChatColor.GREEN + "online" + ChatColor.GRAY + ")"
          : ChatColor.GRAY + " (" + ChatColor.RED + "offline" + ChatColor.GRAY + ")";

      skullMeta.displayName(
          Component.text(
              ChatColor.RED + "" + ChatColor.BOLD + "» "
                  + ChatColor.YELLOW + op.getName()
                  + status));

      skullMeta.lore(
          Arrays.asList(
              "",
              ChatColor.YELLOW + "» " + ChatColor.GRAY + "Total: " + ChatColor.YELLOW
                  + _timerManager.formatTime(playTime + afkTime),
              ChatColor.YELLOW + "» " + ChatColor.GRAY + "Playtime: " + ChatColor.YELLOW
                  + _timerManager.formatTime(playTime),
              ChatColor.YELLOW + "» " + ChatColor.GRAY + "AFK: " + ChatColor.YELLOW
                  + _timerManager.formatTime(afkTime),
              "",
              ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Show Message").stream()
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

  private List<CustomGUI.SortOption> _createPlaytimeSortOptions(Map<String, PlaytimeSortData> sortData) {
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
        new CustomGUI.SortOption("Total (High - Low)", byTotalDesc),
        new CustomGUI.SortOption("Total (Low - High)", byTotalAsc),
        new CustomGUI.SortOption("Playtime (High - Low)", byPlayDesc),
        new CustomGUI.SortOption("Playtime (Low - High)", byPlayAsc),
        new CustomGUI.SortOption("AFK-Time (High - Low)", byAfkDesc),
        new CustomGUI.SortOption("AFK-Time (Low - High)", byAfkAsc),
        new CustomGUI.SortOption("Name (A - Z)", byNameAsc),
        new CustomGUI.SortOption("Name (Z - A)", byNameDesc));
  }

  private record PlaytimeSortData(String name, long playTime, long afkTime, long totalTime) {
  }
}