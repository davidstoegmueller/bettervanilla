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
import com.daveestar.bettervanilla.manager.MaintenanceManager;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.CustomGUI;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.ChatColor;

public class SettingsGUI implements Listener {
  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final MaintenanceManager _maintenanceManager;
  private final Map<UUID, Boolean> _afkTimePending;
  private final Map<UUID, Boolean> _maintenanceMessagePending;

  public SettingsGUI() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
    _maintenanceManager = _plugin.getMaintenanceManager();
    _afkTimePending = new HashMap<>();
    _maintenanceMessagePending = new HashMap<>();
    _plugin.getServer().getPluginManager().registerEvents(this, _plugin);
  }

  public void displayGUI(Player p) {
    Map<String, ItemStack> entries = new HashMap<>();
    entries.put("maintenance", _createMaintenanceItem());
    entries.put("creeperdamage", _createCreeperDamageItem());
    entries.put("toggleend", _createToggleEndItem());
    entries.put("sleepingrain", _createSleepingRainItem());
    entries.put("afktime", _createAFKTimeItem());

    Map<String, Integer> customSlots = new HashMap<>();
    customSlots.put("maintenance", 1);
    customSlots.put("creeperdamage", 3);
    customSlots.put("toggleend", 5);
    customSlots.put("sleepingrain", 7);
    customSlots.put("afktime", 13);

    CustomGUI gui = new CustomGUI(_plugin, p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Settings",
        entries, 3, customSlots, null,
        EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    Map<String, CustomGUI.ClickAction> actions = new HashMap<>();
    actions.put("maintenance", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player player) {
        _toggleMaintenance(player, null);
        displayGUI(player);
      }

      @Override
      public void onRightClick(Player player) {
        if (!_maintenanceManager.getState()) {
          player.sendMessage(Main.getPrefix() + "Enter maintenance message:");
          _maintenanceMessagePending.put(player.getUniqueId(), true);
          player.closeInventory();
        } else {
          _toggleMaintenance(player, null);
          displayGUI(player);
        }
      }
    });

    actions.put("creeperdamage", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player player) {
        _toggleCreeperDamage(player);
        displayGUI(player);
      }
    });

    actions.put("toggleend", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player player) {
        _toggleEnd(player);
        displayGUI(player);
      }
    });

    actions.put("sleepingrain", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player player) {
        _toggleSleepingRain(player);
        displayGUI(player);
      }
    });

    actions.put("afktime", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player player) {
        player.sendMessage(Main.getPrefix() + "Enter AFK time in minutes:");
        _afkTimePending.put(player.getUniqueId(), true);
        player.closeInventory();
      }
    });

    gui.setClickActions(actions);
    gui.open(p);
  }

  private ItemStack _createMaintenanceItem() {
    boolean state = _maintenanceManager.getState();
    ItemStack item = new ItemStack(Material.IRON_BARS);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Maintenance"));
      meta.lore(Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: " + (state ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Right-Click: Toggle with message")
          .stream().map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }
    return item;
  }

  private ItemStack _createCreeperDamageItem() {
    boolean state = _settingsManager.getToggleCreeperDamage();
    ItemStack item = new ItemStack(Material.CREEPER_HEAD);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Creeper Damage"));
      meta.lore(Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: " + (state ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }
    return item;
  }

  private ItemStack _createToggleEndItem() {
    boolean state = _settingsManager.getToggleEnd();
    ItemStack item = new ItemStack(Material.ENDER_EYE);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Toggle End"));
      meta.lore(Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: " + (state ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }
    return item;
  }

  private ItemStack _createSleepingRainItem() {
    boolean state = _settingsManager.getSleepingRain();
    ItemStack item = new ItemStack(Material.BLUE_BED);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Sleeping Rain"));
      meta.lore(Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: " + (state ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }
    return item;
  }

  private ItemStack _createAFKTimeItem() {
    int minutes = _settingsManager.getAFKTime();
    ItemStack item = new ItemStack(Material.CLOCK);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "AFK Time"));
      meta.lore(Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Current: " + ChatColor.YELLOW + minutes + ChatColor.GRAY + " minutes",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Set value")
          .stream().map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }
    return item;
  }

  @EventHandler
  public void onPlayerChat(AsyncChatEvent e) {
    Player p = e.getPlayer();
    UUID id = p.getUniqueId();
    if (_afkTimePending.containsKey(id)) {
      e.setCancelled(true);
      String content = ((TextComponent) e.message()).content();
      try {
        int minutes = Integer.parseInt(content);
        _settingsManager.setAFKTime(minutes);
        p.sendMessage(Main.getPrefix() + "AFK time set to: " + ChatColor.YELLOW + minutes + ChatColor.GRAY + " minutes");
        p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
        _afkTimePending.remove(id);
        displayGUI(p);
      } catch (NumberFormatException ex) {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Please provide a valid number.");
      }
      return;
    }

    if (_maintenanceMessagePending.containsKey(id)) {
      e.setCancelled(true);
      String message = ((TextComponent) e.message()).content();
      _maintenanceMessagePending.remove(id);
      _toggleMaintenance(p, message);
      p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
      displayGUI(p);
    }
  }

  private void _toggleMaintenance(Player p, String message) {
    boolean newState = !_maintenanceManager.getState();
    _maintenanceManager.setState(newState, newState ? message : null);
    String stateText = newState ? "ON" : "OFF";
    p.sendMessage(Main.getPrefix() + "The maintenance mode is now turned: " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
    if (newState && message != null) {
      p.sendMessage(Main.getPrefix() + "Message was set to: " + ChatColor.YELLOW + message);
    }
    _maintenanceManager.kickAll(_plugin.getServer().getOnlinePlayers());
  }

  private void _toggleCreeperDamage(Player p) {
    boolean newState = !_settingsManager.getToggleCreeperDamage();
    _settingsManager.setToggleCreeperDamage(newState);
    String stateText = newState ? "ON" : "OFF";
    p.sendMessage(Main.getPrefix() + "Creeper damage is now turned: " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleEnd(Player p) {
    boolean newState = !_settingsManager.getToggleEnd();
    _settingsManager.setToggleEnd(newState);
    String stateText = newState ? "ON" : "OFF";
    p.sendMessage(Main.getPrefix() + "The End is now turned: " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleSleepingRain(Player p) {
    boolean newState = !_settingsManager.getSleepingRain();
    _settingsManager.setSleepingRain(newState);
    String stateText = newState ? "ON" : "OFF";
    p.sendMessage(Main.getPrefix() + "Sleeping Rain is now turned: " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }
}
