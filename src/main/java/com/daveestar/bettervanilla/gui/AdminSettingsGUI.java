package com.daveestar.bettervanilla.gui;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.AFKManager;
import com.daveestar.bettervanilla.manager.MaintenanceManager;
import com.daveestar.bettervanilla.manager.HealthDisplayManager;
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
  private final HealthDisplayManager _healthDisplayManager;
  private final Map<UUID, CustomGUI> _afkTimePending;
  private final Map<UUID, CustomGUI> _maintenanceMessagePending;
  private final Map<UUID, CustomGUI> _motdPending;
  private final BackpackSettingsGUI _backpackSettingsGUI;
  private final VeinMinerSettingsGUI _veinMinerSettingsGUI;
  private final VeinChopperSettingsGUI _veinChopperSettingsGUI;

  public AdminSettingsGUI() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
    _afkManager = _plugin.getAFKManager();
    _maintenanceManager = _plugin.getMaintenanceManager();
    _healthDisplayManager = _plugin.getHealthDisplayManager();
    _afkTimePending = new HashMap<>();
    _maintenanceMessagePending = new HashMap<>();
    _motdPending = new HashMap<>();
    _backpackSettingsGUI = new BackpackSettingsGUI();
    _veinMinerSettingsGUI = new VeinMinerSettingsGUI();
    _veinChopperSettingsGUI = new VeinChopperSettingsGUI();
    _plugin.getServer().getPluginManager().registerEvents(this, _plugin);
  }

  public void displayGUI(Player p, CustomGUI parentMenu) {
    Map<String, ItemStack> entries = new HashMap<>();
    // first row
    entries.put("maintenance", _createMaintenanceItem());
    entries.put("creeperdamage", _createCreeperDamageItem());
    entries.put("enableend", _createEnableEndItem());
    entries.put("enablenether", _createEnableNetherItem());
    entries.put("sleepingrain", _createSleepingRainItem());

    // second row
    entries.put("motd", _createMOTDItem());
    entries.put("afkprotection", _createAFKProtectionItem());
    entries.put("afktime", _createAFKTimeItem());
    entries.put("backpacksettings", _createBackpackSettingsItem());

    // third row
    entries.put("cropprotection", _createCropProtectionItem());
    entries.put("rightclickcropharvest", _createRightClickCropHarvestItem());
    entries.put("displayhearts", _createDisplayHeartsItem());
    entries.put("veinminersettings", _createVeinMinerSettingsItem());
    entries.put("veinchoppersettings", _createVeinChopperSettingsItem());

    Map<String, Integer> customSlots = new HashMap<>();
    // first row
    customSlots.put("maintenance", 0);
    customSlots.put("creeperdamage", 2);
    customSlots.put("enableend", 4);
    customSlots.put("enablenether", 6);
    customSlots.put("sleepingrain", 8);

    // second row
    customSlots.put("motd", 10);
    customSlots.put("afkprotection", 12);
    customSlots.put("afktime", 14);
    customSlots.put("backpacksettings", 16);

    // third row
    customSlots.put("cropprotection", 18);
    customSlots.put("rightclickcropharvest", 20);
    customSlots.put("displayhearts", 22);
    customSlots.put("veinminersettings", 24);
    customSlots.put("veinchoppersettings", 26);

    CustomGUI gui = new CustomGUI(_plugin, p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Admin Settings",
        entries, 4, customSlots, parentMenu,
        EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    Map<String, CustomGUI.ClickAction> actions = new HashMap<>();
    actions.put("maintenance", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleMaintenance(p, null);
        displayGUI(p, parentMenu);
      }

      @Override
      public void onRightClick(Player p) {
        if (!_maintenanceManager.getState()) {
          p.sendMessage(Main.getPrefix() + "Enter maintenance message:");
          _maintenanceMessagePending.put(p.getUniqueId(), parentMenu);
          p.closeInventory();
        } else {
          _toggleMaintenance(p, null);
          displayGUI(p, parentMenu);
        }
      }
    });

    actions.put("creeperdamage", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleCreeperDamage(p);
        displayGUI(p, parentMenu);
      }
    });

    actions.put("enableend", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleEnd(p);
        displayGUI(p, parentMenu);
      }
    });

    actions.put("enablenether", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleNether(p);
        displayGUI(p, parentMenu);
      }
    });

    actions.put("sleepingrain", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleSleepingRain(p);
        displayGUI(p, parentMenu);
      }
    });

    actions.put("cropprotection", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleCropProtection(p);
        displayGUI(p, parentMenu);
      }
    });

    actions.put("rightclickcropharvest", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleRightClickCropHarvest(p);
        displayGUI(p, parentMenu);
      }
    });

    actions.put("displayhearts", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleDisplayHearts(p);
        displayGUI(p, parentMenu);
      }
    });

    actions.put("afkprotection", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleAFKProtection(p);
        displayGUI(p, parentMenu);
      }
    });

    actions.put("afktime", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        p.sendMessage(Main.getPrefix() + "Enter AFK time in minutes:");
        _afkTimePending.put(p.getUniqueId(), parentMenu);
        p.closeInventory();
      }
    });

    actions.put("motd", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        p.sendMessage(Main.getPrefix() + "Enter server MOTD:");
        _motdPending.put(p.getUniqueId(), parentMenu);
        p.closeInventory();
      }
    });

    actions.put("backpacksettings", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _backpackSettingsGUI.displayGUI(p, gui);
      }
    });

    actions.put("veinminersettings", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _veinMinerSettingsGUI.displayGUI(p, gui);
      }
    });

    actions.put("veinchoppersettings", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _veinChopperSettingsGUI.displayGUI(p, gui);
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
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Prevents players without permissions from joining the server.",
          "",

          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Message: "
              + (message != null && !message.isEmpty() ? ChatColor.YELLOW + message : ChatColor.RED + ""),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Right-Click: Toggle and set message")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
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
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Prevents creepers from destroying blocks.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
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
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Control the entry into 'The End' dimension.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
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
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Control the entry into 'The Nether' dimension.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
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
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Allows players to skip rain by sleeping.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createCropProtectionItem() {
    boolean state = _settingsManager.getCropProtection();
    ItemStack item = new ItemStack(Material.WHEAT_SEEDS);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Crop Protection"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Prevents crops from being trampled.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createRightClickCropHarvestItem() {
    boolean state = _settingsManager.getRightClickCropHarvest();
    ItemStack item = new ItemStack(Material.IRON_HOE);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Right-Click Crop Harvest"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Allows players to harvest crops by right-clicking them.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createDisplayHeartsItem() {
    boolean state = _settingsManager.getHealthDisplay();
    ItemStack item = new ItemStack(Material.RED_DYE);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component
          .text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Display Player Hearts"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Shows players' hearts above their heads.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
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
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Makes the player invulnerable while AFK.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
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
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Set the AFK timeout in minutes.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Current: " + ChatColor.YELLOW + minutes + ChatColor.GRAY
              + " minutes",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Set value")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createMOTDItem() {
    String motd = _settingsManager.getServerMOTD();
    ItemStack item = new ItemStack(Material.OAK_SIGN);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Server MOTD"));

      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY
              + "Set the server message of the day (MOTD) visible in the server list.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Current: "
              + (motd != null && !motd.isEmpty() ? ChatColor.translateAlternateColorCodes('&', motd)
                  : ChatColor.RED + "Not set"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Set value")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createBackpackSettingsItem() {
    ItemStack item = new ItemStack(Material.BARREL);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Backpack Settings"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Manage the global backpack settings.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Open")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createVeinMinerSettingsItem() {
    ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Vein Miner Settings"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Manage the global vein miner settings.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Open")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createVeinChopperSettingsItem() {
    ItemStack item = new ItemStack(Material.DIAMOND_AXE);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Vein Chopper Settings"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Manage the global vein chopper settings.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Open")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
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

          CustomGUI parentMenu = _afkTimePending.remove(id);
          displayGUI(p, parentMenu);
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
        CustomGUI parentMenu = _maintenanceMessagePending.remove(id);
        _toggleMaintenance(p, message);

        p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
        displayGUI(p, parentMenu);
      });

      return;
    }

    if (_motdPending.containsKey(id)) {
      e.setCancelled(true);

      String message = ((TextComponent) e.message()).content();

      _plugin.getServer().getScheduler().runTask(_plugin, () -> {
        CustomGUI parentMenu = _motdPending.remove(id);
        _settingsManager.setServerMOTD(message);

        p.sendMessage(Main.getPrefix() + "MOTD set to: " + ChatColor.translateAlternateColorCodes('&', message));
        p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
        displayGUI(p, parentMenu);
      });
      return;
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

  private void _toggleCropProtection(Player p) {
    boolean newState = !_settingsManager.getCropProtection();
    _settingsManager.setCropProtection(newState);
    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Crop protection is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleRightClickCropHarvest(Player p) {
    boolean newState = !_settingsManager.getRightClickCropHarvest();
    _settingsManager.setRightClickCropHarvest(newState);
    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(
        Main.getPrefix() + "Right-Click crop harvest is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleDisplayHearts(Player p) {
    boolean newState = !_settingsManager.getHealthDisplay();
    _settingsManager.setHealthDisplay(newState);
    _healthDisplayManager.applyHealthDisplaySetting();
    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(
        Main.getPrefix() + "Player hearts display is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleAFKProtection(Player p) {
    boolean newState = !_settingsManager.getAFKProtection();
    _settingsManager.setAFKProtection(newState);
    _afkManager.applyProtectionToAFKPlayers(newState);
    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "AFK protection is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }
}
