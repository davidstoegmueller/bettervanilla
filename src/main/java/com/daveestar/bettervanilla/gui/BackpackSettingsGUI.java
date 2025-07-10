package com.daveestar.bettervanilla.gui;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.CustomGUI;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.ChatColor;

public class BackpackSettingsGUI implements Listener {
  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final Map<UUID, CustomGUI> _rowsPending;
  private final Map<UUID, CustomGUI> _pagesPending;

  public BackpackSettingsGUI() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
    _rowsPending = new HashMap<>();
    _pagesPending = new HashMap<>();
    _plugin.getServer().getPluginManager().registerEvents(this, _plugin);
  }

  public void displayGUI(Player p, CustomGUI parentMenu) {
    final CustomGUI par = parentMenu;
    Map<String, ItemStack> entries = new HashMap<>();
    entries.put("enabled", _createEnabledItem());
    entries.put("rows", _createRowsItem());
    entries.put("pages", _createPagesItem());

    Map<String, Integer> customSlots = new HashMap<>();
    customSlots.put("enabled", 10);
    customSlots.put("rows", 13);
    customSlots.put("pages", 16);

    CustomGUI gui = new CustomGUI(_plugin, p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Backpack Settings",
        entries, 3, customSlots, par,
        EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    Map<String, CustomGUI.ClickAction> actions = new HashMap<>();
    actions.put("enabled", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player pl) {
        _toggleEnabled(pl);
        displayGUI(pl, par);
      }
    });
    actions.put("rows", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player pl) {
        pl.sendMessage(Main.getPrefix() + "Enter backpack rows:");
        _rowsPending.put(pl.getUniqueId(), par);
        pl.closeInventory();
      }
    });
    actions.put("pages", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player pl) {
        pl.sendMessage(Main.getPrefix() + "Enter backpack pages:");
        _pagesPending.put(pl.getUniqueId(), par);
        pl.closeInventory();
      }
    });

    gui.setClickActions(actions);
    gui.open(p);
  }

  private ItemStack _createEnabledItem() {
    boolean state = _settingsManager.getBackpackEnabled();
    ItemStack item = new ItemStack(Material.ENDER_CHEST);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Backpacks"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Enable player backpacks.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createRowsItem() {
    int rows = _settingsManager.getBackpackRows();
    ItemStack item = new ItemStack(Material.CHEST);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Backpack Rows"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Set number of rows per page.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Current: " + ChatColor.YELLOW + rows,
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Set value")
          .stream().map(Component::text).collect(Collectors.toList());
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createPagesItem() {
    int pages = _settingsManager.getBackpackPages();
    ItemStack item = new ItemStack(Material.PAPER);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Backpack Pages"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Set number of pages.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Current: " + ChatColor.YELLOW + pages,
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Set value")
          .stream().map(Component::text).collect(Collectors.toList());
      item.setItemMeta(meta);
    }

    return item;
  }

  @EventHandler
  public void onPlayerChat(AsyncChatEvent e) {
    Player p = e.getPlayer();
    UUID id = p.getUniqueId();

    if (_rowsPending.containsKey(id)) {
      e.setCancelled(true);
      String content = ((TextComponent) e.message()).content();

      try {
        int rows = Integer.parseInt(content);

        _plugin.getServer().getScheduler().runTask(_plugin, () -> {
          _settingsManager.setBackpackRows(rows);
          _plugin.getBackpackManager().setRows(rows);
          p.sendMessage(Main.getPrefix() + "Backpack rows set to: " + ChatColor.YELLOW + rows);
          p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
          CustomGUI parentMenu = _rowsPending.remove(id);
          displayGUI(p, parentMenu);
        });
      } catch (NumberFormatException ex) {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Please provide a valid number.");
      }
      return;
    }

    if (_pagesPending.containsKey(id)) {
      e.setCancelled(true);
      String content = ((TextComponent) e.message()).content();

      try {
        int pages = Integer.parseInt(content);

        _plugin.getServer().getScheduler().runTask(_plugin, () -> {
          _settingsManager.setBackpackPages(pages);
          _plugin.getBackpackManager().setPages(pages);
          p.sendMessage(Main.getPrefix() + "Backpack pages set to: " + ChatColor.YELLOW + pages);
          p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
          CustomGUI parentMenu = _pagesPending.remove(id);
          displayGUI(p, parentMenu);
        });
      } catch (NumberFormatException ex) {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Please provide a valid number.");
      }
    }
  }

  private void _toggleEnabled(Player p) {
    boolean newState = !_settingsManager.getBackpackEnabled();
    _settingsManager.setBackpackEnabled(newState);
    _plugin.getBackpackManager().setEnabled(newState);
    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Backpacks are now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }
}
