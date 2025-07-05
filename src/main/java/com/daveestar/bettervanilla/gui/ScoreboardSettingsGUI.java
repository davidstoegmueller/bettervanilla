package com.daveestar.bettervanilla.gui;

import java.util.*;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import io.papermc.paper.event.player.AsyncChatEvent;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.ScoreboardStat;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.CustomGUI;

import net.md_5.bungee.api.ChatColor;
import net.kyori.adventure.text.Component;

public class ScoreboardSettingsGUI implements Listener {
  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final com.daveestar.bettervanilla.manager.ScoreboardManager _scoreboardManager;

  private final Map<java.util.UUID, ScoreboardStat> _renamePending;
  private final Map<java.util.UUID, CustomGUI> _pendingMenu;
  private final Map<java.util.UUID, CustomGUI> _titlePending;

  public ScoreboardSettingsGUI() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
    _scoreboardManager = _plugin.getScoreboardManager();
    _renamePending = new java.util.HashMap<>();
    _pendingMenu = new java.util.HashMap<>();
    _titlePending = new java.util.HashMap<>();
    _plugin.getServer().getPluginManager().registerEvents(this, _plugin);
  }

  public void displayGUI(Player p, CustomGUI parentMenu) {
    Map<String, ItemStack> entries = new LinkedHashMap<>();
    entries.put("title", _createTitleItem());
    for (ScoreboardStat stat : ScoreboardStat.values()) {
      entries.put(stat.name(), _createStatItem(stat));
    }

    CustomGUI gui = new CustomGUI(_plugin, p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Scoreboard", entries, 3, null, parentMenu,
        EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    Map<String, CustomGUI.ClickAction> actions = new HashMap<>();
    actions.put("title", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player player) {
        player.sendMessage(Main.getPrefix() + "Enter scoreboard title:");
        _titlePending.put(player.getUniqueId(), gui);
        player.closeInventory();
      }
    });

    for (ScoreboardStat stat : ScoreboardStat.values()) {
      actions.put(stat.name(), new CustomGUI.ClickAction() {
        @Override
        public void onLeftClick(Player player) {
          _toggleStat(stat);
          displayGUI(player, parentMenu);
        }

        @Override
        public void onRightClick(Player player) {
          player.sendMessage(Main.getPrefix() + "Enter display name:");
          _renamePending.put(player.getUniqueId(), stat);
          _pendingMenu.put(player.getUniqueId(), gui);
          player.closeInventory();
        }

        @Override
        public void onShiftLeftClick(Player player) {
          _moveStat(stat, -1);
          displayGUI(player, parentMenu);
        }

        @Override
        public void onShiftRightClick(Player player) {
          _moveStat(stat, 1);
          displayGUI(player, parentMenu);
        }
      });
    }

    gui.setClickActions(actions);
    gui.open(p);
  }

  private ItemStack _createStatItem(ScoreboardStat stat) {
    List<String> enabled = _settingsManager.getScoreboardStats();
    boolean state = enabled.contains(stat.name());
    ItemStack item = new ItemStack(state ? Material.LIME_WOOL : Material.RED_WOOL);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      String display = _settingsManager.getScoreboardDisplayName(stat.name());
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + display));

      var lore = new ArrayList<String>();
      lore.add("");
      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
          + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));
      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle");
      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Right-Click: Rename");
      if (state) {
        lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Shift-L: Move Up");
        lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Shift-R: Move Down");
      }
      meta.lore(lore.stream().map(Component::text).toList());
      item.setItemMeta(meta);
    }

    return item;
  }

  private void _toggleStat(ScoreboardStat stat) {
    List<String> enabled = new ArrayList<>(_settingsManager.getScoreboardStats());
    if (enabled.contains(stat.name())) {
      enabled.remove(stat.name());
    } else {
      enabled.add(stat.name());
    }
    _settingsManager.setScoreboardStats(enabled);
    _scoreboardManager.showScoreboardForAll();
  }

  private ItemStack _createTitleItem() {
    String title = _settingsManager.getScoreboardTitle();
    ItemStack item = new ItemStack(Material.NAME_TAG);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Scoreboard Title"));
      meta.lore(java.util.Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Current: " + ChatColor.YELLOW + title,
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Set value")
          .stream().map(Component::text).toList());
      item.setItemMeta(meta);
    }
    return item;
  }

  private void _moveStat(ScoreboardStat stat, int offset) {
    java.util.List<String> list = new java.util.ArrayList<>(_settingsManager.getScoreboardStats());
    int idx = list.indexOf(stat.name());
    if (idx == -1)
      return;
    int newIdx = idx + offset;
    if (newIdx < 0 || newIdx >= list.size())
      return;
    java.util.Collections.swap(list, idx, newIdx);
    _settingsManager.setScoreboardStats(list);
    _scoreboardManager.showScoreboardForAll();
  }

  @EventHandler
  public void onChat(AsyncChatEvent e) {
    Player p = e.getPlayer();
    java.util.UUID id = p.getUniqueId();

    if (_renamePending.containsKey(id)) {
      e.setCancelled(true);
      String text = ((net.kyori.adventure.text.TextComponent) e.message()).content();
      ScoreboardStat stat = _renamePending.remove(id);
      CustomGUI menu = _pendingMenu.remove(id);
      _settingsManager.setScoreboardDisplayName(stat.name(), text);
      _scoreboardManager.showScoreboardForAll();
      _plugin.getServer().getScheduler().runTask(_plugin, () -> {
        p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
        displayGUI(p, menu);
      });
      return;
    }

    if (_titlePending.containsKey(id)) {
      e.setCancelled(true);
      String text = ((net.kyori.adventure.text.TextComponent) e.message()).content();
      CustomGUI menu = _titlePending.remove(id);
      _settingsManager.setScoreboardTitle(text);
      _scoreboardManager.showScoreboardForAll();
      _plugin.getServer().getScheduler().runTask(_plugin, () -> {
        p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
        displayGUI(p, menu);
      });
    }
  }
}
