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

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.manager.AFKManager;
import com.daveestar.bettervanilla.manager.MaintenanceManager;
import com.daveestar.bettervanilla.manager.NameTagManager;
import com.daveestar.bettervanilla.manager.SettingsManager;
import com.daveestar.bettervanilla.manager.TabListManager;
import com.daveestar.bettervanilla.manager.TranslationManager;
import com.daveestar.bettervanilla.utils.CustomDialog;
import com.daveestar.bettervanilla.utils.CustomGUI;
import com.daveestar.bettervanilla.utils.Theme;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;

import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

public class AdminSettingsGUI {
  private final Main _plugin;
  private final SettingsManager _settingsManager;
  private final com.daveestar.bettervanilla.manager.HeadsManager _headsManager;
  private final AFKManager _afkManager;
  private final MaintenanceManager _maintenanceManager;
  private final NameTagManager _nameTagManager;
  private final TabListManager _tabListManager;
  private final BackpackSettingsGUI _backpackSettingsGUI;
  private final VeinMinerSettingsGUI _veinMinerSettingsGUI;
  private final VeinChopperSettingsGUI _veinChopperSettingsGUI;
  private final CraftingRecipeSettingsGUI _craftingRecipeSettingsGUI;
  private final ThemeSettingsGUI _themeSettingsGUI;
  private final TranslationManager _translations;
  private Player _viewer;

  public AdminSettingsGUI() {
    _plugin = Main.getInstance();
    _settingsManager = _plugin.getSettingsManager();
    _headsManager = _plugin.getHeadsManager();
    _afkManager = _plugin.getAFKManager();
    _maintenanceManager = _plugin.getMaintenanceManager();
    _nameTagManager = _plugin.getNameTagManager();
    _tabListManager = _plugin.getTabListManager();
    _backpackSettingsGUI = new BackpackSettingsGUI();
    _veinMinerSettingsGUI = new VeinMinerSettingsGUI();
    _veinChopperSettingsGUI = new VeinChopperSettingsGUI();
    _craftingRecipeSettingsGUI = new CraftingRecipeSettingsGUI();
    _themeSettingsGUI = new ThemeSettingsGUI();
    _translations = _plugin.getTranslationManager();
  }

  public void displayGUI(Player p, CustomGUI parentMenu) {
    displayGUI(p, parentMenu, null);
  }

  public void displayGUI(Player p, CustomGUI parentMenu, Consumer<Player> backAction) {
    _viewer = p;
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
    entries.put("themesettings", _createThemeSettingsItem());

    // fifth row
    entries.put("recipesync", _createRecipeSyncItem());
    entries.put("actionbartimer", _createActionBarTimerItem());
    entries.put("sleepingpercentage", _createSleepingPercentageItem());
    entries.put("playertag", _createTagsItem());
    entries.put("headsexplorer", _createHeadsExplorerItem());
    entries.put("language", _createLanguageItem(p));

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
    customSlots.put("backpacksettings", 27);
    customSlots.put("veinminersettings", 29);
    customSlots.put("craftingrecipes", 31);
    customSlots.put("veinchoppersettings", 33);
    customSlots.put("themesettings", 35);

    // fifth row - slots 36 to 44
    customSlots.put("headsexplorer", 36);
    customSlots.put("recipesync", 38);
    customSlots.put("actionbartimer", 40);
    customSlots.put("sleepingpercentage", 42);
    customSlots.put("playertag", 44);
    customSlots.put("language", 49);

    CustomGUI gui = new CustomGUI(_plugin, p,
        Theme.titlePrefix() + _translations.translate(p, "admin-settings-gui-title"),
        entries, 6, customSlots, parentMenu,
        EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    if (backAction != null) {
      gui.setBackAction(backAction);
    }

    Map<String, CustomGUI.ClickAction> actions = new HashMap<>();
    actions.put("language", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _openLanguageDialog(p, parentMenu, backAction);
      }
    });
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

    actions.put("playertag", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleTags(p);
        displayGUI(p, parentMenu, backAction);
      }
    });

    actions.put("headsexplorer", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _toggleHeadsExplorer(p);
        displayGUI(p, parentMenu, backAction);
      }

      @Override
      public void onRightClick(Player p) {
        _openHeadsExplorerApiKeyDialog(p, parentMenu, backAction);
      }

      @Override
      public void onShiftLeftClick(Player p) {
        _refreshHeadsExplorerData(p, parentMenu, backAction);
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
    actions.put("themesettings", new CustomGUI.ClickAction() {
      @Override
      public void onLeftClick(Player p) {
        _themeSettingsGUI.displayGUI(p, gui, player -> displayGUI(player, parentMenu, backAction));
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

  private ItemStack _createLanguageItem(Player viewer) {
    String code = _settingsManager.getServerLanguage();
    String language = com.daveestar.bettervanilla.enums.Language.fromCode(code).getDisplayName(viewer);
    ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.displayName(Component.text(Theme.titlePrefix()
          + _translations.translate(viewer, "admin-language-item-title")));
      meta.lore(List.of(
          Component.text(Theme.textPrefix() + _translations.translate(viewer,
              "admin-language-item-description")),
          Component.empty(),
          Component.text(Theme.textPrefix() + _translations.translate(viewer,
              "admin-language-item-current", "language", language)),
          Component.empty(),
          Component.text(Theme.textPrefix() + _translations.translate(viewer,
              "admin-language-item-action"))));
      item.setItemMeta(meta);
    }
    return item;
  }

  private void _openLanguageDialog(Player viewer, CustomGUI parentMenu, Consumer<Player> backAction) {
    DialogInput input = CustomDialog.createSelectInput("language",
        Theme.textPrefix() + _translations.translate(viewer, "admin-language-dialog-input-label"),
        _translations.getLanguageOptions(viewer), _settingsManager.getServerLanguage());
    Dialog dialog = CustomDialog.createConfirmationDialog(
        _translations.translate(viewer, "admin-language-dialog-title"),
        _translations.translate(viewer, "admin-language-dialog-description"),
        null, List.of(input), (view, audience) -> {
          String code = Optional.ofNullable(view.getText("language")).orElse("en");
          _settingsManager.setServerLanguage(code);
          Player player = (Player) audience;
          String name = com.daveestar.bettervanilla.enums.Language.fromCode(code).getDisplayName(player);
          player.sendMessage(Main.getPrefix() + _translations.translate(player,
              "admin-language-changed-message", "language", name));
          displayGUI(player, parentMenu, backAction);
        }, null, _translations.translate(viewer, "dialog-button-apply"),
        _translations.translate(viewer, "dialog-button-cancel"));
    viewer.showDialog(dialog);
  }

  private ItemStack _createMaintenanceItem() {
    boolean state = _settingsManager.getMaintenanceState();
    String message = _settingsManager.getMaintenanceMessage();
    ItemStack item = new ItemStack(Material.IRON_BARS);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(Theme.titlePrefix() + _t("admin-maintenance-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t("admin-maintenance-description"),
          "",

          Theme.textPrefix() + _t("gui-common-state", "state", _state(state)),
          Theme.textPrefix() + _t("admin-maintenance-message", "message",
              message != null && !message.isEmpty() ? Theme.highlight() + message
                  : Theme.error() + _t("common-value-none")),
          "",
          Theme.textPrefix() + _t("gui-common-action-toggle"),
          Theme.textPrefix() + _t("admin-maintenance-action-message"))
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
          Component.text(Theme.titlePrefix() + _t("admin-creeper-block-damage-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t("admin-creeper-block-damage-description"),
          "",
          Theme.textPrefix() + _t("gui-common-state", "state", _state(state)),
          "",
          Theme.textPrefix() + _t("gui-common-action-toggle"))
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
          Component.text(Theme.titlePrefix() + _t("admin-creeper-entity-damage-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t("admin-creeper-entity-damage-description"),
          "",
          Theme.textPrefix() + _t("gui-common-state", "state", _state(state)),
          "",
          Theme.textPrefix() + _t("gui-common-action-toggle"))
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
      meta.displayName(Component.text(Theme.titlePrefix() + _t("admin-end-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t("admin-end-description"),
          "",
          Theme.textPrefix() + _t("gui-common-state", "state", _state(state)),
          "",
          Theme.textPrefix() + _t("gui-common-action-toggle"))
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
      meta.displayName(Component.text(Theme.titlePrefix() + _t("admin-nether-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t("admin-nether-description"),
          "",
          Theme.textPrefix() + _t("gui-common-state", "state", _state(state)),
          "",
          Theme.textPrefix() + _t("gui-common-action-toggle"))
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
      meta.displayName(Component.text(Theme.titlePrefix() + _t("admin-sleeping-rain-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t("admin-sleeping-rain-description"),
          "",
          Theme.textPrefix() + _t("gui-common-state", "state", _state(state)),
          "",
          Theme.textPrefix() + _t("gui-common-action-toggle"))
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
          Component.text(Theme.titlePrefix() + _t("admin-sleep-percentage-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t("admin-sleep-percentage-description"),
          "",
          Theme.textPrefix() + _t("gui-common-value", "value", Theme.highlight().toString() + percentage),
          "",
          Theme.textPrefix() + _t("admin-sleep-percentage-action"))
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
          Component.text(Theme.titlePrefix() + _t("admin-crop-protection-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t("admin-crop-protection-description"),
          "",
          Theme.textPrefix() + _t("gui-common-state", "state", _state(state)),
          "",
          Theme.textPrefix() + _t("gui-common-action-toggle"))
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
      meta.displayName(
          Component.text(Theme.titlePrefix() + _t("admin-right-click-harvest-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t("admin-right-click-harvest-description"),
          "",
          Theme.textPrefix() + _t("gui-common-state", "state", _state(state)),
          "",
          Theme.textPrefix() + _t("gui-common-action-toggle"))
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
          Component.text(Theme.titlePrefix() + _t("admin-locator-bar-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t("admin-locator-bar-description"),
          "",
          Theme.textPrefix() + _t("gui-common-state", "state", _state(state)),
          "",
          Theme.textPrefix() + _t("gui-common-action-toggle"))
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
          Component.text(Theme.titlePrefix() + _t("admin-recipe-sync-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t("admin-recipe-sync-description"),
          "",
          Theme.textPrefix() + _t("gui-common-state", "state", _state(state)),
          "",
          Theme.textPrefix() + _t("gui-common-action-toggle"))
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
          Component.text(Theme.titlePrefix() + _t("admin-action-bar-timer-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t("admin-action-bar-timer-description"),
          "",
          Theme.textPrefix() + _t("gui-common-state", "state", _state(state)),
          "",
          Theme.textPrefix() + _t("gui-common-action-toggle"))
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createHeadsExplorerItem() {
    boolean state = _settingsManager.getHeadsExplorerEnabled();
    String apiKey = _settingsManager.getHeadsExplorerApiKey();
    boolean hasApiKey = apiKey != null && !apiKey.trim().isEmpty();
    ItemStack item = new ItemStack(Material.PLAYER_HEAD);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(Theme.titlePrefix() + _t("admin-heads-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t("admin-heads-description"),
          Theme.textPrefix() + _t("admin-heads-command-description"),
          "",
          Theme.textPrefix() + _t("gui-common-state", "state", _state(state)),
          Theme.textPrefix() + _t("admin-heads-api-state", "state",
              (hasApiKey ? Theme.highlight() : Theme.error())
                  + _t(hasApiKey ? "common-state-set" : "common-state-not-set")),
          "",
          Theme.textPrefix() + _t("admin-heads-api-optional"),
          Theme.textPrefix() + _t("admin-heads-api-update-hint"),
          "",
          Theme.textPrefix() + _t("gui-common-action-toggle"),
          Theme.textPrefix() + _t("admin-heads-action-api-key"),
          Theme.textPrefix() + _t("admin-heads-action-refresh"))
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
          Component.text(Theme.titlePrefix() + _t("admin-tags-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t("admin-tags-description"),
          "",
          Theme.textPrefix() + _t("gui-common-state", "state", _state(state)),
          "",
          Theme.textPrefix() + _t("gui-common-action-toggle"))
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
          Component.text(Theme.titlePrefix() + _t("admin-death-chest-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t("admin-death-chest-description"),
          Theme.textPrefix() + _t("admin-death-chest-disabled-description"),
          "",
          Theme.textPrefix() + _t("gui-common-state", "state", _state(state)),
          "",
          Theme.textPrefix() + _t("gui-common-action-toggle"))
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
          Component.text(Theme.titlePrefix() + _t("admin-item-restock-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t("admin-item-restock-description"),
          "",
          Theme.textPrefix() + _t("gui-common-state", "state", _state(state)),
          "",
          Theme.textPrefix() + _t("gui-common-action-toggle"))
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
          Component.text(Theme.titlePrefix() + _t("admin-crafting-recipes-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t("admin-crafting-recipes-description"),
          "",
          Theme.textPrefix() + _t("gui-common-action-open"))
          .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));

      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createThemeSettingsItem() {
    ItemStack item = new ItemStack(Material.PAINTING);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(Component.text(Theme.titlePrefix() + _t("admin-theme-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t("admin-theme-description"),
          "",
          Theme.textPrefix() + _t("gui-common-action-open"))
          .stream().filter(Objects::nonNull).map(Component::text).toList());
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
          Component.text(Theme.titlePrefix() + _t("admin-afk-protection-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t("admin-afk-protection-description"),
          "",
          Theme.textPrefix() + _t("gui-common-state", "state", _state(state)),
          "",
          Theme.textPrefix() + _t("gui-common-action-toggle"))
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
      meta.displayName(Component.text(Theme.titlePrefix() + _t("admin-afk-time-title")));
      meta.lore(Arrays.asList(
          Theme.textPrefix() + _t("admin-afk-time-description"),
          "",
          Theme.textPrefix() + _t("admin-afk-time-current", "minutes", minutes),
          "",
          Theme.textPrefix() + _t("gui-common-action-set-value"))
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
          Theme.titlePrefix() + _t("admin-motd-title")));

      List<String> lore = new ArrayList<>();
      lore.add(Theme.textPrefix()
          + _t("admin-motd-description"));
      lore.add("");

      if (motd != null && !motd.isEmpty()) {
        lore.add(Theme.textPrefix() + _t("gui-common-current"));
        String[] lines = motd.split("\\n");
        for (String line : lines) {
          lore.add(Theme.primary() + ChatColor.translateAlternateColorCodes('&', line));
        }
      } else {
        lore.add(Theme.textPrefix() + _t("gui-common-current-value", "value",
            Theme.error() + _t("common-state-not-set")));
      }

      lore.add("");
      lore.add(Theme.textPrefix() + _t("gui-common-action-set-value"));

      meta.lore(lore.stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
      item.setItemMeta(meta);
    }

    return item;
  }

  private ItemStack _createBackpackSettingsItem() {
    boolean enabled = _settingsManager.getBackpackEnabled();
    int pages = _settingsManager.getBackpackPages();
    int rows = _settingsManager.getBackpackRows();
    ItemStack item = new ItemStack(Material.BUNDLE);
    ItemMeta meta = item.getItemMeta();

    if (meta != null) {
      meta.displayName(
          Component.text(Theme.titlePrefix() + _t("admin-backpack-settings-title")));
      List<String> lore = new ArrayList<>(Arrays.asList(
          Theme.textPrefix() + _t("admin-backpack-settings-description"),
          Theme.textPrefix() + _t("admin-backpack-settings-command-description"),
          ""));
      lore.add(Theme.textPrefix() + _t("gui-common-state", "state", _state(enabled)));
      lore.add(Theme.textPrefix() + _t("admin-backpack-settings-pages", "pages", pages));
      lore.add(Theme.textPrefix() + _t("admin-backpack-settings-rows", "rows", rows));
      lore.add("");
      lore.add(Theme.textPrefix() + _t("gui-common-action-open"));
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
      meta.displayName(
          Component.text(Theme.titlePrefix() + _t("admin-vein-miner-settings-title")));
      List<String> lore = new ArrayList<>(Arrays.asList(
          Theme.textPrefix() + _t("admin-vein-miner-settings-description"),
          ""));
      lore.add(Theme.textPrefix() + _t("gui-common-state", "state", _state(enabled)));
      lore.add(Theme.textPrefix() + _t("admin-vein-settings-max-size", "size", maxSize));
      lore.add(Theme.textPrefix() + _t("admin-vein-settings-sound", "state", _state(sound)));
      lore.add(
          Theme.textPrefix() + _t("admin-vein-settings-tools", "selected", allowedTools.size(), "total", totalTools));
      lore.add(Theme.textPrefix()
          + _t("admin-vein-settings-blocks", "selected", allowedBlocks.size(), "total", totalBlocks));
      lore.add("");
      lore.add(Theme.textPrefix() + _t("gui-common-action-open"));
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
      meta.displayName(
          Component.text(Theme.titlePrefix() + _t("admin-vein-chopper-settings-title")));
      List<String> lore = new ArrayList<>(Arrays.asList(
          Theme.textPrefix() + _t("admin-vein-chopper-settings-description"),
          ""));
      lore.add(Theme.textPrefix() + _t("gui-common-state", "state", _state(enabled)));
      lore.add(Theme.textPrefix() + _t("admin-vein-settings-max-size", "size", maxSize));
      lore.add(Theme.textPrefix() + _t("admin-vein-settings-sound", "state", _state(sound)));
      lore.add(
          Theme.textPrefix() + _t("admin-vein-settings-tools", "selected", allowedTools.size(), "total", totalTools));
      lore.add(Theme.textPrefix()
          + _t("admin-vein-settings-blocks", "selected", allowedBlocks.size(), "total", totalBlocks));
      lore.add("");
      lore.add(Theme.textPrefix() + _t("gui-common-action-open"));
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
        .text("line1", Component.text(Theme.textPrefix() + Main.tr(p, "admin-motd-dialog-line-one")))
        .initial(motdLine1)
        .maxLength(Integer.MAX_VALUE)
        .build();
    DialogInput inputMotdLine2 = DialogInput
        .text("line2", Component.text(Theme.textPrefix() + Main.tr(p, "admin-motd-dialog-line-two")))
        .initial(motdLine2)
        .maxLength(Integer.MAX_VALUE)
        .build();

    Dialog dialog = CustomDialog.createConfirmationDialog(
        Main.tr(p, "admin-motd-dialog-title"),
        Main.tr(p, "admin-motd-dialog-description"),
        null,
        List.of(inputMotdLine1, inputMotdLine2),
        (view, audience) -> _setServerMOTDDialogCB(view, audience, parentMenu, backAction),
        null, Main.tr(p, "dialog-button-apply"), Main.tr(p, "dialog-button-cancel"));

    p.showDialog(dialog);
  }

  private void _openMaintenanceMessageDialog(Player p, CustomGUI parentMenu, Consumer<Player> backAction) {
    String maintenanceMessage = _settingsManager.getMaintenanceMessage();

    DialogInput inputMessage = DialogInput
        .text("message", Component.text(Theme.textPrefix() + Main.tr(p, "admin-maintenance-dialog-input")))
        .initial(maintenanceMessage != null ? maintenanceMessage : "")
        .maxLength(Integer.MAX_VALUE)
        .build();

    Dialog dialog = CustomDialog.createConfirmationDialog(
        Main.tr(p, "admin-maintenance-dialog-title"),
        Main.tr(p, "admin-maintenance-dialog-description"),
        null,
        List.of(inputMessage),
        (view, audience) -> _setMaintenanceMessageDialogCB(view, audience, parentMenu, backAction),
        null, Main.tr(p, "dialog-button-apply"), Main.tr(p, "dialog-button-cancel"));

    p.showDialog(dialog);
  }

  private void _openAFKTimeDialog(Player p, CustomGUI parentMenu, Consumer<Player> backAction) {
    int afkTime = _settingsManager.getAFKTime();

    DialogInput inputMinutes = CustomDialog.createNumberInput("minutes",
        Theme.textPrefix() + Main.tr(p, "admin-afk-time-dialog-input"), 1, 300, 1, (float) afkTime);

    Dialog dialog = CustomDialog.createConfirmationDialog(
        Main.tr(p, "admin-afk-time-dialog-title"),
        Main.tr(p, "admin-afk-time-dialog-description"),
        null,
        List.of(inputMinutes),
        (view, audience) -> _setAFKTimeDialogCB(view, audience, parentMenu, backAction),
        null, Main.tr(p, "dialog-button-apply"), Main.tr(p, "dialog-button-cancel"));

    p.showDialog(dialog);
  }

  private void _openSleepingPercentageDialog(Player p, CustomGUI parentMenu, Consumer<Player> backAction) {
    int percentage = _settingsManager.getPlayersSleepingPercentage();

    DialogInput inputPercentage = CustomDialog.createNumberInput("percentage",
        Theme.textPrefix() + Main.tr(p, "admin-sleep-percentage-dialog-input"), 0, 100, 1, (float) percentage);

    Dialog dialog = CustomDialog.createConfirmationDialog(
        Main.tr(p, "admin-sleep-percentage-dialog-title"),
        Main.tr(p, "admin-sleep-percentage-dialog-description"),
        null,
        List.of(inputPercentage),
        (view, audience) -> _setSleepingPercentageDialogCB(view, audience, parentMenu, backAction),
        null, Main.tr(p, "dialog-button-apply"), Main.tr(p, "dialog-button-cancel"));

    p.showDialog(dialog);
  }

  private void _openHeadsExplorerApiKeyDialog(Player p, CustomGUI parentMenu, Consumer<Player> backAction) {
    String apiKey = _settingsManager.getHeadsExplorerApiKey();

    DialogInput inputApiKey = DialogInput
        .text("apikey", Component.text(Theme.textPrefix() + Main.tr(p, "admin-heads-api-dialog-input")))
        .initial(apiKey != null ? apiKey : "")
        .maxLength(Integer.MAX_VALUE)
        .build();

    Dialog dialog = CustomDialog.createConfirmationDialog(
        Main.tr(p, "admin-heads-api-dialog-title"),
        Main.tr(p, "admin-heads-api-dialog-description"),
        null,
        List.of(inputApiKey),
        (view, audience) -> _setHeadsExplorerApiKeyDialogCB(view, audience, parentMenu, backAction),
        null, Main.tr(p, "dialog-button-apply"), Main.tr(p, "dialog-button-cancel"));

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

    String confirmationTemplate = Main.tr(p, "admin-motd-set-message").replace("\\n", "\n");
    String confirmation = Main.getPrefix() + confirmationTemplate
        .replace("{line1}", ChatColor.translateAlternateColorCodes('&', line1))
        .replace("{line2}", ChatColor.translateAlternateColorCodes('&', line2));
    p.sendMessage(LegacyComponentSerializer.legacySection().deserialize(confirmation));
    p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);

    displayGUI(p, parentMenu, backAction);
  }

  private void _setMaintenanceMessageDialogCB(DialogResponseView view, Audience audience, CustomGUI parentMenu,
      Consumer<Player> backAction) {
    Player p = (Player) audience;
    String message = Optional.ofNullable(view.getText("message")).map(String::trim).orElse("");

    _settingsManager.setMaintenanceMessage(message);

    p.sendMessage(Component.text(Main.getPrefix() + Main.tr(p, "admin-maintenance-set-message", "message", message)));
    p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);

    displayGUI(p, parentMenu, backAction);
  }

  private void _setAFKTimeDialogCB(DialogResponseView view, Audience audience, CustomGUI parentMenu,
      Consumer<Player> backAction) {
    Player p = (Player) audience;
    int minutes = Math.round(view.getFloat("minutes"));

    _settingsManager.setAFKTime((int) minutes);

    p.sendMessage(Component.text(Main.getPrefix() + Main.tr(p, "admin-afk-time-set-message", "minutes", minutes)));
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

    p.sendMessage(Component.text(Main.getPrefix() + Main.tr(p, "admin-sleep-percentage-set-message",
        "percentage", percentage)));
    p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);

    displayGUI(p, parentMenu, backAction);
  }

  private void _setHeadsExplorerApiKeyDialogCB(DialogResponseView view, Audience audience, CustomGUI parentMenu,
      Consumer<Player> backAction) {
    Player p = (Player) audience;
    String apiKey = Optional.ofNullable(view.getText("apikey")).map(String::trim).orElse("");

    _settingsManager.setHeadsExplorerApiKey(apiKey);

    p.sendMessage(Component.text(Main.getPrefix() + Main.tr(p, "admin-heads-api-updated-message")));
    p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);

    _headsManager.fetchHeadsData().thenAccept(success -> {
      _plugin.getServer().getScheduler().runTask(_plugin, () -> {
        if (!p.isOnline()) {
          return;
        }

        if (success) {
          _plugin.getLogger().info("Heads Explorer data refreshed for API key update by " + p.getName() + ".");
          p.sendMessage(Component.text(Main.getPrefix() + Main.tr(p, "admin-heads-refresh-success")));
          p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
        } else {
          _plugin.getLogger()
              .warning("Heads Explorer data refresh failed for API key update by " + p.getName() + ".");
          long remainingSeconds = _headsManager.getRemainingFetchCooldownSeconds();
          p.sendMessage(Component.text(Main.getPrefix() + Theme.error() + Main.tr(p,
              remainingSeconds > 0 ? "admin-heads-refresh-failed-wait" : "admin-heads-refresh-failed",
              "seconds", remainingSeconds)));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
        }
      });
    });

    displayGUI(p, parentMenu, backAction);
  }

  // ----------------
  // SETTING APPLIERS
  // ----------------

  private void _toggleMaintenance(Player p) {
    boolean newState = !_settingsManager.getMaintenanceState();
    String message = _settingsManager.getMaintenanceMessage();

    _settingsManager.setMaintenanceState(newState);
    _sendToggleMessage(p, "admin-maintenance-toggle-message", newState);

    if (newState) {
      p.sendMessage(Main.getPrefix() + Main.tr(p, "admin-maintenance-current-message", "message", message));
    }

    _maintenanceManager.kickAll(_plugin.getServer().getOnlinePlayers());
  }

  private void _toggleCreeperBlockDamage(Player p) {
    boolean newState = !_settingsManager.getCreeperBlockDamage();
    _settingsManager.setCreeperBlockDamage(newState);
    _sendToggleMessage(p, "admin-creeper-block-damage-toggle-message", newState);
  }

  private void _toggleCreeperEntityDamage(Player p) {
    boolean newState = !_settingsManager.getCreeperEntityDamage();
    _settingsManager.setCreeperEntityDamage(newState);
    _sendToggleMessage(p, "admin-creeper-entity-damage-toggle-message", newState);
  }

  private void _toggleEnd(Player p) {
    boolean newState = !_settingsManager.getEnableEnd();
    _settingsManager.setEnableEnd(newState);
    _sendToggleMessage(p, "admin-end-toggle-message", newState);
  }

  private void _toggleNether(Player p) {
    boolean newState = !_settingsManager.getEnableNether();
    _settingsManager.setEnableNether(newState);
    _sendToggleMessage(p, "admin-nether-toggle-message", newState);
  }

  private void _toggleSleepingRain(Player p) {
    boolean newState = !_settingsManager.getSleepingRain();
    _settingsManager.setSleepingRain(newState);
    _sendToggleMessage(p, "admin-sleeping-rain-toggle-message", newState);
  }

  private void _toggleCropProtection(Player p) {
    boolean newState = !_settingsManager.getCropProtection();
    _settingsManager.setCropProtection(newState);
    _sendToggleMessage(p, "admin-crop-protection-toggle-message", newState);
  }

  private void _toggleRightClickCropHarvest(Player p) {
    boolean newState = !_settingsManager.getRightClickCropHarvest();
    _settingsManager.setRightClickCropHarvest(newState);
    _sendToggleMessage(p, "admin-right-click-harvest-toggle-message", newState);
  }

  private void _toggleLocatorBar(Player p) {
    boolean newState = !_settingsManager.getLocatorBarEnabled();
    _settingsManager.setLocatorBarEnabled(newState);
    _settingsManager.applyLocatorBarSetting();
    _sendToggleMessage(p, "admin-locator-bar-toggle-message", newState);
  }

  private void _toggleRecipeSync(Player p) {
    boolean newState = !_settingsManager.getRecipeSyncEnabled();
    _settingsManager.setRecipeSyncEnabled(newState);
    _sendToggleMessage(p, "admin-recipe-sync-toggle-message", newState);
  }

  private void _toggleActionBarTimer(Player p) {
    boolean newState = !_settingsManager.getActionBarTimerEnabled();
    _settingsManager.setActionBarTimerEnabled(newState);
    _sendToggleMessage(p, "admin-action-bar-timer-toggle-message", newState);
  }

  private void _toggleTags(Player p) {
    boolean newState = !_settingsManager.getTagsEnabled();
    _settingsManager.setTagsEnabled(newState);
    _sendToggleMessage(p, "admin-tags-toggle-message", newState);

    for (Player online : p.getServer().getOnlinePlayers()) {
      _nameTagManager.updateNameTag(online);
      _tabListManager.refreshPlayerListEntry(online);
    }
  }

  private void _toggleHeadsExplorer(Player p) {
    boolean newState = !_settingsManager.getHeadsExplorerEnabled();
    _settingsManager.setHeadsExplorerEnabled(newState);
    _sendToggleMessage(p, "admin-heads-toggle-message", newState);

    if (newState) {
      _headsManager.fetchHeadsData().thenAccept(success -> {
        _plugin.getServer().getScheduler().runTask(_plugin, () -> {
          if (!p.isOnline()) {
            return;
          }

          if (success) {
            _plugin.getLogger().info("Heads Explorer data refreshed after enabling by " + p.getName() + ".");
            p.sendMessage(Component.text(Main.getPrefix() + Main.tr(p, "admin-heads-refresh-success")));
            p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
          } else {
            _plugin.getLogger().warning("Heads Explorer data refresh failed after enabling by " + p.getName() + ".");
            long remainingSeconds = _headsManager.getRemainingFetchCooldownSeconds();
            p.sendMessage(Component.text(Main.getPrefix() + Theme.error() + Main.tr(p,
                remainingSeconds > 0 ? "admin-heads-refresh-failed-wait" : "admin-heads-refresh-failed",
                "seconds", remainingSeconds)));
            p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
          }
        });
      });
    }
  }

  private void _refreshHeadsExplorerData(Player p, CustomGUI parentMenu, Consumer<Player> backAction) {
    if (!_settingsManager.getHeadsExplorerEnabled()) {
      p.sendMessage(Component.text(Main.getPrefix() + Theme.error() + Main.tr(p, "common-error-heads-disabled")));
      p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
      displayGUI(p, parentMenu, backAction);
      return;
    }

    _headsManager.fetchHeadsData().thenAccept(success -> {
      _plugin.getServer().getScheduler().runTask(_plugin, () -> {
        if (!p.isOnline()) {
          return;
        }

        if (success) {
          _plugin.getLogger().info("Heads Explorer data refreshed via manual refresh by " + p.getName() + ".");
          p.sendMessage(Component.text(Main.getPrefix() + Main.tr(p, "admin-heads-refresh-success")));
          p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1);
        } else {
          _plugin.getLogger().warning("Heads Explorer data refresh failed via manual refresh by " + p.getName() + ".");
          long remainingSeconds = _headsManager.getRemainingFetchCooldownSeconds();
          p.sendMessage(Component.text(Main.getPrefix() + Theme.error() + Main.tr(p,
              remainingSeconds > 0 ? "admin-heads-refresh-failed-wait" : "admin-heads-refresh-failed",
              "seconds", remainingSeconds)));
          p.playSound(p, Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
        }
      });
    });

    displayGUI(p, parentMenu, backAction);
  }

  private void _toggleDeathChest(Player p) {
    boolean newState = !_settingsManager.getDeathChestEnabled();
    _settingsManager.setDeathChestEnabled(newState);
    _sendToggleMessage(p, "admin-death-chest-toggle-message", newState);
  }

  private void _toggleAFKProtection(Player p) {
    boolean newState = !_settingsManager.getAFKProtection();
    _settingsManager.setAFKProtection(newState);
    _afkManager.applyProtectionToAFKPlayers(newState);
    _sendToggleMessage(p, "admin-afk-protection-toggle-message", newState);
  }

  private void _toggleItemRestock(Player p) {
    boolean newState = !_settingsManager.getItemRestockEnabled();
    _settingsManager.setItemRestockEnabled(newState);
    _sendToggleMessage(p, "admin-item-restock-toggle-message", newState);
  }

  private String _t(String key, Object... replacements) {
    return _translations.translate(_viewer, key, replacements);
  }

  private String _state(boolean enabled) {
    return (enabled ? Theme.highlight() : Theme.error())
        + _t(enabled ? "common-state-enabled" : "common-state-disabled");
  }

  private void _sendToggleMessage(Player player, String key, boolean enabled) {
    player.sendMessage(Main.getPrefix() + Main.tr(player, key, "state",
        Theme.highlight().toString() + ChatColor.BOLD
            + Main.tr(player, enabled ? "common-state-enabled" : "common-state-disabled")));
  }
}
