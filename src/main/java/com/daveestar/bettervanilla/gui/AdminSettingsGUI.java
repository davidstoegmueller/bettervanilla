package com.daveestar.bettervanilla.gui;

import java.util.Arrays;
import java.util.ArrayList;
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
import com.daveestar.bettervanilla.manager.AFKManager;
import com.daveestar.bettervanilla.manager.MaintenanceManager;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.CustomGUI;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.ChatColor;

public class AdminSettingsGUI implements Listener {
  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final AFKManager _afkManager;
  private final MaintenanceManager _maintenanceManager;
  private final Map<UUID, CustomGUI> _afkTimePending;
  private final Map<UUID, CustomGUI> _maintenanceMessagePending;

  public AdminSettingsGUI() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
    _afkManager = _plugin.getAFKManager();
    _maintenanceManager = _plugin.getMaintenanceManager();
    _afkTimePending = new HashMap<>();
    _maintenanceMessagePending = new HashMap<>();
    _plugin.getServer().getPluginManager().registerEvents(this, _plugin);
  }

  public void displayGUI(Player p) {
    displayGUI(p, null);
  }

  public void displayGUI(Player p, CustomGUI parentMenu) {
    final CustomGUI par = parentMenu;
    Map<String, ItemStack> entries = new HashMap<>();
    entries.put("maintenance", _createMaintenanceItem());
    entries.put("creeperdamage", _createCreeperDamageItem());
    entries.put("enableend", _createEnableEndItem());
    entries.put("enablenether", _createEnableNetherItem());
    entries.put("sleepingrain", _createSleepingRainItem());
    entries.put("afkprotection", _createAFKProtectionItem());
    entries.put("afktime", _createAFKTimeItem());

    Map<String, Integer> customSlots = new HashMap<>();
    // first row
    customSlots.put("maintenance", 0);
    customSlots.put("creeperdamage", 2);
    customSlots.put("enableend", 4);
    customSlots.put("enablenether", 6);
    customSlots.put("sleepingrain", 8);

    // second row
    customSlots.put("afkprotection", 12);
    customSlots.put("afktime", 14);

    CustomGUI gui = new CustomGUI(_plugin, p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Admin Settings",
        entries, 3, customSlots, par,
        EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));


    Map<String, CustomGUI.ClickAction> actions = new HashMap<>();
    actions.put("maintenance", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleMaintenance(p, null);
        displayGUI(p, par);
      }

      @Override
      public void onRightClick(Player p) {
        if (!_maintenanceManager.getState()) {
          p.sendMessage(Main.getPrefix() + "Enter maintenance message:");
          _maintenanceMessagePending.put(p.getUniqueId(), par);
          p.closeInventory();
        } else {
          _toggleMaintenance(p, null);
          displayGUI(p, par);
        }
      }
    });

    actions.put("creeperdamage", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleCreeperDamage(p);
        displayGUI(p, par);
      }
    });

    actions.put("enableend", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleEnd(p);
        displayGUI(p, par);
      }
    });

    actions.put("enablenether", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleNether(p);
        displayGUI(p, par);
      }
    });

    actions.put("sleepingrain", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleSleepingRain(p);
        displayGUI(p, par);
      }
    });

    actions.put("afkprotection", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleAFKProtection(p);
        displayGUI(p, par);
      }
    });

    actions.put("afktime", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        p.sendMessage(Main.getPrefix() + "Enter AFK time in minutes:");
        _afkTimePending.put(p.getUniqueId(), par);
        p.closeInventory();
      }
    });

    gui.setClickActions(actions);
    gui.open(p);
  }

  private ItemStack _createMaintenanceItem() {
    boolean state = _maintenanceManager.getState();
    String message = _settingsManager.getMaintenanceMessage();
    ItemStack item = new ItemStack(Material.IRON_BARS);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Maintenance"));

      var lore = new ArrayList<String>();
      lore.add("");
      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
          + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));
      if (message != null && !message.isEmpty()) {
        lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Message: " + ChatColor.YELLOW + message);
      }
      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle");
      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Right-Click: Toggle with message");

      meta.lore(lore.stream().map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }
    return item;
  }

  private ItemStack _createCreeperDamageItem() {
    boolean state = _settingsManager.getToggleCreeperDamage();
    ItemStack item = new ItemStack(Material.CREEPER_HEAD);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Creeper Damage"));
      meta.lore(Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }
    return item;
  }

  private ItemStack _createEnableEndItem() {
    boolean state = _settingsManager.getEnableEnd();
    ItemStack item = new ItemStack(Material.ENDER_EYE);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Enable End"));
      meta.lore(Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }
    return item;
  }

  private ItemStack _createEnableNetherItem() {
    boolean state = _settingsManager.getEnableNether();
    ItemStack item = new ItemStack(Material.BLAZE_ROD);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Enable Nether"));
      meta.lore(Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
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
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }
    return item;
  }

  private ItemStack _createAFKProtectionItem() {
    boolean state = _settingsManager.getAFKProtection();
    ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "AFK Protection"));
      meta.lore(Arrays.asList(
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
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
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Current: " + ChatColor.YELLOW + minutes + ChatColor.GRAY
              + " minutes",
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
        _plugin.getServer().getScheduler().runTask(_plugin, () -> {
          _settingsManager.setAFKTime(minutes);
          p.sendMessage(
              Main.getPrefix() + "AFK time set to: " + ChatColor.YELLOW + minutes + ChatColor.GRAY + " minutes");
          p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
          CustomGUI parMenu = _afkTimePending.remove(id);
          displayGUI(p, parMenu);
        });
      } catch (NumberFormatException ex) {
        p.sendMessage(Main.getPrefix() + ChatColor.RED + "Please provide a valid number.");
      }
      return;
    }

    if (_maintenanceMessagePending.containsKey(id)) {
      e.setCancelled(true);
      String message = ((TextComponent) e.message()).content();
      _plugin.getServer().getScheduler().runTask(_plugin, () -> {
        CustomGUI parMenu = _maintenanceMessagePending.remove(id);
        _toggleMaintenance(p, message);
        p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
        displayGUI(p, parMenu);
      });
    }
  }

  private void _toggleMaintenance(Player p, String message) {
    boolean newState = !_maintenanceManager.getState();
    _maintenanceManager.setState(newState, newState ? message : null);
    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(
        Main.getPrefix() + "The maintenance mode is now turned: " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
    if (newState && message != null) {
      p.sendMessage(Main.getPrefix() + "Message was set to: " + ChatColor.YELLOW + message);
    }
    _maintenanceManager.kickAll(_plugin.getServer().getOnlinePlayers());
  }

  private void _toggleCreeperDamage(Player p) {
    boolean newState = !_settingsManager.getToggleCreeperDamage();
    _settingsManager.setToggleCreeperDamage(newState);
    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Creeper damage is now turned: " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleEnd(Player p) {
    boolean newState = !_settingsManager.getEnableEnd();
    _settingsManager.setEnableEnd(newState);
    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "The End is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleNether(Player p) {
    boolean newState = !_settingsManager.getEnableNether();
    _settingsManager.setEnableNether(newState);
    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "The Nether is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleSleepingRain(Player p) {
    boolean newState = !_settingsManager.getSleepingRain();
    _settingsManager.setSleepingRain(newState);
    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Sleeping Rain is now turned: " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleAFKProtection(Player p) {
    boolean newState = !_settingsManager.getAFKProtection();
    _settingsManager.setAFKProtection(newState);
    _afkManager.applyProtectionToAFKPlayers(newState);
    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "AFK protection is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }
}
