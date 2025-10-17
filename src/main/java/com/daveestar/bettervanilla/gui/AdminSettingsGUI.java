package com.daveestar.bettervanilla.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.AFKManager;
import com.daveestar.bettervanilla.manager.MaintenanceManager;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.utils.CustomDialog;
import com.daveestar.bettervanilla.utils.CustomGUI;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;

import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class AdminSettingsGUI {
  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final AFKManager _afkManager;
  private final MaintenanceManager _maintenanceManager;
  private final BackpackSettingsGUI _backpackSettingsGUI;
  private final VeinMinerSettingsGUI _veinMinerSettingsGUI;
  private final VeinChopperSettingsGUI _veinChopperSettingsGUI;

  public AdminSettingsGUI() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
    _afkManager = _plugin.getAFKManager();
    _maintenanceManager = _plugin.getMaintenanceManager();
    _backpackSettingsGUI = new BackpackSettingsGUI();
    _veinMinerSettingsGUI = new VeinMinerSettingsGUI();
    _veinChopperSettingsGUI = new VeinChopperSettingsGUI();
  }

  public void displayGUI(Player p, CustomGUI parentMenu) {
    displayGUI(p, parentMenu, null);
  }

  public void displayGUI(Player p, CustomGUI parentMenu, Consumer<Player> backAction) {
    Map<String, ItemStack> entries = new HashMap<>();
    // first row
    entries.put("maintenance", _createMaintenanceItem());
    entries.put("creeperblockdamage", _createCreeperBlockDamageItem());
    entries.put("creeperentitydamage", _createCreeperEntityDamageItem());
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
    entries.put("locatorbar", _createLocatorBarItem());
    entries.put("veinminersettings", _createVeinMinerSettingsItem());
    entries.put("veinchoppersettings", _createVeinChopperSettingsItem());

    Map<String, Integer> customSlots = new HashMap<>();
    // first row
    customSlots.put("maintenance", 0);
    customSlots.put("sleepingrain", 2);
    customSlots.put("enablenether", 4);
    customSlots.put("enableend", 6);

    // second row
    customSlots.put("motd", 10);
    customSlots.put("afkprotection", 12);
    customSlots.put("afktime", 14);
    customSlots.put("backpacksettings", 16);

    // third row
    customSlots.put("cropprotection", 18);
    customSlots.put("rightclickcropharvest", 20);
    customSlots.put("locatorbar", 22);
    customSlots.put("veinminersettings", 24);
    customSlots.put("veinchoppersettings", 26);

    // fourth row
    customSlots.put("creeperblockdamage", 30);
    customSlots.put("creeperentitydamage", 34);

    CustomGUI gui = new CustomGUI(_plugin, p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Admin Settings",
        entries, 5, customSlots, parentMenu,
        EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    if (backAction != null) {
      gui.setBackAction(backAction);
    }

    Map<String, CustomGUI.ClickAction> actions = new HashMap<>();
    actions.put("maintenance", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleMaintenance(p);
        displayGUI(p, parentMenu, backAction);
      }

      @Override
      public void onRightClick(Player p) {
        _openMaintenanceMessageDialog(p, parentMenu, backAction);
      }
    });

    actions.put("creeperblockdamage", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleCreeperBlockDamage(p);
        displayGUI(p, parentMenu, backAction);
      }
    });

    actions.put("creeperentitydamage", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleCreeperEntityDamage(p);
        displayGUI(p, parentMenu, backAction);
      }
    });

    actions.put("enableend", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleEnd(p);
        displayGUI(p, parentMenu, backAction);
      }
    });

    actions.put("enablenether", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleNether(p);
        displayGUI(p, parentMenu, backAction);
      }
    });

    actions.put("sleepingrain", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleSleepingRain(p);
        displayGUI(p, parentMenu, backAction);
      }
    });

    actions.put("cropprotection", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleCropProtection(p);
        displayGUI(p, parentMenu, backAction);
      }
    });

    actions.put("rightclickcropharvest", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleRightClickCropHarvest(p);
        displayGUI(p, parentMenu, backAction);
      }
    });

    actions.put("locatorbar", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleLocatorBar(p);
        displayGUI(p, parentMenu, backAction);
      }
    });

    actions.put("afkprotection", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleAFKProtection(p);
        displayGUI(p, parentMenu, backAction);
      }
    });

    actions.put("afktime", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _openAFKTimeDialog(p, parentMenu, backAction);
      }
    });

    actions.put("motd", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _openMOTDDialog(p, parentMenu, backAction);
      }
    });

    actions.put("backpacksettings", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _backpackSettingsGUI.displayGUI(p, gui, player -> displayGUI(player, parentMenu, backAction));
      }
    });

    actions.put("veinminersettings", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _veinMinerSettingsGUI.displayGUI(p, gui, player -> displayGUI(player, parentMenu, backAction));
      }
    });

    actions.put("veinchoppersettings", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _veinChopperSettingsGUI.displayGUI(p, gui, player -> displayGUI(player, parentMenu, backAction));
      }
    });

    gui.setClickActions(actions);
    gui.open(p);
  }

  private ItemStack _createMaintenanceItem() {
    boolean state = _settingsManager.getMaintenanceState();
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
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Right-Click: Set message")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createCreeperBlockDamageItem() {
    boolean state = _settingsManager.getCreeperBlockDamage();
    ItemStack item = new ItemStack(Material.CREEPER_HEAD);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Creeper Block Damage"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Controls whether creepers destroy blocks when exploding.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createCreeperEntityDamageItem() {
    boolean state = _settingsManager.getCreeperEntityDamage();
    ItemStack item = new ItemStack(Material.TNT);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Creeper Entity Damage"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Controls whether creepers damage non-player entities.",
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

  private ItemStack _createLocatorBarItem() {
    boolean state = _settingsManager.getLocatorBarEnabled();
    ItemStack item = new ItemStack(Material.FILLED_MAP);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Locator Bar"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Toggle the locator bar gamerule for all worlds.",
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
      meta.displayName(Component.text(
          ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Server MOTD"));

      List<String> lore = new ArrayList<>();
      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY
          + "Set the server message of the day (MOTD) visible in the server list.");
      lore.add("");

      if (motd != null && !motd.isEmpty()) {
        lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Current:");
        String[] lines = motd.split("\\n");
        for (String line : lines) {
          lore.add(ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', line));
        }
      } else {
        lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Current: " + ChatColor.RED + "Not set");
      }

      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Set value");

      meta.lore(lore.stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createBackpackSettingsItem() {
    boolean enabled = _settingsManager.getBackpackEnabled();
    int pages = _settingsManager.getBackpackPages();
    int rows = _settingsManager.getBackpackRows();
    ItemStack item = new ItemStack(Material.BARREL);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Backpack Settings"));
      List<String> lore = new ArrayList<>(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Manage the global backpack settings.",
          ""));
      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
          + (enabled ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));
      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Pages: " + ChatColor.YELLOW + pages);
      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Rows/Page: " + ChatColor.YELLOW + rows);
      lore.add("");
      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Open");
      meta.lore(lore.stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createVeinMinerSettingsItem() {
    boolean enabled = _settingsManager.getVeinMinerEnabled();
    int maxSize = _settingsManager.getVeinMinerMaxVeinSize();
    boolean sound = _settingsManager.getVeinMinerSound();
    List<String> allowedTools = _settingsManager.getVeinMinerAllowedTools();
    List<String> allowedBlocks = _settingsManager.getVeinMinerAllowedBlocks();
    int totalTools = SettingsManager.VEIN_MINER_TOOLS.size();
    int totalBlocks = SettingsManager.VEIN_MINER_BLOCKS.size();
    ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Vein Miner Settings"));
      List<String> lore = new ArrayList<>(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Manage the global vein miner settings.",
          ""));
      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
          + (enabled ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));
      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Max Size: " + ChatColor.YELLOW + maxSize);
      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Sound: "
          + (sound ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));
      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Tools: "
          + ChatColor.YELLOW + allowedTools.size() + ChatColor.GRAY + "/" + ChatColor.YELLOW + totalTools);
      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Blocks: "
          + ChatColor.YELLOW + allowedBlocks.size() + ChatColor.GRAY + "/" + ChatColor.YELLOW + totalBlocks);
      lore.add("");
      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Open");
      meta.lore(lore.stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createVeinChopperSettingsItem() {
    boolean enabled = _settingsManager.getVeinChopperEnabled();
    int maxSize = _settingsManager.getVeinChopperMaxVeinSize();
    boolean sound = _settingsManager.getVeinChopperSound();
    List<String> allowedTools = _settingsManager.getVeinChopperAllowedTools();
    List<String> allowedBlocks = _settingsManager.getVeinChopperAllowedBlocks();
    int totalTools = SettingsManager.VEIN_CHOPPER_TOOLS.size();
    int totalBlocks = SettingsManager.VEIN_CHOPPER_BLOCKS.size();
    ItemStack item = new ItemStack(Material.DIAMOND_AXE);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Vein Chopper Settings"));
      List<String> lore = new ArrayList<>(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Manage the global vein chopper settings.",
          ""));
      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
          + (enabled ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));
      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Max Size: " + ChatColor.YELLOW + maxSize);
      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Sound: "
          + (sound ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));
      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Tools: "
          + ChatColor.YELLOW + allowedTools.size() + ChatColor.GRAY + "/" + ChatColor.YELLOW + totalTools);
      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Blocks: "
          + ChatColor.YELLOW + allowedBlocks.size() + ChatColor.GRAY + "/" + ChatColor.YELLOW + totalBlocks);
      lore.add("");
      lore.add(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Open");
      meta.lore(lore.stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  // -------
  // DIALOGS
  // -------

  private void _openMOTDDialog(Player p, CustomGUI parentMenu, Consumer<Player> backAction) {
    String[] motdLines = _settingsManager.getServerMOTDRaw();

    String motdLine1 = (motdLines != null && motdLines.length > 0 && motdLines[0] != null) ? motdLines[0] : "";
    String motdLine2 = (motdLines != null && motdLines.length > 1 && motdLines[1] != null) ? motdLines[1] : "";

    DialogInput inputMotdLine1 = DialogInput
        .text("line1", Component.text(ChatColor.YELLOW + "» " + ChatColor.GRAY + "MOTD Line 1"))
        .initial(motdLine1)
        .maxLength(Integer.MAX_VALUE)
        .build();
    DialogInput inputMotdLine2 = DialogInput
        .text("line2", Component.text(ChatColor.YELLOW + "» " + ChatColor.GRAY + "MOTD Line 2"))
        .initial(motdLine2)
        .maxLength(Integer.MAX_VALUE)
        .build();

    Dialog dialog = CustomDialog.createConfirmationDialog(
        "Server MOTD",
        "Set the server message of the day (MOTD) visible in the server list.",
        null,
        List.of(inputMotdLine1, inputMotdLine2),
        (view, audience) -> _setServerMOTDDialogCB(view, audience, parentMenu, backAction),
        null);

    p.showDialog(dialog);
  }

  private void _openMaintenanceMessageDialog(Player p, CustomGUI parentMenu, Consumer<Player> backAction) {
    String maintenanceMessage = _settingsManager.getMaintenanceMessage();

    DialogInput inputMessage = DialogInput
        .text("message", Component.text(ChatColor.YELLOW + "» " + ChatColor.GRAY + "Maintenance Message"))
        .initial(maintenanceMessage != null ? maintenanceMessage : "")
        .maxLength(Integer.MAX_VALUE)
        .build();

    Dialog dialog = CustomDialog.createConfirmationDialog(
        "Maintenance Message",
        "Set the maintenance message displayed to players when they are kicked.",
        null,
        List.of(inputMessage),
        (view, audience) -> _setMaintenanceMessageDialogCB(view, audience, parentMenu, backAction),
        null);

    p.showDialog(dialog);
  }

  private void _openAFKTimeDialog(Player p, CustomGUI parentMenu, Consumer<Player> backAction) {
    int afkTime = _settingsManager.getAFKTime();

    DialogInput inputMinutes = CustomDialog.createNumberInput("minutes",
        ChatColor.YELLOW + "» " + ChatColor.GRAY + "AFK Time (minutes)", 1, 300, 1, (float) afkTime);

    Dialog dialog = CustomDialog.createConfirmationDialog(
        "AFK Time",
        "Set the AFK timeout in minutes.",
        null,
        List.of(inputMinutes),
        (view, audience) -> _setAFKTimeDialogCB(view, audience, parentMenu, backAction),
        null);

    p.showDialog(dialog);
  }

  // ----------------
  // DIALOG CALLBACKS
  // ----------------

  private void _setServerMOTDDialogCB(DialogResponseView view, Audience audience, CustomGUI parentMenu,
      Consumer<Player> backAction) {
    Player p = (Player) audience;
    String line1 = Optional.ofNullable(view.getText("line1")).map(String::trim).orElse("");
    String line2 = Optional.ofNullable(view.getText("line2")).map(String::trim).orElse("");

    _settingsManager.setServerMOTD(line1, line2);

    p.sendMessage(Component.text(Main.getPrefix() + "Server MOTD set to:\n" +
        "Line 1: " + ChatColor.translateAlternateColorCodes('&', line1) + "\n" + ChatColor.GRAY +
        "Line 2: " + ChatColor.translateAlternateColorCodes('&', line2)));
    p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);

    displayGUI(p, parentMenu, backAction);
  }

  private void _setMaintenanceMessageDialogCB(DialogResponseView view, Audience audience, CustomGUI parentMenu,
      Consumer<Player> backAction) {
    Player p = (Player) audience;
    String message = Optional.ofNullable(view.getText("message")).map(String::trim).orElse("");

    _settingsManager.setMaintenanceMessage(message);

    p.sendMessage(Component.text(Main.getPrefix() + "Maintenance message set to: " + ChatColor.YELLOW + message));
    p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);

    displayGUI(p, parentMenu, backAction);
  }

  private void _setAFKTimeDialogCB(DialogResponseView view, Audience audience, CustomGUI parentMenu,
      Consumer<Player> backAction) {
    Player p = (Player) audience;
    int minutes = Math.round(view.getFloat("minutes"));

    _settingsManager.setAFKTime((int) minutes);

    p.sendMessage(Component
        .text(Main.getPrefix() + "AFK time set to: " + ChatColor.YELLOW + minutes + ChatColor.GRAY + " minutes"));
    p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);

    displayGUI(p, parentMenu, backAction);
  }

  // ----------------
  // SETTING APPLIERS
  // ----------------

  private void _toggleMaintenance(Player p) {
    boolean newState = !_settingsManager.getMaintenanceState();
    String message = _settingsManager.getMaintenanceMessage();

    _settingsManager.setMaintenanceState(newState);
    String stateText = newState ? "ENABLED" : "DISABLED";

    p.sendMessage(
        Main.getPrefix() + "The maintenance mode is now: " + ChatColor.YELLOW + ChatColor.BOLD + stateText);

    if (newState) {
      p.sendMessage(Main.getPrefix() + "Message: " + ChatColor.YELLOW + message);
    }

    _maintenanceManager.kickAll(_plugin.getServer().getOnlinePlayers());
  }

  private void _toggleCreeperBlockDamage(Player p) {
    boolean newState = !_settingsManager.getCreeperBlockDamage();
    _settingsManager.setCreeperBlockDamage(newState);
    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(
        Main.getPrefix() + "Creeper block damage is now: " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleCreeperEntityDamage(Player p) {
    boolean newState = !_settingsManager.getCreeperEntityDamage();
    _settingsManager.setCreeperEntityDamage(newState);
    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(
        Main.getPrefix() + "Creeper entity damage is now: " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
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

  private void _toggleLocatorBar(Player p) {
    boolean newState = !_settingsManager.getLocatorBarEnabled();
    _settingsManager.setLocatorBarEnabled(newState);
    _settingsManager.applyLocatorBarSetting();
    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Locator bar is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleAFKProtection(Player p) {
    boolean newState = !_settingsManager.getAFKProtection();
    _settingsManager.setAFKProtection(newState);
    _afkManager.applyProtectionToAFKPlayers(newState);
    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "AFK protection is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }
}
