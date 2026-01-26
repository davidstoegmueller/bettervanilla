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
import com.daveestar.bettervanilla.manager.NameTagManager;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.manager.TabListManager;
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
  private final NameTagManager _nameTagManager;
  private final TabListManager _tabListManager;
  private final BackpackSettingsGUI _backpackSettingsGUI;
  private final VeinMinerSettingsGUI _veinMinerSettingsGUI;
  private final VeinChopperSettingsGUI _veinChopperSettingsGUI;
  private final CraftingRecipeSettingsGUI _craftingRecipeSettingsGUI;

  public AdminSettingsGUI() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
    _afkManager = _plugin.getAFKManager();
    _maintenanceManager = _plugin.getMaintenanceManager();
    _nameTagManager = _plugin.getNameTagManager();
    _tabListManager = _plugin.getTabListManager();
    _backpackSettingsGUI = new BackpackSettingsGUI();
    _veinMinerSettingsGUI = new VeinMinerSettingsGUI();
    _veinChopperSettingsGUI = new VeinChopperSettingsGUI();
    _craftingRecipeSettingsGUI = new CraftingRecipeSettingsGUI();
  }

  public void displayGUI(Player p, CustomGUI parentMenu) {
    displayGUI(p, parentMenu, null);
  }

  public void displayGUI(Player p, CustomGUI parentMenu, Consumer<Player> backAction) {
    Map<String, ItemStack> entries = new HashMap<>();
    // top row
    entries.put("maintenance", _createMaintenanceItem());
    entries.put("motd", _createMOTDItem());
    entries.put("sleepingrain", _createSleepingRainItem());
    entries.put("enablenether", _createEnableNetherItem());
    entries.put("enableend", _createEnableEndItem());

    // second row
    entries.put("creeperblockdamage", _createCreeperBlockDamageItem());
    entries.put("creeperentitydamage", _createCreeperEntityDamageItem());
    entries.put("cropprotection", _createCropProtectionItem());
    entries.put("rightclickcropharvest", _createRightClickCropHarvestItem());

    // third row
    entries.put("afkprotection", _createAFKProtectionItem());
    entries.put("afktime", _createAFKTimeItem());
    entries.put("locatorbar", _createLocatorBarItem());
    entries.put("deathchest", _createDeathChestItem());
    entries.put("itemrestock", _createItemRestockItem());

    // fourth row
    entries.put("backpacksettings", _createBackpackSettingsItem());
    entries.put("veinminersettings", _createVeinMinerSettingsItem());
    entries.put("craftingrecipes", _createCraftingRecipesItem());
    entries.put("veinchoppersettings", _createVeinChopperSettingsItem());

    // fifth row
    entries.put("recipesync", _createRecipeSyncItem());
    entries.put("actionbartimer", _createActionBarTimerItem());
    entries.put("sleepingpercentage", _createSleepingPercentageItem());
    entries.put("playertag", _createTagsItem());

    Map<String, Integer> customSlots = new HashMap<>();
    // top row - slots 0 to 8
    customSlots.put("maintenance", 0);
    customSlots.put("motd", 2);
    customSlots.put("sleepingrain", 4);
    customSlots.put("enablenether", 6);
    customSlots.put("enableend", 8);

    // second row - slots 9 to 17
    customSlots.put("creeperblockdamage", 10);
    customSlots.put("creeperentitydamage", 12);
    customSlots.put("cropprotection", 14);
    customSlots.put("rightclickcropharvest", 16);

    // third row - slots 18 to 26
    customSlots.put("deathchest", 18);
    customSlots.put("afkprotection", 20);
    customSlots.put("afktime", 22);
    customSlots.put("locatorbar", 24);
    customSlots.put("itemrestock", 26);

    // fourth row - slots 27 to 35
    customSlots.put("backpacksettings", 28);
    customSlots.put("veinminersettings", 30);
    customSlots.put("craftingrecipes", 32);
    customSlots.put("veinchoppersettings", 34);

    // fifth row - slots 36 to 44
    customSlots.put("recipesync", 38);
    customSlots.put("actionbartimer", 40);
    customSlots.put("sleepingpercentage", 42);
    customSlots.put("tags", 44);

    CustomGUI gui = new CustomGUI(_plugin, p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» Admin Settings",
        entries, 6, customSlots, parentMenu,
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

    actions.put("sleepingpercentage", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _openSleepingPercentageDialog(p, parentMenu, backAction);
      }
    });

    actions.put("tags", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleTags(p);
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

    actions.put("actionbartimer", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleActionBarTimer(p);
        displayGUI(p, parentMenu, backAction);
      }
    });

    actions.put("recipesync", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleRecipeSync(p);
        displayGUI(p, parentMenu, backAction);
      }
    });

    actions.put("deathchest", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleDeathChest(p);
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
    actions.put("craftingrecipes", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _craftingRecipeSettingsGUI.displayGUI(p, gui, player -> displayGUI(player, parentMenu, backAction));
      }
    });

    actions.put("veinchoppersettings", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _veinChopperSettingsGUI.displayGUI(p, gui, player -> displayGUI(player, parentMenu, backAction));
      }
    });
    actions.put("itemrestock", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleItemRestock(p);
        displayGUI(p, parentMenu, backAction);
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

  private ItemStack _createSleepingPercentageItem() {
    int percentage = _settingsManager.getPlayersSleepingPercentage();
    ItemStack item = new ItemStack(Material.RED_BED);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Sleeping Percentage"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Set the players sleeping percentage gamerule.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Value: " + ChatColor.YELLOW + percentage,
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Set percentage")
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

  private ItemStack _createRecipeSyncItem() {
    boolean state = _settingsManager.getRecipeSyncEnabled();
    ItemStack item = new ItemStack(Material.BOOK);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Recipe Sync"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Sync custom and vanilla recipes to modded clients.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createActionBarTimerItem() {
    boolean state = _settingsManager.getActionBarTimerEnabled();
    ItemStack item = new ItemStack(Material.CLOCK);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Action-Bar Timer"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Globally enable the timer in the actionbar.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createTagsItem() {
    boolean state = _settingsManager.getTagsEnabled();
    ItemStack item = new ItemStack(Material.NAME_TAG);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Player Tag"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Globally enable player tags in chat and tab.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createDeathChestItem() {
    boolean state = _settingsManager.getDeathChestEnabled();
    ItemStack item = new ItemStack(Material.CHEST);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Death Chest"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Controls whether death chests spawn on player death.",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Disabling drops items and hides stored inventories.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createItemRestockItem() {
    boolean state = _settingsManager.getItemRestockEnabled();
    ItemStack item = new ItemStack(Material.HOPPER);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Item Restock"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Globally enable automatic hotbar restocking.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: "
              + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createCraftingRecipesItem() {
    ItemStack item = new ItemStack(Material.CRAFTING_TABLE);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + "Crafting Recipes"));
      meta.lore(Arrays.asList(
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Configure custom crafting recipes.",
          "",
          ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Open")
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

  private void _openSleepingPercentageDialog(Player p, CustomGUI parentMenu, Consumer<Player> backAction) {
    int percentage = _settingsManager.getPlayersSleepingPercentage();

    DialogInput inputPercentage = CustomDialog.createNumberInput("percentage",
        ChatColor.YELLOW + "» " + ChatColor.GRAY + "Players Sleeping Percentage", 0, 100, 1, (float) percentage);

    Dialog dialog = CustomDialog.createConfirmationDialog(
        "Players Sleeping Percentage",
        "Set the percentage of players required to sleep to skip the night.",
        null,
        List.of(inputPercentage),
        (view, audience) -> _setSleepingPercentageDialogCB(view, audience, parentMenu, backAction),
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

  private void _setSleepingPercentageDialogCB(DialogResponseView view, Audience audience, CustomGUI parentMenu,
      Consumer<Player> backAction) {
    Player p = (Player) audience;
    int percentage = Math.round(view.getFloat("percentage"));
    percentage = Math.max(0, Math.min(100, percentage));

    _settingsManager.setPlayersSleepingPercentage(percentage);
    _settingsManager.applyPlayersSleepingPercentageSetting();

    p.sendMessage(
        Component.text(Main.getPrefix() + "Players sleeping percentage set to: " + ChatColor.YELLOW + percentage));
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

  private void _toggleRecipeSync(Player p) {
    boolean newState = !_settingsManager.getRecipeSyncEnabled();
    _settingsManager.setRecipeSyncEnabled(newState);
    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Recipe sync is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleActionBarTimer(Player p) {
    boolean newState = !_settingsManager.getActionBarTimerEnabled();
    _settingsManager.setActionBarTimerEnabled(newState);
    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Action-Bar timer is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleTags(Player p) {
    boolean newState = !_settingsManager.getTagsEnabled();
    _settingsManager.setTagsEnabled(newState);
    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Player tags are now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);

    for (Player online : p.getServer().getOnlinePlayers()) {
      _nameTagManager.updateNameTag(online);
      _tabListManager.refreshPlayerListEntry(online);
    }
  }

  private void _toggleDeathChest(Player p) {
    boolean newState = !_settingsManager.getDeathChestEnabled();
    _settingsManager.setDeathChestEnabled(newState);
    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Death chest creation is now "
        + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleAFKProtection(Player p) {
    boolean newState = !_settingsManager.getAFKProtection();
    _settingsManager.setAFKProtection(newState);
    _afkManager.applyProtectionToAFKPlayers(newState);
    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "AFK protection is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }

  private void _toggleItemRestock(Player p) {
    boolean newState = !_settingsManager.getItemRestockEnabled();
    _settingsManager.setItemRestockEnabled(newState);
    String stateText = newState ? "ENABLED" : "DISABLED";
    p.sendMessage(Main.getPrefix() + "Item restock is now " + ChatColor.YELLOW + ChatColor.BOLD + stateText);
  }
}
