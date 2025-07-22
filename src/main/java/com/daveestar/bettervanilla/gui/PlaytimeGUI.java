package com.daveestar.bettervanilla.gui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

public class PlaytimeGUI {
  private final Main _plugin;
  private final TimerManager _timerManager;

  public PlaytimeGUI() {
    _plugin = Main.getInstance();
    _timerManager = _plugin.getTimerManager();
  }

  public void displayGUI(Player p) {
    Map<String, ItemStack> entries = new LinkedHashMap<>();

    // self playtime item
    entries.put("self", _createSelfItem(p));

    // other players
    for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
      if (op.getUniqueId().equals(p.getUniqueId()))
        continue;
      if (op.getName() == null)
        continue;
      entries.put(op.getUniqueId().toString(), _createPlayerItem(op));
    }

    int rows = Math.max(2, (int) Math.ceil((entries.size()) / 9.0) + 1);
    Map<String, Integer> customSlots = new HashMap<>();
    customSlots.put("self", 4); // center of first row

    CustomGUI gui = new CustomGUI(_plugin, p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Playtime",
        entries, rows, customSlots, null, null);

    Map<String, CustomGUI.ClickAction> actions = new HashMap<>();
    actions.put("self", new CustomGUI.ClickAction() {
      public void onLeftClick(Player player) {
        _sendPlaytimeMessage(player, player);
      }
    });

    for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
      if (op.getUniqueId().equals(p.getUniqueId()))
        continue;
      if (op.getName() == null)
        continue;
      String key = op.getUniqueId().toString();
      actions.put(key, new CustomGUI.ClickAction() {
        public void onLeftClick(Player player) {
          _sendPlaytimeMessage(player, op);
        }
      });
    }

    gui.setClickActions(actions);
    gui.open(p);
  }

  private void _sendPlaytimeMessage(Player viewer, OfflinePlayer target) {
    int playTime = _timerManager.getPlayTime(target.getUniqueId());
    int afkTime = _timerManager.getAFKTime(target.getUniqueId());
    viewer.sendMessage(Main.getPrefix() + ChatColor.YELLOW + ChatColor.BOLD + "PLAYTIME" + ChatColor.RESET + ChatColor.YELLOW
        + " » " + ChatColor.GRAY + (target.getName() != null ? target.getName() : "Unknown"));
    viewer.sendMessage(Main.getShortPrefix() + "Totaltime: " + ChatColor.YELLOW + _timerManager.formatTime(playTime + afkTime));
    viewer.sendMessage(Main.getShortPrefix() + "Playtime: " + ChatColor.YELLOW + _timerManager.formatTime(playTime));
    viewer.sendMessage(Main.getShortPrefix() + "AFK: " + ChatColor.YELLOW + _timerManager.formatTime(afkTime));
  }

  private ItemStack _createSelfItem(Player p) {
    int playTime = _timerManager.getPlayTime(p.getUniqueId());
    int afkTime = _timerManager.getAFKTime(p.getUniqueId());

    ItemStack item = new ItemStack(Material.CLOCK);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Your Playtime"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Total: " + ChatColor.YELLOW + _timerManager.formatTime(playTime + afkTime),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Playtime: " + ChatColor.YELLOW + _timerManager.formatTime(playTime),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "AFK: " + ChatColor.YELLOW + _timerManager.formatTime(afkTime),
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Show Message")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createPlayerItem(OfflinePlayer op) {
    int playTime = _timerManager.getPlayTime(op.getUniqueId());
    int afkTime = _timerManager.getAFKTime(op.getUniqueId());

    ItemStack item = new ItemStack(Material.PLAYER_HEAD);
    ItemMeta meta = item.getItemMeta();

    if (meta instanceof SkullMeta skullMeta) {
      skullMeta.setOwningPlayer(op);
      skullMeta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + op.getName()));
      skullMeta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Total: " + ChatColor.YELLOW + _timerManager.formatTime(playTime + afkTime),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Playtime: " + ChatColor.YELLOW + _timerManager.formatTime(playTime),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "AFK: " + ChatColor.YELLOW + _timerManager.formatTime(afkTime),
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Show Message")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(skullMeta);
    }

    return item;
  }
}
