package com.daveestar.bettervanilla.gui;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.CustomGUI;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class VeinMinerSettingsGUI implements Listener {
  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final MaterialToggleGUI _toolsGUI;
  private final MaterialToggleGUI _blocksGUI;

  public VeinMinerSettingsGUI() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
    _toolsGUI = new MaterialToggleGUI("Vein Miner Tools",
        SettingsManager.DEFAULT_VEIN_MINER_TOOLS.stream().map(Material::valueOf).toList(),
        () -> _settingsManager.getVeinMinerAllowedTools(),
        list -> _settingsManager.setVeinMinerAllowedTools(list));
    _blocksGUI = new MaterialToggleGUI("Vein Miner Blocks",
        SettingsManager.DEFAULT_VEIN_MINER_BLOCKS.stream().map(Material::valueOf).toList(),
        () -> _settingsManager.getVeinMinerAllowedBlocks(),
        list -> _settingsManager.setVeinMinerAllowedBlocks(list));
    _plugin.getServer().getPluginManager().registerEvents(this, _plugin);
  }

  public void displayGUI(Player p, CustomGUI parent) {
    Map<String, ItemStack> entries = new HashMap<>();
    entries.put("enabled", _createEnabledItem());
    entries.put("maxsize", _createMaxSizeItem());
    entries.put("tools", _createToolsItem());
    entries.put("blocks", _createBlocksItem());

    Map<String, Integer> customSlots = new HashMap<>();
    customSlots.put("enabled", 1);
    customSlots.put("maxsize", 3);
    customSlots.put("tools", 5);
    customSlots.put("blocks", 7);

    CustomGUI gui = new CustomGUI(_plugin, p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Vein Miner Settings",
        entries, 3, customSlots, parent,
        EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    Map<String, CustomGUI.ClickAction> actions = new HashMap<>();
    actions.put("enabled", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player player) {
        boolean newState = !_settingsManager.getVeinMiner();
        _settingsManager.setVeinMiner(newState);
        displayGUI(player, parent);
      }
    });
    actions.put("maxsize", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player player) {
        int newVal = _settingsManager.getVeinMinerMaxVeinSize() + 1;
        _settingsManager.setVeinMinerMaxVeinSize(newVal);
        displayGUI(player, parent);
      }

      @Override
      public void onRightClick(Player player) {
        int newVal = Math.max(1, _settingsManager.getVeinMinerMaxVeinSize() - 1);
        _settingsManager.setVeinMinerMaxVeinSize(newVal);
        displayGUI(player, parent);
      }
    });
    actions.put("tools", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player player) {
        _toolsGUI.displayGUI(player, gui);
      }
    });
    actions.put("blocks", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player player) {
        _blocksGUI.displayGUI(player, gui);
      }
    });

    gui.setClickActions(actions);
    gui.open(p);
  }

  private ItemStack _createEnabledItem() {
    boolean state = _settingsManager.getVeinMiner();
    ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Enabled"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }
    return item;
  }

  private ItemStack _createMaxSizeItem() {
    int val = _settingsManager.getVeinMinerMaxVeinSize();
    ItemStack item = new ItemStack(Material.PAPER);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Max Vein Size"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Current: " + ChatColor.YELLOW + val,
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: +1",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Right-Click: -1")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }
    return item;
  }

  private ItemStack _createToolsItem() {
    ItemStack item = new ItemStack(Material.IRON_PICKAXE);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Allowed Tools"));
      meta.lore(List.of(Component.text(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Open")));
      item.setItemMeta(meta);
    }
    return item;
  }

  private ItemStack _createBlocksItem() {
    ItemStack item = new ItemStack(Material.COAL_ORE);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Allowed Blocks"));
      meta.lore(List.of(Component.text(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Open")));
      item.setItemMeta(meta);
    }
    return item;
  }
}
