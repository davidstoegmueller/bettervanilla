package com.daveestar.bettervanilla.gui;

import java.util.*;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.enums.ScoreboardStat;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.CustomGUI;

import net.md_5.bungee.api.ChatColor;
import net.kyori.adventure.text.Component;

public class ScoreboardSettingsGUI {
  private final Main _plugin;
  private final SettingsManager _settingsManager;

  public ScoreboardSettingsGUI() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
  }

  public void displayGUI(Player p, CustomGUI parentMenu) {
    Map<String, ItemStack> entries = new LinkedHashMap<>();
    for (ScoreboardStat stat : ScoreboardStat.values()) {
      entries.put(stat.name(), _createStatItem(stat));
    }

    CustomGUI gui = new CustomGUI(_plugin, p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Scoreboard", entries, 3, null, parentMenu,
        EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    Map<String, CustomGUI.ClickAction> actions = new HashMap<>();
    for (ScoreboardStat stat : ScoreboardStat.values()) {
      actions.put(stat.name(), new CustomGUI.ClickAction() {
        @Override
        public void onLeftClick(Player player) {
          _toggleStat(stat);
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
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW
          + stat.getDisplayName()));
      meta.lore(Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().map(Component::text).toList());
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
  }
}
